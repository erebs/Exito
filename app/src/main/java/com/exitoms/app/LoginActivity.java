package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import de.mateware.snacky.Snacky;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    EditText Phone,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        Phone = findViewById(R.id.EditLoginPhone);
        Password = findViewById(R.id.EditLoginPassword);

    }

    public void LoginBtn(View view)
    {
        if(Phone.length()>0 && Password.length()>2)
            LoginApi(Phone.getText().toString(),Password.getText().toString());
        else
            showMsg("Some required fields are missing!");
    }

    public void tNc(View view)
    {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("u",getString(R.string.site_url)+"app/page/tnc");
        i.putExtra("t","Terms & Conditions");
        startActivity(i);
    }

    public void privacyPolicy(View view)
    {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("u",getString(R.string.site_url)+"app/page/privacy");
        i.putExtra("t","Privacy Policy");
        startActivity(i);
    }

    public void ForgotPassword(View view)
    {

        if(Phone.length()>0)
            ForgotApi(Phone.getText().toString());
        else
            showMsg("Please enter a valid member ID!");
    }

    public void LoginApi(final String username, final String password)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"member/login";
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
                                String UserObjectString = Res.getString("user");
                                JSONObject UserObject = new JSONObject(UserObjectString);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("mid", username);
                                editor.putString("password", password);
                                editor.putString("id", UserObject.getString("id"));
                                editor.putString("name", UserObject.getString("name"));
                                editor.putString("phone", UserObject.getString("phone"));
                                editor.putString("email", UserObject.getString("email"));
                                editor.putString("join", UserObject.getString("created_at"));
                                editor.apply();

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            showMsg(msg);

                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(LoginActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid",username);
                params.put("password",password);
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }


    public void ForgotApi(final String username)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"member/forget";
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

                            Snacky.builder()
                                    .setActivity(LoginActivity.this)
                                    .setActionText("GOT IT")
                                    .setActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //do something
                                        }
                                    })
                                    .setText(msg)
                                    .setDuration(Snacky.LENGTH_LONG)
                                    .build()
                                    .show();

                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(LoginActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone",username);
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    void showMsg(String Msg)
    {
        Snacky.builder()
                .setActivity(LoginActivity.this)
//                .setActionText("ACTION")
                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do something
                    }
                })
                .setText(Msg)
                .setDuration(Snacky.LENGTH_LONG)
                .build()
                .show();
    }

}