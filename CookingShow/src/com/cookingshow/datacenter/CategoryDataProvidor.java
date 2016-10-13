package com.cookingshow.datacenter;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

public class CategoryDataProvidor {


    
	public CategoryDataProvidor() {
		// TODO Auto-generated constructor stub
 
	}

    public AppListResponse getAppListFromNetwork(Context context, int si, int c, String listType, String category, String code) {
        AppListResponse res = new AppListResponse();
        List<AppInfo> mAppData = new ArrayList<AppInfo>();
        
        if(code.equals("home")) {
            if(si <= 12) {
                AppInfo data = new AppInfo();
                data.setName("home1");
                data.setDownloadCount("3");
                mAppData.add(data);
                
                AppInfo data2 = new AppInfo();
                data2.setName("home2");
                data2.setDownloadCount("3");
                mAppData.add(data2);
                
                AppInfo data3 = new AppInfo();
                data3.setName("home3");
                data3.setDownloadCount("3");
                mAppData.add(data3);
                
                AppInfo data4 = new AppInfo();
                data4.setName("home4");
                data4.setDownloadCount("5");
                mAppData.add(data4);
                
                AppInfo data5 = new AppInfo();
                data5.setName("home5");
                data5.setDownloadCount("3");
                mAppData.add(data5);
                
                AppInfo data6 = new AppInfo();
                data6.setName("home6");
                data6.setDownloadCount("3");
                mAppData.add(data6);
                
                AppInfo data7 = new AppInfo();
                data7.setName("home7");
                data7.setDownloadCount("3");
                mAppData.add(data7);
                
                AppInfo data8 = new AppInfo();
                data8.setName("home8");
                data8.setDownloadCount("3");
                mAppData.add(data8);
                
                AppInfo data9 = new AppInfo();
                data9.setName("home9");
                data9.setDownloadCount("3");
                mAppData.add(data9);
                
                AppInfo data10 = new AppInfo();
                data10.setName("home10");
                data10.setDownloadCount("3");
                mAppData.add(data10);
                
                AppInfo data11 = new AppInfo();
                data11.setName("home11");
                data11.setDownloadCount("3");
                mAppData.add(data11);     
                
                AppInfo data12 = new AppInfo();
                data12.setName("home12");
                data12.setDownloadCount("3");
                mAppData.add(data12);
            }
            else {
                AppInfo data13 = new AppInfo();
                data13.setName("home13");
                data13.setDownloadCount("3");
                mAppData.add(data13);   
                
                AppInfo data14 = new AppInfo();
                data14.setName("home14");
                data14.setDownloadCount("3");
                mAppData.add(data14); 
            }
            
            res.setAllCount(14);
            //res.setmApplications(mAppData);
            res.setmIsSuccess(true);
            mAppData = null;
        }
        else if(code.equals("healthy")) {
            if(si <= 12) {
                AppInfo data = new AppInfo();
                data.setName("shared1");
                data.setDownloadCount("3");
                mAppData.add(data);
                
                AppInfo data2 = new AppInfo();
                data2.setName("shared2");
                data2.setDownloadCount("3");
                mAppData.add(data2);
                
                AppInfo data3 = new AppInfo();
                data3.setName("shared3");
                data3.setDownloadCount("3");
                mAppData.add(data3);
                
                AppInfo data4 = new AppInfo();
                data4.setName("shared4");
                data4.setDownloadCount("5");
                mAppData.add(data4);
                
                AppInfo data5 = new AppInfo();
                data5.setName("shared5");
                data5.setDownloadCount("3");
                mAppData.add(data5);
                
                AppInfo data6 = new AppInfo();
                data6.setName("shared6");
                data6.setDownloadCount("3");
                mAppData.add(data6);
                
                AppInfo data7 = new AppInfo();
                data7.setName("shared7");
                data7.setDownloadCount("3");
                mAppData.add(data7);
                
                AppInfo data8 = new AppInfo();
                data8.setName("shared8");
                data8.setDownloadCount("3");
                mAppData.add(data8);
                
                AppInfo data9 = new AppInfo();
                data9.setName("shared9");
                data9.setDownloadCount("3");
                mAppData.add(data9);
                
                AppInfo data10 = new AppInfo();
                data10.setName("shared10");
                data10.setDownloadCount("3");
                mAppData.add(data10);
                
                AppInfo data11 = new AppInfo();
                data11.setName("shared11");
                data11.setDownloadCount("3");
                mAppData.add(data11);     
                
                AppInfo data12 = new AppInfo();
                data12.setName("shared12");
                data12.setDownloadCount("3");
                mAppData.add(data12);
            }
            else if(si <= 24 && si > 12){
                AppInfo data13 = new AppInfo();
                data13.setName("shared13");
                data13.setDownloadCount("3");
                mAppData.add(data13);
                
                AppInfo data14 = new AppInfo();
                data14.setName("shared14");
                data14.setDownloadCount("3");
                mAppData.add(data14); 
                
                AppInfo data15 = new AppInfo();
                data15.setName("shared15");
                data15.setDownloadCount("3");
                mAppData.add(data15); 
                
                AppInfo data16 = new AppInfo();
                data16.setName("shared16");
                data16.setDownloadCount("3");
                mAppData.add(data16); 
                
                AppInfo data17 = new AppInfo();
                data17.setName("shared17");
                data17.setDownloadCount("3");
                mAppData.add(data17); 
                
                AppInfo data18 = new AppInfo();
                data18.setName("shared18");
                data18.setDownloadCount("3");
                mAppData.add(data18); 
                
                AppInfo data19 = new AppInfo();
                data19.setName("shared19");
                data19.setDownloadCount("3");
                mAppData.add(data19); 
                
                AppInfo data20 = new AppInfo();
                data20.setName("shared20");
                data20.setDownloadCount("3");
                mAppData.add(data20); 
                
                AppInfo data21 = new AppInfo();
                data21.setName("shared21");
                data21.setDownloadCount("3");
                mAppData.add(data21); 
                
                AppInfo data22 = new AppInfo();
                data22.setName("shared22");
                data22.setDownloadCount("3");
                mAppData.add(data22); 
                
                AppInfo data23 = new AppInfo();
                data23.setName("shared23");
                data23.setDownloadCount("3");
                mAppData.add(data23); 
                
                AppInfo data24 = new AppInfo();
                data24.setName("shared24");
                data24.setDownloadCount("3");
                mAppData.add(data24); 
            }
            else {
                AppInfo data25 = new AppInfo();
                data25.setName("shared25");
                data25.setDownloadCount("3");
                mAppData.add(data25); 
                
                AppInfo data26 = new AppInfo();
                data26.setName("shared26");
                data26.setDownloadCount("3");
                mAppData.add(data26);           	
            }
            
            res.setAllCount(26);
            //res.setmApplications(mAppData);
            res.setmIsSuccess(true);
            mAppData = null;
        }
        else if(code.equals("share")) {
            if(si <= 12) {
                AppInfo data = new AppInfo();
                data.setName("other1");
                data.setDownloadCount("3");
                mAppData.add(data);
                
                AppInfo data2 = new AppInfo();
                data2.setName("other2");
                data2.setDownloadCount("3");
                mAppData.add(data2);
                
                AppInfo data3 = new AppInfo();
                data3.setName("other3");
                data3.setDownloadCount("3");
                mAppData.add(data3);
                
                AppInfo data4 = new AppInfo();
                data4.setName("other4");
                data4.setDownloadCount("5");
                mAppData.add(data4);
                
                AppInfo data5 = new AppInfo();
                data5.setName("other5");
                data5.setDownloadCount("3");
                mAppData.add(data5);
                
                AppInfo data6 = new AppInfo();
                data6.setName("other6");
                data6.setDownloadCount("3");
                mAppData.add(data6);
                
                AppInfo data7 = new AppInfo();
                data7.setName("other7");
                data7.setDownloadCount("3");
                mAppData.add(data7);
                
                AppInfo data8 = new AppInfo();
                data8.setName("other8");
                data8.setDownloadCount("3");
                mAppData.add(data8);
                
                AppInfo data9 = new AppInfo();
                data9.setName("other9");
                data9.setDownloadCount("3");
                mAppData.add(data9);
                
                AppInfo data10 = new AppInfo();
                data10.setName("other10");
                data10.setDownloadCount("3");
                mAppData.add(data10);
                
                AppInfo data11 = new AppInfo();
                data11.setName("other11");
                data11.setDownloadCount("3");
                mAppData.add(data11);     
                
                AppInfo data12 = new AppInfo();
                data12.setName("other12");
                data12.setDownloadCount("3");
                mAppData.add(data12);
            }
            else {
                AppInfo data13 = new AppInfo();
                data13.setName("other13");
                data13.setDownloadCount("3");
                mAppData.add(data13);   
            }
            
            res.setAllCount(13);
            //res.setmApplications(mAppData);
            res.setmIsSuccess(true);
            mAppData = null;
        }

        

        
        return res;
    }
}
