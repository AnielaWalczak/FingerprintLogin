package com.example.fingerprint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EnterPasswordActivity extends AppCompatActivity {
    Secure secure = new Secure();
    KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(true)
            .setUserAuthenticationValidityDurationSeconds(150)
            .setUnlockedDeviceRequired(true)
            .setIsStrongBoxBacked(true)
            .build();
    MasterKey masterKey;




    EditText editText;
    Button button;

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        SharedPreferences settings = null;
        try {
            masterKey = new MasterKey.Builder(this)
                    .setKeyGenParameterSpec(spec)
                    .build();
            settings = EncryptedSharedPreferences.create(
                    EnterPasswordActivity.this, // fileName
                    "encrypted_preferences", // masterKeyAlias
                    masterKey, // context
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // prefKeyEncryptionScheme
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // prefvalueEncryptionScheme
                        );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        settings = this.getSharedPreferences("encrypted_preferences", 0);

// use the shared preferences and editor as you normally would
            password = settings.getString("password", "");


        editText = (EditText) findViewById(R.id.editTextEnter);
        button = (Button) findViewById(R.id.buttonEnter);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String text  = editText.getText().toString();
                String hash3 =secure.main(text);

                if(secure.verify(text, password)){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(EnterPasswordActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}