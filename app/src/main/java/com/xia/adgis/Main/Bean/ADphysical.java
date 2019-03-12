package com.xia.adgis.Main.Bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;


public class ADphysical extends BmobObject implements Serializable {
    private String name;
    private String height;
    private String width;
    private String length;
    private String material;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
