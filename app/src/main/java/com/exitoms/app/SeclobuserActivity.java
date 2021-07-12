package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class SeclobuserActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener{

    EditText asu_name_e,asu_cname_e,asu_stype_e,asu_phone_e,asu_address_e,asu_wphone_e,asu_email_e,asu_fb_e,asu_inst_e,asu_amt_e;
    SharedPreferences sharedPreferences;
    ModalBottomSheetDialog dialog;
    KProgressHUD Loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seclobuser);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);


        asu_name_e = findViewById(R.id.asu_name_e);
        asu_cname_e = findViewById(R.id.asu_cname_e);
        asu_stype_e = findViewById(R.id.asu_stype_e);
        asu_phone_e = findViewById(R.id.asu_phone_e);
        asu_address_e = findViewById(R.id.asu_address_e);
        asu_wphone_e = findViewById(R.id.asu_wphone_e);
        asu_email_e = findViewById(R.id.asu_email_e);
        asu_fb_e = findViewById(R.id.asu_fb_e);
        asu_inst_e = findViewById(R.id.asu_inst_e);
        asu_amt_e = findViewById(R.id.asu_amt_e);

        dialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Select a Service")
                .add(R.menu.service_type)
                .build();


    }

    @Override
    public void onItemSelected(String tag, Item item) {
        asu_stype_e.setText(item.getTitle().toString());
        dialog.dismiss();
    }

    public void Submit(View view)
    {
        if(asu_name_e.length()>2 && asu_cname_e.length()>2 && asu_phone_e.length()==10 && asu_wphone_e.length()==10)
        RegisterMenberApi();
        else
            Toast.makeText(this, "Some required fields are missing!", Toast.LENGTH_SHORT).show();

    }

    public void SelectType(View view)
    {
            dialog.show(getSupportFragmentManager(), "BankDetails");
    }

    public void RegisterMenberApi()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"users/add";
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
                                Toast.makeText(SeclobuserActivity.this, "New User added successfully.", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            else
                                Toast.makeText(SeclobuserActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(SeclobuserActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SeclobuserActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name",asu_name_e.getText().toString());
                params.put("companyname",asu_cname_e.getText().toString());
                params.put("contactnumber",asu_phone_e.getText().toString());
                params.put("address",asu_address_e.getText().toString());
                params.put("whatsappnumber",asu_wphone_e.getText().toString());
                params.put("email",asu_email_e.getText().toString());
                params.put("facebook",asu_fb_e.getText().toString());
                params.put("instagram",asu_inst_e.getText().toString());
                params.put("category",asu_stype_e.getText().toString());
                params.put("member_id",sharedPreferences.getString("id",""));
                params.put("amount",asu_amt_e.getText().toString());
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

}