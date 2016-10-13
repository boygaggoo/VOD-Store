package com.cookingshow.datacenter;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String averageStar;
    private String developerId;
    private String developerName;
    private String discount;
    private String fState;
    private String hState;
    private String iconAddr;
    private String ispay;
    private String lState;
    private String name;
    private String packageName;
    private String price;
    private String publishDate;
    private String size;
    private String vState;
    private String oState;
    private String version;
    private String versioncode;
    private String oldVersion;
    private String oldVersionCode;
    private boolean appLocation;
    private String appStatus;
    private String fileDownloadUrl;
    private String appId;
    private int progress;
    private int state;
    private long appLocalDate;
    //private Drawable iconDrawable;
    private String apptype;
    private String pinyin;
    private boolean isSystemApp;
    private String signture;
    private String from;
    private String catTypeId;
    private String downloadCount;
    private String wifiStatus;
    private String lmd5;
    private String tmd5;
    private long patchSize;
    private int isSmart;
    private String apkFilePath;
    private String definition;
    private int supportHandShank;
    private int supportAirMouse;
    private int supportFiveKey;

//    "suportHandShank":0,"suportAirMouse":0,"suportFiveKey":0

    public AppInfo() {
        averageStar = "";
        developerId = "";
        developerName = "";
        discount = "";
        fState = "";
        hState = "";
        iconAddr = "";
        ispay = "";
        lState = "";
        name = "";
        packageName = "";
        price = "";
        publishDate = "";
        size = "";
        apptype = "";
        vState = "";
        version = "";
        versioncode = "";
        pinyin = "";
        isSystemApp = false;
        signture = "";
        from = "";
        catTypeId = "";
        oState = "";
        downloadCount = "";
        wifiStatus = "2";
        lmd5 = "";
        tmd5 = "";
        patchSize = 0l;
        isSmart = -1;
        apkFilePath = "";
        definition = "";
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getAverageStar() {
        return averageStar;
    }

    public void setAverageStar(String averageStar) {
        this.averageStar = averageStar;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getfState() {
        return fState;
    }

    public void setfState(String fState) {
        this.fState = fState;
    }

    public String gethState() {
        return hState;
    }

    public void sethState(String hState) {
        this.hState = hState;
    }

    public String getIconAddr() {
        return iconAddr;
    }

    public void setIconAddr(String iconAddr) {
        this.iconAddr = iconAddr;
    }

    public String getIspay() {
        return ispay;
    }

    public void setIspay(String ispay) {
        this.ispay = ispay;
    }

    public String getlState() {
        return lState;
    }

    public void setlState(String lState) {
        this.lState = lState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getvState() {
        return vState;
    }

    public void setvState(String vState) {
        this.vState = vState;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getOldVersionCode() {
        return oldVersionCode;
    }

    public void setOldVersionCode(String oldVersionCode) {
        this.oldVersionCode = oldVersionCode;
    }

    public boolean isAppLocation() {
        return appLocation;
    }

    public void setAppLocation(boolean appLocation) {
        this.appLocation = appLocation;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getAppLocalDate() {
        return appLocalDate;
    }

    public void setAppLocalDate(long appLocalDate) {
        this.appLocalDate = appLocalDate;
    }
/*
    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }
*/
    public String getApptype() {
        return apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }

    public String getSignture() {
        return signture;
    }

    public void setSignture(String signture) {
        this.signture = signture;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCatTypeId() {
        return catTypeId;
    }

    public void setCatTypeId(String catTypeId) {
        this.catTypeId = catTypeId;
    }

    public String getoState() {
        return oState;
    }

    public void setoState(String oState) {
        this.oState = oState;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(String wifiStatus) {
        this.wifiStatus = wifiStatus;
    }

    public String getLmd5() {
        return lmd5;
    }

    public void setLmd5(String lmd5) {
        this.lmd5 = lmd5;
    }

    public String getTmd5() {
        return tmd5;
    }

    public void setTmd5(String tmd5) {
        this.tmd5 = tmd5;
    }

    public long getPatchSize() {
        return patchSize;
    }

    public void setPatchSize(long patchSize) {
        this.patchSize = patchSize;
    }

    public int getIsSmart() {
        return isSmart;
    }

    public void setIsSmart(int isSmart) {
        this.isSmart = isSmart;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public int getSupportHandShank() {
        return supportHandShank;
    }

    public void setSupportHandShank(int supportHandShank) {
        this.supportHandShank = supportHandShank;
    }

    public int getSupportAirMouse() {
        return supportAirMouse;
    }

    public void setSupportAirMouse(int supportAirMouse) {
        this.supportAirMouse = supportAirMouse;
    }

    public int getSupportFiveKey() {
        return supportFiveKey;
    }

    public void setSupportFiveKey(int supportFiveKey) {
        this.supportFiveKey = supportFiveKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof AppInfo) {
        	AppInfo app = (AppInfo) o;
            if (packageName.equals(app.getPackageName())) {
                if (versioncode.equals(app.getVersioncode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (packageName + versioncode).hashCode();
    }

    @Override
    public String toString() {
        return "Application{" +
                "developerName='" + developerName + '\'' +
                ", iconAddr='" + iconAddr + '\'' +
                ", lState='" + lState + '\'' +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", size='" + size + '\'' +
                ", version='" + version + '\'' +
                ", versioncode='" + versioncode + '\'' +
                ", oldVersion='" + oldVersion + '\'' +
                ", oldVersionCode='" + oldVersionCode + '\'' +
                ", fileDownloadUrl='" + fileDownloadUrl + '\'' +
                ", apptype='" + apptype + '\'' +
                ", supportHandShank=" + supportHandShank +
                ", supportAirMouse=" + supportAirMouse +
                ", supportFiveKey=" + supportFiveKey +
                '}';
    }

    //    @Override
    public int describeContents() {
        return 0;
    }

//    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(averageStar);
        dest.writeString(developerId);
        dest.writeString(developerName);
        dest.writeString(discount);
        dest.writeString(fState);
        dest.writeString(hState);
        dest.writeString(iconAddr);
        dest.writeString(ispay);
        dest.writeString(lState);
        dest.writeString(name);
        dest.writeString(packageName);
        dest.writeString(price);
        dest.writeString(publishDate);
        dest.writeString(size);
        dest.writeString(vState);
        dest.writeString(oState);
        dest.writeString(version);
        dest.writeString(versioncode);
        dest.writeString(oldVersion);
        dest.writeString(oldVersionCode);
        if (appLocation) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(appStatus);
        dest.writeString(fileDownloadUrl);
        dest.writeString(appId);
        dest.writeInt(progress);
        dest.writeInt(state);
        dest.writeLong(appLocalDate);
//        dest.writeString(iconDrawable);
        dest.writeString(apptype);
        dest.writeString(pinyin);
        if (isSystemApp) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(signture);
        dest.writeString(from);
        dest.writeString(catTypeId);
        dest.writeString(downloadCount);
        dest.writeString(wifiStatus);
        dest.writeString(lmd5);
        dest.writeString(tmd5);
        dest.writeLong(patchSize);
        dest.writeInt(isSmart);
        dest.writeString(apkFilePath);
        dest.writeString(definition);
        dest.writeInt(supportHandShank);
        dest.writeInt(supportAirMouse);
        dest.writeInt(supportFiveKey);
    }

    public AppInfo(Parcel in) {
        averageStar = in.readString();
        developerId = in.readString();
        developerName = in.readString();
        discount = in.readString();
        fState = in.readString();
        hState = in.readString();
        iconAddr = in.readString();
        ispay = in.readString();
        lState = in.readString();
        name = in.readString();
        packageName = in.readString();
        price = in.readString();
        publishDate = in.readString();
        size = in.readString();
        vState = in.readString();
        oState = in.readString();
        version = in.readString();
        versioncode = in.readString();
        oldVersion = in.readString();
        oldVersionCode = in.readString();
        appLocation = true;
        if (in.readInt() == 0) {
            appLocation = false;
        }
        appStatus = in.readString();
        fileDownloadUrl = in.readString();
        appId = in.readString();
        progress = in.readInt();
        state = in.readInt();
        appLocalDate = in.readLong();
        apptype = in.readString();
        pinyin = in.readString();
        isSystemApp = true;
        if (in.readInt() == 0) {
            isSystemApp = false;
        }
        signture = in.readString();
        from = in.readString();
        catTypeId = in.readString();
        downloadCount = in.readString();
        wifiStatus = in.readString();
        lmd5 = in.readString();
        tmd5 = in.readString();
        patchSize = in.readLong();
        isSmart = in.readInt();
        apkFilePath = in.readString();
        definition = in.readString();
        supportHandShank = in.readInt();
        supportAirMouse = in.readInt();
        supportFiveKey = in.readInt();
    }

    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

}
