package com.xia.adgis.Main.Bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;


public class ADmaintain extends BmobObject implements Serializable {
    private String name;
    private String company;
    private String context;
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
