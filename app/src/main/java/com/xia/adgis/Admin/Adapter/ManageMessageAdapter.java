package com.xia.adgis.Admin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.Main.Bean.Messages;
import com.xia.adgis.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ManageMessageAdapter extends RecyclerView.Adapter<ManageMessageAdapter.ViewHolder> {

    private static final int MODE_CHECK = 0;
    private int mEditMode = MODE_CHECK;

    private List<Messages> mMessageList;
    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

    public ManageMessageAdapter(List<Messages> lists){
        mMessageList = lists;
    }

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
        Messages messages = mMessageList.get(holder.getAdapterPosition());
        holder.mUserTitle.setText("留言者:" + messages.getUserName() + "   留言位置:" + messages.getAdName());
        holder.mContent.setText(messages.getContent());
        holder.mADImg.setVisibility(View.INVISIBLE);
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

}
