package com.miko.zd.loginnwuweb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btLogin;
    Button btGetScore;
    EditText etUser;
    EditText etPassword;
    EditText etVerifation;
    ImageView ivVerifation;
    ImageButton ibRefresh;
    Bitmap bmVerifation;

    String VERIFATIONURL = "http://jwxt.nwu.edu.cn/(dlxrmg55j21wlaqv2z5rcdyi)/CheckCode.aspx";
    String LOGINURL = "http://jwxt.nwu.edu.cn/(dlxrmg55j21wlaqv2z5rcdyi)/Default2.aspx";
    String HOSTURL = "http://jwxt.nwu.edu.cn";
    String MAINBODYHTML = "";
    String GETSCOREURL = "";
    String GETSCOREHOST = "http://jwxt.nwu.edu.cn/(dlxrmg55j21wlaqv2z5rcdyi)/";
    public static String[][] score = new String[3][10];

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case 0:
                        Toast.makeText(MainActivity.this, "验证码不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "密码或用户名错误", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(MainActivity.this, "验证码不能为空，如看不清请刷新", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(MainActivity.this, "用户名不存在或未按照要求参加教学活动", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 10:
                        Log.i("xyz","gengixni");
                        ivVerifation.setImageBitmap(bmVerifation);
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        DoGetVerifation();
    }

    private void initView() {
        btLogin = (Button) findViewById(R.id.bt_login);
        btGetScore = (Button) findViewById(R.id.bt_getscore);
        etUser = (EditText) findViewById(R.id.et_user);
        etPassword = (EditText) findViewById(R.id.et_password);
        etVerifation = (EditText) findViewById(R.id.et_verifation);
        ivVerifation = (ImageView) findViewById(R.id.iv_verifation);
        ibRefresh = (ImageButton) findViewById(R.id.ib_refresh);
    }

    private void initEvent() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_login:
                        DoLogin(etUser.getText().toString(), etPassword.getText().toString(), etVerifation.getText().toString());
                        break;
                    case R.id.bt_getscore:
                        DoGetScore();
                        break;
                    case R.id.ib_refresh:
                        DoGetVerifation();
                        break;
                }
            }
        };
        btLogin.setOnClickListener(onClickListener);
        btGetScore.setOnClickListener(onClickListener);
        ibRefresh.setOnClickListener(onClickListener);
    }

    private void DoGetVerifation() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost httPost = new HttpPost(VERIFATIONURL);
                HttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse httpResponse = client.execute(httPost);
                    byte[] bytes = new byte[1024];
                    bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                    bmVerifation = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bmVerifation == null)
                    Toast.makeText(MainActivity.this, "获取验证码失败请检查网络设置", Toast.LENGTH_SHORT).show();
                Message msg = new Message();
                msg.arg1 = 10;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void DoGetScore() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                MainActivity.this.startActivity(intent);
            }
        };

        if (MAINBODYHTML.equals("")) {
            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        }
        Document doc = Jsoup.parse(MAINBODYHTML);
        Elements links = doc.select("a[href]");
        StringBuffer sb = new StringBuffer();
        for (Element link : links) {
            //获取所要查询的URL,这里对应地址按钮的名字叫成绩查询
            if (link.text().equals("等级考试查询")) {
                sb.append(link.attr("href"));
            }
        }
        GETSCOREURL = sb.toString();
        Log.i("xyz", GETSCOREURL);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(GETSCOREHOST + GETSCOREURL);
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    Log.i("xyz", String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                    StringBuffer sb2 = new StringBuffer();
                    HttpEntity entity = httpResponse.getEntity();
                    String re = EntityUtils.toString(entity);
                    parse(re);
                    Message msg = new Message();
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void DoLogin(final String user, final String password, final String verifation) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient defaultclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(LOGINURL);
                HttpResponse httpResponse;

                //设置post参数
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("__VIEWSTATE", "dDwyODE2NTM0OTg7Oz6ZmvWn7xzjizifHN9MgLoDNTRtjQ=="));
                params.add(new BasicNameValuePair("Button1", ""));
                params.add(new BasicNameValuePair("hidPdrs", ""));
                params.add(new BasicNameValuePair("hidsc", ""));
                params.add(new BasicNameValuePair("lbLanguage", ""));
                params.add(new BasicNameValuePair("RadioButtonList1", "%D1%A7%C9%FA"));
                params.add(new BasicNameValuePair("TextBox2", password));
                params.add(new BasicNameValuePair("txtSecretCode", verifation));
                params.add(new BasicNameValuePair("txtUserName", user));

                //获得个人主界面的HTML
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = defaultclient.execute(httpPost);
                    Log.i("xyz", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        StringBuffer sb = new StringBuffer();
                        HttpEntity entity = httpResponse.getEntity();
                        MAINBODYHTML = EntityUtils.toString(entity);
                        IsLoginSuccessful(MAINBODYHTML);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void IsLoginSuccessful(String loginresult) {
        Document doc = Jsoup.parse(loginresult);
        Elements alert = doc.select("script[language]");
        Elements success = doc.select("a[href]");

        Message msg = new Message();
        //先判断是否登录成功，若成功直接退出
        for (Element link : success) {
            //获取所要查询的URL,这里对应地址按钮的名字叫成绩查询
            if (link.text().equals("等级考试查询")) {
                Log.i("xyz", "登录成功");
                msg.arg1 = 6;
                handler.sendMessage(msg);
                return;
            }
        }

        for (Element link : alert) {
            //刷新验证码
            DoGetVerifation();
            //获取错误信息
            if (link.data().contains("验证码不正确")) {
                Log.i("xyz", "验证码错误");
                msg.arg1 = 0;
                handler.sendMessage(msg);
            } else if (link.data().contains("用户名不能为空")) {
                Log.i("xyz", "用户名不能为空");
                msg.arg1 = 1;
                handler.sendMessage(msg);
            } else if (link.data().contains("密码错误")) {
                Log.i("xyz", "密码或用户名错误");
                msg.arg1 = 2;
                handler.sendMessage(msg);
            } else if (link.data().contains("密码不能为空")) {
                Log.i("xyz", "密码不能为空");
                msg.arg1 = 3;
                handler.sendMessage(msg);
            } else if (link.data().contains("验证码不能为空，如看不清请刷新")) {
                Log.i("xyz", "验证码不能为空，如看不清请刷新");
                msg.arg1 = 4;
                handler.sendMessage(msg);
            }
            else if (link.data().contains("用户名不存在")) {
                Log.i("xyz", "用户名不存在或未按照要求参加教学活动");
                msg.arg1 = 5;
                handler.sendMessage(msg);
            }
        }

    }

    private void parse(String parse) {

        Document doc = Jsoup.parse(parse);
        Elements trs = doc.select("table").select("tr");
        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            for (int j = 0; j < tds.size(); j++) {
                String text = tds.get(j).text();
                score[i][j] = text;
                Log.i("xyz", score[i][j]);
            }
        }
    }
}
