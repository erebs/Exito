package com.exitoms.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AccdetailsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText Name,Mobile,Email,MemberID,Pan,Aadhaar,Dist,State,Pincode,Address;

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

    public void goBack(View view)
    {
        super.onBackPressed();
    }

}