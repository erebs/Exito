package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class BankActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener{
    EditText Bname,Hname,Branch,Account,IFSC,Type;
    SharedPreferences sharedPreferences;
    ModalBottomSheetDialog dialog;
    Drawable save,edit;
    TextView editSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        Bname=findViewById(R.id.acd_bname_e);
        Hname=findViewById(R.id.acd_acch_e);
        Account=findViewById(R.id.acd_accnum_e);
        Branch=findViewById(R.id.acd_branch_e);
        IFSC=findViewById(R.id.acd_ifsc_e);
        Type = findViewById(R.id.acd_btype_e);
        editSave = findViewById(R.id.editSave_bn);

        save = getResources().getDrawable(R.drawable.outline_save_24);
        edit = getResources().getDrawable(R.drawable.outline_edit_24);

        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);

        dialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Select your Bank account type")
                .add(R.menu.bank_account_type)
                .build();

        getBank();

        IFSC.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 11)
                    if(IFSC.isEnabled())
                    GetIFSC(s.toString());

            }
        });
    }


    public void EditBank(View view)
    {
        if(!IFSC.isEnabled()) {
            Bname.setEnabled(true);
            Hname.setEnabled(true);
            Account.setEnabled(true);
            Branch.setEnabled(true);
            IFSC.setEnabled(true);
            Type.setEnabled(true);
            editSave.setCompoundDrawablesWithIntrinsicBounds(save,null,null,null);
            editSave.setText("Save");

        }else
        {
            updateBank();
        }
    }

    @Override
    public void onItemSelected(String tag, Item item) {
        Type.setText(item.getTitle().toString());
        dialog.dismiss();
    }

    public void SelectType(View view)
    {
        if(IFSC.isEnabled())
        dialog.show(getSupportFragmentManager(), "BankDetails");
    }

    public void getBank()
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"bankdetails?memberid="+sharedPreferences.getString("id","");
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
                                String UserObjectString = Res.getString("bankdetails");
                                JSONObject UserObject = new JSONObject(UserObjectString);

                                Bname.setEnabled(false);
                                Hname.setEnabled(false);
                                Type.setEnabled(false);
                                Account.setEnabled(false);
                                Branch.setEnabled(false);
                                IFSC.setEnabled(false);
                                Type.setEnabled(false);

                                editSave.setCompoundDrawablesWithIntrinsicBounds(save,null,null,null);
                                editSave.setText("Edit");

                                Bname.setText(UserObject.getString("bank_name"));
                                Hname.setText(UserObject.getString("acc_name"));
                                Account.setText(UserObject.getString("acc_no"));
                                Branch.setText(UserObject.getString("branch_name"));
                                Type.setText(UserObject.getString("acc_type"));
                                IFSC.setText(UserObject.getString("ifsc_code"));


                            }
                            else
                            {
                                Bname.setEnabled(true);
                                Hname.setEnabled(true);
                                Account.setEnabled(true);
                                Type.setEnabled(true);
                                Branch.setEnabled(true);
                                IFSC.setEnabled(true);
                                Type.setEnabled(true);

                                editSave.setCompoundDrawablesWithIntrinsicBounds(edit,null,null,null);
                                editSave.setText("Save");
                            }


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
                params.put("uid","");
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


    public void updateBank()
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Updating...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"bankdetails";
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

                                Type.setClickable(false);
                                Bname.setEnabled(false);
                                Hname.setEnabled(false);
                                Account.setEnabled(false);
                                Branch.setEnabled(false);
                                IFSC.setEnabled(false);
                                Type.setEnabled(false);
                                editSave.setCompoundDrawablesWithIntrinsicBounds(edit,null,null,null);
                                editSave.setText("Edit");
                                Toast.makeText(getApplicationContext(), "Your account details updated successfully...", Toast.LENGTH_SHORT).show();

                            }
                            else if(sts.equalsIgnoreCase("00"))
                            {

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
                params.put("memberid",sharedPreferences.getString("id",""));
                params.put("bank_name",Bname.getText().toString());
                params.put("branch_name",Branch.getText().toString());
                params.put("acc_name",Hname.getText().toString());
                params.put("acc_type",Type.getText().toString());
                params.put("acc_no",Account.getText().toString());
                params.put("ifsc_code",IFSC.getText().toString());
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void GetIFSC(String IFSC)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(BankActivity.this);
        String URL = "https://ifsc.razorpay.com/"+IFSC;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Log.i("VOLLEYES", response);
                        Loader.dismiss();
                        try {
                            JSONObject Res=new JSONObject(response);
                            String BRANCH    = Res.getString("BRANCH");
                            String BANK    = Res.getString("BANK");
                            Bname.setText(BANK.toUpperCase());
                            Branch.setText(BRANCH.toUpperCase());

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                            Loader.dismiss();

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
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("subiconid","subid");
                Log.i("loginp ", params.toString());

                return params;
            }

        };


        // Add the realibility on the connection.
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }



    
}