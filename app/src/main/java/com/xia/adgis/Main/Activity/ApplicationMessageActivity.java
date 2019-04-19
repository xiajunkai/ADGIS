package com.xia.adgis.Main.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

import com.xia.adgis.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ApplicationMessageActivity extends AppCompatActivity {

    @BindView(R.id.app_message_toolbar)
    Toolbar toolbar;
    @BindView(R.id.about_share)
    TextView share;
    @BindView(R.id.about_guide)
    TextView guide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_message);
        ButterKnife.bind(this);
        toolbar.setTitle("应用信息");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSystemShare(ApplicationMessageActivity.this,"https://github.com/xiajunkai/ADGIS");
            }
        });
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSystemBrowser(ApplicationMessageActivity.this,"https://github.com/xiajunkai/ADGIS/blob/master/README.md");
            }
        });
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
    //分享
    private void startSystemShare(Context context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }
}
