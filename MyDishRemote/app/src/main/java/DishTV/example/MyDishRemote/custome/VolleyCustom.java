package DishTV.example.MyDishRemote.custome;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class VolleyCustom implements Response.ErrorListener {

    private Context context;

    private VolleyCustom(){
    }

    public VolleyCustom(Context context){
        this.context=context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        NetworkResponse networkResponse = error.networkResponse;
        Log.d("onErrorResponse",""+networkResponse.statusCode);


        onError(error);
    }

    public void onError(VolleyError error){
        Log.d("error", "VolleyError");
    };

}
