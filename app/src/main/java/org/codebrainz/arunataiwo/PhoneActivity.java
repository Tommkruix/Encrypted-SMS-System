package org.codebrainz.arunataiwo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class PhoneActivity extends AppCompatActivity {

    String message;
    AppCompatButton smsBtn, goBackBtn;

    private EditText phoneno1, phoneno2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        smsBtn = findViewById(R.id.smsBtn);
        goBackBtn = findViewById(R.id.goBackBtn);
        phoneno1 = findViewById(R.id.phoneno1);
        phoneno2 = findViewById(R.id.phoneno2);

        Intent intent = getIntent();

        if (intent.hasExtra("key_message")){

            message = getIntent().getExtras().get("key_message").toString();

        }else{
            Toast.makeText(this, "Message not found!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendSMS(findViewById(android.R.id.content));
                sendSMS((phoneno1.getText().toString()+phoneno2.getText().toString()), message);

            }
        });

    }

    private void sendSMS(String phoneNumber, String message) {
        /*try {
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber,null,message,null,null);
            Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Some fiels is Empty. "+e,Toast.LENGTH_LONG).show();
        }*/
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        //ArrayList<String> texts = sms.divideMessage(message);
        //sms.sendMultipartTextMessage(phoneNumber, null, texts, null, null);
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

    }

    /*public void sendSMS (View view){
        String number = phoneno1.getText().toString()+phoneno2.getText().toString();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);

        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PhoneActivity.this, ChooseActivity.class);
        startActivity(intent);
    }*/
}
