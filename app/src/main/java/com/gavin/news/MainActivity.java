package com.gavin.news;

import android.app.ProgressDialog;
import android.content.SyncAdapterType;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

//    private TextView textView;
//    private TextView news_title;
//    private ImageView news_image;
//    private TextView news_content;
    private String strUrl= null;
    private Map mapParams;
    private List<String> searchList= null;
    private List<Map<String, Object>> newsList = null;
    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsList = new ArrayList<>();
        new getList().start();
        new getData(0,9).start();
        uiUpdate uiUpdate = new uiUpdate();
        uiUpdate.execute();
    }

    class getList extends Thread {
        private final String DEF_CHARSET = "UTF-8";//编码模式
        private final int DEF_CONNECT_TIMEOUT = 30000;//默认连接超时
        private final int DEF_READ_TIMEOUT = 30000;//默认连接超时
        private final String APP_KEY = "bee5c17ff931878b0c3a52b1db35554f";//应用key

        @Override
        public void run() {
            super.run();
            setSearchList();
        }

        protected String urlEncode(Map<String, Object> data) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry i : data.entrySet()) {
                try {
                    stringBuilder.append(i.getKey())
                                 .append("=")
                                 .append(URLEncoder.encode(i.getValue() + "", DEF_CHARSET))
                                 .append("&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return stringBuilder.toString();
        }
        //时事热点
        protected void setGetParams() {
            strUrl = "http://op.juhe.cn/onebox/news/words";
            mapParams = new HashMap();
            mapParams.put("dtype", "");
            mapParams.put("key", APP_KEY);
        }
        //填充搜索列表
        protected void setSearchList(){
            String result;
            setGetParams();
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            try {
                strUrl = strUrl + "?" + urlEncode(mapParams);   Log.d("NewsNetFunction", strUrl);
                URL url = new URL(strUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(DEF_CONNECT_TIMEOUT);
                httpURLConnection.setReadTimeout(DEF_READ_TIMEOUT);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, DEF_CHARSET));
                StringBuilder stringBuilder = new StringBuilder();
                String strRead;
                while ((strRead = bufferedReader.readLine()) != null) {
                    stringBuilder.append(strRead);
                }
                result = stringBuilder.toString();  Log.d("NewsNetFunction", result);

                if(result == null){
                    Log.d("GetListThread", "连接错误，返回为空");
                }else{
                    try{
                        JSONObject jsonObject = new JSONObject(result);
                        String reason = jsonObject.getString("reason");
                        if (reason.equals("查询成功")){
                            Log.d("GetListThread", "连接成功，"+reason);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            searchList = new ArrayList<>();
                            int i;
                            for (i=0; i<jsonArray.length(); i++)
                                searchList.add(jsonArray.getString(i));
                        }else{
                            Log.d("GetListThread", "连接成功，"+reason);
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if(bufferedReader != null){
                    try{
                        bufferedReader.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    class getData extends Thread{
        private final String DEF_CHARSET = "UTF-8";//编码模式
        private final int DEF_CONNECT_TIMEOUT = 30000;//默认连接超时
        private final int DEF_READ_TIMEOUT = 30000;//默认连接超时
        private final String APP_KEY = "bee5c17ff931878b0c3a52b1db35554f";//应用key
        int begin_num;
        int end_num;

        @Override
        public void run() {
            super.run();
            while (searchList == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            doTask();
        }

        public void doTask(){
            while (begin_num <= end_num) {
                StringBuilder result;
                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;
                try {
                    setQueryParams(searchList.get(begin_num));
                    strUrl = strUrl + urlEncode(mapParams);
                    URL url = new URL(strUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(DEF_CONNECT_TIMEOUT);
                    httpURLConnection.setReadTimeout(DEF_READ_TIMEOUT);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setInstanceFollowRedirects(false);
                    httpURLConnection.connect();
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    result = new StringBuilder();
                    String strRead;
                    while ((strRead = bufferedReader.readLine()) != null)
                        result.append(strRead);
                    Log.d("NewsNetFunction", result.toString());
                    if (result.toString() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getString("reason").equals("查询成功")) {
                                JSONObject target = jsonObject.getJSONArray("result").getJSONObject(0);
                                Map map = new HashMap();
                                map.put("news_title", target.getString("title"));
                                map.put("news_image", target.getString("img"));
                                map.put("news_content", Html.fromHtml(target.getString("content")));
                                map.put("news_update", target.getString("pdate"));
                                newsList.add(map);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                    try {
                        if (bufferedReader != null)
                            bufferedReader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                begin_num++;
            }
        }

        public getData(int begin, int end) {
            begin_num = begin;
            end_num = end;
        }

        protected String urlEncode(Map<String, Object> data) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry i : data.entrySet()) {
                try {
                    stringBuilder.append(i.getKey())
                            .append("=")
                            .append(URLEncoder.encode(i.getValue() + "", DEF_CHARSET))
                            .append("&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return stringBuilder.toString();
        }

        protected void setQueryParams(String searchKey){
            strUrl = "http://op.juhe.cn/onebox/news/query?";
            mapParams = new HashMap();
            mapParams.put("dtype", "");
            mapParams.put("key",APP_KEY);
            mapParams.put("q",searchKey);
        }
    }

    class uiUpdate extends AsyncTask<Void, Void, Void>{
        private ProgressDialog progressDialog;
        @Override
        protected Void doInBackground(Void... params) {
            while(newsList.size() <= 9){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            Log.d("MainAcitivity", "newslist != null");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myListView = (ListView)findViewById(R.id.news_list);
            MyAdapter myAdapter = new MyAdapter(MainActivity.this,
                    newsList,
                    R.layout.listview_layout,
                    new String[]{"news_title", "news_update"},
                    new int[]{R.id.news_title, R.id.news_update});
            myListView.setAdapter(myAdapter);
            progressDialog.dismiss();
            Log.d("MainAcitivity", "listview init");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            Log.d("MainAcitivity", "init progressdialog");
        }
    }
}
