package com.xia.adgis.Main.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xia.adgis.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedBackActivity extends AppCompatActivity {

    @BindView(R.id.feedback_toolbar)
    Toolbar toolbar;
    @BindView(R.id.feedback_input)
    EditText input;
    @BindView(R.id.feedback_send)
    Button send;
    private final int SMS_REQUEST_CODE = 0x1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        toolbar.setTitle("反馈");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(FeedBackActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(FeedBackActivity.this, new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
                }else {
                    sendFeedback();
                }
            }
        });
    }

    private void sendFeedback(){
        String ss = input.getText().toString();
        if (TextUtils.isEmpty(ss)){
            Toast.makeText(this, "请输入反馈内容", Toast.LENGTH_SHORT).show();
            return;
        }
        handleFeedback(ss);
    }

    private void handleFeedback(final String str) {

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("发送中...");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                loading.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                String phone = "15559721961";
                SmsManager manager = SmsManager.getDefault();
                //因为一条短信有字数限制，因此要将长短信拆分
                ArrayList<String> list = manager.divideMessage(str);
                for (String text : list) {
                    manager.sendTextMessage(phone, null, text, null, null);
                }
                pretendToRun(3000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loading.dismiss();
                Toast.makeText(FeedBackActivity.this, "短信已发送，我会认真参考你的意见和建议，谢谢反馈 :)", Toast.LENGTH_SHORT).show();
            }
        }.execute();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_REQUEST_CODE) {
            if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sendFeedback();
            }else {
                Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                //onBackPressed();
            }
        }
    }

    private void pretendToRun(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1, R.anim.out_1);
    }
}
