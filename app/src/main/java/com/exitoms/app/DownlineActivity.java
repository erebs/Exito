package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownlineActivity extends AppCompatActivity implements ApprovedAdaptor.Action {

    List<ApprovedModel> approvedModels = new ArrayList<>(1000);
    ApprovedAdaptor approvedAdaptor;
    RecyclerView approvedView;
    SharedPreferences sharedPreferences;
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
    KProgressHUD Loader;
    String memberID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downline);

        try {
            Intent intent = getIntent();
            memberID=intent.getStringExtra("memberID");
        }catch (Exception e)
        {
        }

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        approvedView = findViewById(R.id.downloneView);
        this.approvedAdaptor = new ApprovedAdaptor(this,this);
        approvedView.setAdapter(approvedAdaptor);
        approvedView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        approvedView.setNestedScrollingEnabled(false);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        approvedMembers();
    }

    public void Delete(String uID)
    {

    }

    public void Addmem(View view)
    {
        Intent intents = new Intent(DownlineActivity.this, AddmemActivity.class);
        startActivity(intents);
    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    public void approvedMembers()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"members";
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
                                approvedModels.clear();
                                String Orders = Res.getString("members");
                                JSONArray Results = new JSONArray(Orders);
                                if(Results.length()<1)
                                    findViewById(R.id.nomemDownline).setVisibility(View.VISIBLE);
                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    ApprovedModel approvedModel = new ApprovedModel();
                                    approvedModel.setmID(rst.getString("uid"));
                                    approvedModel.setName(rst.getString("name"));
                                    approvedModel.setShowDownLine("false");
                                    approvedModel.setTxnID(rst.getString("trans_id"));
                                    approvedModel.setSatus(rst.getString("status"));
                                    Date date = inputFormat.parse(rst.getString("created_at"));
                                    approvedModel.setCdate(outputFormat.format(date));
                                    approvedModels.add(approvedModel);
                                }
                                approvedAdaptor.renewItems(approvedModels);

                            }
                            else
                                Toast.makeText(DownlineActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(DownlineActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DownlineActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("memberid",memberID);
                params.put("limit","200");
                params.put("offset","");

                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

}