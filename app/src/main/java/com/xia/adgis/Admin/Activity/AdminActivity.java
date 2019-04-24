package com.xia.adgis.Admin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xia.adgis.Login.LoginActivity;
import com.xia.adgis.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.admin_toolbar)
    Toolbar toolbar;
    @BindView(R.id.manage_user)
    RelativeLayout manageUser;
    @BindView(R.id.manage_ad)
    RelativeLayout manageAD;
    @BindView(R.id.manage_message)
    RelativeLayout manageMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);
        toolbar.setTitle("管理端");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_manage);
        }
        manageUser.setOnClickListener(this);
        manageAD.setOnClickListener(this);
        manageMessage.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.admin_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("确认登出管理端？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobUser.logOut();
                        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                        onBackPressed();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manage_user:
                startActivity(new Intent(AdminActivity.this, ManageUserActivity.class));
                overridePendingTransition(R.anim.in, R.anim.out);
                break;
            case R.id.manage_ad:
                startActivity(new Intent(AdminActivity.this, ManageADsActivity.class));
                overridePendingTransition(R.anim.in, R.anim.out);
                break;
            case R.id.manage_message:
                startActivity(new Intent(AdminActivity.this, ManageMessagesActivity.class));
                overridePendingTransition(R.anim.in, R.anim.out);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in, R.anim.out);
    }
}
