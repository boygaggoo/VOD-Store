package com.cookingshow.datacenter;

import java.io.Serializable;

public class PageContent implements Serializable{
    private int id;
    private String uploader;
    private String name;
    private String thumbUrl;
    private String videoUrl;
    private String tips;
    private String materials;
    private int position;

    public PageContent() {
        id = 0;
        uploader = "";
        name = "";
        thumbUrl = "";
        videoUrl = "";
        tips = "";
        materials = "";
        position = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "PageContent5{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", uploader='" + uploader + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", tips='" + tips + '\'' +
                ", materials='" + materials + '\'' +
                ", position=" + position +
                '}';
    }
}
