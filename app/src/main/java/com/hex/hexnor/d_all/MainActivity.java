package com.hex.hexnor.d_all;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Vibrator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    WebView webView;
    Button Download;
    EditText Url;
    String address;
    ProgressDialog progressDialog;
    DownloadManager downloadManager;
    String title="",ext="";
    boolean websiteoption=false;
    SharedPreferences  pref;
    SharedPreferences.Editor edit;


    String text="<body style='background:#222'><hr><h1 style='text-align:center;color:#FFF'>D-all</h1><hr><br><br><div style='font-size:2em; text-align:center;color:#FFF'><br>ENTER THE URL ON THE TOP OR SEARCH ANY VIDEOS FROM THERE <br></br>OR VISIT</br><br><button  style='background-color:#F00;font-size:1em'><a style='color:#FFF;text-decoration: none;' href='http://youtube.com'> Youtube</a></button></br></br><hr>ALL FILES WILL BE DOWNLOADED TO D-ALL FOLDER IN ROOT DIRECTORY  <br><hr></div> <br><br><div  style='position:relative; margin-bottom:20px;text-align:center;color:#FFF;font-size:2em;'> DEVELOPED BY </br>YOKESH RANA CSE BIET JHANSI </div><br><hr><div  style='text-align:center;color:#FFF;font-size:2em;'> <br>TERMS AND SERVICES <br></div>" +
            " <br><br> <ol style='color:#FFF'>\n" +
            "<li class=\"small\">\n" +
            "This android application is only for private use not for commercial use only.\n" +
            "<p>&nbsp;</p>\n" +
            "</li>\n" +
            "<li class=\"small\">D-all is only to be used to access content which are authorized by third party. The user is fully responsible for checking before visiting any content. In case a third party right is  violated  in any means by downloading the content, the user bears complete responsibility .\n" +
            "<p>&nbsp;</p>\n" +
            "</li>\n" +
            "<li class=\"small\">\n" +
            "The user bears full responsibility for all actions related to the data transmitted by D-all . D-all does not grant any rights to the contents, as it only acts as a technical service provider.\n" +
            "<p>&nbsp;</p>\n" +
            "</li>\n" +
            "<li class=\"small\">Accessing pornographic content or such content that infringes any right in any other way is strictly forbidden. \n" +
            "<p>&nbsp;</p>\n" +
            "</li>\n" +
            "<li class=\"small\"> D-all only provides the medium b/w user and webserver. Hence, D-all does not take liability towards the user or any third party for the permissibility of downloading content through D-all.\n" +
            "<p>&nbsp;</p>\n" +
            "</li>\n" +
            "\n" +
            "<li class=\"small\">D-all allows user to  download content for  their private use (\"fair use\") and user is only responsible for that . Any further use of the content transmitted by D-all is liable to user only and D-all does not take any responsibility. \n" +
            "<p>&nbsp;</p>\n" +
            "<li class=\"small\">D-all must only be used for private purposes. Any commercial use of D-all is strictly forbidden and will be pursued in a court of law.\n" +
            "<p>&nbsp;</p>\n" +
            "</li>\n" +
            "</li>\n" +
            "</ol>" +
            "<br><br> <div style='font-size:2em; text-align:center;color:#FFF'>!!! HAPPY SURFING !!!</div><br><div style='color:#FFF;text-align:center'>Contact me at cse.ykr@gmail.com</div><hr>" +
            "" +
            "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Download = (Button) findViewById(R.id.Download);
        Url = (EditText) findViewById(R.id.url);
        Url.setImeActionLabel("Downloading", KeyEvent.KEYCODE_ENTER);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = webView.getSettings();

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pref=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        edit = pref.edit();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        webView.loadData(text, "text/html; charset=utf-8", "utf-8");
        webView.requestFocus();

      //  Log.d("url", webView.getUrl());
        Url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
              //  Toast.makeText(MainActivity.this, "" + Url.getText().toString(), Toast.LENGTH_SHORT).show();
                String address = Url.getText().toString();
                if(address.endsWith("youtube.com"))
                    address="http://youtube.com";
                else if (address.contains("watch"))
                    address="https://www.youtube.com/watch?v="+address.substring(address.indexOf('v') + 2, address.indexOf('v') + 13);
                else if (address.contains("youtu.be"))
                    address="https://www.youtube.com/watch?v="+address.substring(address.indexOf('.') + 4, address.indexOf('.') + 15);
                else
                    address="https://www.youtube.com/results?search_query="+address;
                webView.loadUrl(address);
                Url.setText(null);
                webView.requestFocus();


                return true;
            }
        });
        haveStoragePermission();
        //for download
//        webView.setDownloadListener(new DownloadListener() {
//
//            @Override
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//                DownloadManager.Request request = new DownloadManager.Request(
//                        Uri.parse(url));
//
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title+"."+ext);
//                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                dm.enqueue(request);
//                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
//                        Toast.LENGTH_LONG).show();
//
//            }
//        });


        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(200);
                 address = webView.getUrl().toString();
                if(!address.contains("watch"))
                    Toast.makeText(MainActivity.this, "GO TO ANY VIDEO PAGE FIRST", Toast.LENGTH_SHORT).show();
                else
                boom();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        webView.loadData(text, "text/html; charset=utf-8", "utf-8");
    }

    private void boom() {
        int count =pref.getInt("count",1);
        edit.putInt("count",1);
        String boomurl=address;
        String url = "http://dyall.herokuapp.com/api/info?url="+boomurl;
        Log.d("URL",url);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonobject=new JSONObject(response);
                    JSONObject js=jsonobject.getJSONObject("info");
                    title=js.getString("title");
                    title=title.replace(' ','_');
                    ext=js.getString("ext");
                    download(js.getString("url").toString());


                    //  Toast.makeText(MainActivity.this, js.getString("url").toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                  //  Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Check Internet Connectivity ", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("authorization", "token ce3fe9a203703c7ea3da8727ff8fbafec8ddbf44");
                return params;
            }
        };
        requestQueue.add(stringRequest);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Parsing the file buddy ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

}

    private void download(String url) {
        Uri uri = Uri.parse(url);
        final long downloadReference=DownloadData(uri);
        Toast.makeText(this, "!! Boom  Download Started !!", Toast.LENGTH_LONG).show();
        final Vibrator vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadReference == reference) {
                    Toast.makeText(MainActivity.this, "Download Completed !!", Toast.LENGTH_SHORT).show();
                    vibrator.vibrate(200);

                }
            }
        };
        registerReceiver(receiver, filter);
    }

    private long DownloadData (Uri uri)  {
        long downloadReference;
       downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setTitle(title);
        request.setDescription("Downloading using D-all");
            request.setDestinationInExternalPublicDir(
                    "/1_D-all",title+"."+ext);
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        String webUrl = webView.getUrl();
                       // Toast.makeText(this, ""+webUrl, Toast.LENGTH_SHORT).show();
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    class CustomWebViewClient extends WebViewClient {
        boolean timeout = true;

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            view.loadUrl("");
            Toast.makeText(MainActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
            view.loadData("<h1 style='color:#FFF;text-align:center'>CHECK YOUR INTERNET CONNECTIVITY</h1> "+text, "text/html; charset=utf-8", "utf-8");

        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            url = view.getUrl().toString();

            return true;
        }

        @Override
        public void onPageStarted(final WebView view, String url, Bitmap favicon) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(timeout) {
                        Toast.makeText(MainActivity.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                       // view.loadData("<h1 style='color:#FFF;text-align:center'>CHECK YOUR INTERNET CONNECTIVITY</h1> "+text, "text/html; charset=utf-8", "utf-8");

                    }
                }
            }).start();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            timeout = false;
        }

    }

}