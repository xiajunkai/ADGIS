package com.xia.adgis.Main.Tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomdialog.BaseBottomDialog;
import com.xia.adgis.Main.Bean.InviteCode;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class UpgradeAdminDialog extends BaseBottomDialog {

    @BindView(R.id.cancel_admin)
    TextView cancel;
    @BindView(R.id.confirm_admin)
    TextView confirm;
    @BindView(R.id.invite_code)
    TextView inviteCode;
    @BindView(R.id.admin_send)
    TextView send;
    ProgressDialog progressDialog;
    //判断是否通过验证
    boolean isPass = false;
    @Override
    public int getLayoutRes() {
        return R.layout.upgrade_admin_bottom_dialog;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        //取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //发送邀请码
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInviteCode();
            }
        });
        //确认升级
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAdmin();
            }
        });
    }

    //发送邀请码
    private void requestInviteCode() {
        String invite = inviteCode.getText().toString();
        if(TextUtils.isEmpty(invite)){
            Toast.makeText(getActivity(), "邀请码为空！", Toast.LENGTH_SHORT).show();
        }else if(!invite.matches("[\\d]{4}")){
            Toast.makeText(getActivity(), "邀请码为四位", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setMessage("正在验证邀请码");
            progressDialog.show();
            BmobQuery<InviteCode> temp = new BmobQuery<>();
            temp.addWhereEqualTo("code",invite);
            temp.findObjects(new FindListener<InviteCode>() {
                @Override
                public void done(List<InviteCode> list, BmobException e) {
                    if(e == null){
                        if(list.size() != 0){
                            Toast.makeText(getActivity(), "通过验证", Toast.LENGTH_SHORT).show();
                            isPass = true;
                            progressDialog.dismiss();
                        }else {
                            Toast.makeText(getActivity(), "邀请码无效", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    //确认升级为管理员
    private void confirmAdmin(){
        String code = inviteCode.getText().toString();
        if(TextUtils.isEmpty(code)){
            Toast.makeText(getActivity(), "邀请码为空！", Toast.LENGTH_SHORT).show();
        }else if(!code.matches("[\\d]{4}")){
            Toast.makeText(getActivity(), "邀请码为四位", Toast.LENGTH_SHORT).show();
        }else if(!isPass){
            Toast.makeText(getActivity(), "未通过邀请码验证", Toast.LENGTH_SHORT).show();
        }
        //通过验证后
        if (isPass){
            progressDialog.setMessage("正在升级为管理员...");
            progressDialog.show();
            User temp = new User();
            temp.setAdmin(true);
            User user = BmobUser.getCurrentUser(User.class);
            temp.update(user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        Toast.makeText(getActivity(), "管理员升级成功！", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        dismiss();
                    }else {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public float getDimAmount() {
        return 0.5f;
    }

    @Override
    public boolean getCancelOutside() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
