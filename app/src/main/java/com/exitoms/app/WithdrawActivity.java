package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawActivity extends AppCompatActivity implements RequestAdaptor.Action{

    EditText Amount;
    TextInputLayout TDS;
    SharedPreferences sharedPreferences;
    TextView Wallet;
    double requestAmount,afterTDS,maxAmount;
    KProgressHUD Loader;
    List<RequestModel> requestModels = new ArrayList<>(1000);
    RequestAdaptor requestAdaptor;
    RecyclerView RequestView;
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        RequestView = findViewById(R.id.RequestView);
        this.requestAdaptor = new RequestAdaptor(this,this);
        RequestView.setAdapter(requestAdaptor);
        RequestView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        RequestView.setNestedScrollingEnabled(false);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        seclobUsers();

        Amount = findViewById(R.id.with_amount_e);
        TDS = findViewById(R.id.with_amount);
        Wallet = findViewById(R.id.with_wallet_balance);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);

        maxAmount = Double.parseDouble(sharedPreferences.getString("wallet",""));
        Wallet.setText(maxAmount+"");

        Amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    requestAmount = Double.parseDouble(s.toString());
                }catch (Exception e){}

                if(s.length()>0)
                {
                    if(requestAmount>=500 && requestAmount<=maxAmount)
                    {
                        afterTDS = requestAmount-(requestAmount/100)*10;
                        TDS.setHelperText("After TDS amount ₹"+afterTDS);
                        TDS.setError("");
                    }
                    else
                    {
                        TDS.setError("Invalid request amount!");
                    }
                }else
                {
                    TDS.setHelperText("");
                    TDS.setError("");
                }


            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void Delete(String ID)
    {}

    public void RequestBtn(View view)
    {
        if(requestAmount>0 && requestAmount<=maxAmount)
        withdrawRequest();
        else
            Toast.makeText(this, "Invalid amount!", Toast.LENGTH_SHORT).show();
    }

    public void withdrawRequest()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"withdrawrequest";
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

                                Toast.makeText(WithdrawActivity.this, msg, Toast.LENGTH_SHORT).show();
                                seclobUsers();
                            }
                            else
                                Toast.makeText(WithdrawActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(WithdrawActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(WithdrawActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
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
                params.put("wallet_balence",sharedPreferences.getString("wallet",""));
                params.put("amount",Amount.getText().toString());
                params.put("dtd_amount",afterTDS+"");



                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    public void seclobUsers()
    {
        Loader.show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"withdrawrequest?memberid="+sharedPreferences.getString("id","");
        Log.e("URLLLLLL",URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
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
                                requestModels.clear();
                                String Orders = Res.getString("wrequests");
                                JSONArray Results = new JSONArray(Orders);
                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    RequestModel requestModel = new RequestModel();
                                    requestModel.setID(rst.getString("id"));
                                    requestModel.setRAmt(rst.getString("amount"));
                                    requestModel.setWAmt(rst.getString("wallet_amount"));
                                    requestModel.setTds(rst.getString("dtd_amount"));
                                    requestModel.setCmt(rst.getString("admin_comments"));
                                    requestModel.setSatus(rst.getString("status"));
                                    requestModel.setRdate(rst.getString("created_at"));
                                    requestModels.add(requestModel);
                                }
                                requestAdaptor.renewItems(requestModels);

                            }
                            else
                                Toast.makeText(WithdrawActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(WithdrawActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(WithdrawActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
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