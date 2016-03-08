package com.gavin.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PicLoadTest extends AsyncTask<String, Void, Bitmap>{
    private String strUrl;
    private ImageView imageView;
    private final int DEF_CONNECT_TIMEOUT = 30000;//默认连接超时
    private final int DEF_READ_TIMEOUT = 30000;//默认连接超时

    public PicLoadTest(String strUrl, ImageView imageView) {
        super();
        this.strUrl = strUrl;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        Bitmap bitmap = null;
        try{
            URL url = new URL(strUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(DEF_CONNECT_TIMEOUT);
            httpURLConnection.setReadTimeout(DEF_READ_TIMEOUT);
            httpURLConnection.connect();
            InputStream is = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            httpURLConnection.disconnect();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }
}
