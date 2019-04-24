package com.xia.adgis.Admin.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xia.adgis.Main.Activity.UserCentreActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> {

    private static final int MODE_CHECK = 0;
    private int mEditMode = MODE_CHECK;

    private List<User> mUserList;
    private OnItemClickListener mOnItemClickListener;

    private Context mContext;

    private ProgressDialog loading;

    //删除的图像
    private BmobFile deleteImg;
    public ManageUserAdapter(List<User> lists){
        mUserList = lists;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.manage_img)
        ImageView mUserImg;
        @BindView(R.id.manage_title)
        TextView mUserTitle;
        @BindView(R.id.manage_source)
        TextView mUserMotto;
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = mUserList.get(holder.getAdapterPosition());
        holder.mUserTitle.setText(user.getUsername());
        holder.mUserMotto.setText(user.getMotto());
        Glide.with(mContext).load(user.getUserIcon()).thumbnail(0.5f)
                .into(holder.mUserImg);
        if (mEditMode == MODE_CHECK) {
            holder.mCheckBox.setVisibility(View.GONE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (user.isSelect()) {
                holder.mCheckBox.setImageResource(R.drawable.ic_checked);
            } else {
                holder.mCheckBox.setImageResource(R.drawable.ic_uncheck);
            }
        }
        //点击逻辑
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mUserList);
            }
        });

        holder.mUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UserCentreActivity.class);
                intent.putExtra("user_name", user.getUsername());
                ActivityCompat.startActivity(mContext, intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (AppCompatActivity)mContext,
                                new Pair<>(view, "icon"))
                                .toBundle());
            }
        });

        holder.mRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading = new ProgressDialog(mContext);
                loading.setMessage("删除中");
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(mContext);
                deleteDialog.setTitle("删除用户");
                deleteDialog.setMessage("确定删除“" + user.getUsername() + "”吗？\n警告，删除后不可恢复！！！");
                deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loading.show();
                        deleteUser(user.getObjectId(),user);
                    }
                });
                deleteDialog.setNegativeButton("取消", null);
                deleteDialog.show();
                return false;
            }
        });
    }

    private void deleteUser(final String objectId, final User temp){
        //先判断是否有未删除的广告节点
        BmobQuery<AD> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("editor",temp.getUsername());
        bmobQuery.findObjects(new FindListener<AD>() {
            @Override
            public void done(List<AD> list, BmobException e) {
                if (e == null){
                    if (list.size() != 0){
                        loading.dismiss();
                        Toast.makeText(mContext, "该用户尚有未删除的广告牌，请先删除广告牌后删除用户", Toast.LENGTH_LONG).show();
                    }else {
                        //没有则继续删除图像
                        deleteImg = new BmobFile();
                        deleteImg.setUrl(temp.getUserIcon());
                        deleteImg.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null){
                                    //继续删除用户
                                    User deleteUser = new User();
                                    deleteUser.setObjectId(objectId);
                                    deleteUser.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null){
                                                mUserList.remove(temp);
                                                notifyDataSetChanged();
                                                loading.dismiss();
                                            }else {
                                                loading.dismiss();
                                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    loading.dismiss();
                                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public List<User> getUserList() {
        if (mUserList == null) {
            mUserList = new ArrayList<>();
        }
        return mUserList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClickListener(int pos,List<User> myUserList);
    }
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }
}
