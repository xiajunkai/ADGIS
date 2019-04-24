package com.xia.adgis.Main.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.xia.adgis.Main.Adapter.MyMessageAdapter;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MessageActivity extends AppCompatActivity {

    @BindView(R.id.my_message_list)
    RecyclerView message;
    @BindView(R.id.my_message_toolbar)
    Toolbar messageToolbar;

    private String userName;

    private List<Messages> messageList = new ArrayList<>();
    private MyMessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initList();
    }

    private void initList(){
        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name");
        //初始化工具栏
        initToolbar();
        BmobQuery<Messages> messageBmobQuery = new BmobQuery<>();
        //先判断是否有缓存
        boolean isCache = messageBmobQuery.hasCachedResult(Messages.class);
        if(isCache){
            // 先从缓存取数据，如果没有的话，再从网络取。
            messageBmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        }else {
            // 如果没有缓存的话，则先从网络中取
            messageBmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        messageBmobQuery.addWhereEqualTo("userName", userName);
        messageBmobQuery.findObjects(new FindListener<Messages>() {
            @Override
            public void done(List<Messages> list, BmobException e) {
                if(e == null){
                    messageList = list;
                    GridLayoutManager manager = new GridLayoutManager(MessageActivity.this,1);
                    message.setLayoutManager(manager);
                    adapter = new MyMessageAdapter(messageList,MessageActivity.this);
                    message.setAdapter(adapter);
                }else {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initToolbar(){
        messageToolbar.setTitle(userName + "发布的留言");
        setSupportActionBar(messageToolbar);
        messageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1, R.anim.out_1);
    }
}
