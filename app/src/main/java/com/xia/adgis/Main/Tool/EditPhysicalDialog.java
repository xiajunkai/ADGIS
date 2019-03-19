package com.xia.adgis.Main.Tool;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomdialog.BottomDialog;
import com.xia.adgis.Main.Activity.EditADsActivity;
import com.xia.adgis.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditPhysicalDialog extends BottomDialog implements View.OnClickListener {

    @BindView(R.id.cancel_physical)
    TextView cancel;
    @BindView(R.id.confirm_physical)
    TextView confirm;
    @BindView(R.id.length_physical)
    EditText length;
    @BindView(R.id.width_physical)
    EditText width;
    @BindView(R.id.height_physical)
    EditText height;
    @BindView(R.id.material_physical)
    EditText material;

    public static EditPhysicalDialog newInstance(String length,String width, String height, String material) {
        EditPhysicalDialog temp = new EditPhysicalDialog();
        Bundle bundle = new Bundle();
        bundle.putString("length", length);
        bundle.putString("width", width);
        bundle.putString("height",height);
        bundle.putString("material", material);
        temp.setArguments(bundle);
        return temp;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        //父活动加载的信息
        Bundle args = getArguments();
        if(args != null){
            length.setText(args.getString("length"));
            width.setText(args.getString("width"));
            height.setText(args.getString("height"));
            material.setText(args.getString("material"));
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.edit_physical_bottom_dialog;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_physical:
                dismiss();
                break;
            case R.id.confirm_physical:
                InputData();
                break;
        }
    }

    private void InputData(){
        String adLength = length.getText().toString();
        String adWidth = width.getText().toString();
        String adHeight = height.getText().toString();
        String adMaterial = material.getText().toString();
        if(TextUtils.isEmpty(adLength)){
            Toast.makeText(getActivity(), "长度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(adWidth)){
            Toast.makeText(getActivity(), "宽度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(adHeight)){
            Toast.makeText(getActivity(), "高度不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(adMaterial)){
            Toast.makeText(getActivity(), "材料不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        EditADsActivity parent = (EditADsActivity) getActivity();
        parent.getEditLength().setText(adLength);
        parent.getEditWidth().setText(adWidth);
        parent.getEditHeight().setText(adHeight);
        parent.getEditMaterial().setText(adMaterial);
        dismiss();
    }
}
