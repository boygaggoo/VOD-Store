package com.cookingshow.datacenter;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class DishDataInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private int albumId = 0;
	private int dishId = 0;
	private String uploadTime = null;
	private String uploader ="";
    private String title = "";
    private String type = "";
    private String thumbUrl = "";
    private String videoUrl = "";
    private String tips = "";
    private String materials = "";
	private int viewTimes = 0;
	private int likeTimes = 0;
    private int published = 1;

	public DishDataInfo() {
		// TODO Auto-generated constructor stub
	}

    public int getViewTimes() {
        return viewTimes;
    }

    public void setViewTimes(int viewTimes) {
        this.viewTimes = viewTimes;
    }

    public int getLikeTimes() {
        return likeTimes;
    }

    public void setLikeTimes(int likeTimes) {
        this.likeTimes = likeTimes;
    }
    
    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }
    
    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
    
    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

//  @Override
  public void writeToParcel(Parcel dest, int flags) {
	  dest.writeInt(albumId);
	  dest.writeInt(dishId);
      dest.writeString(uploadTime);
      dest.writeString(uploader);
      dest.writeString(title);
      dest.writeString(type);
      dest.writeString(thumbUrl);
      dest.writeString(videoUrl);
      dest.writeString(tips);
      dest.writeString(materials);
      dest.writeInt(viewTimes);
      dest.writeInt(likeTimes);
      dest.writeInt(published);
  }
  
  public DishDataInfo(Parcel in) {
	  albumId = in.readInt();
	  dishId = in.readInt();
	  uploadTime = in.readString();
	  uploader = in.readString();
	  title = in.readString();
	  type = in.readString();
	  thumbUrl = in.readString();
	  videoUrl = in.readString();
	  tips = in.readString();
	  materials = in.readString();
	  viewTimes = in.readInt();
	  likeTimes = in.readInt();
	  published = in.readInt();
  }
  
  public static final Parcelable.Creator<DishDataInfo> CREATOR = new Parcelable.Creator<DishDataInfo>() {
      public DishDataInfo createFromParcel(Parcel in) {
          return new DishDataInfo(in);
      }

      public DishDataInfo[] newArray(int size) {
          return new DishDataInfo[size];
      }
  };
}
