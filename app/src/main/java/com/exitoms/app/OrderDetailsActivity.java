package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class OrderDetailsActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener{

    String ID;
    TextView  Customer,Mobile,Address,Amount,Payment,Status,pStatus,orderID;
    String mobileNumber,name,amt;
    ModalBottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Customer = findViewById(R.id.details_customer);
        Mobile = findViewById(R.id.details_mobile);
        Address = findViewById(R.id.details_address);

        Amount = findViewById(R.id.details_amount);
        Payment = findViewById(R.id.details_payment);
        Status = findViewById(R.id.details_status);
        pStatus = findViewById(R.id.details_pstatus);
        orderID = findViewById(R.id.details_orderid);

        try {
            Intent intent = getIntent();
            ID=intent.getStringExtra("orderID");
        }catch (Exception e)
        {
        }
        getOrderDetails(ID);
        orderID.setText("#WAS"+ID);

        dialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Update Order Status")
                .add(R.menu.order_status)
                .build();

    }

    public void getOrderDetails(final String orderid)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/order";
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
                                String orders    = Res.getString("orders");

                                JSONObject OrderObj = new JSONObject(orders);
                                name = OrderObj.getString("cname");
                                Customer.setText(name);
                                mobileNumber = OrderObj.getString("cphone");
                                Mobile.setText(mobileNumber);
                                Address.setText(OrderObj.getString("cemail"));
                                amt = "₹ "+OrderObj.getString("amount");
                                Amount.setText(amt);
                                String pMode = OrderObj.getString("paytype");
                                pStatus.setTextColor(getResources().getColor(R.color.white));
                                if (pMode.equalsIgnoreCase("CoD"))
                                {
                                    Payment.setText("Cash on Delivery");
                                    if (OrderObj.getString("paystatus").equalsIgnoreCase("Pending"))
                                    {
                                        pStatus.setText("Not Received");
                                        pStatus.setBackground(getDrawable(R.drawable.pending));
                                    }
                                    else if (OrderObj.getString("paystatus").equalsIgnoreCase("Success"))
                                    {
                                        pStatus.setText("Received");
                                        pStatus.setBackground(getDrawable(R.drawable.success));
                                    }
                                    else
                                    {
                                        pStatus.setText("Unknown");
                                        pStatus.setBackground(getDrawable(R.drawable.btn_border));
                                        pStatus.setTextColor(getResources().getColor(R.color.purple_200));
                                    }
                                }
                                else
                                { Payment.setText("Razorpay");
                                    if (OrderObj.getString("paystatus").equalsIgnoreCase("Pending"))
                                    {
                                        pStatus.setText("Pending");
                                        pStatus.setBackground(getDrawable(R.drawable.pending));
                                    }
                                    else if (OrderObj.getString("paystatus").equalsIgnoreCase("Success"))
                                    {
                                        pStatus.setText("Success");
                                        pStatus.setBackground(getDrawable(R.drawable.success));
                                    }
                                    else
                                    {
                                        pStatus.setText("Failed");
                                        pStatus.setBackground(getDrawable(R.drawable.danger));
                                    }
                                }
                                Status.setText(OrderObj.getString("adminstatus"));
                            }
                            else
                            Toast.makeText(OrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(OrderDetailsActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(OrderDetailsActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("orderid",orderid);
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void Refresh(View view)
    {
        getOrderDetails(ID);
    }

    public void Back(View view)
    {
        super.onBackPressed();
    }

    public void Call(View view)
    {
        {
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.CALL_PHONE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+mobileNumber));
                                startActivity(callIntent);
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        }

    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onItemSelected(String tag, Item item) {
        updateOrderStatus(item.getTitle().toString());
        dialog.dismiss();
    }


    public void Update(View view)
    {
        dialog.show(getSupportFragmentManager(), "OrderStatus");
    }

    public void updateOrderStatus(final String status)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.ajax_url)+"order/status?status="+status+"&id="+ID;
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
                                Status.setText(status);
                            }
                                Toast.makeText(OrderDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(OrderDetailsActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(OrderDetailsActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("orderid",ID);
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void WhatsApp(View view)
    {
        whatsapp(this,mobileNumber,name,amt,ID,Status.getText().toString());
    }

    @SuppressLint("NewApi")
    public static void whatsapp(Activity activity, String phone, String name,String Amt, String ID,String Status) {
        String formattedNumber = "91"+phone;

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.putExtra("jid", formattedNumber +"@s.whatsapp.net");
        whatsappIntent.setPackage("com.whatsapp.w4b");
    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "*ORDER CONFIRMATION*\nHi "+name+", thank you for your order. We have received your order of Amount "+Amt+" with Order ID #WAS"
    +ID+". Your order status is *"+Status+"*.");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            activity.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }

    }

}