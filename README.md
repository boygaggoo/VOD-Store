# VOD-Store
The vod store which runs on the android platform mainly includes the management of vod resources and a media player. The management pulls some online video sources from the server into android database called sqlite.  Also, all the media player function is built by android sdk about media player and surfaceview. 

The app is called cookingshow. so all the viode sources in the remote server shows how to cook delicilos foods. All members can refer to the android client source code to develop the needs about UI(fragment + ViewPager + custom control) and video playback(mediaplayer + surfaceview).

explain the main fold:

volley : opensource code for android-async-http.

network: please focus on the cache which create files in the app data path(*****/data) to store some data by itself, and the size is 50M defaultly.

service: the app send http request to server to pull viode sources which are stored in the sqlite by android provider.

push: It contains some next activities, especially AlbumDetailInfo.java and AppDetailInfo.java. They describe the video playback by MediaPlayer, SurfaceView and SurfaceHolder. All the members can refer to the playback feature:play,pause,seek, zoom in, zoom out.


the follow folds are related to the UI.

navigation: it is in the home launcher page to manage the show of video source, and the data is dynamicly pulled from server, such as Recommend, Top, Share, All,MIne.

recommend: the ui show some recommend video by daily job.

top: the ui show the rank and the max count is 12.

share: it manages some video album uploaded by users;

All: it manages all the video resources from server, including home, baby, healty.

Mine: displaying user information, such as history. so further refer to the folder useinfo.

view: it relates to the custom control.


In addition, the feature of user statistics is supported by Umeng server in China which provides some sdk. 

ps: if you want to refer to the server code, please also view another repository: VOD-Store-server.


![image](https://github.com/hansonLGE/VOD-Store/blob/master/z_introduce_picture/Screenshot_2016-10-13-01.png)
![image](https://github.com/hansonLGE/VOD-Store/blob/master/z_introduce_picture/Screenshot_2016-10-13-02.png)
![image](https://github.com/hansonLGE/VOD-Store/blob/master/z_introduce_picture/Screenshot_2016-10-13-03.png)
![image](https://github.com/hansonLGE/VOD-Store/blob/master/z_introduce_picture/Screenshot_2016-10-13-04.png)
![image](https://github.com/hansonLGE/VOD-Store/blob/master/z_introduce_picture/Screenshot_2016-10-13-05.png)

