package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.exitoms.app.adaptors.OrderList;
import com.exitoms.app.models.OrderDetails;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreActivity extends AppCompatActivity {

    List<NotifyModel> notifyModels = new ArrayList<>(1000);
    private NotifyAdaptor notifyAdaptor;
    RecyclerView NotifyView;
    KProgressHUD Loader;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        NotifyView = findViewById(R.id.notify_view);
        notifyAdaptor = new NotifyAdaptor(getApplication());
        NotifyView.setAdapter(notifyAdaptor);
        NotifyView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        NotifyView.setNestedScrollingEnabled(false);

        getNotifications();

    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(MoreActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void AccountBtn(View view)
    {
        Intent intents = new Intent(MoreActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(MoreActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }


    public void getNotifications()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"notifications";
        Log.e("URLLLLLL",URL);
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Loader.dismiss();
                        Log.i("VOLLEYES", response);
                        try {

                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {
                                notifyModels.clear();
                                String Orders = Res.getString("notifications");
                                JSONArray Results = new JSONArray(Orders);

                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    NotifyModel notifyModel = new NotifyModel();
                                    notifyModel.setTitle(rst.getString("title"));
                                    notifyModel.setContent(rst.getString("sub_title"));
                                    notifyModel.setDate(rst.getString("created_at"));
                                    notifyModels.add(notifyModel);
                                }
                                notifyAdaptor.renewItems(notifyModels);

                            }
                            else
                                Toast.makeText(MoreActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(MoreActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null)
                        {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();
                            Toast.makeText(MoreActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("limit","200");

                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

}