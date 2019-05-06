package com.xia.adgis.Main.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.Main.Activity.ADsDetailActivity;
import com.xia.adgis.Main.Adapter.MessageAdapter;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ADsMessageFragment extends Fragment {

    @BindView(R.id.message_empty)
    TextView empty;
    @BindView(R.id.message_detail)
    ListView detail;

    private List<Messages> messageList = new ArrayList<>();
    private MessageAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ads_message, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDataFirst();
    }

    //抓取数据
    private void fetchDataFirst(){
        empty.setVisibility(View.GONE);
        final ADsDetailActivity aDsDetailActivity = (ADsDetailActivity) getActivity();
        String title = aDsDetailActivity.getAdsName();
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
        messageBmobQuery.addWhereEqualTo("adName",title);
        messageBmobQuery.findObjects(new FindListener<Messages>() {
            @Override
            public void done(List<Messages> list, BmobException e) {
                if (e == null){
                        //获取到数据，并显示在其中
                        detail.setVisibility(View.VISIBLE);
                        messageList = list;
                        Collections.reverse(messageList);
                        adapter = new MessageAdapter(getActivity(),messageList);
                        detail.setAdapter(adapter);
                }else {
                    empty.setText(e.getMessage());
                }
            }
        });
    }

    //刷新数据
    private void fetchDataAgain(){
        final ADsDetailActivity aDsDetailActivity = (ADsDetailActivity) getActivity();
        String title = aDsDetailActivity.getAdsName();
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
        messageBmobQuery.addWhereEqualTo("adName",title);
        messageBmobQuery.findObjects(new FindListener<Messages>() {
            @Override
            public void done(List<Messages> list, BmobException e) {
                if (e == null){
                    //获取到数据，并显示在其中
                    empty.setVisibility(View.GONE);
                    detail.setVisibility(View.VISIBLE);
                    messageList.clear();
                    messageList.addAll(list);
                    Collections.reverse(messageList);
                    adapter.notifyDataSetChanged();
                }else {
                    empty.setText(e.getMessage());
                }
            }
        });
    }
    //接口回调
    public void refresh(CallBack callback){
        try {
            if(callback.isLeaveMessageSuccess()) {
                fetchDataAgain();
            }else {
                Toast.makeText(getActivity(), "载入失败", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public interface CallBack{
        boolean isLeaveMessageSuccess();
    }
}
