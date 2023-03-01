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


public class EditPasswordActivity extends AppCompatActivity {
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



    EditText editText3, editText4;
    Button button;
    String password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        SharedPreferences settings = null;
        try {
            masterKey = new MasterKey.Builder(this)
                    .setKeyGenParameterSpec(spec)
                    .build();
            settings = EncryptedSharedPreferences.create(
                    EditPasswordActivity.this,
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

        password =settings.getString("password", "");

        editText3 = (EditText) findViewById(R.id.editTextEdit1);
        editText4 = (EditText) findViewById(R.id.editTextEdit2);

        button = (Button) findViewById(R.id.buttonEdit);
        SharedPreferences finalSettings = settings;
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String text3 = editText3.getText().toString();
                String text4 = editText4.getText().toString();
                String hash1 = secure.main(text3);
                String hash2 =secure.main(text4);

                if(text3.equals("")|| text4.equals("")){
                    Toast.makeText(EditPasswordActivity.this, "No password entered", Toast.LENGTH_SHORT).show();

                }else{
                    if(text3.equals(text4)){
                        Toast.makeText(EditPasswordActivity.this, "Passwords are the same", Toast.LENGTH_SHORT).show();
                    }else if(!secure.verify(text3, password)){
                        Toast.makeText(EditPasswordActivity.this, "Wrong old password entered", Toast.LENGTH_SHORT).show();
                    }
                    if(text4.length()<10){
                        Toast.makeText(EditPasswordActivity.this, "Too short password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        SharedPreferences.Editor editor = finalSettings.edit();
                        editor.putString("password", hash2);
                        editor.apply();
                        //enter the app
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        });

    }
}