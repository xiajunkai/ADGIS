package com.xia.adgis.Main.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class AboutMeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.me_toolbar)
    Toolbar toolbar;
    @BindView(R.id.my_github)
    LinearLayout github;
    @BindView(R.id.my_qq)
    LinearLayout qq;
    @BindView(R.id.me_email)
    LinearLayout email;
    @BindView(R.id.me_telphone)
    LinearLayout telphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        toolbar.setTitle("关于开发者");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        github.setOnClickListener(this);
        qq.setOnClickListener(this);
        email.setOnClickListener(this);
        telphone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_github:
                startSystemBrowser(AboutMeActivity.this, "https://github.com/xiajunkai");
                break;
            case R.id.my_qq:
                startQQChartPanel(AboutMeActivity.this,"2374975161");
                break;
            case R.id.me_email:
                startSystemEmailPanel(AboutMeActivity.this,"xiatiandefeng45@gmail.com");
                break;
            case R.id.me_telphone:
                startSystemPhoneCallPanel(AboutMeActivity.this,"15559721960");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }

    //打开系统浏览器
    private void startSystemBrowser(Context context, @NonNull String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    //打开QQ
    private void startQQChartPanel(Context context, String qqId) {
        try {
            //可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqId;//uin是发送过去的qq号码
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请检查是否安装QQ", Toast.LENGTH_SHORT).show();
        }
    }
    //发送邮件
    private void startSystemEmailPanel(Context context, String email) {
        Uri uri = Uri.parse("mailto:" + email);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, "ADGIS反馈"); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, ""); // 正文
        context.startActivity(Intent.createChooser(intent, "请选择邮件应用类型"));
    }
    //电话
    private void startSystemPhoneCallPanel(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
