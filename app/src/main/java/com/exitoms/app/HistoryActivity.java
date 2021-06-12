package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.exitoms.app.models.OrderDetails;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements UserAdaptor.Action{

    List<UserModel> userModels = new ArrayList<>(1000);
    UserAdaptor userAdaptor;
    RecyclerView userView;
    SharedPreferences sharedPreferences;
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        userView = findViewById(R.id.userView);
        this.userAdaptor = new UserAdaptor(this,this);
        userView.setAdapter(userAdaptor);
        userView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        userView.setNestedScrollingEnabled(false);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        seclobUsers();



//        RelativeLayout layout = new RelativeLayout(this);
//        ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
//        progressBar.setIndeterminate(true);
//        progressBar.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        layout.addView(progressBar,params);
//        setContentView(layout);


    }

    public void Delete(String uID)
    {
        new BottomDialog.Builder(this)
                .setTitle("Warning!")
                .setContent("Do you really want to delete this user?")
                .setNegativeText("No")
                .setPositiveText("Yes")
                .setPositiveBackgroundColorResource(R.color.danger)
                .setPositiveTextColorResource(android.R.color.white)
                //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                 .onNegative(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                            dialog.dismiss();
                    }
                })
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {

                        Toast.makeText(getApplicationContext(), uID, Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void AccountBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void Add(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, SeclobuserActivity.class);
        startActivity(intents);
    }

    public void seclobUsers()
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"users";
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
                                userModels.clear();
                                String Orders = Res.getString("users");
                                JSONArray Results = new JSONArray(Orders);
                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    UserModel userModel = new UserModel();
                                    userModel.setuID(rst.getString("id"));
                                    userModel.setID(rst.getString("token"));
                                    userModel.setName(rst.getString("name"));
                                    userModel.setCname(rst.getString("companyname"));
                                    userModel.setCnumber(rst.getString("contactnumber"));
                                    userModel.setAmount(rst.getString("amount"));
                                    userModel.setSatus(rst.getString("status"));
                                    Date date = inputFormat.parse(rst.getString("created_at"));
                                    userModel.setCdate(outputFormat.format(date));
                                    userModels.add(userModel);
                                }
                                userAdaptor.renewItems(userModels);

                            }
                            else
                                Toast.makeText(HistoryActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(HistoryActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(HistoryActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("memberid",sharedPreferences.getString("id",""));
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