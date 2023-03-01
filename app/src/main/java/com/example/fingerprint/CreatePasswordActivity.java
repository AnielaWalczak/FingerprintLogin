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


public class CreatePasswordActivity extends AppCompatActivity {


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



    EditText editText1, editText2;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        editText1 = (EditText) findViewById(R.id.editTextPass1);
        editText2 = (EditText) findViewById(R.id.editTextPass2);
        button = (Button) findViewById(R.id.buttonCreate);
        SharedPreferences settings;

        try {
            masterKey = new MasterKey.Builder(this)
                    .setKeyGenParameterSpec(spec)
                    .build();
            settings = EncryptedSharedPreferences.create(
                    CreatePasswordActivity.this,
                    "encrypted_preferences",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        settings = this.getSharedPreferences("encrypted_preferences", 0);
        SharedPreferences finalSettings = settings;
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                String text1 = editText1.getText().toString();
                String text2 = editText2.getText().toString();
                if(text1.equals("")|| text2.equals("")) {
                    Toast.makeText(CreatePasswordActivity.this, "No password entered", Toast.LENGTH_SHORT).show();
                }
                if(text1.length()<10){
                    Toast.makeText(CreatePasswordActivity.this, "Too short password", Toast.LENGTH_SHORT).show();
                }
                else{
                    String hash = secure.main(text1);
                    if(text1.equals(text2)){
                        SharedPreferences.Editor editor = null;
                        editor = finalSettings.edit();
                        editor.putString("password", hash);
                        editor.apply();


                        //enter the app
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(CreatePasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }
}