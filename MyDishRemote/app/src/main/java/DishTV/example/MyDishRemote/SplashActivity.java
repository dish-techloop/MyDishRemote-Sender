package DishTV.example.MyDishRemote;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import DishTV.example.MyDishRemote.Utility.Constants;
import DishTV.example.MyDishRemote.Utility.GlobalData;
import DishTV.example.MyDishRemote.custome.VolleySingleton;

import com.example.MyDishRemote.R;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private static String Appurl;
    private static int Appvr=0;
    GlobalData globalData;
    ProgressDialog progressDialog;
    private VolleySingleton volleySingleton;
    private Intent intent;
    private String UserType;
    ImageView rotateImage;
    DownloadManager downloadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        //myProgressBar(true,"Loading.....");

        rotateImage = (ImageView) findViewById(R.id.rotate_image);
        Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);
        rotateImage.startAnimation(startRotateAnimation);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;

        Appvr=0;
        globalData=new GlobalData(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (Appvr==0) {
                    if(globalData.getUserId().length()>0)
                    {
                        UserType=globalData.getUserType();
                        //myProgressBar(false,"");
                        rotateImage.clearAnimation();
                        intent = new Intent(getApplicationContext(), Main_Parent_Activity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        myProgressBar(false,"");
                        Intent it=new Intent(SplashActivity.this,Login.class);
                        startActivity(it);
                        finish();
                    }
                }
                else
                {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(SplashActivity.this);
                    View mview = getLayoutInflater().inflate(R.layout.update_dialog,null);
                    final Button btn_ok = (Button)mview.findViewById(R.id.btn_downloadNow);
                    alert.setView(mview);
                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                    final String DownloadUrl="http://esamiksha.myschoolsoft.com/UserUploads/eSamiksha.apk";
                    btn_ok.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public  void onClick(View view)
                        {
                            myProgressBar(false,"");
                            downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(DownloadUrl);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            Long referance  = downloadManager.enqueue(request);
                            btn_ok.setText("Downloading....");
                        }

                    });
                }
            }

        }, SPLASH_TIME_OUT);
    }




    public void myProgressBar(boolean isStart,String msg)
    {
        if (isStart)
        {
            progressDialog.setMessage(msg);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else {
            progressDialog.dismiss();
        }
    }



}
