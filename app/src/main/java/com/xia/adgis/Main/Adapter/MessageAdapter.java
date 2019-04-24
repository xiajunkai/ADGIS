package com.xia.adgis.Main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Activity.UserCentreActivity;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MessageAdapter extends BaseAdapter {

    private List<Messages> mMessagesList;
    private LayoutInflater inflater;
    private Context mContext;
    private User user;
    private AppCompatActivity activity;
    private AlertDialog.Builder dialog;
    public MessageAdapter(Context context, List<Messages> messagesList){
        inflater = LayoutInflater.from(context);
        mMessagesList = messagesList;
        mContext = context;
        user = BmobUser.getCurrentUser(User.class);
        activity = (AppCompatActivity) context;
    }
    @Override
    public int getCount() {
        int ret = 0;
        if(mMessagesList != null){
            ret = mMessagesList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        return mMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Messages messages = (Messages) this.getItem(position);
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_message, null);
            viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.message_user_icon);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.message_user_name);
            viewHolder.userContent = (TextView) convertView.findViewById(R.id.message_content);
            viewHolder.item = (LinearLayout) convertView.findViewById(R.id.message_total);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userName.setText(messages.getUserName());
        //若是当前用户，直接加载，不是再去服务器查询
        if(user.getUsername().equals(messages.getUserName())){
            Glide.with(mContext).load(user.getUserIcon())
                    .thumbnail(0.8f).into(viewHolder.userIcon);
        }else {
            BmobQuery<User> userBmobQuery = new BmobQuery<>();
            userBmobQuery.addWhereEqualTo("username", messages.getUserName());
            userBmobQuery.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e == null){
                        Glide.with(mContext).load(list.get(list.size()-1).getUserIcon())
                                .thumbnail(0.5f).into(viewHolder.userIcon);
                    }
                }
            });
        }
        viewHolder.userContent.setText(messages.getContent());
        //点击事件
        viewHolder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserCentreActivity.class);
                intent.putExtra("user_name", messages.getUserName());
                ActivityCompat.startActivity(mContext, intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                activity,
                                new Pair<>(v, "icon"))
                                .toBundle());
            }
        });
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(mContext);
                View view = LayoutInflater.from(mContext).inflate(R.layout.message_detail, null);
                dialog.setTitle("留言详情");
                dialog.setView(view);
                TextView user = (TextView) view.findViewById(R.id.message_detail_user_name);
                TextView ad = (TextView) view.findViewById(R.id.message_detail_ads_name);
                TextView content = (TextView) view.findViewById(R.id.message_detail_content);
                user.setText(messages.getUserName());
                ad.setText(messages.getAdName());
                content.setText(messages.getContent());
                dialog.setPositiveButton("取消",null);
                dialog.show();
            }
        });
        return convertView;
    }

    private static class ViewHolder{
        ImageView userIcon;
        TextView userName;
        TextView userContent;
        LinearLayout item;
    }
}
