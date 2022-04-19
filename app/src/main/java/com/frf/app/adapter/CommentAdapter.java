package com.frf.app.adapter;

import static com.frf.app.fragments.BottomSheetMoreFragment.ARENA_COMMENTS;
import static com.frf.app.fragments.BottomSheetMoreFragment.ARENA_POSTS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.data.CommentData;
import com.frf.app.data.RepliesData;
import com.frf.app.fragments.BottomSheetMoreFragment;
import com.frf.app.informations.UserInformation;
import com.frf.app.utils.UTILS;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<CommentData> data;
    FragmentManager fragmentManager;
    Listener callback;
    int typeFeature;

    public CommentAdapter(Context context, List<CommentData> data, FragmentManager fragmentManager, int typeFeature, Listener callback) {
        this.context = context;
        this.data = data;
        this.callback = callback;
        this.typeFeature = typeFeature;
        this.fragmentManager = fragmentManager;
    }

    public void add(CommentData data) {
        this.data.add(data);
        notifyDataSetChanged();
    }


    public List<CommentData> getData() {
        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CommentData item = data.get(position);

        holder.bntDelete.setVisibility(UserInformation.getUserId().equals(item.getIdUser()) ? View.VISIBLE : View.GONE);

        Drawable drawablePlace = context.getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawablePlace.setTint(context.getResources().getColor(R.color.ThirdBackground));

        UTILS.getImageAt(context, item.getUserImg(), holder.userImg, new RequestOptions()
                .circleCrop()
                .placeholder(drawablePlace)
                .error(drawablePlace));

        holder.text.setText(item.getText());
        holder.userType.setText(item.getUserType());
        holder.userName.setText(item.getUserName());

        PrettyTime prettyTime = new PrettyTime(context.getResources().getConfiguration().locale);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = dateFormatData.parse(item.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.created_at.setText(prettyTime.format(date));

        holder.llComentary.setOnClickListener(v -> callback.isComment(item.getId()));

        holder.bntDelete.setOnClickListener(v -> callback.isPressed(item.getId()));

        for(int i = 0; i <= item.getReplies().size()-1; i++){
            initReplies(holder, item.getReplies().get(i), item.getId());
        }

        holder.more.setOnClickListener(view1-> {
            BottomSheetMoreFragment fragment = new BottomSheetMoreFragment(typeFeature, item.getId(), item.getIdUser(), () -> callback.onCallBack(item.getIdUser()));
            fragment.show(fragmentManager, fragment.getTag());
        });

        holder.more.setVisibility(UserInformation.getUserId().equals(item.getIdUser()) ? View.GONE : View.VISIBLE);

        holder.moreReplies.setVisibility(item.getMoreReplies() == 1 ? View.VISIBLE : View.GONE);
    }

    void initReplies(ViewHolder holder, RepliesData data, int id) {
        View view = LayoutInflater.from(context).inflate(R.layout.include_comentary_replies, holder.contentItens, false);
        ImageView avatar = view.findViewById(R.id.avatar);
        TextView txtUserName = view.findViewById(R.id.txtUserName);
        TextView typeFan = view.findViewById(R.id.typeFan);
        TextView text = view.findViewById(R.id.text);
        ImageView ReplyBntDelete = view.findViewById(R.id.ReplyBntDelete);
        TextView hours = view.findViewById(R.id.hours);
        ImageView more2 = view.findViewById(R.id.more_replies);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawablePlace = context.getResources().getDrawable(R.drawable.ic_placeholderuser);
        drawablePlace.setTint(context.getResources().getColor(R.color.ThirdBackground));

        ReplyBntDelete.setVisibility(UserInformation.getUserId().equals(data.getIdUser()) ? View.VISIBLE : View.GONE);
        ReplyBntDelete.setOnClickListener(v -> callback.isPressed(data.getId()));

        PrettyTime prettyTime = new PrettyTime(context.getResources().getConfiguration().locale);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = dateFormatData.parse(data.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        hours.setText(prettyTime.format(date));
        typeFan.setText(data.getUserType());
        txtUserName.setText(data.getUserName());
        text.setText(data.getText());
        UTILS.getImageAt(context, data.getUserImg(), avatar, new RequestOptions().circleCrop().placeholder(drawablePlace));

        more2.setOnClickListener(view1-> {
            BottomSheetMoreFragment fragment = new BottomSheetMoreFragment(typeFeature, data.getId(), data.getIdUser(), () -> callback.onCallBack(data.getIdUser()));
            fragment.show(fragmentManager, fragment.getTag());
        });

        more2.setVisibility(UTILS.id.equals(data.getIdUser()) ? View.GONE : View.VISIBLE);

        holder.moreReplies.setOnClickListener(v -> {
            callback.getPostReplies(id, holder.contentItens);
            holder.moreReplies.setVisibility(View.GONE);
        });

        holder.contentItens.addView(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Listener {
        void isPressed(int id);
        void isComment(int id);
        void getPostReplies(int id, LinearLayout contentItens);
        void onCallBack(String id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        ImageView bntDelete;
        ImageView more;
        LinearLayout llComentary;
        TextView text;
        TextView userName;
        TextView userType;
        TextView created_at;
        LinearLayout contentItens;
        LinearLayout moreReplies;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moreReplies = itemView.findViewById(R.id.moreReplies);
            contentItens = itemView.findViewById(R.id.containerHomeItens);
            more = itemView.findViewById(R.id.more);
            bntDelete = itemView.findViewById(R.id.bntDelete);
            llComentary = itemView.findViewById(R.id.llComentary);
            userImg = itemView.findViewById(R.id.userImg);
            userName = itemView.findViewById(R.id.userName);
            userType = itemView.findViewById(R.id.userType);
            created_at = itemView.findViewById(R.id.created_at);
            text = itemView.findViewById(R.id.text);
            container = itemView.findViewById(R.id.container);
        }
    }
}
