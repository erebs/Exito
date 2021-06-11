package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccdetailsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText Name,Mobile,Email,MemberID,Pan,Aadhaar,Dist,State,Pincode,Address;
    TextView editSave;
    Drawable save,edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accdetails);

        MemberID=findViewById(R.id.acd_memberid_e);
        Name=findViewById(R.id.acd_name_e);
        Mobile=findViewById(R.id.acd_mobile_e);
        Email=findViewById(R.id.acd_email_e);
        Pan=findViewById(R.id.acd_pan_e);
        Aadhaar=findViewById(R.id.acd_aadhaar_e);
        Address=findViewById(R.id.acd_address_e);
        Pincode=findViewById(R.id.acd_pincode_e);
        State=findViewById(R.id.acd_state_e);
        Dist=findViewById(R.id.acd_dist_e);
        editSave = findViewById(R.id.editSave_acd);
        save = getResources().getDrawable(R.drawable.outline_save_24);
        edit = getResources().getDrawable(R.drawable.outline_edit_24);

        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        Name.setText(sharedPreferences.getString("name",""));
        MemberID.setText(sharedPreferences.getString("mid",""));
        Mobile.setText(sharedPreferences.getString("phone",""));
        Email.setText(sharedPreferences.getString("email",""));
        Pan.setText(sharedPreferences.getString("pan",""));
        Aadhaar.setText(sharedPreferences.getString("aadhaar",""));
        Pincode.setText(sharedPreferences.getString("pincode",""));
        State.setText(sharedPreferences.getString("state",""));
        Dist.setText(sharedPreferences.getString("district",""));
        Address.setText(sharedPreferences.getString("address",""));

    }

    public void editAddress(View view) {
        if (Address.isEnabled()) {
            updateAddress();
        } else
        {
            Address.setEnabled(true);
            editSave.setCompoundDrawablesWithIntrinsicBounds(save,null,null,null);
            editSave.setText("Save");
        }
    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    public void updateAddress()
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Updating...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"member/update/"+sharedPreferences.getString("id","");
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

                                Address.setEnabled(false);
                                editSave.setCompoundDrawablesWithIntrinsicBounds(edit, null, null, null);
                                editSave.setText("Edit");
                                Toast.makeText(getApplicationContext(), "Your address updated successfully...", Toast.LENGTH_SHORT).show();

                            }
                            else
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name",Name.getText().toString());
                params.put("phone",Mobile.getText().toString());
                params.put("email",Email.getText().toString());
                params.put("address",Address.getText().toString());
                params.put("aadhaar",Aadhaar.getText().toString());
                params.put("pan",Pan.getText().toString());
                params.put("pincode",Pincode.getText().toString());
                params.put("state",State.getText().toString());
                params.put("district",Dist.getText().toString());

                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

}