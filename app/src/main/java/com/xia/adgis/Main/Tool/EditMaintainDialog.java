package com.xia.adgis.Main.Tool;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomdialog.BottomDialog;
import com.xia.adgis.Main.Activity.EditADsActivity;
import com.xia.adgis.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditMaintainDialog extends BottomDialog implements View.OnClickListener {
    @BindView(R.id.confirm_maintain)
    TextView confirm;
    @BindView(R.id.cancel_maintain)
    TextView cancel;
    @BindView(R.id.company_maintain)
    TextView company;
    @BindView(R.id.content_maintain)
    TextView content;

    public static EditMaintainDialog newInstance(String company,String content){
        EditMaintainDialog temp = new EditMaintainDialog();
        Bundle bundle = new Bundle();
        bundle.putString("company",company);
        bundle.putString("content",content);
        temp.setArguments(bundle);
        return temp;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.edit_maintain_bottom_dialog;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this,v);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        //父活动加载信息
        Bundle args = getArguments();
        if(args != null){
            company.setText(args.getString("company"));
            content.setText(args.getString("content"));
        }
    }

    @Override
    public float getDimAmount() {
        return 0.5f;
    }

    @Override
    public boolean getCancelOutside() {
        return super.getCancelOutside();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_maintain:
                dismiss();
                break;
            case R.id.confirm_maintain:
                InputData();
                break;
        }
    }

    private void InputData(){
        String adCompany = company.getText().toString();
        String adContent = content.getText().toString();
        if(TextUtils.isEmpty(adCompany)){
            Toast.makeText(getActivity(), "维护公司不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(adContent)){
            Toast.makeText(getActivity(), "维护内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EditADsActivity patent = (EditADsActivity) getActivity();
        patent.getEditMaintainCompany().setText(adCompany);
        patent.getEditContent().setText(adContent);
        dismiss();
    }
}
