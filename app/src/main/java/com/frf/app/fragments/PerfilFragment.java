package com.frf.app.fragments;

import static com.frf.app.activitys.MainActivity.emptyAsync;
import static com.frf.app.activitys.MainActivity.prefs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.activitys.ArenaCreatePostActivity;
import com.frf.app.activitys.ProfileEditActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.adapter.ArenaItensAdapter;
import com.frf.app.adapter.SlideAdapter;
import com.frf.app.data.EditProfileCoinData;
import com.frf.app.data.GalleryArenaData;
import com.frf.app.informations.UserInformation;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.arena.ArenaRepository;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.frf.app.vmodels.arena.ArenaViewModel;
import com.frf.app.vmodels.fragments.FragmentHolderViewModel;
import com.frf.app.vmodels.user.UserViewModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PerfilFragment extends MainFragment implements AsyncOperation.IAsyncOpCallback {

    private FragmentHolderViewModel mfragmentViewModel;
    private ViewHolder mHolder;
    private UserViewModel userViewModel;
    private ArenaViewModel arenaViewModel;
    private ArenaItensAdapter adapterRankItem;
    private static final int OP_GET_PROFILE_COINS = 1;
    private static final int OP_GET_ALL_NEWS = 0;

    private boolean isPage = false;

    ArrayList<EditProfileCoinData> editProfileCoinData = new ArrayList<>();

    public PerfilFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        mHolder = new ViewHolder(view);

        try {
            ((StartMenuActivity)requireActivity()).resetBottomMenu();
        }catch (Exception e){
            e.printStackTrace();
        }

        mHolder.btnPost.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, ArenaCreatePostActivity.class);
            postResult.launch(intent);
        });

        return view;
    }


    ActivityResultLauncher<Intent> postResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                arenaViewModel.getItens(UserInformation.getUserId(), "1", "", "");
            }
        }
    });

    private final Handler handlerOp = new Handler(message -> {
        int opId = -1;
        JSONObject response;
        boolean success = false;

        try{
            opId = message.getData().getInt("opId");
            response = new JSONObject(Objects.requireNonNull(message.getData().getString("response")));
            success = message.getData().getBoolean("success");
        }
        catch (JSONException e){
            UTILS.DebugLog("Error", "Error getting handlers params.");
            return false;
        }

        if(success) {
            OnAsyncOperationSuccess(opId, response);
        }
        else{
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

    @Override
    public void OnAsyncOperationSuccess(int opId, JSONObject response) {
        switch (opId) {
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
                            editProfileCoinData.addAll(Arrays.asList(data));

                            for (EditProfileCoinData index : editProfileCoinData) {
                                if(index.getT() == 2){
                                    mHolder.llEditProfileCoin.setVisibility(View.VISIBLE);
                                    mHolder.txtEditProfileCoin.setText("+"+index.getT()+"");
                                } else {
                                    mHolder.llEditProfileCoin.setVisibility(View.GONE);
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

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {

    }

    @Override
    public void bind() {
        super.bind();
        mfragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentHolderViewModel.class);

        GetEditProfileCoins();
        mHolder.btnEdit.setOnClickListener(view -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_USER_PROFILE_EDIT).execute();
            Intent intent1 = new Intent(activity, ProfileEditActivity.class);
            startActivity(intent1);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        UserRepository userRepository = new UserRepository(activity, prefs);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setmRepository(userRepository);
        userViewModel.getUserData().observe(this, this::initHeader);
        userViewModel.getUser(false);

        ArenaRepository newsRepository = new ArenaRepository(activity);
        arenaViewModel = new ViewModelProvider(requireActivity()).get(ArenaViewModel.class);
        arenaViewModel.setmRepository(newsRepository);
        arenaViewModel.clearData();
        arenaViewModel.getHome().observe(this, this::initArena);
        arenaViewModel.getItens(UserInformation.getUserId(), "1", "", "");

        ((StartMenuActivity)getActivity()).changeHeader(1);
    }

    private void initArena(JSONObject response) {
        try {
            GalleryArenaData[] gData = new Gson().fromJson(response.getString("itens"), GalleryArenaData[].class);

            if (gData != null && gData.length > 0) {
                mHolder.placeholder.setVisibility(View.GONE);
                mHolder.recyclerArena.setVisibility(View.VISIBLE);
                initGallery(gData, response.getInt("margin"));
            }else {
                if (adapterRankItem == null) {
                    mHolder.placeholder.setVisibility(View.VISIBLE);
                    mHolder.recyclerArena.setVisibility(View.GONE);
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    void GetEditProfileCoins(){
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_PROFILE_COINS, OP_GET_PROFILE_COINS, this).execute();
    }

    void initGallery(GalleryArenaData[] data, int margin) {

        if (!isPage) {
            adapterRankItem = new ArenaItensAdapter(activity, new ArrayList<>(Arrays.asList(data)), margin, false, getChildFragmentManager(), new ArenaItensAdapter.Listener() {
                @Override
                public void onArenaClick(Integer id) {
                    new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_USER_PROFILE_OPEN_POST_ARENA, id).execute();
                    isPage = false;
                    mfragmentViewModel.changeFragment(new ArenaViewPostFragment((id)), "ArenaViewPostFragment", 3);
                }

                @Override
                public void onLikedClick(Integer id) {
                    arenaViewModel.setLike(id);
                }

                @Override
                public void onMargin(int page) {
                    isPage = true;
                    arenaViewModel.getItens(UserInformation.getUserId(), page+"", "", "");
                }

                @Override
                public void onGalleryClick(ArrayList<String> imagens) {
                    initSlides(imagens);
                }

                @Override
                public void onCallBack() {

                }
            });

            mHolder.recyclerArena.setAdapter(adapterRankItem);
            mHolder.recyclerArena.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        }else {
            for (GalleryArenaData data1 : data) {
                adapterRankItem.addItem(data1);
            }
        }
    }

    void initSlides(ArrayList<String> imgs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogFullScreen);
        builder.setCancelable(true);
        View view = LayoutInflater.from(activity).inflate(R.layout.include_slides, null, false);

        AlertDialog dialog = builder.create();

        ViewPager viewPager = view.findViewById(R.id.pager_next_img);
        ImageView close = view.findViewById(R.id.img_close);

        close.setOnClickListener(v -> dialog.dismiss());

        SlideAdapter slideAdapter = new SlideAdapter(activity, imgs);
        viewPager.setAdapter(slideAdapter);

        dialog.setView(view);
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    void initHeader(UserModel user) {
        UTILS.getImageAt(activity, user.getImg(), mHolder.imgUser, new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_placeholderuser)
                .error(R.drawable.ic_placeholderuser));

        mHolder.txtNomeUser.setText(user.getName()+"");
        mHolder.typeFan.setText(user.getUserType()+"");
        mHolder.rank.setText("#" + user.getRank());
        TextView points = getActivity().findViewById(R.id.txt_moedas);
        points.setText(user.getCoins()+"");
        mHolder.locale.setText(user.getSso_info().getCity()+"");
        mHolder.date.setText("Membru din " + toDate(user.getDtCreated()));
    }

    String toDate(String date) {
        String[] meses = activity.getResources().getStringArray(R.array.months);
        int mesInt = Integer.parseInt(date.substring(5, 7));
        mesInt--;
        return meses[mesInt] + " " + date.substring(0, 4);
    }

    static class ViewHolder {
        ImageView imgUser;
        TextView txtNomeUser;
        TextView typeFan;
        TextView locale;
        TextView date;
        Button btnEdit;
        Button rank;
        Button btnPost;
        RecyclerView recyclerArena;
        LinearLayout llEditProfileCoin;
        ConstraintLayout placeholder;
        TextView txtEditProfileCoin;

        public ViewHolder(View view){
            recyclerArena = view.findViewById(R.id.recycler_arena);
            btnEdit = view.findViewById(R.id.btn_edit_enter);
            btnPost = view.findViewById(R.id.btn_post);
            imgUser = view.findViewById(R.id.imgUser);
            placeholder = view.findViewById(R.id.placeholder);
            rank = view.findViewById(R.id.btn_cat);
            date = view.findViewById(R.id.date);
            locale = view.findViewById(R.id.locale);
            typeFan = view.findViewById(R.id.typeFan);
            imgUser = view.findViewById(R.id.imgUser);
            typeFan = view.findViewById(R.id.typeFan);
            txtNomeUser = view.findViewById(R.id.txt_nome_user);
            llEditProfileCoin = view.findViewById(R.id.llEditProfileCoin);
            txtEditProfileCoin = view.findViewById(R.id.txtEditProfileCoin);
        }
    }
}