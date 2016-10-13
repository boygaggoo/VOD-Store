package com.cookingshow.datacenter;

public class ShareListItem {
    private int shareId;
    private String dishName;
	private String uploaderTime;
    private String uploader;
    private String url;
    private String feeling;
    private int likeTimes;
    private int published;

	public ShareListItem() {
		// TODO Auto-generated constructor stub
		shareId = 0;
		dishName = "";
		uploaderTime = "";
		uploader = "";
		url = "";
		feeling = "";
		likeTimes = 0;
		published = 1;
	}

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
