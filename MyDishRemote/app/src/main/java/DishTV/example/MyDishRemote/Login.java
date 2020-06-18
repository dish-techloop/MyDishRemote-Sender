package DishTV.example.MyDishRemote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class Login extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button BtnLogin;
    ProgressDialog progressDialog;
    private VolleySingleton volleySingleton;
    private Intent intent;
    private String UserType;
    private static String Appurl;
    private static int Appvr=0;
    private int backButtonCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Name =(EditText)findViewById(R.id.txtUserName);
        Password=(EditText)findViewById(R.id.txtPassword);
        BtnLogin=(Button)findViewById(R.id.btnLogin);
        volleySingleton = VolleySingleton.getInstance(getApplicationContext());

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        //Check_AppVersion(versionNumber,versionName);

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  UserName21 = Name.getText().toString();
                String  Password21= Password.getText().toString();
                //validate form
                if(validateLogin(UserName21, Password21)){
                    myProgressBar(true,"Loading.....");
                    attemptLogin(UserName21, Password21);
                }
            }
        });
    }


    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Name.setError("Enter UserName");
            return false;

        }
        if(password == null || password.trim().length() == 0){
            Password.setError("Enter Password");
            return false;
        }
        return true;
    }

   /* private void attemptLogin(String UserName,String Password) {
        final JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("UserName",UserName);
            requestObject.put("Password",Password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.VerifyUrl, requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("Status").equals("True")){

                                JSONArray jsonArray = new JSONArray();
                                try {
                                    jsonArray = response.getJSONArray("Data");
                                    JSONObject objJson = jsonArray.getJSONObject(0);
                                    GlobalData gd = new GlobalData(Login.this);
                                    gd.setUserId(objJson.get("UserId").toString());
                                    gd.setUserName(objJson.get("UserName").toString());
                                    gd.setUserType(objJson.get("UserType").toString());
                                    gd.setMobileNo(objJson.get("MobileNo").toString());
                                    UserType=objJson.get("UserType").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(),"Welcome to e Samiksha",Toast.LENGTH_LONG).show();
                                Log.d("responseA", response.toString());
                                switch(UserType) {
                                    case "Admin":
                                        intent = new Intent(getApplicationContext(),Main_Parent_Activity.class);
                                        break;
                                }
                                try {
                                    intent.putExtra("id",response.getString("data"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);

                            }
                            else{
                                myProgressBar(false,"");
                                Toast.makeText(getApplicationContext(),"Invalid UserName or Password",Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseE", error.toString());
            }
        });
        //  requestQueue.add(jsonObjectRequest);
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }*/


    private void attemptLogin(String UserName,String Password) {
        if (UserName.equals("admin") && Password.equals("admin")){

            GlobalData gd = new GlobalData(Login.this);
            gd.setUserId("12345");
            gd.setUserName("admin");
            gd.setUserType("admin");
            gd.setMobileNo("9999999999");
            Toast.makeText(getApplicationContext(),"Welcome to my dish remote.",Toast.LENGTH_LONG).show();
            intent = new Intent(getApplicationContext(),Main_Parent_Activity.class);
            intent.putExtra("id","rantosh21");
            startActivity(intent);
        }
        else{
            myProgressBar(false,"");
            Toast.makeText(getApplicationContext(),"Invalid UserName or Password",Toast.LENGTH_LONG).show();

        }
    }

    private void Check_AppVersion(int versionCode,String versionName) {
        final JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("versionCode",versionCode);
            requestObject.put("versionName",versionName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constants.AppVersion, requestObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("Status").equals("True")){
                                Appurl = response.getString("value1");
                                Appvr=21;
                                Log.d("responseA", response.toString());
                            }
                            else{
                                Appvr=0;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("responseE", error.toString());
                    }
                });
        volleySingleton.addToRequestQueue(jsonObjectRequest);
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

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }


}
