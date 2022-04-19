package com.frf.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.frf.app.R;
import com.frf.app.data.ShopProductData;
import com.frf.app.utils.UTILS;

import java.util.ArrayList;

public class ShopItensAdapter extends RecyclerView.Adapter<ShopItensAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShopProductData> list;
    private Listener listener;

    public ShopItensAdapter(Context context, ArrayList<ShopProductData> list, Listener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_item_shop, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopProductData data = list.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            UTILS.getImageAt(context, data.getPic(), holder.image);
        }else {
            UTILS.getImageAt(context, data.getPic(), holder.image, new RequestOptions().transform(new CenterCrop(), new RoundedCorners(UTILS.convertDpToPixels(context.getResources().getDimension(R.dimen._3sdp)))));
        }

        holder.txtDescription.setText(data.getInfo().getProduct());
        holder.cardLoja.setOnClickListener(view -> listener.onClick(data.getProductID()));

        if (data.getOldprice() != null && !data.getOldprice().equals("null") && !data.getOldprice().equals("")) {
            holder.oldValue.setVisibility(View.VISIBLE);

            String txt = data.getOldprice();

            if (data.getOldprice().contains(".")) {
                String decimal = data.getOldprice().substring(data.getOldprice().indexOf("."));

                if (decimal.length() >= 2) {
                    txt = txt.substring(0, data.getOldprice().indexOf(".") + 3);
                }else if (decimal.length() == 1) {
                    txt = txt.substring(0, data.getOldprice().indexOf(".") + 2);
                }
            }

            SpannableString string = new SpannableString(txt + " RON");
            string.setSpan(new StrikethroughSpan(), 0, txt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.oldValue.setText(string);
        }else {
            holder.oldValue.setVisibility(View.GONE);
        }

        if (data.getSave_percent() != null && !data.getSave_percent().equals("null") && !data.getSave_percent().equals("")) {
            holder.contentDiscount.setVisibility(View.VISIBLE);
            String txt = data.getSave_percent().contains(".") ? "-"+data.getSave_percent().substring(0, data.getSave_percent().indexOf("."))+"%" : data.getSave_percent();
            holder.txtPercentDiscount.setText(txt);
            holder.value.setTextColor(context.getResources().getColor(R.color.ColorRed));
        }else {
            holder.contentDiscount.setVisibility(View.GONE);
            holder.value.setTextColor(context.getResources().getColor(R.color.PrimaryBackground));
        }

        if (data.getPrice() != null && !data.getPrice().equals("null") && !data.getPrice().equals("")) {
            String txt = data.getPrice();

            if (data.getPrice().contains(".")) {
                String decimal = data.getPrice().substring(data.getPrice().indexOf("."));

                if (decimal.length() >= 2) {
                    txt = txt.substring(0, data.getPrice().indexOf(".") + 3);
                }else if (decimal.length() == 1) {
                    txt = txt.substring(0, data.getPrice().indexOf(".") + 2);
                }
            }

            holder.value.setText(txt + " RON");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface Listener {
        void onClick(int id);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardDiscont, contentDiscount;
        View cardLoja;
        TextView txtDescription, txtPercentDiscount, oldValue, value;
        ImageView image;
        FrameLayout llFrame;

        //layout 2
        LinearLayout content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDiscont = itemView.findViewById(R.id.cardDiscont);
            contentDiscount = itemView.findViewById(R.id.content_discount);
            llFrame = itemView.findViewById(R.id.llFrame);
            image = itemView.findViewById(R.id.image);
            txtDescription = itemView.findViewById(R.id.titulo);
            txtPercentDiscount = itemView.findViewById(R.id.discount_percent);
            oldValue = itemView.findViewById(R.id.old_value);
            value = itemView.findViewById(R.id.value);
            cardLoja = itemView.findViewById(R.id.cardLoja);
            content = itemView.findViewById(R.id.contact);
        }
    }
}
