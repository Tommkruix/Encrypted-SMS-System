package org.codebrainz.arunataiwo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionActivity extends AppCompatActivity {

    AppCompatEditText inputText, inputPassword;
    TextView outputText;
    AppCompatButton encBtn, smsBtn;

    String outputString;
    String AES = "AES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

            ActivityCompat.requestPermissions(EncryptionActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS},
                    PackageManager.PERMISSION_GRANTED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#7E57C2"));
        }

            inputText = findViewById(R.id.inputText);
            inputPassword = findViewById(R.id.password);

            outputText = findViewById(R.id.outputText);
            outputText.setVisibility(View.GONE);
            encBtn = findViewById(R.id.encBtn);

            smsBtn = findViewById(R.id.smsBtn);

            encBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!inputText.getText().toString().equals("") && !inputPassword.getText().toString().equals("")) {
                        try {
                            outputString = encrypt(inputText.getText().toString(), inputPassword.getText().toString());
                            outputText.setText(outputString);
                            showDialog(outputString);
                            smsBtn.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(EncryptionActivity.this, "You need to enter a message and encryption key", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!outputText.getText().toString().equals("")){
                    Intent intent = new Intent(EncryptionActivity.this, PhoneActivity.class);
                    intent.putExtra("key_message", outputText.getText().toString());
                    startActivity(intent);
                }else{
                    Toast.makeText(EncryptionActivity.this, "Message body cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        }


        private String encrypt(String Data, String password) throws Exception {
            SecretKeySpec key = generateKey(password);
            Cipher c = Cipher.getInstance(AES);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            String encyptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
            return encyptedValue;
        }

        private SecretKeySpec generateKey(String password) throws Exception {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = password.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            return secretKeySpec;
        }

    public void showDialog(String message){
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(EncryptionActivity.this);
        builder.setMessage(message);
        builder.setTitle("Encrypted Message:");
        builder.setCancelable(false);

        builder
                .setPositiveButton(
                        "Ok",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}