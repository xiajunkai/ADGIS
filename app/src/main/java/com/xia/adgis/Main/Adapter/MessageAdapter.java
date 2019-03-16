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

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private List<Messages> mMessagesList;
    private LayoutInflater inflater;
    private Context mContext;

    public MessageAdapter(Context context, List<Messages> messagesList){
        inflater = LayoutInflater.from(context);
        mMessagesList = messagesList;
        mContext = context;
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
        ViewHolder viewHolder;
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
        Glide.with(mContext).load(messages.getUserIcon())
                .thumbnail(0.1f).into(viewHolder.userIcon);
        viewHolder.userContent.setText(messages.getContent());
        return convertView;
    }

    private static class ViewHolder{
        public ImageView userIcon;
        public TextView userName;
        public TextView userContent;
    }
}
