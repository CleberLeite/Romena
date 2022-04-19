package com.frf.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class URLImageParser implements Html.ImageGetter {
    Context c;
    TextView container;

    
    public URLImageParser(TextView t, Context c) {
        this.c = c;

        this.container = t;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();

        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask( urlDrawable);

        asyncTask.execute(source);

        return urlDrawable;
    }

    @SuppressLint("StaticFieldLeak")
    public class ImageGetterAsyncTask extends AsyncTask<String, Void, ParserData> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected ParserData doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(ParserData result) {

            urlDrawable.setBounds(0, 0, result.getWidth(), result.getHeight());

            urlDrawable.drawable = result.getDrawable();

            URLImageParser.this.container.invalidate();
            URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight()
                    + result.getHeight()));


        }

        public ParserData fetchDrawable(String urlString) {
            try {

                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");

                int sizeHeight = 0;
                int sizeWidth = 0;

                if (drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth()) {
                    sizeHeight = (URLImageParser.this.container.getWidth() - 150) + UTILS.convertDpToPixels((drawable.getIntrinsicHeight() - drawable.getIntrinsicWidth()));
                    sizeWidth = URLImageParser.this.container.getWidth() - 150;
                }else if (drawable.getIntrinsicHeight() < drawable.getIntrinsicWidth()) {
                    sizeHeight = (URLImageParser.this.container.getWidth() - 150) - UTILS.convertDpToPixels((drawable.getIntrinsicWidth() - drawable.getIntrinsicHeight()) + 50);
                    sizeWidth = URLImageParser.this.container.getWidth() - 150;
                }else {
                    sizeHeight = URLImageParser.this.container.getWidth() - 150;
                    sizeWidth = URLImageParser.this.container.getWidth() - 150;
                }

                drawable.setBounds(0, 0, sizeWidth, sizeHeight);

                ParserData pd = new ParserData();
                pd.setWidth(sizeWidth);
                pd.setHeight(sizeHeight);
                pd.setDrawable(drawable);

                return pd;
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private InputStream fetch(String urlString) throws IOException {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }


            return response.body().byteStream();
        }
    }

    public class URLDrawable extends BitmapDrawable {
        protected Drawable drawable;
        @Override
        public void draw(Canvas canvas) {
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }

    private class ParserData {
        private int width = 0;
        private int height = 0;
        private Drawable drawable = null;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }
}
