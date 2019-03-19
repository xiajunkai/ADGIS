package com.xia.adgis.Main.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    public MessageAdapter(Context context, List<Messages> messagesList){
        inflater = LayoutInflater.from(context);
        mMessagesList = messagesList;
        mContext = context;
        user = BmobUser.getCurrentUser(User.class);
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
        Messages messages = (Messages) this.getItem(position);
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_message, null);
            viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.message_user_icon);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.message_user_name);
            viewHolder.userContent = (TextView) convertView.findViewById(R.id.message_content);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userName.setText(messages.getUserName());
        if(user.getUsername().equals(messages.getUserName())){
            Glide.with(mContext).load(user.getUserIcon())
                    .thumbnail(0.1f).into(viewHolder.userIcon);
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
        return convertView;
    }

    private static class ViewHolder{
        public ImageView userIcon;
        public TextView userName;
        public TextView userContent;
    }
}
