package com.frf.app.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.data.EditProfileCoinData;
import com.frf.app.models.user.UserModel;
import com.frf.app.picker.ImageContract;
import com.frf.app.picker.ImagePresenter;
import com.frf.app.repository.MainRepository;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class ProfileEditActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback {
    private ViewHolder mHolder;
    private UserViewModel userViewModel;

    private Uri photoURI;
    public static final int REQUEST_TAKE_PHOTO = 101;
    public static final int REQUEST_GALLERY_PHOTO = 102;
    private static final int CAMERA_PIC_REQUEST = 123;
    private static final int MY_READ_PERMISSION_CODE = 101;
    private static final int OP_SET_UPDATE_USER = 0;
    private static final int OP_GET_PROFILE_COINS = 1;
    private static final int OP_SET_REMOVE_AVATAR = 2;

    UserModel newUser;

    String type = "";
    String txtGenero = "";
    private boolean mPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        UTILS.setColorStatusBar(this, R.color.PrimaryText);

        mHolder = new ViewHolder();


        //Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    }, MY_READ_PERMISSION_CODE);
            mPermission = true;
        } else {
            initAction();
        }
        GetEditProfileCoins();
    }

    @Override
    public void onBackPressed() {
        finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    void GetEditProfileCoins() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_PROFILE_COINS, OP_GET_PROFILE_COINS, this).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Ler permissão de armazenamento externo concedida
                Toast.makeText(this, getString(R.string.PERMISSION_GRANTED), Toast.LENGTH_SHORT).show();
                initAction();
            } else {
                //Ler permissão de armazenamento externo negada
                Toast.makeText(this, getString(R.string.PERMISSION_DENIED), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void initAction() {
        mHolder.imgBack.setOnClickListener(view -> onBackPressed());

        //mHolder.edtDate.addTextChangedListener(MaskEditUtil.mask(mHolder.edtDate, MaskEditUtil.FORMAT_DATE));

        mHolder.nickname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    activeButton();
                }
            }
        });

        mHolder.btnEdit.setOnClickListener(view -> SetUpdateUser(newUser));

        mHolder.selectPhoto.setOnClickListener(v -> {
            selectImage();
        });

        mHolder.mdc.setOnClickListener(view -> {
            Intent intent = new Intent (Intent.ACTION_VIEW);
            intent.setData (Uri.parse("https://id.frf.ro/Profile/Edit/1?ReturnUrl=frf://opentheapp"));
            startActivity(intent);
        });


        mHolder.btnEdit.setEnabled(false);
        mHolder.btnEdit.setAlpha(0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null) {
            if (uri.toString().equals("frf://opentheapp") ) {
                if (prefs.containsKey(MainRepository.SHARED_KEY_USER)) {
                    resetUserCoins();
                    userViewModel.getUserProfile();
                    setIntent(null);
                }else {
                    finish();
                    Intent resultIntent = new Intent(activity, SplashActivity.class);
                    resultIntent.putExtra("resetSSO", true);
                    startActivity(new Intent(activity, SplashActivity.class));
                }
            }
        }
    }

    private void selectImage() {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_EDIT_PROFILE_ALTER_IMG).execute();
        final CharSequence[] items = {getString(R.string.foto), getString(R.string.galery), getString(R.string.delete_photo), getString(R.string.anulare)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Fă o fotografie")) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            } else if (items[item].equals("Alege din galerie")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_PHOTO);
            } else if (items[item].equals("Anulare")) {
                try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
            } else if (items[item].equals("Șterge fotografia")) {
                Hashtable<String, Object> params = new Hashtable<>();
                params.put("remove", 1);
                new AsyncOperation(activity, AsyncOperation.TASK_ID_UPLOAD_PICTURE, OP_SET_REMOVE_AVATAR, this).execute(params);
                try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadPhoto(requestCode, resultCode, data);
    }

    public void activeButton() {
        mHolder.btnEdit.setAlpha(1);
        mHolder.btnEdit.setEnabled(true);
        mHolder.btnEdit.setBackgroundTintList(ContextCompat.getColorStateList(ProfileEditActivity.this, R.color.PrimaryBackground));
    }

    void uploadPhoto(int requestCode, int resultCode, Intent data) {

        boolean resultOK = false;

        if (resultCode == RESULT_OK) {
            //Request de tirar foto

            if (requestCode == REQUEST_TAKE_PHOTO) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                photoURI = Uri.parse(UTILS.getRealPathFromURI(this, getImageUri(getApplicationContext(), image)));
                resultOK = true;
            }
            //Request de pegar foto da galeria
            else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = Objects.requireNonNull(data).getData();
                //String mPhotoPath = getRealPathFromUri(selectedImage);

                if (selectedImage != null) {
                    try {
                        photoURI = Uri.parse(UTILS.getRealPathFromURI(activity, selectedImage));
                        resultOK = true;
                    } catch (Exception ignored) {
                        try {
                            photoURI = Uri.parse(UTILS.getPath(activity, selectedImage));
                            resultOK = true;
                        } catch (Exception e) {
                            Log.d("FragmentController", e.getMessage() + "");
                            Toast.makeText(activity, getResources().getString(R.string.eroare_la_incarcarea_fotografiei_incercati_altul), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.eroare_la_incarcarea_fotografiei_incercati_altul), Toast.LENGTH_LONG).show();
                }
            }
        }

        if (resultOK) {
            userViewModel.uploadPhoto(photoURI);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindViewModel() {
        super.bindViewModel();
        UserRepository repository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(repository);
        userViewModel.getUserData().observe(this, user -> {
            UTILS.getImageAt(activity, user.getImg(), mHolder.idUserImg, new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder));

            //mHolder.edtNume.setText(user.getName());

            mHolder.nickname.setHint(user.getName());
            mHolder.edtNume.setText(user.getSso_info().getReal_name());
            mHolder.edtOras.setText(user.getSso_info().getCity());
            mHolder.edtDate.setText(user.getSso_info().getBirthdate());
            mHolder.spnGen.setText(user.getSso_info().getGender());

            //uploadUser(user);
            newUser = user;
            txtGenero = "";

            initUser(user);
        });
        userViewModel.getMessage().observe(this, message -> {
            if (message.getMsg() != null) {
                showMessage(message.getMsg());
            }
        });
        userViewModel.getUser(true);
    }

    public void showMessage(String message) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorRed600));
        });
        dialog.show();
    }

    String convertDate(String date) {
        try {
            if (date.length() > 0) {
                return date.substring(8, 10) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4);
            } else {
                return "";
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    void SetUpdateUser(UserModel user) {

        Hashtable<String, Object> params = new Hashtable<>();

        if (!user.getName().equals(mHolder.nickname.getText().toString()) &&
                !mHolder.nickname.getText().toString().replaceAll(" ", "").equals("")) {
            params.put("name", mHolder.nickname.getText().toString());
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_EDIT_PROFILE_ALTER_NICKNAME).execute();
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_UPDATE_USER, OP_SET_UPDATE_USER, this).execute(params);
        }

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
    public void CallHandler(int opId, JSONObject response, boolean success) {
        Message message = new Message();
        Bundle b = new Bundle();
        b.putInt("opId", opId);
        b.putString("response", response.toString());
        b.putBoolean("success", success);
        message.setData(b);
        handlerOp.sendMessage(message);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        switch (opId) {
            case OP_SET_REMOVE_AVATAR:
            case OP_SET_UPDATE_USER: {
                int status = 0;

                if (response.has("Status")) {
                    try {
                        status = (int) response.get("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (status == 200) {
                    try {
                        mHolder.btnEdit.setEnabled(false);
                        mHolder.btnEdit.setAlpha(0.5f);
                        resetUserCoins();
                        userViewModel.getUserProfile();

                        Toast.makeText(activity, response.getString("msg"), Toast.LENGTH_SHORT).show();
                        // activity.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;

            case OP_GET_PROFILE_COINS: {
                int status = 0;
                if (response.has("Status")) {
                    try {
                        status = (int) response.get("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (status == 200) {
                    if (response.has("Object")) {
                        try {
                            Gson gson = new Gson();
                            JSONObject obj = response.getJSONObject("Object");
                            EditProfileCoinData[] data = gson.fromJson(obj.getString("itens"), EditProfileCoinData[].class);
                            ArrayList<EditProfileCoinData> editProfileCoinData = new ArrayList<>();
                            editProfileCoinData.addAll(Arrays.asList(data));

                            for (EditProfileCoinData index : editProfileCoinData) {
                                if (index.getT() == 2) {
                                    mHolder.contentCoinImg.setVisibility(View.GONE);
                                    mHolder.coinImg.setText("+" + index.getT());
                                }

                                if (index.getT() == 3) {
                                    mHolder.llEditProfileCoinAddress.setVisibility(View.GONE);
                                    mHolder.txtEditProfileCoinAddress.setText("+" + index.getT());
                                }

                                if (index.getT() == 4) {
                                    mHolder.llEditProfileCoinBirthdate.setVisibility(View.GONE);
                                    mHolder.txtEditProfileCoinBirthdate.setText("+" + index.getT());
                                }

                                if (index.getT() == 5) {
                                    mHolder.llEditProfileCoinGender.setVisibility(View.GONE);
                                    mHolder.txtEditProfileCoinGender.setText("+" + index.getT());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
        }
    }

    private void resetUserCoins() {
        mHolder.contentCoinImg.setVisibility(View.GONE);
        mHolder.llEditProfileCoinAddress.setVisibility(View.GONE);
        mHolder.llEditProfileCoinBirthdate.setVisibility(View.GONE);
        mHolder.llEditProfileCoinGender.setVisibility(View.GONE);
        GetEditProfileCoins();
    }

    private void initUser(UserModel userModel) {
        final String SHARED_KEY_USER = "prts:user:11FsERsC$*sdaQ!2";
        Gson gson = new Gson();
        prefs.removeValue(SHARED_KEY_USER);
        prefs.put(SHARED_KEY_USER, gson.toJson(userModel));
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        switch (opId) {
            case OP_SET_UPDATE_USER: {
                if (response.has("msg")) {
                    try {
                        showMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showMessage(getString(R.string.nu_a_putut_actualiza_datele));
                    }
                }else {
                    showMessage(getString(R.string.nu_a_putut_actualiza_datele));
                }
            }
            break;

            case OP_SET_REMOVE_AVATAR: {
                if (response.has("msg")) {
                    try {
                        showMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showMessage(getString(R.string.imaginea_nu_a_putut_fi_eliminata));
                    }
                }else {
                    showMessage(getString(R.string.imaginea_nu_a_putut_fi_eliminata));
                }
            }
        }
    }

    //endregion
    class ViewHolder {
        ImageView imgBack;
        EditText edtNume;
        EditText edtDate;
        EditText edtOras;
        EditText nickname;
        EditText spnGen;
        Button btnEdit;
        View selectPhoto;

        LinearLayout contentCoinImg;
        TextView coinImg;

        LinearLayout llEditProfileCoinAddress;
        TextView txtEditProfileCoinAddress;

        LinearLayout llEditProfileCoinGender;
        TextView txtEditProfileCoinGender;

        LinearLayout llEditProfileCoinBirthdate;
        TextView txtEditProfileCoinBirthdate;
        TextView mdc;

        ImageView idUserImg;

        public ViewHolder() {
            idUserImg = findViewById(R.id.idUserImg);
            imgBack = findViewById(R.id.img_back);
            btnEdit = findViewById(R.id.btn_edit_enter);
            edtNume = findViewById(R.id.edt_nume);
            nickname = findViewById(R.id.nickname);
            edtOras = findViewById(R.id.id_oras);
            edtDate = findViewById(R.id.edt_date);
            spnGen = findViewById(R.id.spn_gen);
            selectPhoto = findViewById(R.id.selectPhoto);
            contentCoinImg = findViewById(R.id.content_coin_img);
            coinImg = findViewById(R.id.txt_coin_img);
            txtEditProfileCoinAddress = findViewById(R.id.txtEditProfileCoinAddress);
            llEditProfileCoinAddress = findViewById(R.id.llEditProfileCoinAddress);
            llEditProfileCoinGender = findViewById(R.id.llEditProfileCoinGender);
            txtEditProfileCoinGender = findViewById(R.id.txtEditProfileCoinGender);
            llEditProfileCoinBirthdate = findViewById(R.id.llEditProfileCoinBirthdate);
            txtEditProfileCoinBirthdate = findViewById(R.id.txtEditProfileCoinBirthdate);
            mdc = findViewById(R.id.mdc);
        }
    }
}