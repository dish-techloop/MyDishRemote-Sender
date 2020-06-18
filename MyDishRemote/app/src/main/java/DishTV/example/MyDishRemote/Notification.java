package DishTV.example.MyDishRemote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import DishTV.example.MyDishRemote.Utility.GlobalData;
import DishTV.example.MyDishRemote.custome.VolleySingleton;

import com.example.MyDishRemote.R;


public class Notification extends AppCompatActivity {
    private String notification,GoBack,ActionId;
    private TextView txt_notification;
    private Button BtnGoBack,btn_resetpayment;
    private VolleySingleton volleySingleton;
    GlobalData globalData;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notification");
        final Bundle extras = getIntent().getExtras();
        globalData = new GlobalData(Notification.this);
        txt_notification=(TextView)findViewById(R.id.lbl_notification);
        BtnGoBack=(Button)findViewById(R.id.btn_goback);
        BtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent  = new Intent(getApplicationContext(), Main_Parent_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
