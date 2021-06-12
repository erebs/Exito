package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class AddmemActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener{

    EditText Package, Gender, Name, Mobile, Email, Aadhaar, Pan, Pincode, Dist, State, Address,TxnID, GstNumber;
    SharedPreferences sharedPreferences;
    ModalBottomSheetDialog dialog;
    KProgressHUD Loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmem);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        Package = findViewById(R.id.aem_package_e);
        Gender = findViewById(R.id.aem_gender_e);
        Name = findViewById(R.id.aem_name_e);
        Mobile = findViewById(R.id.aem_phone_e);
        Email = findViewById(R.id.aem_email_e);
        Aadhaar = findViewById(R.id.aem_aadhaar_e);
        Pan = findViewById(R.id.aem_pan_e);
        Pincode = findViewById(R.id.aem_pincode_e);
        Dist = findViewById(R.id.aem_dist_e);
        State = findViewById(R.id.aem_state_e);
        Address = findViewById(R.id.acd_address_e);
        TxnID = findViewById(R.id.aem_txnid_e);
        GstNumber = findViewById(R.id.aem_gst_e);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        Pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==6)
                {
                    getPincode(s.toString());
                }
                else
                {}
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onItemSelected(String tag, Item item) {
        if(tag.equalsIgnoreCase("Gender"))
            Gender.setText(item.getTitle().toString());
        else
            Package.setText(item.getTitle().toString());
        dialog.dismiss();
    }

    public void selectGender(View view)
    {
        dialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Choose Gender")
                .add(R.menu.gender_type)
                .build();
        dialog.show(getSupportFragmentManager(), "Gender");
    }

    public void selectPackage(View view)
    {
        dialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Choose Package")
                .add(R.menu.package_type)
                .build();
        dialog.show(getSupportFragmentManager(), "Package");
    }

    public void Submit(View view)
    {
        if(Name.length()>2 && Mobile.length()==10 && Aadhaar.length()>2 && Package.length()>2 && Pan.length()>2 && Pincode.length()>2 && Address.length()>2 && TxnID.length()>2)
            RegisterMenberApi();
        else
            Toast.makeText(this, "Some required fields are missing!", Toast.LENGTH_SHORT).show();
    }

    public void RegisterMenberApi()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"member/add";
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
                                Toast.makeText(AddmemActivity.this, "New member added successfully.", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                            else
                            Toast.makeText(AddmemActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(AddmemActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AddmemActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
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
                params.put("package","7000");
                params.put("phone",Mobile.getText().toString());
                params.put("email",Email.getText().toString());
                params.put("address",Address.getText().toString());
                params.put("aadhaar",Aadhaar.getText().toString());
                params.put("pan",Pan.getText().toString());
                params.put("pincode",Pincode.getText().toString());
                params.put("state",State.getText().toString());
                params.put("district",Dist.getText().toString());
                params.put("added_by",sharedPreferences.getString("mid",""));
                params.put("gender",Gender.getText().toString());
                params.put("trans_id",TxnID.getText().toString());
                params.put("gst_number",GstNumber.getText().toString());
                params.put("status","Active");
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

    public void getPincode(String pincode)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "https://api.postalpincode.in/pincode/"+pincode;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Loader.dismiss();
                        Log.e("Respone", response);

                        try {

                            JSONArray CartItemsArray = new JSONArray(response);

                            JSONArray Arrs = new JSONArray(response);
                            String res = Arrs.getString(0);
                            JSONObject Res=new JSONObject(res);
                            String sts    = Res.getString("Status");
                            if(sts.equalsIgnoreCase("Success"))
                            {
                                String PostOffice = Res.getString("PostOffice");
                                JSONArray Names = new JSONArray(PostOffice);
                                String SNamePlaces = Names.getString(Names.length()-1);
                                JSONObject SNamePlace =new JSONObject(SNamePlaces);

                                String dist = SNamePlace.getString("District");
                                String state = SNamePlace.getString("State");
                                State.setText(state);
                                Dist.setText(dist);
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();

                        }
                    }
                }
        ) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("x-rapidapi-key", "9d34b8a2d6mshdbf52a39a0f4571p1580f3jsne94202f9bda4");
                params.put("x-rapidapi-host", "india-pincode-with-latitude-and-longitude.p.rapidapi.com");

                return params;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

}