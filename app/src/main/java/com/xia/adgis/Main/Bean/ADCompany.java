package com.xia.adgis.Main.Bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class ADCompany extends BmobObject implements Serializable {
    private String name;
    private String Hoder;
    private String Designer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHoder() {
        return Hoder;
    }

    public void setHoder(String hoder) {
        Hoder = hoder;
    }

    public String getDesigner() {
        return Designer;
    }

    public void setDesigner(String designer) {
        Designer = designer;
    }
}
