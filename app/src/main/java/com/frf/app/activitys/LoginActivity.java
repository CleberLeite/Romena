package com.frf.app.activitys;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.frf.app.R;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.MaskEditUtil;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Objects;


public class LoginActivity extends MainActivity implements GoogleApiClient.OnConnectionFailedListener {

    private enum Screen {
        whait,
        loginForm,
        createForm,
        forgotForm
    }

    String email = "";
    String password = "";

    Screen screenStates = Screen.loginForm;
    private ViewHolder mHolder;
    private UserViewModel userViewModel;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        UTILS.setColorStatusBar(this, android.R.color.transparent);
        mHolder = new ViewHolder();

        initVars();
        initActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TRACKING.setTrackingClassName(this, "login_screen");
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initVars (){
        WebSettings webSettings = mHolder.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);

        mHolder.webView.setWebViewClient(new WebViewClient());
        mHolder.webView.setWebChromeClient(new ChromeClient());

        if (getIntent().getBooleanExtra("resetSSO", false)) {
            mHolder.webView.loadUrl("https://adm.app-admin.frf.ro/sso/ssoSignOut.php");
            clearCookies(this);
        }else {
            mHolder.webView.loadUrl("https://adm.app-admin.frf.ro/sso/ssoSignIn.php");
        }
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

     class WebViewClient extends android.webkit.WebViewClient
    {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (getIntent().getBooleanExtra("resetSSO", false)) {
                getIntent().removeExtra("resetSSO");
                mHolder.webView.loadUrl("https://adm.app-admin.frf.ro/sso/ssoSignIn.php");
            }else {
                mHolder.contentSplash.animate()
                        .alpha(0.0f)
                .setDuration(600)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mHolder.contentSplash.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains("ssoSignInRedirect.php?user_id=")) {
                String id = url.replace("https://adm.app-admin.frf.ro/sso/ssoSignInRedirect.php?user_id=", "");
                id = id.replaceAll(" ", "");
                mHolder.webView.setVisibility(View.GONE);
                mHolder.load.setVisibility(View.VISIBLE);
                userViewModel.auth(id);
            }

            return false;
        }
    }

    class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    void initActions() {

        mHolder.enter.setClickable(true);
        mHolder.enter.setOnClickListener(v -> changeStateLogin());
        mHolder.txtForgotPssword.setOnClickListener(view -> activeForgotLaytout());
        mHolder.llBack.setOnClickListener(view -> activeLoginLayout());
        mHolder.btnSignup.setOnClickListener(view -> activeCreateAccontLayout());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mHolder.loginGoogle.setOnClickListener(view -> {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, RC_SIGN_IN);
        });

        mHolder.loginFacebook.setOnClickListener(view -> Fblogin());

        mHolder.edtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 1) {
                    mHolder.inputDate.setHint(getString(R.string.zz_ll_aaaa));
                } else {
                    mHolder.inputDate.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mHolder.edtDate.addTextChangedListener(MaskEditUtil.mask(mHolder.edtDate, MaskEditUtil.FORMAT_DATE));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 7 //Instead use android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void bindViewModel() {
        super.bindViewModel();
        UserRepository repository = new UserRepository(activity, prefs);
        userViewModel = ViewModelProviders.of(LoginActivity.this).get(UserViewModel.class);
        userViewModel.setmRepository(repository);

        userViewModel.getUserData().observe(this, user -> {
            if (user.getIdMsg() == 1) {
                userViewModel.singIn(Objects.requireNonNull(mHolder.edtEmail.getText()).toString(), mHolder.edtPassword.getText().toString());
            } else {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LOGIN_AUTHENTICATED).execute();
                mHolder.enter.setClickable(true);
                UTILS.setUserLoggedIn(true);
                mHolder.load.setVisibility(View.GONE);
                mHolder.webView.clearCache(true);
                callMainMenu();
            }
        });

        userViewModel.getMessage().observe(this, message -> {
            mHolder.enter.setClickable(true);
            showMessage(message.getMsg());
        });

        userViewModel.getError().observe(this, error -> {
            mHolder.enter.setClickable(true);
            showMessage(error.getMsg());
        });
    }

    @Override
    public void unBindViewModel() {
        super.unBindViewModel();
        try{userViewModel.getUserData().removeObservers(this);}catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onBackPressed() {
        if(mHolder.webView.canGoBack()) {
            mHolder.webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    private void Fblogin() {
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        gotoProfile();


                        /*GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), (json, response) -> {
                                    if (response.getError() != null) {
                                        // handle error
                                        System.out.println("ERROR");
                                    } else {
                                        System.out.println("Success");
                                        try {
                                            String jsonresult = String.valueOf(json);
                                            System.out.println("JSON Result"+jsonresult);

                                            String str_email = json.getString("email");
                                            String str_id = json.getString("id");
                                            String str_firstname = json.getString("first_name");
                                            String str_lastname = json.getString("last_name");
                                          //  gotoProfile();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //gotoProfile();
                                    }
                                }).executeAsync();*/
                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG_CANCEL", "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("TAG_ERROR", error.toString());
                    }
                });
    }

    // Ativa tela de login
    void activeLoginLayout() {
        mHolder.enter.setText(activity.getResources().getString(R.string.autentificare));
        mHolder.txtFogotTitle.setVisibility(View.GONE);
        mHolder.txtPassword.setVisibility(View.VISIBLE);
        mHolder.edtPassword.setVisibility(View.VISIBLE);
        mHolder.txtForgotPssword.setVisibility(View.VISIBLE);

        mHolder.llIconSocial.setVisibility(View.VISIBLE);
        mHolder.llCreateAccount.setVisibility(View.VISIBLE);
        mHolder.llBack.setVisibility(View.GONE);
        mHolder.llNume.setVisibility(View.GONE);
        mHolder.llBirth.setVisibility(View.GONE);

        mHolder.inputPassword.setVisibility(View.VISIBLE);
        mHolder.edtPassword.setVisibility(View.VISIBLE);
//        mHolder.imgErrorPassword.setVisibility(View.GONE);

        screenStates = Screen.loginForm;
    }

    //Ativa tela de criar a conta
    void activeCreateAccontLayout() {
        mHolder.enter.setText(activity.getResources().getString(R.string.creeaza_cont));
        mHolder.llBack.setVisibility(View.VISIBLE);
        mHolder.txtForgotPssword.setVisibility(View.GONE);
        mHolder.llIconSocial.setVisibility(View.INVISIBLE);
        mHolder.llCreateAccount.setVisibility(View.INVISIBLE);
        mHolder.llNume.setVisibility(View.VISIBLE);
        mHolder.llBirth.setVisibility(View.VISIBLE);
        screenStates = Screen.createForm;
    }

    // Ativa tela de recuperar Senha
    void activeForgotLaytout() {
        mHolder.enter.setText(activity.getResources().getString(R.string.trimit_link));
        mHolder.txtFogotTitle.setVisibility(View.VISIBLE);
        mHolder.txtPassword.setVisibility(View.GONE);
        mHolder.edtPassword.setVisibility(View.GONE);
        mHolder.txtForgotPssword.setVisibility(View.GONE);
        mHolder.llIconSocial.setVisibility(View.INVISIBLE);
        mHolder.llCreateAccount.setVisibility(View.INVISIBLE);
        mHolder.llBack.setVisibility(View.VISIBLE);
        mHolder.inputPassword.setVisibility(View.GONE);
        mHolder.edtPassword.setVisibility(View.GONE);
        mHolder.imgErrorPassword.setVisibility(View.GONE);
        screenStates = Screen.forgotForm;
    }

    //Ao clicar no botão de ação abaixo
    void changeStateLogin() {
        //primeira tela
        if (screenStates == Screen.whait) {
            screenStates = Screen.createForm;

            return;
        }

        //Tela de login
        if (screenStates == Screen.loginForm) {
            String email = mHolder.edtEmail.getText().toString();
            String password = mHolder.edtPassword.getText().toString();


            if (validate("E-mail", mHolder.edtEmail, mHolder.inputEmail, mHolder.imgErrorEmail)
                    &&
                    validate("Parola", mHolder.edtPassword, mHolder.inputPassword, mHolder.imgErrorPassword)
            ) {

                userViewModel.singIn(email, password);
                mHolder.enter.setClickable(true);

            }
        }

        //Tela de criar
        if (screenStates == Screen.createForm) {
            String name = mHolder.edtNume.getText().toString();
            email = mHolder.edtEmail.getText().toString();
            password = mHolder.edtPassword.getText().toString();
            String date = mHolder.edtDate.getText().toString();

            if (
                    validate("Nume", mHolder.edtNume, mHolder.inputNume, mHolder.imgErrorNume) &&
                            validate("E-mail", mHolder.edtEmail, mHolder.inputEmail, mHolder.imgErrorEmail) &&
                            validate("Parola", mHolder.edtEmail, mHolder.inputPassword, mHolder.imgErrorPassword) &&
                            validate("Date", mHolder.edtDate, mHolder.inputDate, mHolder.imgErrorDate)
            ) {
                userViewModel.singUp(name, email, password, date);
                mHolder.enter.setClickable(true);
                TRACKING.setTrackingClassName(this, "create_account");

            }

        }

        //Tela de esqueceu a senha
        if (screenStates == Screen.forgotForm) {
            //Final da ação
            String email = mHolder.edtEmail.getText().toString();
            mHolder.enter.setClickable(true);

            if (validate("E-mail", mHolder.edtEmail, mHolder.inputEmail, mHolder.imgErrorEmail)) {
                userViewModel.forgot(email);
            }
        }
    }


    public boolean validate(String item, EditText edt, TextInputLayout inputLayout, ImageView error) {

        switch (item) {
            case "E-mail":
                if (!UTILS.isValidEmail(edt.getText().toString())) {
                    inputLayout.setError(item + " " + activity.getResources().getString(R.string.email_invalido));
                    requestFocus(edt);
                    error.setVisibility(View.VISIBLE);
                    return false;
                }
                break;
            case "Parola":
                if (edt.getText().toString().length() < 4) {
                    inputLayout.setError(item + " " + activity.getResources().getString(R.string.senha_invalida));
                    requestFocus(edt);
                    error.setVisibility(View.VISIBLE);
                    return false;
                }
                break;
            case "Date":
                if (!mHolder.edtDate.getText().toString().replaceAll(" ", "").equals("")) {
                    if (MaskEditUtil.unmask(edt.getText().toString()).length() != 8) {
                        inputLayout.setError(item + " " + activity.getResources().getString(R.string.data_invalida));
                        requestFocus(edt);
                        error.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
                break;
        }

        return true;
    }


    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //Chama o main menu
    void callMainMenu() {
        //finish();
        Intent intent = new Intent(activity, StartMenuActivity.class);
        intent.putExtra("login", 1);
        intent.putExtra("fromIsGetVersion", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            gotoProfile();
        } else {
            Toast.makeText(getApplicationContext(), "Login cancelado", Toast.LENGTH_LONG).show();
        }
    }

    private void gotoProfile() {
        Intent intent = new Intent(LoginActivity.this, StartMenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Public methods
    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
        });
        dialog.show();
    }

    class ViewHolder {
        LinearLayout llNume;
        LinearLayout llBirth;

        TextInputLayout inputNume;
        EditText edtNume;

        TextInputLayout inputDate;
        EditText edtDate;

        TextInputLayout inputPassword;
        TextInputLayout inputEmail;
        TextInputEditText edtEmail;
        TextView txtFogotTitle;
        TextView txtPassword;
        EditText edtPassword;
        TextView txtForgotPssword;
        Button enter;
        Button btnSignup;
        LinearLayout llIconSocial;
        LinearLayout llBack;
        LinearLayout llCreateAccount;

        ImageView imgErrorEmail;
        ImageView imgErrorPassword;
        ImageView imgErrorNume;
        ImageView imgErrorDate;
        View loginFacebook;
        View loginGoogle;

        WebView webView;
        ConstraintLayout load, contentSplash;

        public ViewHolder() {
            llNume = findViewById(R.id.ll_nume);
            llBirth = findViewById(R.id.ll_date_birth);
            txtFogotTitle = findViewById(R.id.txt_fogot_title);
            inputPassword = findViewById(R.id.til_password);
            inputNume = findViewById(R.id.til_nume);
            inputEmail = findViewById(R.id.til_email);
            edtNume = findViewById(R.id.tiedt_nume);
            edtEmail = findViewById(R.id.tiedt_email);
            txtPassword = findViewById(R.id.txt_password);
            edtPassword = findViewById(R.id.tiedt_senha);
            inputDate = findViewById(R.id.til_date);
            edtDate = findViewById(R.id.tiedt_date);

            enter = findViewById(R.id.login_enter);
            btnSignup = findViewById(R.id.bt_signup);
            txtForgotPssword = findViewById(R.id.txt_forgot_password);
            llIconSocial = findViewById(R.id.ll_icon_social);
            llCreateAccount = findViewById(R.id.ll_create_account);
            llBack = findViewById(R.id.ll_back);

            imgErrorEmail = findViewById(R.id.img_error_email);
            imgErrorPassword = findViewById(R.id.img_error_password);
            imgErrorNume = findViewById(R.id.img_error_nume);
            imgErrorDate = findViewById(R.id.img_error_date);
            loginFacebook = findViewById(R.id.img_login_facebook);
            loginGoogle = findViewById(R.id.img_login_google);

            webView = findViewById(R.id.webView);
            load = findViewById(R.id.animation_load);
            contentSplash = findViewById(R.id.content_splash);
        }
    }
}