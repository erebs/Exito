package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.exitoms.app.adaptors.OrderList;
import com.exitoms.app.models.OrderDetails;

public class MainActivity extends AppCompatActivity implements WhAdaptor.Action{

    ImageView DashboardIcon,HistoryIcon,AccountIcon,MoreIcon;
    TextView DashboardText, HistoryText, AccountText, MoreText,memID,joinDate,Wallet,EBSCoins;
    SharedPreferences sharedPreferences;
    ImageView Logo;
    TextView Name,refIncome,refNum,renewalIncome,seclobAct,actIncome,negPoints;
    String memberID;
    List<ApprovedModel> approvedModels = new ArrayList<>(1000);
    WhAdaptor approvedAdaptor;
    RecyclerView approvedView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refIncome = findViewById(R.id.refIncome);
        refNum = findViewById(R.id.refNum);
        renewalIncome = findViewById(R.id.renewalIncome);
        seclobAct = findViewById(R.id.seclobAct);
        actIncome = findViewById(R.id.actIncome);
        negPoints = findViewById(R.id.negPoints);


        Wallet = findViewById(R.id.main_wallet_balance);
        EBSCoins = findViewById(R.id.ebsCoins);
        memID = findViewById(R.id.memID);
        joinDate = findViewById(R.id.joinDate);
        DashboardIcon = findViewById(R.id.img_dashboard);
        DashboardText = findViewById(R.id.txt_dashboard);
        HistoryIcon = findViewById(R.id.img_history);
        HistoryText = findViewById(R.id.txt_history);
        AccountIcon = findViewById(R.id.img_account);
        AccountText = findViewById(R.id.txt_account);
        MoreIcon = findViewById(R.id.img_more);
        MoreText = findViewById(R.id.txt_more);

        Logo  = findViewById(R.id.logo_image);
        Name  = findViewById(R.id.main_name);



        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
       //LoginApi(sharedPreferences.getString("phone",""),sharedPreferences.getString("password",""));
        Name.setText("Hi "+sharedPreferences.getString("name",""));
        memID.setText(sharedPreferences.getString("mid",""));
        OtherDetails(sharedPreferences.getString("id",""));

        Glide.with(this).load(getString(R.string.site_url)+"assets/images/profilepics/"+sharedPreferences.getString("mid","")+".jpg").placeholder(R.drawable.man)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(Logo);

        approvedView = findViewById(R.id.main_oders_view);
        this.approvedAdaptor = new WhAdaptor(this,this);
        approvedView.setAdapter(approvedAdaptor);
        approvedView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        approvedView.setNestedScrollingEnabled(false);
        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        memberID = sharedPreferences.getString("id","");
        approvedMembers();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String message = intent.getStringExtra("message");

        }
    };

    @Override
    public void onResume() {
        OtherDetails(sharedPreferences.getString("id",""));
        super.onResume();
        registerReceiver(mMessageReceiver, new IntentFilter("was-push"));
    }

    public void Delete(String uID)
    {

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }



    public void AccountBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void listSecUsers(View view)
    {
        Intent intents = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void Listmems(View view)
    {
        Intent intents = new Intent(MainActivity.this, ListmemsActivity.class);
        startActivity(intents);
    }

    public void Amembers(View view)
    {
        Intent intents = new Intent(MainActivity.this, AmembersActivity.class);
        startActivity(intents);
    }

    public void Wallet(View view)
    {
        Intent intents = new Intent(MainActivity.this, WalletActivity.class);
        startActivity(intents);
    }

    public void Addmem(View view)
    {
        Intent intents = new Intent(MainActivity.this, AddmemActivity.class);
        startActivity(intents);
    }

    public void EPB(View view)
    {
        Intent intents = new Intent(MainActivity.this, EpbActivity.class);
        startActivity(intents);
    }

    public void adduser(View view)
    {
        Intent intents = new Intent(MainActivity.this, SeclobuserActivity.class);
        startActivity(intents);
    }

    public void Withdraw(View view)
    {
        Intent intents = new Intent(MainActivity.this, WithdrawActivity.class);
        startActivity(intents);
    }


    public void OtherDetails(final String memid)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"home";
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
                                String UserObjectString = Res.getString("member");
                                JSONObject UserObject = new JSONObject(UserObjectString);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id", UserObject.getString("id"));
                                editor.putString("name", UserObject.getString("name"));
                                editor.putString("phone", UserObject.getString("phone"));
                                editor.putString("email", UserObject.getString("email"));
                                editor.putString("address", UserObject.getString("address"));
                                editor.putString("aadhaar", UserObject.getString("aadhaar"));
                                editor.putString("pan", UserObject.getString("pan"));
                                editor.putString("pincode", UserObject.getString("pincode"));
                                editor.putString("state", UserObject.getString("state"));
                                editor.putString("district", UserObject.getString("district"));
                                editor.putString("points", UserObject.getString("points"));
                                editor.putString("wallet", UserObject.getString("wallet"));
                                editor.apply();
                                Wallet.setText("???"+UserObject.getString("wallet"));
                                EBSCoins.setText(UserObject.getString("points"));

                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM. dd yyyy");
                                Date date = inputFormat.parse( UserObject.getString("created_at"));
                                String formattedDate = outputFormat.format(date);
                                joinDate.setText(formattedDate);
                                negPoints.setText("-"+Res.getString("exitonegativepoints"));
                                refIncome.setText("???"+Res.getString("referalincome"));
                                refNum.setText("/"+Res.getString("referalactive"));
                                renewalIncome.setText("???"+Res.getString("renewelincome"));
                                seclobAct.setText(Res.getString("seclobusercount"));
                                actIncome.setText("???"+Res.getString("seclobuseramount"));
                            }
                            else
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

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
                Map<String, String> params = new HashMap<String, String> ();
                params.put("memberid",memid);
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void approvedMembers()
    {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"wallethistory?memberid="+sharedPreferences.getString("id","");
        Log.e("URLLLLLL",URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        Log.i("VOLLEYES", response);
                        try {

                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {
                                approvedModels.clear();
                                String Orders = Res.getString("wallethistory");
                                JSONArray Results = new JSONArray(Orders);
                                if(Results.length()<1)
                                    findViewById(R.id.nomemDownline3).setVisibility(View.VISIBLE);
                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    ApprovedModel approvedModel = new ApprovedModel();
                                    approvedModel.setmID("EW"+rst.getString("id")+" - "+rst.getString("purpose"));
                                    approvedModel.setName(rst.getString("amount"));
                                    approvedModel.setTxnID(rst.getString("comments"));
                                    approvedModel.setSatus(rst.getString("type"));
                                    approvedModel.setCdate(rst.getString("created_at"));
                                    approvedModels.add(approvedModel);
                                }
                                approvedAdaptor.renewItems(approvedModels);

                            }
                            else
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(MainActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
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

                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }


}