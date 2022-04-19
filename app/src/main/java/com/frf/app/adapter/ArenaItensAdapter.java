package com.frf.app.adapter;

import static com.frf.app.fragments.BottomSheetMoreFragment.ARENA_POSTS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.activitys.MainActivity;
import com.frf.app.activitys.StartMenuActivity;
import com.frf.app.data.GalleryArenaData;
import com.frf.app.fragments.BottomSheetMoreFragment;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;

import java.util.ArrayList;

public class ArenaItensAdapter extends RecyclerView.Adapter<ArenaItensAdapter.ViewHolder> {

    Context context;
    ArrayList<GalleryArenaData> data;
    FragmentManager fragmentManager;
    Listener listener;
    int margin;
    int page = 1;
    boolean isArena;

    public ArenaItensAdapter(Context context, ArrayList<GalleryArenaData> data, int margin, boolean isArena, FragmentManager fragmentManager, Listener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.margin = margin;
        this.fragmentManager = fragmentManager;
        this.isArena = isArena;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.include_card_tipo_2, parent, false);
        return new ViewHolder(view);
    }

    public void addItem(GalleryArenaData data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == (data.size() - margin)) {
            page++;
            listener.onMargin(page);
        }

        GalleryArenaData data =  this.data.get(position);

        Drawable drawablePlace = context.getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawablePlace.setTint(context.getResources().getColor(R.color.ThirdBackground));

        UTILS.getImageAt(context, data.getImgUser(), holder.avatar, new RequestOptions()
                .circleCrop()
                .placeholder(drawablePlace)
                .error(drawablePlace));

        holder.desc.setText(data.getText());
        holder.nome.setText(data.getUserName());
        holder.nivel.setText(data.getUserType());
        holder.likes.setText(data.getLikesCount()+"");

        if(String.valueOf(data.getIdUser()).equals(UTILS.id)){
            holder.more.setVisibility(View.GONE);
        }else{
            holder.more.setVisibility(View.VISIBLE);
        }

        holder.more.setOnClickListener(view1-> {
            BottomSheetMoreFragment fragment = new BottomSheetMoreFragment(ARENA_POSTS, data.getId(), data.getIdUser(), () -> listener.onCallBack());
            fragment.show(fragmentManager, fragment.getTag());
        });

        if (data.isSponsored()) {
            holder.sponsored.setVisibility(View.VISIBLE);
            holder.nivel.setVisibility(View.GONE);
            holder.more.setVisibility(View.GONE);
            holder.comentary.setVisibility(View.INVISIBLE);
            holder.comentarios.setText("");
            holder.sponsorName.setText(data.getUserType());
            holder.card.setOnClickListener(view -> {});
        }else {
            holder.comentarios.setText(data.getCommentsCount()+"");
            holder.card.setOnClickListener(v -> listener.onArenaClick(data.getId()));
            holder.sponsored.setVisibility(View.GONE);
            holder.nivel.setVisibility(View.VISIBLE);
        }

        holder.like.setImageDrawable(data.getLiked() != 0 ? context.getResources().getDrawable(R.drawable.ic_like) : context.getResources().getDrawable(R.drawable.ic_unlike));

         holder.like.setOnClickListener(v -> {
             listener.onLikedClick(data.getId());
            if (data.getLiked() == 1) {
                if (isArena) {
                    new AsyncOperation((Activity) context, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ARENA_LIKE_ARENA, data.getId()).execute();
                }else {
                    new AsyncOperation((Activity) context, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_USER_PROFILE_LIKE_ARENA, data.getId()).execute();
                }
                holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unlike));
                data.setLikesCount((data.getLikesCount() - 1));
                data.setLiked(0);
                holder.likes.setText(data.getLikesCount() + "");
            } else {
                if (isArena) {
                    new AsyncOperation((Activity) context, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_ARENA_UNLIKE_ARENA, data.getId()).execute();
                }else {
                    new AsyncOperation((Activity) context, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, MainActivity.emptyAsync, TRACKING.TRACKING_USER_PROFILE_UNLIKE_ARENA, data.getId()).execute();
                }
                holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                data.setLikesCount((data.getLikesCount() + 1));
                data.setLiked(1);
                holder.likes.setText(data.getLikesCount() + "");
            }
        });

        int sizeGallery = data.getImages().size();

       if (sizeGallery >= 3) {
           holder.imgFoco.setVisibility(View.VISIBLE);
           holder.imgSecundariaCard.setVisibility(View.VISIBLE);
           holder.imgTernariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(context, data.getImages().get(0), holder.imgFoco);
            UTILS.getImageAt(context, data.getImages().get(1), holder.imgSecundaria);
            UTILS.getImageAt(context, data.getImages().get(2), holder.imgTernaria);
        } else if (sizeGallery == 2) {
           holder.imgFoco.setVisibility(View.VISIBLE);
           holder.imgSecundariaCard.setVisibility(View.VISIBLE);
            UTILS.getImageAt(context, data.getImages().get(0), holder.imgFoco);
            UTILS.getImageAt(context, data.getImages().get(1), holder.imgSecundaria);
            holder.imgTernariaCard.setVisibility(View.GONE);
        } else if (sizeGallery == 1) {
           holder.imgFoco.setVisibility(View.VISIBLE);
            UTILS.getImageAt(context, data.getImages().get(0), holder.imgFoco);
            holder.llImgsRight.setVisibility(View.GONE);
            holder.imgSecundariaCard.setVisibility(View.GONE);
            holder.imgTernariaCard.setVisibility(View.GONE);

           FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Math.round(UTILS.pxFromDp(context, 150)), Math.round(UTILS.pxFromDp(context, 205)));

           params.height = Math.round(UTILS.pxFromDp(context, 220));
           params.width = Math.round(UTILS.pxFromDp(context, 405));

           params.setMargins(0,0,0,0);
           holder.imgFoco.setLayoutParams(params);
           holder.imgFoco.requestLayout();
        } else {
            holder.imgFoco.setVisibility(View.GONE);
            holder.imgSecundariaCard.setVisibility(View.GONE);
            holder.imgTernariaCard.setVisibility(View.GONE);
        }

        if (sizeGallery > 3) {
            holder.countImgs.setText("+" + (sizeGallery - 3));
        }

        if (data.getCat() != null && !data.getCat().equals("")) {
            holder.cat.setText(data.getCat());
        }else {
            holder.cat.setVisibility(View.GONE);
        }

        holder.horario.setText(UTILS.PrettyTime(context, data.getCreated_at()));


        holder.contentImagens.setOnClickListener(v -> listener.onGalleryClick(data.getImages()));
        holder.llImgsRight.setOnClickListener(v -> listener.onGalleryClick(data.getImages()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface Listener {
        void onArenaClick(Integer id);
        void onLikedClick(Integer id);
        void onMargin(int page);
        void onGalleryClick(ArrayList<String> imagens);
        void onCallBack();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View card;
        TextView cat;
        TextView horario;
        TextView nome;
        TextView nivel;
        TextView countImgs;
        ImageView avatar;
        CardView imgSecundariaCard;
        CardView imgTernariaCard;
        //CardView imgPrimariaCard;
        ImageView imgFoco;
        ImageView imgSecundaria;
        ImageView imgTernaria;
        ImageView like;
        ImageView more;
        TextView likes;
        TextView comentarios;
        TextView desc;
        ImageView comentary;
        LinearLayout contentImagens, llImgsRight, sponsored;
        TextView sponsorName;

        public ViewHolder(@NonNull View view) {
            super(view);
            cat = view.findViewById(R.id.cat);
            horario = view.findViewById(R.id.horario);
            nome = view.findViewById(R.id.nome);
            nivel = view.findViewById(R.id.nivel);
            countImgs = view.findViewById(R.id.count_imgs);
            avatar = view.findViewById(R.id.avatar);
            imgSecundariaCard = view.findViewById(R.id.card_img_secundaria);
            imgTernariaCard = view.findViewById(R.id.card_img_ternario);
            //imgPrimariaCard = view.findViewById(R.id.img_principal);
            imgFoco = view.findViewById(R.id.img_foco);
            imgSecundaria = view.findViewById(R.id.img_secondary);
            imgTernaria = view.findViewById(R.id.img_ternaria);
            like = view.findViewById(R.id.ic_like);
            likes = view.findViewById(R.id.num_likes);
            comentarios = view.findViewById(R.id.num_comentarios);
            more = view.findViewById(R.id.more);
            desc = view.findViewById(R.id.desc);
            comentary = view.findViewById(R.id.ic_comentarios);
            contentImagens = view.findViewById(R.id.llImages);
            llImgsRight = view.findViewById(R.id.ll_img_right);
            sponsored = view.findViewById(R.id.sponsored);
            card = view.findViewById(R.id.container);
            sponsorName = view.findViewById(R.id.name_sponsor);
        }
    }
}