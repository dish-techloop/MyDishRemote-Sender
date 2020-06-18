package DishTV.example.MyDishRemote.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class GlobalData {

    Context context;
    SharedPreferences sharedPreferences;
    private  String UserType,UserId,UserName,MobileNo;

    public GlobalData(Context context)
    {
        this.context=context;
        sharedPreferences=context.getSharedPreferences("login_details",Context.MODE_PRIVATE);
    }

    public String getUserId() {
        UserId=sharedPreferences.getString("UserId","");
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
        sharedPreferences.edit().putString("UserId",UserId).commit();
    }

    public String getUserName() {
        UserName=sharedPreferences.getString("UserName","");
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
        sharedPreferences.edit().putString("UserName",UserName).commit();
    }

    public String getMobileNo() {
        MobileNo=sharedPreferences.getString("LevelId1","");
        return MobileNo;
    }

    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
        sharedPreferences.edit().putString("MobileNo",MobileNo).commit();
    }

    public String getUserType() {
        UserType=sharedPreferences.getString("UserType","");
        return UserType;
    }

    public void setUserType(String UserType) {
        this.UserType = UserType;
        sharedPreferences.edit().putString("UserType",UserType).commit();
    }


    public void remobeGlobalData()
    {
        sharedPreferences.edit().clear().commit();
    }


}
