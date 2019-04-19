package com.xia.adgis.Main.Bean;

import cn.bmob.v3.BmobObject;

import java.io.Serializable;


public class Messages extends BmobObject implements Serializable {
    private String adName;
    private String userName;
    private String content;
    public boolean isSelect;

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
