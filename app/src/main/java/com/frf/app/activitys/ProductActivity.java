package com.frf.app.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.frf.app.BuildConfig;
import com.frf.app.R;
import com.frf.app.adapter.ViewPageAdapter;
import com.frf.app.data.PrizesItemData;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.TRACKING;
import com.frf.app.utils.UTILS;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Objects;

public class ProductActivity extends MainActivity implements AsyncOperation.IAsyncOpCallback {

    private ViewHolder mHolder;
    int id;
    long file_size;
    int idCupom = -1;
    private static final int OP_GET_PRODUTO = 0;
    private static final int OP_SET_RESCUE = 1;

    boolean isPush = false;
    boolean isMy = false;
    boolean us = false;
    boolean blockPurchase = false;

    int value = -1;
    int tipo = -1;
    private String fileName = "";
    private String url = "";
    private String typeValue = "";

    private PrizesItemData data;

    private AlertDialog dialog;
    TextView txtProgresso;
    ProgressBar pbProgresso;
    Button btnCancelar;

    private static  final String TAG = "ProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        UTILS.setColorStatusBar(this, R.color.TabBarBackground);

        mHolder = new ViewHolder();
        
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        idCupom = intent.getIntExtra("idCupom", -1);
        isPush = intent.getBooleanExtra("push", false);
        isMy = intent.getBooleanExtra("isMy", false);
        blockPurchase = intent.hasExtra("typeValue");
        if(blockPurchase) {
            typeValue = intent.getStringExtra("typeValue");
        }

        getProduto();

        mHolder.imgBack.setOnClickListener(view -> {
            if (us) {
                Intent intent1 = new Intent();
                intent1.putExtra("us", true);
                setResult(RESULT_OK, intent1);
            }
            finish();
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TRACKING.setTrackingClassName(this, "product_screen");
    }

    void getProduto() {
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        if (idCupom != -1) {
            params.put("idCupom", idCupom);
        }
        new AsyncOperation(activity, AsyncOperation.TASK_ID_GET_PRIZES_DETAILS, OP_GET_PRODUTO, this).execute(params);
    }

    void redeemProduct(PrizesItemData data) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_RESCUES_RESCUE, data.getId()).execute();
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", data.getId());
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SET_RESCUE, OP_SET_RESCUE, this).execute(params);
    }

    public void showDialog(PrizesItemData data) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_item_loja);

        ViewPager viewPager = dialog.findViewById(R.id.pager);
        DotsIndicator dotsIndicator = dialog.findViewById(R.id.dots_indicator);
        TextView title = dialog.findViewById(R.id.txt_title);
        TextView desc = dialog.findViewById(R.id.txt_description);

        ViewPageAdapter mCustomPagerAdapter = new ViewPageAdapter(this, data.getImg());
        viewPager.setAdapter(mCustomPagerAdapter);
        dotsIndicator.setViewPager(viewPager);

        title.setText(data.getName());
        desc.setText(data.getDescription());

        dialog.findViewById(R.id.btn_edit_enter).setOnClickListener(view -> {
            try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
            mHolder.load.setVisibility(View.VISIBLE);
            redeemProduct(data);
        });
        dialog.findViewById(R.id.menu_itm_dialog_ranking).setOnClickListener(view -> dialog.dismiss());
        try{dialog.show();}catch (Exception e){e.printStackTrace();}
    }

    //region Async
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
            response = new JSONObject(Objects.requireNonNull(message.getData().getString("response")));
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
        mHolder.load.setVisibility(View.GONE);
        switch (opId) {
            case OP_GET_PRODUTO: {
                //pegar quiz
                int status = 0;
                //pegar categoria de quiz
                if (response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //pegar categoria de quiz
                if (response.has("Object")) {
                    if (status == 200) {
                        try {
                            Gson gson = new Gson();
                            PrizesItemData data = gson.fromJson(response.getString("Object"), PrizesItemData.class);

                            if (data != null) {
                                this.data = data;
                                value = data.getPrice();
                                tipo = data.getType();
                                initProduto(data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if (isPush) {
                        Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_acest_articol_nu_mai_exista), Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        Toast.makeText(activity, getResources().getString(R.string.nu_sa_putut_afisa_elementul), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case OP_SET_RESCUE: {
                int status = 0;

                if (response.has("Status")) {
                    try {
                        status = response.getInt("Status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (response.has("Object")) {
                    if (status == 200) {
                        try {

                            if (response.getJSONObject("Object").has("idCupom")) {
                                idCupom = response.getJSONObject("Object").getInt("idCupom");
                            }

                            if (response.has("url")) {
                                data.setUrl(response.getJSONObject("Object").getString("url"));
                            }

                            if (response.has("us")) {
                                us = true;
                            }

                            isMy = true;

                            getProduto();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            alertMessage(response.getString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            alertMessage(getString(R.string.nu_a_fost_posibila_salvarea));
                        }
                    }
                }else {
                    try {
                        alertMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        alertMessage(getString(R.string.nu_a_fost_posibila_salvarea));
                    }
                }
            }
            break;
        }
    }

    @Override
    public void OnAsyncOperationError(int opId, JSONObject response) {
        mHolder.load.setVisibility(View.GONE);
        if (opId == OP_GET_PRODUTO) {
            if (isPush) {
                Toast.makeText(activity, getResources().getString(R.string.se_pare_ca_acest_articol_nu_mai_exista), Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(activity, getResources().getString(R.string.nu_sa_putut_afisa_elementul), Toast.LENGTH_SHORT).show();
            }
        }else if (opId == OP_SET_RESCUE) {
            if (response.has("msg")) {
                try {
                    alertMessage(response.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    alertMessage(getString(R.string.nu_a_fost_posibila_salvarea));
                }
            }else {
                alertMessage(getString(R.string.nu_a_fost_posibila_salvarea));
            }
        }
    }

    private void alertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.inchide, (dialog, which) -> {
            try{dialog.dismiss();}catch (Exception e){e.printStackTrace();}
        });

        try{builder.create().show();}catch (Exception e){e.printStackTrace();}
    }

    @SuppressLint("SetTextI18n")
    private void initProduto(PrizesItemData data) {
        if (mHolder.mViewPager.getAdapter() == null) {
            ViewPageAdapter mCustomPagerAdapter = new ViewPageAdapter(this, data.getImg());
            mHolder.mViewPager.setAdapter(mCustomPagerAdapter);
            mHolder.dotsIndicator.setViewPager(mHolder.mViewPager);
        }

        mHolder.title.setText(data.getName()+"");
        mHolder.description.setText(data.getDescription()+"");

        if (value != -1) {
            mHolder.value.setText(value+"");
        }else {
            mHolder.llCoins.setVisibility(View.GONE);
        }

        if (data.getType() == 1 && isMy) {
            //é um arquivo e já foi resgatado
            mHolder.btnDownloadEnter.setVisibility(View.VISIBLE);
            mHolder.btnBuyEnter.setVisibility(View.GONE);
            mHolder.txtBtn.setText(R.string.revendica);
            url = data.getUrl();
        }else if (data.getType() == 0 && isMy) {
            //O item já foi resgatado e é um cupom
            mHolder.contentTicket.setVisibility(View.VISIBLE);
            mHolder.ticket.setText(data.getTicket());

            if (!data.getExpirationDate().equals("")) {
                String expiration = getResources().getString(R.string.expira_in);
                mHolder.expirationDate.setText(expiration.replace("#$", UTILS.formatDateFromDB(data.getExpirationDate())));
            }

            mHolder.contentTicket.setOnClickListener(v -> setClipboard(ProductActivity.this, mHolder.ticket.getText().toString()));
            mHolder.btnBuyEnter.setVisibility(View.GONE);
            mHolder.btnDownloadEnter.setVisibility(View.GONE);
        }else if (data.getType() == 1) {
            //O item não foi resgatado e é um arquivo
            url = data.getUrl();
            mHolder.btnBuyEnter.setVisibility(View.VISIBLE);
            mHolder.btnDownloadEnter.setVisibility(View.GONE);
            mHolder.contentTicket.setVisibility(View.GONE);
        }else  {
            //O item não foi resgatado e é um cupom
            mHolder.btnBuyEnter.setVisibility(View.VISIBLE);
            mHolder.btnDownloadEnter.setVisibility(View.GONE);
            mHolder.contentTicket.setVisibility(View.GONE);
        }

        mHolder.btnBuyEnter.setOnClickListener(v -> {
            mHolder.load.setVisibility(View.VISIBLE);
            redeemProduct(data);
        });

        mHolder.btnDownloadEnter.setOnClickListener(v -> {
            new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_RESCUES_DOWNLOAD_FILE, data.getId()).execute();
            fileName = data.getFile_name().contains(".pdf") ? Calendar.getInstance().getTimeInMillis()+".pdf" : Calendar.getInstance().getTimeInMillis()+".png";
            file_size = data.getFile_size();

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            View view = getLayoutInflater().inflate(R.layout.dialog_download, null);
            txtProgresso = view.findViewById(R.id.txt_progresso_download);
            pbProgresso = view.findViewById(R.id.pb_download_arquivo);
            btnCancelar = view.findViewById(R.id.btn_cancelar);

            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int downloadId = initDownload(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());

            btnCancelar.setOnClickListener(v1 -> PRDownloader.cancel(downloadId));

        });

        if (blockPurchase) {
            mHolder.btnBuyEnter.setAlpha(0.5f);
            mHolder.btnBuyEnter.setClickable(false);
            mHolder.llCoins.setVisibility(View.GONE);
            mHolder.txtButton.setText(typeValue);
        }
    }

    private void setClipboard(Context context, String text) {
        new AsyncOperation(activity, AsyncOperation.TASK_ID_SAVE_TRACKING, 999, emptyAsync, TRACKING.TRACKING_RESCUES_TICKET_COPY, data.getId()).execute();
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, activity.getResources().getString(R.string.cuponul_a_fost_copiat), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    int initDownload(String dir) {
        return PRDownloader.download(url, dir, fileName)
                .build()
                .setOnStartOrResumeListener(() -> {
                    dialog.show();
                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {
                    dialog.dismiss();
                    Toast.makeText(activity, getResources().getString(R.string.descarcarea_a_fost_anulata), Toast.LENGTH_SHORT).show();
                })
                .setOnProgressListener(progress -> {
                    Log.e("progress", progress.currentBytes+"");
                    Log.e("size", file_size+"");
                    int p = (int) (((float)progress.currentBytes / file_size) * 100);
                    if (p <= 100) {
                        this.pbProgresso.setProgress(p);
                        txtProgresso.setText(p + "%");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        pbProgresso.setProgress(100);
                        txtProgresso.setText(100 + "%");

                        new Handler().postDelayed(() -> {
                            Toast.makeText(activity, getResources().getString(R.string.descarcare_terminata), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            openFile();
                        }, 500);
                    }

                    @Override
                    public void onError(Error error) {
                        dialog.dismiss();
                        Toast.makeText(activity, getResources().getString(R.string.a_aparut_o_eroare_la_descarcarea_fisierului), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFile() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName );

            fileName = "";

            String type = getType(file);

            if (type.equals("application/pdf")) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://docs.google.com/viewer?url="+url));
                startActivity(i);
            }else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                intent.setDataAndType(uri, type);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                new Handler().postDelayed(() -> startActivity(intent), 2000);
            }

        } catch (Exception ex) {
            Log.e(TAG, "Erro na função openFile: " + ex.getMessage()+"");
        }
    }

    private String getType(File file) {
        String type = null;
        try {
            URL u = file.toURL();
            URLConnection uc;
            uc = u.openConnection();
            type = uc.getContentType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    @Override
    public void onBackPressed() {
        if (us) {
            Intent intent1 = new Intent();
            intent1.putExtra("us", true);
            setResult(RESULT_OK, intent1);
        }
        finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    class ViewHolder {
        ImageView imgBack;
        ViewPager mViewPager;
        DotsIndicator dotsIndicator;
        ConstraintLayout cardDiscont;
        TextView txtPriceNews;
        TextView txtPricePrevious;
        TextView title;
        TextView description;
        TextView value;
        TextView txtBtn;
        HorizontalScrollView layoutScrollViewSize;
        TextView txtColor;
        HorizontalScrollView layoutScrollViewColor;
        ImageView imgIconCart;
        LinearLayout llCoins;
        LinearLayout contentPromo;
        LinearLayout contentTicket;
        TextView txtButton;
        TextView ticket;
        TextView expirationDate;
        View btnBuyEnter;
        View btnDownloadEnter;
        DotsIndicator dots_indicator;
        LinearLayout load;

        public ViewHolder() {
            imgBack = findViewById(R.id.img_back);
            dots_indicator = findViewById(R.id.dots_indicator);
            title = findViewById(R.id.txt_title);
            value = findViewById(R.id.value);
            description = findViewById(R.id.txt_description);
            mViewPager = findViewById(R.id.pager);
            ticket = findViewById(R.id.ticket);
            txtBtn = findViewById(R.id.txt_btn);
            expirationDate = findViewById(R.id.expiration_date);
            cardDiscont = findViewById(R.id.cardDiscont);
            contentTicket = findViewById(R.id.contentTicket);
            txtPriceNews = findViewById(R.id.txtPriceNews);
            txtPricePrevious = findViewById(R.id.txtPricePrevious);
            dotsIndicator = findViewById(R.id.dots_indicator);
            layoutScrollViewColor = findViewById(R.id.hozontalScrollColor);
            layoutScrollViewSize = findViewById(R.id.hozontalScrollSize);
            txtColor = findViewById(R.id.txt_color);
            imgIconCart = findViewById(R.id.imgIconCart);
            llCoins = findViewById(R.id.ll_coins);
            btnBuyEnter = findViewById(R.id.btn_buy_enter);
            btnDownloadEnter = findViewById(R.id.btn_download_enter);
            txtButton = findViewById(R.id.txtButton);
            contentPromo = findViewById(R.id.contentPromo);
            load = findViewById(R.id.load);
        }
    }
}