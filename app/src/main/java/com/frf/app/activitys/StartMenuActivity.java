package com.frf.app.activitys;

import static com.frf.app.utils.CONSTANTS.LOADING_SCREEN_KEY;
import static com.frf.app.utils.CONSTANTS.SCREEN_REDIRECT_PUSH;
import static com.frf.app.utils.CONSTANTS.SCREEN_SEARCH;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_DEFAUT;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_FEEDCOMMENT;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_FEEDLIKE;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_MATCHS;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_NOTICIA;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_QUIZ;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_SHOP;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_STORE;
import static com.frf.app.utils.RomenaNotificationManager.PUSH_ID_VIDEO;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.BuildConfig;
import com.frf.app.R;
import com.frf.app.fragments.ArenaFragment;
import com.frf.app.fragments.ArenaViewPostFragment;
import com.frf.app.fragments.EventsFragment;
import com.frf.app.fragments.EventsUpcomingMatchFragment;
import com.frf.app.fragments.HomeFragment;
import com.frf.app.fragments.NewsFragment;
import com.frf.app.fragments.PerfilFragment;
import com.frf.app.fragments.QuizFragment;
import com.frf.app.fragments.RankingFragment;
import com.frf.app.fragments.RewardsFragment;
import com.frf.app.fragments.SettingsFragment;
import com.frf.app.fragments.ShopFragment;
import com.frf.app.fragments.VideosFragment;
import com.frf.app.informations.UserInformation;
import com.frf.app.models.HeaderModel;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.CONSTANTS;
import com.frf.app.utils.RomenaNotificationManager;
import com.frf.app.utils.SecurePreferences;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.utils.fragment.FragmentsHolderController;
import com.frf.app.viewpresenter.HeaderPresenter;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class StartMenuActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback, NavigationView.OnNavigationItemSelectedListener  {

    private ViewHolder mHolder;
    private FragmentsHolderController mFragmentsC;
    private FragmentHolderViewModel mfragmentViewModel;
    private UserViewModel userViewModel;
    private static final String TAG = "StartMenuActivity";

    private boolean isInitHeader = true;
    private boolean isGetProfile = true;
    private boolean getVersionOK = false;
    private boolean hasPushToCall = false;

    private boolean canChangeFragment;

    private Screen currentScreen;
    private boolean competitionsIsOpen = false;
    private boolean quizIsOpen = false;
    private boolean rankingIsOpen = false;
    private boolean badgesAndAwardsIsOpen = false;
    private boolean settingsIsOpen = false;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public enum Screen {
        Home,
        Arena,
        News,
        Video,
        Shop
    }

    private static final int OP_ID_SING_OUT = 2;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_start_menu);
        mHolder = new ViewHolder();


        mFragmentsC = new FragmentsHolderController(getSupportFragmentManager(), R.id.fragment_container);

        callGetVersion();
        checkIfCameFromLogin();

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    public void changeHeader(int type) {
        mfragmentViewModel.setCanChangeHeader(type);
    }

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    void initActions() {
        initBottonView();

        if (mfragmentViewModel == null) {
            mfragmentViewModel = new ViewModelProvider(this).get(FragmentHolderViewModel.class);
            mfragmentViewModel.canChange().observe(this, change -> canChangeFragment = change);
        }

        mfragmentViewModel.setCanChangeHeader(1);
        mFragmentsC.changeToFragmentContext(new HomeFragment(), "HomeFragment", "Home", currentScreen == Screen.Home);
        currentScreen = Screen.Home;

        if (CONSTANTS.link_ecommerce == null) CONSTANTS.link_ecommerce = "";
        if (CONSTANTS.link_ticketing == null) CONSTANTS.link_ticketing = "";

        if (CONSTANTS.link_ecommerce.equals("")) {
            mHolder.itmRequests.setVisibility(View.GONE);
        }

        if (CONSTANTS.link_ticketing.equals("")) {
            mHolder.itmTickets.setVisibility(View.GONE);
        }

        if (CONSTANTS.link_ticketing.equals("") && CONSTANTS.link_ecommerce.equals("")) {
            mHolder.bottomNav.getMenu().removeItem(R.id.shop);
        }

        mHolder.itmSetings.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_CONFIGURATIONS).execute();
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
            mfragmentViewModel.changeFragment(new SettingsFragment(activity, new HeaderModel(R.layout.include_home_header, (ViewGroup) mHolder.holderUserHeader)), "Settings", 1);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mHolder.itmMatchesResults.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_COMPATITIONS_AND_RESULTS).execute();
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
            mfragmentViewModel.changeFragment(new EventsFragment(mfragmentViewModel), "EventsFragment", 1);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mHolder.itmRequests.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_ECOMMERCE).execute();
            mfragmentViewModel.setCanChangeHeader(1);
            mFragmentsC.changeToFragmentContext(new ShopFragment(1), "ShopFragment", "Shop", currentScreen == Screen.Shop);
            currentScreen = Screen.Shop;
            mHolder.bottomNav.getMenu().findItem(R.id.shop).setChecked(true);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        mHolder.itmTickets.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_TICKETS).execute();
            mfragmentViewModel.setCanChangeHeader(1);
            mFragmentsC.changeToFragmentContext(new ShopFragment(0), "ShopFragment", "Shop", currentScreen == Screen.Shop);
            mHolder.bottomNav.getMenu().findItem(R.id.shop).setChecked(true);
            currentScreen = Screen.Shop;
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        mHolder.itmLogout.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_LOGOUT).execute();
            alertSignOut();
        });

        mHolder.itmProfile.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_PERFIL).execute();
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
            mfragmentViewModel.changeFragment(new PerfilFragment(), "Profile", 1);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            // getHeader(false, true);
        });

        mHolder.itmPuncte.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_BADGES_AND_RESCUES).execute();
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
            mfragmentViewModel.changeFragment(new RewardsFragment(), "RewardsFragment", 1);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mHolder.itmClasament.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_RANKING).execute();
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
            mfragmentViewModel.changeFragment(new RankingFragment(), "Ranking", 1);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mHolder.itmQuiz.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_QUIZ).execute();
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
            mfragmentViewModel.changeFragment(new QuizFragment(this), "Quiz", 1);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        mHolder.version.setText(getResources().getString(R.string.versiune) + ": " + BuildConfig.VERSION_NAME);


        checkBundle(getIntent());
    }

    public void resetBottomMenu() {
        mHolder.bottomNav.getMenu().setGroupCheckable(0, false, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{mFragmentsC.getCurrentFragment().onActivityResult(requestCode, resultCode, data);}catch (Exception e){e.printStackTrace();}
    }

    void alertSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage(getResources().getString(R.string.sigur_doriti_sa_iesiti));

        builder.setPositiveButton(R.string.a_confirma, (dialog, which) -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_LOGOUT_CONFIRM).execute();
            Hashtable<String, Object> params = new Hashtable<>();
            params.put("sId", UserInformation.getSessionId());
            params.put("endType", 3);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            params.put("time", currentDateandTime);
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_END_SESSION, OP_ID_SING_OUT, StartMenuActivity.this).execute(params);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
        });

        builder.setNegativeButton(R.string.anulare, (dialog, which) -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_LATERALMENU_LOGOUT_CANCEL).execute();
            try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
        });

        AlertDialog alert = builder.create();

        try{alert.show();
            alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
            alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryBackground));}catch (Exception e){e.printStackTrace();}
    }

    void checkBundle (Intent _intent){
        Bundle b = _intent.getExtras();
        Bundle pushBundle = b;
        int screenToLoad = -1;

        if((b != null) && (b.containsKey(LOADING_SCREEN_KEY)) ){
            screenToLoad = b.getInt(LOADING_SCREEN_KEY);

            switch (screenToLoad) {
                //Quando o push leva para uma tela nova
                case SCREEN_REDIRECT_PUSH:
                    pushBundle = b.getBundle("pushBundle");
                    break;

                //Quando o push é de tipo diferente
                case SCREEN_SEARCH:
                    pushBundle = b;
                    break;
            }

            if(getVersionOK){
                callPush(pushBundle);
            }else{
                hasPushToCall = true;
            }
        }else{
            Log.d("Push","Não foi chamado");
        }
    }

    //Faz chamada do push
    @SuppressLint("WrongConstant")
    void callPush(Bundle pushBundle){
        if(getIntent() != null && getIntent().getExtras() != null){
            getIntent().getExtras().clear();
        }

        if((pushBundle != null) && (pushBundle.containsKey("idPush") ) ){
            int type = pushBundle.getInt("t", -1);
            Hashtable<String, Object> params = new Hashtable<>();
            params.put("idPush", pushBundle.getInt("idPush"));
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_NOTIFICATION, 999, emptyAsync).execute(params);

            switch (type){
                case PUSH_ID_DEFAUT: {

                }
                break;

                case PUSH_ID_NOTICIA:{
                    mfragmentViewModel.setCanChangeHeader(1);
                    mFragmentsC.addFragmentInFrameSimple(new NewsFragment(pushBundle.getInt("v", -1), true), "NewsFragment", "News");
                    mHolder.bottomNav.getMenu().findItem(R.id.navigation_news).setChecked(true);
                    currentScreen = Screen.News;
                }
                break;

                case PUSH_ID_QUIZ: {
                    mfragmentViewModel.changeFragment(new QuizFragment(this), "Quiz", 1);
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
                break;

                case PUSH_ID_STORE: {
                    mfragmentViewModel.changeFragment(new RewardsFragment(true, pushBundle.getInt("v", -1)), "RewardsFragment", 1);
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
                break;

                case PUSH_ID_MATCHS: {
                    mfragmentViewModel.changeFragment(new EventsUpcomingMatchFragment(pushBundle.getInt("v", -1), false, true, mfragmentViewModel), "EventsUpcomingMatchFragment", 3);
                }
                break;

                case PUSH_ID_SHOP: {
                    if (!CONSTANTS.link_ecommerce.equals("") || !CONSTANTS.link_ticketing.equals("")) {
                        mfragmentViewModel.setCanChangeHeader(1);
                        mFragmentsC.changeToFragmentContext(new ShopFragment(), "ShopFragment", "Shop", currentScreen == Screen.Shop);
                        mHolder.bottomNav.getMenu().findItem(R.id.shop).setChecked(true);
                        currentScreen = Screen.Shop;
                    }
                }
                break;

                case PUSH_ID_FEEDLIKE:
                case PUSH_ID_FEEDCOMMENT: {
                    mfragmentViewModel.setCanChangeHeader(1);
                    mFragmentsC.changeToFragmentContext(new ArenaFragment(), "ArenaFragment", "Arena", currentScreen == Screen.Arena);
                    currentScreen = Screen.Arena;
                    mfragmentViewModel.changeFragment(new ArenaViewPostFragment(pushBundle.getInt("v", -1), true), "ArenaViewPostFragment", 3);
                    mHolder.bottomNav.getMenu().findItem(R.id.navigation_arena).setChecked(true);
                }
                break;

                case PUSH_ID_VIDEO: {
                    mfragmentViewModel.setCanChangeHeader(1);
                    mFragmentsC.changeToFragmentContext(new VideosFragment(pushBundle.getInt("v", -1)), "VideosFragment", "Videos", currentScreen == Screen.Video);
                    currentScreen = Screen.Video;
                    mHolder.bottomNav.getMenu().findItem(R.id.videos).setChecked(true);
                }
                break;
            }
        }
    }

    @Override
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try {
            opId = message.getData().getInt("opId");
            response = new JSONObject(message.getData().getString("response"));
            success = message.getData().getBoolean("success");
        } catch (JSONException e) {
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if (success) {
            OnAsyncOperationSuccess(opId, response);
        } else {
            OnAsyncOperationError(opId, response);
        }
        return false;
    });
    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        if (opId == OP_ID_SING_OUT) {
            UserInformation.setSessionId(0);
            UTILS.ClearInformations(this);
            Intent newIntent = new Intent(this, SplashActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.putExtra("resetSSO", true);
            startActivity(newIntent);
            activity.finish();

        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        if (opId == OP_ID_SING_OUT) {
            UserInformation.setSessionId(0);
            UTILS.ClearInformations(this);
            Intent newIntent = new Intent(this, SplashActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.putExtra("resetSSO", true);
            startActivity(newIntent);
            activity.finish();
        }
    }

    @Override
    public void bindViewModel() {
        super.bindViewModel();
        if (mfragmentViewModel == null) {
            mfragmentViewModel = new ViewModelProvider(this).get(FragmentHolderViewModel.class);
            mfragmentViewModel.canChange().observe(this, change -> canChangeFragment = change);
        }

        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, this::initHeader);
        userViewModel.getUser(false);

        mfragmentViewModel.getFragment().observe(this, fragment -> {
            if (canChangeFragment) {
                mFragmentsC.addFragmentInFrameSimple(fragment.getFragment(), fragment.getName(), null, R.anim.slide_in_right, R.anim.slide_out_left);
                mfragmentViewModel.setHasToChange(false);
            }
        });
    }

    @Override
    public void unBindViewModel() {
        super.unBindViewModel();
        try{mfragmentViewModel.getFragment().removeObservers(this);}catch (Exception e){e.printStackTrace();}
    }

    public void initHeader(UserModel userModel) {

        UTILS.getImageAt(activity, userModel.getImg(), mHolder.imgFotoUser, new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_placeholderuser)
                .error(R.drawable.ic_placeholderuser));

        mHolder.txtNomeUser.setText(userModel.getName());
        mHolder.typeFan.setText(userModel.getUserType());

        if (isInitHeader) {
            getHeader(1, userModel);
            isInitHeader = false;
        }

        mfragmentViewModel.canChangeHeader().observe(this, change -> {

            if (change == -1) {
                mHolder.bottomNav.setVisibility(View.GONE);
                mHolder.holderUserHeader.setVisibility(View.GONE);
            } else {
                mHolder.bottomNav.setVisibility(View.VISIBLE);
                mHolder.holderUserHeader.setVisibility(View.VISIBLE);
            }

            getHeader(change, userModel);
        });
    }

    public void setIsUpdateProfile(boolean update) {
        isGetProfile = update;
    }

    public boolean isUpdateProfile() {
        return isGetProfile;
    }

    void callGetVersion (){
        new AsyncOperation(this, AsyncOperation.TASK_ID_GET_VERSION, 999, new AsyncOperation.IAsyncOpCallback() {
            @Override
            public void CallHandler(int opId, JSONObject response, boolean success) {
                if(success){
                    this.OnAsyncOperationSuccess(opId, response);
                }else{
                    this.OnAsyncOperationError(opId, response);
                }
            }

            @SuppressLint("ResourceType")
            @Override
            public void OnAsyncOperationSuccess(int opId, JSONObject response) {
                boolean success = false;
                String msg = "";
                int status = 0;
                try{
                    if( (response.has("Message")) && (response.get("Message") != JSONObject.NULL) ){
                        msg = response.getString("Message");
                    }

                    if( (response.has("Status")) && (response.get("Status") != JSONObject.NULL) ){
                        success = (response.getInt("Status") == HttpURLConnection.HTTP_OK);
                    }

                    if( (response.has("sId")) && (response.getInt("sId") > 0)){
                        UserInformation.setSessionId((int) response.get("sId"));
                    }

                } catch (JSONException e) {
                    UTILS.DebugLog(TAG, e);
                }

                if(success){
                    getVersionOK = true;
                    initActions();
                }else{
                    getVersionOK = false;
                    callGetVersion();
                }

                try {
                    UserInformation.setSessionId(response.getInt("sId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnAsyncOperationError(int opId, JSONObject response) {

            }
        }).execute();
    }


    //Inicia header
    void getHeader(int type, UserModel userModel) {
        new HeaderPresenter(activity, R.layout.include_home_header, (ViewGroup) mHolder.holderUserHeader, type, userModel, new HeaderPresenter.ListenerHeader() {
            @Override
            public void OnNotificationClick() {
                saveTrackNotifications();
                Intent intent = new Intent(StartMenuActivity.this, NotificationsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }

            @Override
            public void OnDrawerClick() {
                drawerLayout.openDrawer(navigationView);
                TRACKING.setTrackingClassName(StartMenuActivity.this, "menu_screen");
            }

            @SuppressLint("WrongConstant")
            @Override
            public void OnPointsandPrizes() {
                if (!(mFragmentsC.getCurrentFragment() instanceof RewardsFragment)) {
                    mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
                    mfragmentViewModel.changeFragment(new RewardsFragment(), "RewardsFragment", 1);
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            }

            @Override
            public void OnBackClick(int type) {
                getHeader(1, userModel);
                if (!mFragmentsC.onBackPressed()) {
                    initCloseAppMessage();
                }
            }
        });
    }

    public SecurePreferences getPref() {
        return prefs;
    }

    void saveTrackNotifications() {
        if (competitionsIsOpen) {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_COMPETITIONS_HEADER_NOTIFICATIONS).execute();
            return;
        }

        if (quizIsOpen) {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_QUIZ_HEADER_NOTIFICATIONS).execute();
            return;
        }

        if (rankingIsOpen) {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_RANKING_HEADER_NOTIFICATIONS).execute();
            return;
        }

        if (badgesAndAwardsIsOpen) {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_BADGES_AND_AWARDS_HEADER_NOTIFICATIONS).execute();
            return;
        }

        if (settingsIsOpen) {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_SETTINGS_HEADER_NOTIFICATIONS).execute();
            return;
        }

        switch (currentScreen) {
            case Home: {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_HEADER_NOTIFICATIONS).execute();
            }
            break;
            case Arena: {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_HEADER_NOTIFICATIONS).execute();
            }
            break;
            case News: {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_HEADER_NOTIFICATIONS).execute();
            }
            break;
            case Video: {
                new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_VIDEOS_HEADER_NOTIFICATIONS).execute();
            }
            break;
        }

    }

    @SuppressLint({"NonConstantResourceId", "WrongConstant"})
    void initBottonView() {
        mHolder.bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            menuItem.setChecked(true);
            mHolder.bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
            mHolder.bottomNav.getMenu().setGroupCheckable(0, true, true);
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    goHome();
                    return true;
                case R.id.navigation_news:
                    goNews();
                    return true;
                case R.id.videos:
                    goVideo();
                    return true;
                case R.id.navigation_arena:
                    goArena();
                    return true;
                case R.id.shop:
                    goShop();
                    return true;
            }
            return false;
        });
    }

    public void changeCurrentScreen(Screen index) {
        this.currentScreen = index;
    }

    void goHome() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_HOME_BOTTOM_MENU_HOME).execute();
        if (mfragmentViewModel == null) {
            mfragmentViewModel = new ViewModelProvider(this).get(FragmentHolderViewModel.class);
            mfragmentViewModel.canChange().observe(this, change -> canChangeFragment = change);
        }
        mfragmentViewModel.setCanChangeHeader(1);
        mFragmentsC.changeToFragmentContext(new HomeFragment(), "HomeFragment", "Home", currentScreen == Screen.Home);
        currentScreen = Screen.Home;
    }

    void goArena() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_ARENA_BOTTOM_MENU_ARENA).execute();
        mfragmentViewModel.setCanChangeHeader(1);
        mFragmentsC.changeToFragmentContext(new ArenaFragment(), "ArenaFragment", "Arena", currentScreen == Screen.Arena);
        currentScreen = Screen.Arena;
    }

    void goNews() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NEWS_BOTTOM_MENU_NEWS).execute();
        mfragmentViewModel.setCanChangeHeader(1);
        mFragmentsC.changeToFragmentContext(new NewsFragment(), "NewsFragment", "News", currentScreen == Screen.News);
        currentScreen = Screen.News;
    }

    void goVideo() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_VIDEOS_BOTTOM_MENU_VIDEOS).execute();
        mfragmentViewModel.setCanChangeHeader(1);
        mFragmentsC.changeToFragmentContext(new VideosFragment(), "VideosFragment", "Videos", currentScreen == Screen.Video);
        currentScreen = Screen.Video;
    }

    void goShop() {
        mfragmentViewModel.setCanChangeHeader(1);
        mFragmentsC.changeToFragmentContext(new ShopFragment(), "ShopFragment", "Shop", currentScreen == Screen.Shop);
        currentScreen = Screen.Shop;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }


        if (!mFragmentsC.onBackPressed()) {
            initCloseAppMessage();
        }

    }

    public void initCloseAppMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getResources().getString(R.string.voce_deseja_fechar_o_app));
        builder.setPositiveButton(getResources().getString(R.string.sim), (dialog, which) -> finishAffinity());
        builder.setNegativeButton(getResources().getString(R.string.nao), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
        });
        dialog.show();
    }

    void checkIfCameFromLogin(){
        if(getIntent().hasExtra("login")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
            alertDialog.setMessage(R.string.doriți_sa_primiți_notificări)
                    .setPositiveButton(R.string.sim, (dialog, which) -> updateApns(1, RomenaNotificationManager.gcm))
                    .setNegativeButton(R.string.nao, (dialog, which) -> {
                        updateApns(0, RomenaNotificationManager.gcm);
                        dialog.dismiss();
                    });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }

    //Faz update da Apns
    public void updateApns(final int allow, String gcmToken) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_NOTIFICATIONS_REQUIRED).execute();
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("apns", gcmToken);
        params.put("allow", allow);
        new AsyncOperation(this, AsyncOperation.TASK_ID_SET_USER_APNS, 0, new AsyncOperation.IAsyncOpCallback() {
            @Override
            public void CallHandler(int opId, JSONObject response, boolean success) {
                if(success){
                    this.OnAsyncOperationSuccess(opId, response);
                }else{
                    this.OnAsyncOperationError(opId, response);
                }
            }

            @Override
            public void OnAsyncOperationSuccess(int opId, JSONObject response) {
                prefs.put(CONSTANTS.SHARED_PREFS_KEY_NEED_TO_UPDATE_APNS, String.valueOf(allow));
            }

            @Override
            public void OnAsyncOperationError(int opId, JSONObject response) {
            }
        }).execute(params);
    }

    public boolean isCompetitionsIsOpen() {
        return competitionsIsOpen;
    }

    public void setCompetitionsIsOpen(boolean competitionsIsOpen) {
        this.competitionsIsOpen = competitionsIsOpen;
    }

    public boolean isQuizIsOpen() {
        return quizIsOpen;
    }

    public void setQuizIsOpen(boolean quizIsOpen) {
        this.quizIsOpen = quizIsOpen;
    }

    public boolean isRankingIsOpen() {
        return rankingIsOpen;
    }

    public void setRankingIsOpen(boolean rankingIsOpen) {
        this.rankingIsOpen = rankingIsOpen;
    }

    public boolean isBadgesAndAwardsIsOpen() {
        return badgesAndAwardsIsOpen;
    }

    public void setBadgesAndAwardsIsOpen(boolean badgesAndAwardsIsOpen) {
        this.badgesAndAwardsIsOpen = badgesAndAwardsIsOpen;
    }

    public boolean isSettingsIsOpenIsOpen() {
        return settingsIsOpen;
    }

    public void setSettingsIsOpenIsOpen(boolean settingsIsOpen) {
        this.settingsIsOpen = settingsIsOpen;
    }

    class ViewHolder {

        DrawerLayout drawerLayout;
        FrameLayout fragmentContainer;
        BottomNavigationView bottomNav;
        View holderUserHeader;
        View itmSetings;
        LinearLayout itmMatchesResults;
        LinearLayout itmRequests;
        LinearLayout itmTickets;
        LinearLayout itmLogout;
        LinearLayout itmProfile;
        LinearLayout itmPuncte;
        LinearLayout itmClasament;
        LinearLayout itmQuiz;

        ImageView imgFotoUser;
        TextView txtNomeUser;
        TextView typeFan;
        TextView version;

        public ViewHolder() {
            drawerLayout = findViewById(R.id.drawerLayout);
            fragmentContainer = findViewById(R.id.fragment_container);
            bottomNav = findViewById(R.id.bottom_navigation);
            holderUserHeader = findViewById(R.id.header_holder);
            itmSetings = findViewById(R.id.item_settings);
            itmMatchesResults = findViewById(R.id.ll_MatchesResults);
            itmRequests = findViewById(R.id.ll_requests);
            itmTickets = findViewById(R.id.ll_tickets);
            itmLogout = findViewById(R.id.ll_loggout);
            itmProfile = findViewById(R.id.ll_menu_perfil);
            itmPuncte = findViewById(R.id.ll_puncte);
            itmClasament = findViewById(R.id.ll_clasament);
            itmQuiz = findViewById(R.id.ll_quiz);
            typeFan = findViewById(R.id.typeFan);
            txtNomeUser = findViewById(R.id.txtNomeUser);
            imgFotoUser = findViewById(R.id.imgFotoUser);
            version = findViewById(R.id.version);
        }
    }
}