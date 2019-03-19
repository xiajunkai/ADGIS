package com.xia.adgis.Main.Bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class InviteCode extends BmobObject implements Serializable {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
