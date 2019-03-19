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


public class EditCompanyDialog extends BottomDialog implements View.OnClickListener {

    @BindView(R.id.confirm_company)
    TextView confirm;
    @BindView(R.id.cancel_company)
    TextView cancel;
    @BindView(R.id.designer_company)
    TextView designer;
    @BindView(R.id.holder_company)
    TextView holder;

    public static EditCompanyDialog newInstance(String designer,String holder){
        EditCompanyDialog temp = new EditCompanyDialog();
        Bundle bundle = new Bundle();
        bundle.putString("designer",designer);
        bundle.putString("holder",holder);
        temp.setArguments(bundle);
        return temp;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.edit_company_bottom_dialog;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this,v);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        //父活动加载信息
        Bundle args = getArguments();
        if(args != null){
            designer.setText(args.getString("designer"));
            holder.setText(args.getString("holder"));
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
            case R.id.cancel_company:
                dismiss();
                break;
            case R.id.confirm_company:
                InputData();
                break;
        }
    }

    private void InputData(){
        String adDesigner = designer.getText().toString();
        String adHolder = holder.getText().toString();
        if(TextUtils.isEmpty(adDesigner)){
            Toast.makeText(getActivity(), "设计者不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(adHolder)){
            Toast.makeText(getActivity(), "拥有者不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EditADsActivity patent = (EditADsActivity) getActivity();
        patent.getEditDesigner().setText(adDesigner);
        patent.getEditHolder().setText(adHolder);
        dismiss();
    }
}
