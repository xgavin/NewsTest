package com.gavin.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MyAdapter extends SimpleAdapter {
    private List<Map<String, Object>> list;
    private LayoutInflater layoutInflater;

    public final class ViewHolder{
        public TextView news_title;
        public ImageView news_image;
        public TextView news_update;
        public TextView news_content;
    }

    public MyAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        list = data;
        layoutInflater = LayoutInflater.from(context);
        Log.d("Myadapter", "init");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.listview_layout, null);
            viewHolder.news_content = (TextView)convertView.findViewById(R.id.news_content);
            viewHolder.news_image = (ImageView)convertView.findViewById(R.id.news_image);
            viewHolder.news_title = (TextView)convertView.findViewById(R.id.news_title);
            viewHolder.news_update = (TextView)convertView.findViewById(R.id.news_update);
            convertView.setTag(viewHolder);
//            if(!list.get(position).get("news_image").equals(""))
//                viewHolder.news_image.setTag(list.get(position).get("news_image"));
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
//            viewHolder.news_image.getTag();
        }
        viewHolder.news_content.setText(list.get(position).get("news_content").toString());
//        if(!list.get(position).get("news_image").equals(""))
//            new MyLoader(list.get(position).get("news_image").toString(), viewHolder.news_image).execute();
        viewHolder.news_title.setText(list.get(position).get("news_title").toString());
        viewHolder.news_update.setText(list.get(position).get("news_update").toString());
        return convertView;
    }

    class MyLoader extends AsyncTask<String, Void, Bitmap> {
        private String strUrl;
        private ImageView imageView;
        private final int DEF_CONNECT_TIMEOUT = 30000;//默认连接超时
        private final int DEF_READ_TIMEOUT = 30000;//默认连接超时

        public MyLoader(String strUrl, ImageView imageView) {
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
                if(httpURLConnection != null)
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
}
