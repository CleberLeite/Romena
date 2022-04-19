package com.frf.app.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.adapter.GalleryRecyclerViewAdapter;
import com.frf.app.data.OrderItemData;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.arena.ArenaRepository;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.ImageGallery;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.arena.ArenaViewModel;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ArenaCreatePostActivity extends MainActivity {

    GalleryRecyclerViewAdapter galeryAdapter;
    List<String> images;
    ViewHolder mHolder;
    private ArenaViewModel arenaViewModel;
    public static final int REQUEST_GALLERY_PHOTO = 102;
    private static final int MY_READ_PERMISSION_CODE = 101;
    private String txtDescription = "";
    // Define the pic id
    private static final int CAMERA_PIC_REQUEST = 123;
    private UserViewModel userViewModel;

    private String categorias = "";
    private String categoria = "";
    private String itemClick = "";

    private boolean trigger = true;

    private int position = -1;
    private int idCat = -1;

    ArrayList<String> paths = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_create_post);
        UTILS.setColorStatusBar(this, R.color.TabBarBackground);

        mHolder = new ViewHolder();

        categorias = getIntent().getStringExtra("categorias");
        categoria = getIntent().getStringExtra("catSlected");

        //Check permission
        if (ContextCompat.checkSelfPermission(ArenaCreatePostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ArenaCreatePostActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    }, MY_READ_PERMISSION_CODE);
        } else {
            initAction();
        }

        mHolder.createeditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtDescription = charSequence.toString();
                if (txtDescription.equals("") && paths.size() == 0) {
                    mHolder.post.setAlpha(0.3f);
                }else {
                    mHolder.post.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
         });
    }


    @Override
    public void bindViewModel() {
        super.bindViewModel();
        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, userModel -> {
            if (userModel.getIdMsg() == 1){
                mHolder.animationLoad.setVisibility(View.GONE);
                DialogSucess(getString(R.string.succes_post), true);
            }else {
                initUser(userModel);
            }
        });
        userViewModel.getUser(false);
        
        userViewModel.getError().observe(this, errorModel -> {
            if (errorModel.getError() == 1) {
                mHolder.animationLoad.setVisibility(View.GONE);
                DialogSucess(errorModel.getMsg(), false);
            }
        });
    }

    private void initUser(UserModel user) {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawable.setTint(Color.BLACK);

        UTILS.getImageAt(activity, user.getImg(), mHolder.img_user_photo, new RequestOptions()
                .circleCrop()
                .placeholder(drawable)
                .error(drawable));

        mHolder.txt_user_name.setText(user.getName());
        mHolder.userType.setText(user.getUserType());
    }
    
    private void setPost() {
        mHolder.animationLoad.setVisibility(View.VISIBLE);
        userViewModel.setPost(paths, mHolder.createeditText.getText().toString(), idCat);
    }


    private void addGallery(List<String> images) {

        for (int i = 0; i <= images.size()-1; i++) {
            View myView = LayoutInflater.from(activity).inflate(R.layout.galery_item, null, false);
            ImageView imageView = myView.findViewById(R.id.image);
            CardView container = myView.findViewById(R.id.container);

            UTILS.getImageAt(activity, images.get(i), imageView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Math.round(UTILS.pxFromDp(activity, 88)), Math.round(UTILS.pxFromDp(activity, 64)));
            if(i != 0) {
                params.setMargins(15, 0, 15, 0);
            } else {
                params.setMargins(0, 0, 15, 0);
            }
            container.setLayoutParams(params);
            container.requestLayout();

            int finalI = i;
            imageView.setOnClickListener(view -> {
                container.setVisibility(View.GONE);
                addSelected(images.get(finalI), () -> container.setVisibility(View.VISIBLE));
            });

            mHolder.gallery.addView(myView);
        }
    }

    private void addSelected(String path, OnCallBackGalleryList listener) {
        File file = new File(path);
        if (!paths.contains(path) && file.exists()) {
            View MyView = LayoutInflater.from(activity).inflate(R.layout.galery_item_selected, null, false);
            final ImageView[] imageView = {MyView.findViewById(R.id.image)};
            CardView container = MyView.findViewById(R.id.container);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Math.round(UTILS.pxFromDp(activity, 88)), Math.round(UTILS.pxFromDp(activity, 64)));

            params.setMargins(10, 0, 10, 0);

            container.setLayoutParams(params);
            container.requestLayout();

            paths.add(path);
            mHolder.post.setAlpha(1f);

            final View[] imgView = new View[1];

            OnCallBackSlideList removeImg = view -> imgView[0] = view;

            UTILS.getImageAt(activity, path, imageView[0]);
            container.setOnClickListener(v -> {
                listener.addItem();
                paths.remove(path);
                if (txtDescription.equals("") && paths.size() == 0) {
                    mHolder.post.setAlpha(0.3f);
                } else {
                    mHolder.post.setAlpha(1f);
                }
                mHolder.selected.removeView(MyView);
                mHolder.contentItens.removeView(imgView[0]);
            });

            mHolder.selected.addView(MyView);
            inicializeScroolIMG(path, listener, MyView, removeImg);
        }else {
            if (!file.exists()) {
                Toast.makeText(activity, getResources().getString(R.string.error_load_img), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface OnCallBackGalleryList {
        void addItem();
    }

    public interface OnCallBackSlideList {
        void removeImg(View view);
    }

    private void initAction() {
        configure();
  /*      mHolder.recyclerView.setHasFixedSize(true);
        mHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));*/
        images = ImageGallery.listOfImGE(this);

        addGallery(images);

       /* final GalleryRecyclerViewAdapter.OnCallBackGalleryList[] callBack = new GalleryRecyclerViewAdapter.OnCallBackGalleryList[1];
        ArrayList<String> imgs = new ArrayList<>();


        galeryAdapter = new GalleryRecyclerViewAdapter(this, images, (path, listener) -> {
            //paths.add(path);
            callBack[0] = listener;
            imgs.add(path);

            ImageSelectedAdapter adapterSelected = new ImageSelectedAdapter(this, imgs, callBack[0]);
            mHolder.recyclerSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mHolder.recyclerSelected.setAdapter(adapterSelected);
        });



        mHolder.recyclerView.setAdapter(galeryAdapter);*/

        //   Toast.makeText(this, "Photos (" + images.size() +")", Toast.LENGTH_SHORT).show();

/*        mHolder.imgClose.setOnClickListener(view -> {
            mHolder.cardSelected.setVisibility(View.GONE);
            finish();
            overridePendingTransition(0, 0);
            Intent intent = getIntent();
            intent.putExtra("SOFT_INPUT_ADJUST_PAN", false);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });*/

        mHolder.imgBack.setOnClickListener(view -> finish());

        mHolder.createeditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && mHolder.createeditText.getText() != null) {
                mHolder.createInputLayout.setHint("Scrie o postare...");
            } else {
                mHolder.createInputLayout.setHint("");
            }
        });

        mHolder.openCam.setOnClickListener(view -> selectImageExternal());

       // mHolder.createeditText.requestFocus();

        mHolder.post.setOnClickListener(v -> {
            if (paths.size() == 0 && mHolder.createeditText.getText().toString().equals("")) {
                Toast.makeText(activity, getString(R.string.msg_erro_empty), Toast.LENGTH_LONG).show();
            }else {
                setPost();
            }
        });

    }

    void inicializeScroolIMG(String path, OnCallBackGalleryList listener, View mView, OnCallBackSlideList back){

        View view = LayoutInflater.from(activity).inflate(R.layout.include_image_galery, mHolder.contentItens, false);

        ImageView imgSelected = view.findViewById(R.id.imgSelected);
        CardView cardSelected = view.findViewById(R.id.card_selected);
        ImageView imgClose = view.findViewById(R.id.img_close);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = metrics.widthPixels - 120;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(60, 20, 60, 10);
        cardSelected.setLayoutParams(lp);
        cardSelected.requestLayout();

        UTILS.getImageAt(this, path, imgSelected);

        imgClose.setOnClickListener(v -> {
            mHolder.contentItens.removeView(view);
            listener.addItem();
            mHolder.selected.removeView(mView);
            paths.remove(path);

            if (txtDescription.equals("") && paths.size() == 0) {
                mHolder.post.setAlpha(0.3f);
            }else {
                mHolder.post.setAlpha(1f);
            }
        });

        back.removeImg(view);
        mHolder.contentItens.addView(view);
        mHolder.scrollGallery.postDelayed(() -> mHolder.scrollGallery.fullScroll(HorizontalScrollView.FOCUS_RIGHT), 100L);
    }

    private void selectImageExternal() {
        final CharSequence[] items = {getString(R.string.foto), getString(R.string.galery), "Chanceler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.foto))) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            } else if (items[item].equals(getString(R.string.galery))) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_PHOTO);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PIC_REQUEST) {
            if(data  != null) {
                try {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), image);
                    addSelected(UTILS.getPath(activity, tempUri), () -> {

                    });
                }catch (Exception e){e.printStackTrace();}
            }
        }else if(requestCode == REQUEST_GALLERY_PHOTO){
            if(data  != null) {
                Uri selectedImage = Objects.requireNonNull(data).getData();
                if(selectedImage != null){
                    try {
                        addSelected(UTILS.getPath(activity, selectedImage), () -> {

                        });
                    }catch (Exception ignored) {
                        try {
                            addSelected(UTILS.getRealPathFromURI(activity,selectedImage), () -> {

                            });
                        }catch (Exception e) {
                            Log.e("ArenaCreatePostActivity", e.getMessage()+"");
                            Toast.makeText(activity, getString(R.string.erro_ao_carregar), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }


    public void configure(){
        ArenaRepository newsRepository = new ArenaRepository(activity);
        arenaViewModel = new ViewModelProvider(activity).get(ArenaViewModel.class);
        arenaViewModel.setmRepository(newsRepository);
        arenaViewModel.getHome().observe(this, jsonObject -> {
            setResult(RESULT_OK, new Intent().putExtra("data", true));
            initArena(jsonObject);
        });

        if (categorias == null || categorias.equals("")) {
            arenaViewModel.getCategories();
        }else {
            try {
                initArena(new JSONObject(categorias));
            } catch (JSONException e) {
                e.printStackTrace();
                arenaViewModel.getCategories();
            }
        }

        mHolder.dropDown.setOnClickListener(v -> {
            if (mHolder.arrow_drop.getRotation() == 0) {
                mHolder.arrow_drop.setRotation(180);
                mHolder.holderDropDown.setVisibility(View.VISIBLE);
            } else {
                mHolder.arrow_drop.setRotation(0);
                mHolder.holderDropDown.setVisibility(View.GONE);
            }
        });
    }

    void initDropdown(ArrayList<OrderItemData> itens) {
        TextView firstClick = null;
        int position = 0;

        for (OrderItemData item : itens) {
            View view = LayoutInflater.from(activity).inflate(R.layout.include_item_dropdown, mHolder.holderDropDown, false);
            TextView txt_item = view.findViewById(R.id.item);
            LinearLayout divider = view.findViewById(R.id.divider);

            if (position == 0) {
                txt_item.setBackground(activity.getResources().getDrawable(R.drawable.back_drop_right_top));
            } else if (position == itens.size() - 1) {
                txt_item.setBackground(activity.getResources().getDrawable(R.drawable.back_drop_bottom));
                divider.setVisibility(View.GONE);
            }

            txt_item.setText(item.getTitle());

            if (this.position == position) {
                if (itemClick.equals(item.getTitle())) {
                    txt_item.setTag(item);
                    txt_item.getBackground().setTint(activity.getResources().getColor(R.color.PrimaryBackground));
                    txt_item.setTextColor(activity.getResources().getColor(R.color.PrimaryText));
                } else {
                    this.position = -1;
                    itemClick = "";
                    txt_item.setTag(null);
                    txt_item.getBackground().setTint(activity.getResources().getColor(R.color.PrimaryText));
                    txt_item.setTextColor(activity.getResources().getColor(R.color.PrimaryBackground));
                }
            } else {
                txt_item.setTag(null);
                txt_item.getBackground().setTint(activity.getResources().getColor(R.color.PrimaryText));
                txt_item.setTextColor(activity.getResources().getColor(R.color.PrimaryBackground));
            }

            int finalPosition = position;

            txt_item.setOnClickListener(v -> {

                if (itemClick.equals("") || txt_item.getTag() == null) {
                    itemClick = item.getTitle();
                    idCat = item.getId();
                } else {
                    itemClick = itens.get(0).getTitle();
                    idCat = itens.get(0).getId();
                }

                if (itemClick.equals("")) {
                    mHolder.txt_drop.setText(itens.get(0).getTitle());
                    itemClick = itens.get(0).getTitle();
                    idCat = itens.get(0).getId();
                } else {
                    mHolder.txt_drop.setText(itemClick);
                }

                mHolder.holderDropDown.removeAllViews();
                this.position = finalPosition;
                initDropdown(itens);

            });

            if (categoria == null) {
                categoria = "";
            }

            if (position == 0 && categoria.equals("")) {
                firstClick = txt_item;
            }else {
                if (item.getTitle().equals(categoria)) {
                    firstClick = txt_item;
                    categoria = "";
                }
            }

            position++;

            mHolder.holderDropDown.addView(view);
        }

        if (trigger){
            trigger = false;
            if (firstClick != null) {
                firstClick.performClick();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    
    void DialogSucess(String msg, boolean isClose){
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        alertDialog.setMessage(msg)
                .setPositiveButton(R.string.sim, (dialog, which) -> {
                    if (isClose) {
                        finish();
                    }
                });
        alertDialog.setCancelable(false);
        androidx.appcompat.app.AlertDialog alert = alertDialog.create();
        alert.setOnShowListener(arg0 -> {
            alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryColorText));
        });
        alert.show();
    }

    private void initArena(JSONObject response) {
        try {
            JSONArray itens;

            if (response.has("Object")) {
                itens = response.getJSONObject("Object").getJSONArray("itens");
            }else {
                itens = response.getJSONArray("itens");
            }

            OrderItemData[] gData = new Gson().fromJson(itens.toString(), OrderItemData[].class);

            initDropdown(new ArrayList<>(Arrays.asList(gData)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean trigger = true;
    }

    class ViewHolder {
        ImageView imgBack;
        ImageView imgClose;
        ImageView img_user_photo;
        TextInputEditText createeditText;
        TextInputLayout createInputLayout;
        View openCam;
        TextView txt_user_name;
        TextView userType;
        LinearLayout contentItens;
        LinearLayout gallery;
        LinearLayout selected;
        LinearLayout contentPost;
        Button post;
        ConstraintLayout animationLoad, container, cl;
        LinearLayout holderDropDown;
        LinearLayout dropDown;
        TextView txt_drop;
        ImageView arrow_drop;
        HorizontalScrollView scrollGallery;

        public ViewHolder() {
            contentPost = findViewById(R.id.content_post);
            container = findViewById(R.id.container);
            scrollGallery = findViewById(R.id.horizontalScrollView);
            cl = findViewById(R.id.cl);
            selected = findViewById(R.id.selected);
            animationLoad = findViewById(R.id.animation_load);
            contentItens = findViewById(R.id.containerHomeItens);
            txt_user_name = findViewById(R.id.txt_user_name);
            userType = findViewById(R.id.userType);
            img_user_photo = findViewById(R.id.img_user_photo);
            gallery = findViewById(R.id.gallery);
            imgClose = findViewById(R.id.img_close);
            imgBack = findViewById(R.id.img_back);
            createeditText = findViewById(R.id.edt_create);
            createInputLayout = findViewById(R.id.txtil_create);
            openCam = findViewById(R.id.open_cam);
            holderDropDown = findViewById(R.id.holder_dropdown);
            dropDown = findViewById(R.id.btn_dropdown);
            txt_drop = findViewById(R.id.txt_dropdown);
            arrow_drop = findViewById(R.id.arrow);
            post = findViewById(R.id.btn_edit_enter);
        }
    }
}