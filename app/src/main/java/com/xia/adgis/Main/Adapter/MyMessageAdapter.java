package com.xia.adgis.Main.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.ViewHolder> {

    private List<Messages> mMessagesList;
    private Context mContext;
    private User user;

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.message_user_icon)
        ImageView userIcon;
        @BindView(R.id.message_user_name)
        TextView userName;
        @BindView(R.id.message_content)
        TextView userContent;
        @BindView(R.id.message_total)
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public MyMessageAdapter(List<Messages> mMessagesList, Context mContext) {
        this.mMessagesList = mMessagesList;
        this.mContext = mContext;
        user = BmobUser.getCurrentUser(User.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Messages messages = mMessagesList.get(position);
        holder.userName.setText(messages.getUserName());
        holder.userName.setText(messages.getUserName());
        //若是当前用户，直接加载，不是再去服务器查询
        if(user.getUsername().equals(messages.getUserName())){
            Glide.with(mContext).load(user.getUserIcon())
                    .thumbnail(0.8f).into(holder.userIcon);
        }else {
            BmobQuery<User> userBmobQuery = new BmobQuery<>();
            userBmobQuery.addWhereEqualTo("username", messages.getUserName());
            userBmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e == null){
                        Glide.with(mContext).load(list.get(list.size()-1).getUserIcon())
                                .thumbnail(0.5f).into(holder.userIcon);
                    }
                }
            });
        }
        holder.userContent.setText(messages.getContent());
        //点击事件
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
