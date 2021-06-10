package com.exitoms.app;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class AccountActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener{

    TextView Name,MemID,EditBank;
    EditText Mobile,Email,Pan,Aadhaar,Address,Pincode,State,Dist;
    EditText Bname,Hname,Branch,Account,IFSC,Type;
    Button BankBtn;
    LinearLayout TypeBtn;
    SharedPreferences sharedPreferences;
    ModalBottomSheetDialog dialog;
    ImageView Propic;
    boolean check = true;
    Bitmap bitmap;
    public static final int REQUEST_IMAGE = 100;
    ProgressDialog progressDialog;
    String ImageName = "image_name";
    String ImagePath = "image_path" ;
    String ServerUploadPath = "https://exitoms.com/assets/images/uploader.php" ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        MemID=findViewById(R.id.pro_memID);
        Name=findViewById(R.id.pro_name);
        Mobile=findViewById(R.id.pro_mobile);
        Email=findViewById(R.id.pro_email);
        Pan=findViewById(R.id.pro_pan);
        Aadhaar=findViewById(R.id.pro_adr);
        Address=findViewById(R.id.pro_address);
        Pincode=findViewById(R.id.pro_pincode);
        State=findViewById(R.id.pro_state);
        Dist=findViewById(R.id.pro_dist);
        Propic = findViewById(R.id.Propic);

        Bname=findViewById(R.id.pro_bname);
        Hname=findViewById(R.id.pro_hname);
        Account=findViewById(R.id.pro_acc);
        Branch=findViewById(R.id.pro_branch);
        IFSC=findViewById(R.id.pro_ifsc);
        EditBank=findViewById(R.id.edit_bank);
        BankBtn = findViewById(R.id.bank_btn);
        Type = findViewById(R.id.pro_acctype);
        TypeBtn= findViewById(R.id.pro_acctypebtn);



        sharedPreferences = getSharedPreferences("WHTS", MODE_PRIVATE);
        Name.setText(sharedPreferences.getString("name",""));
        MemID.setText(sharedPreferences.getString("mid",""));
        Mobile.setText(sharedPreferences.getString("phone",""));
        Email.setText(sharedPreferences.getString("email",""));
        Pan.setText(sharedPreferences.getString("pan",""));
        Aadhaar.setText(sharedPreferences.getString("aadhaar",""));
        Pincode.setText(sharedPreferences.getString("pincode",""));
        State.setText(sharedPreferences.getString("state",""));
        Dist.setText(sharedPreferences.getString("district",""));
        Address.setText(sharedPreferences.getString("address",""));

        Glide.with(this).load(getString(R.string.site_url)+"assets/images/profilepics/"+sharedPreferences.getString("mid","")+".jpg").placeholder(R.drawable.man)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(Propic);

        dialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Select your Bank account type")
                .add(R.menu.bank_account_type)
                .build();

        getBank();

//        Bname.setText(sharedPreferences.getString("aadhaar",""));
//        Hname.setText(sharedPreferences.getString("pincode",""));
//        Account.setText(sharedPreferences.getString("state",""));
//        Branch.setText(sharedPreferences.getString("district",""));
//        IFSC.setText(sharedPreferences.getString("address",""));

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
                    GetIFSC(s.toString());

            }
        });

    }


    public int inDP(String inPX)
    {
        int intPX = Integer.parseInt(inPX);
        int cardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, intPX, getResources().getDisplayMetrics());
        return cardHeight;
    }

    private void loadProfile(String url) {
        Log.d("TAG", "Image cache path: " + url);
        Glide.with(this).load(url)
                .into(Propic);
        Propic.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        ImageUploadToServerFunction();

    }

    public void ProPic(View view)
    {
        PropicChoose();
    }

    public void PropicChoose()
    {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            launchGalleryIntent();
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

    private void launchGalleryIntent() {
        Intent intent = new Intent(AccountActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
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



    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(AccountActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(AccountActivity.this,string1,Toast.LENGTH_LONG).show();



            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, sharedPreferences.getString("mid",""));

                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }

    private Uri saveImage(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "tech.seclob.vivocards.provider", file);
        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

//    END

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void Logout(View view)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mid", "");
        editor.putString("password", "");
        editor.putString("id", "");
        editor.putString("name", "");
        editor.putString("phone", "");
        editor.putString("email", "");
        editor.putString("join", "");
        editor.apply();
        Intent intents = new Intent(AccountActivity.this, LoginActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }


    public void BankUpdateBtn(View view)
    {
        if(Account.length()>2 && IFSC.length()>3)
        {
            updateBank();
        }
        else
        {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public void EditBank(View view)
    {
        if(EditBank.getText().toString().equalsIgnoreCase("Edit")) {
            Bname.setEnabled(true);
            Hname.setEnabled(true);
            Account.setEnabled(true);
            Branch.setEnabled(true);
            IFSC.setEnabled(true);
            TypeBtn.setClickable(true);
            EditBank.setText("Cancel");
            BankBtn.setVisibility(View.VISIBLE);
        }else
        {
            TypeBtn.setClickable(false);
            Bname.setEnabled(false);
            Hname.setEnabled(false);
            Account.setEnabled(false);
            Branch.setEnabled(false);
            IFSC.setEnabled(false);
            EditBank.setText("Edit");
            BankBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(String tag, Item item) {
        Type.setText(item.getTitle().toString());
        dialog.dismiss();
    }

    public void SelectType(View view)
    {
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
                                TypeBtn.setClickable(false);
                                Account.setEnabled(false);
                                Branch.setEnabled(false);
                                IFSC.setEnabled(false);
                                EditBank.setText("Edit");
                                BankBtn.setVisibility(View.GONE);

                                Bname.setText(UserObject.getString("bank_name"));
                                Hname.setText(UserObject.getString("acc_name"));
                                Account.setText(UserObject.getString("acc_no"));
                                Branch.setText(UserObject.getString("branch_name"));
                                Type.setText(UserObject.getString("acc_type"));
                                IFSC.setText(UserObject.getString("ifsc_code"));


                            }
                            else if(sts.equalsIgnoreCase("00"))
                            {
                                Bname.setEnabled(true);
                                Hname.setEnabled(true);
                                Account.setEnabled(true);
                                TypeBtn.setClickable(true);
                                Branch.setEnabled(true);
                                IFSC.setEnabled(true);
                                EditBank.setText("Cancel");
                                BankBtn.setVisibility(View.VISIBLE);
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
                params.put("uid","");
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void DownloadInvoice(View view)
    {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("u",getString(R.string.site_url)+"member/app/invoice/"+sharedPreferences.getString("id",""));
        i.putExtra("t","Invoice");
        startActivity(i);
    }

    public void updateBank()
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
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

                                Bname.setEnabled(false);
                                Hname.setEnabled(false);
                                Account.setEnabled(false);
                                Branch.setEnabled(false);
                                IFSC.setEnabled(false);
                                TypeBtn.setClickable(false);
                                EditBank.setText("Edit");
                                BankBtn.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Your account details updated successfully...", Toast.LENGTH_SHORT).show();

                            }
                            else if(sts.equalsIgnoreCase("00"))
                            {
                                Bname.setEnabled(true);
                                Hname.setEnabled(true);
                                Account.setEnabled(true);
                                Branch.setEnabled(true);
                                IFSC.setEnabled(true);
                                Type.setEnabled(true);
                                TypeBtn.setClickable(true);

                                EditBank.setText("Cancel");
                                BankBtn.setVisibility(View.VISIBLE);
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
        RequestQueue queue = Volley.newRequestQueue(AccountActivity.this);
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