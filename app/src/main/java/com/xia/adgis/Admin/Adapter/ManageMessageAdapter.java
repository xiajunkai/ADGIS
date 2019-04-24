package com.xia.adgis.Admin.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xia.adgis.Admin.Activity.ManageMessagesActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageMessageAdapter extends RecyclerView.Adapter<ManageMessageAdapter.ViewHolder> {

    private static final int MODE_CHECK = 0;
    private int mEditMode = MODE_CHECK;

    private List<Messages> mMessageList;
    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

    public ManageMessageAdapter(List<Messages> lists){
        mMessageList = lists;
        Collections.reverse(mMessageList);
    }

    private AlertDialog.Builder dialog;
    //删除标志
    private ProgressDialog loading;
    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.manage_img)
        ImageView mADImg;
        @BindView(R.id.manage_title)
        TextView mUserTitle;
        @BindView(R.id.manage_source)
        TextView mContent;
        @BindView(R.id.root_view)
        RelativeLayout mRootView;
        @BindView(R.id.check_box)
        ImageView mCheckBox;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_manage, parent, false);
        return new ManageMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Messages messages = mMessageList.get(holder.getAdapterPosition());
        holder.mUserTitle.setText("留言者:" + messages.getUserName() + "   留言位置:" + messages.getAdName());
        holder.mContent.setText(messages.getContent());
        Glide.with(mContext).load(R.drawable.ic_manage_message).into(holder.mADImg);
        if (mEditMode == MODE_CHECK) {
            holder.mCheckBox.setVisibility(View.GONE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (messages.isSelect()) {
                holder.mCheckBox.setImageResource(R.drawable.ic_checked);
            } else {
                holder.mCheckBox.setImageResource(R.drawable.ic_uncheck);
            }
        }
        //点击逻辑
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mMessageList);
            }
        });
        holder.mADImg.setOnClickListener(new View.OnClickListener() {
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

        //删除
        holder.mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading = new ProgressDialog(mContext);
                loading.setMessage("删除中...");
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(mContext);
                deleteDialog.setTitle("删除留言");
                deleteDialog.setMessage("确定删除留言吗？\n警告，删除后不可恢复！！！");
                deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading.show();
                        deleteMessage(messages.getObjectId(),messages);
                    }
                });
                deleteDialog.setNegativeButton("取消", null);
                deleteDialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public List<Messages> getList(){
        if (mMessageList == null) {
            mMessageList = new ArrayList<>();
        }
        return mMessageList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos,List<Messages> myADList);
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    //删除留言
    private void deleteMessage(String objectId, final Messages msg){
        Messages messages = new Messages();
        messages.setObjectId(objectId);
        messages.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mMessageList.remove(msg);
                notifyDataSetChanged();
                loading.dismiss();
            }
        });
    }

}
