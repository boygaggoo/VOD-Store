package com.cookingshow.service.data;

public class ShareImgInfo {
    private int imgId = 0;
	private int dishId = 0;
	private String dishName = "";
	private String uploaderTime = "";
    private String uploader = "";
    private String imgUrl = "";
    private String feeling = "";
    private int likeTimes = 0;
    private int published = 0;
    
	public ShareImgInfo() {
		// TODO Auto-generated constructor stub
	}

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getUploaderTime() {
        return uploaderTime;
    }

    public void setUploaderTime(String uploaderTime) {
        this.uploaderTime = uploaderTime;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public int getLikeTimes() {
        return likeTimes;
    }

    public void setLikeTimes(int likeTimes) {
        this.likeTimes = likeTimes;
    }

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }
}
