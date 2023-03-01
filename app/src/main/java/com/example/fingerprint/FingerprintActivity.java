package com.example.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class FingerprintActivity extends AppCompatActivity {

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Button mMainLayout;
    Button mMainLayout2;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        txt = findViewById(R.id.textlogin);
        mMainLayout = findViewById(R.id.loginfingerprint);
        mMainLayout2 = findViewById(R.id.loginpassword);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch(biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                txt.setText("you can use the fingerprint sensor to login");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                txt.setText("the device don't have a fingerprint sensor");
                mMainLayout2.setVisibility(View.VISIBLE);
                mMainLayout.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                txt.setText("the biometric sensor is currently unavailable");
                mMainLayout2.setVisibility(View.VISIBLE);
                mMainLayout.setVisibility(View.GONE);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                txt.setText("your device don't have any fingerprint saved, please check your security settings ");
                mMainLayout2.setVisibility(View.VISIBLE);
                mMainLayout.setVisibility(View.GONE);
                break;

        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(FingerprintActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Login Sucess", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Use you fingerprint to login to your app")
                .setNegativeButtonText("Cancel")
                .build();
        mMainLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                biometricPrompt.authenticate(promptInfo);
            }
        });

        mMainLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}