package com.example.fingerprint;

import static com.example.fingerprint.MainActivity.arrayAdapter;
import static com.example.fingerprint.MainActivity.notes;
import static com.example.fingerprint.MainActivity.titles;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;

//@RequiresApi(api = Build.VERSION_CODES.R)
public class NoteEditorActivity extends AppCompatActivity {
    KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setUserAuthenticationValidityDurationSeconds(150)
            //.setUserAuthenticationParameters(30, KeyProperties.AUTH_DEVICE_CREDENTIAL|KeyProperties.AUTH_BIOMETRIC_STRONG)
            .setKeySize(256)
            .setUnlockedDeviceRequired(true)
            .setIsStrongBoxBacked(true)
            .build();

    MasterKey masterKey;




    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        SharedPreferences sharedPreferences = null;
        try {
            masterKey = new MasterKey.Builder(this)
                    .setKeyGenParameterSpec(spec)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    NoteEditorActivity.this,
                    "shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        TextView textView = (TextView) findViewById(R.id.textView5);
        EditText ediText = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);
        if (noteId != -1) {
            ediText.setText(MainActivity.notes.get(noteId));
            textView.setText(MainActivity.titles.get(noteId));
        } else {
            MainActivity.notes.add("");
            MainActivity.titles.add("");
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }
        ediText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                MainActivity.notes.set(noteId, String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.titles.set(noteId, String.valueOf(charSequence));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sharedPreferences = this.getSharedPreferences("encrypted_preferences", 0);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        ImageView imageSave = findViewById(R.id.imageDone);
        SharedPreferences finalSharedPreferences = sharedPreferences;
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.arrayAdapter.notifyDataSetChanged();

                HashSet<String> set = new HashSet(MainActivity.notes);
                HashSet<String> set1 = new HashSet(MainActivity.titles);
                finalSharedPreferences.edit().putStringSet("notes", set).apply();
                finalSharedPreferences.edit().putStringSet("titles", set1).apply();
                onBackPressed();
            }
        });

        final LinearLayout layout = findViewById(R.id.layoutDeleteNote);
        SharedPreferences finalSharedPreferences1 = sharedPreferences;
        layout.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new android.app.AlertDialog.Builder(NoteEditorActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Are you sure?")
                                .setMessage("Do you want to delete this note?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        notes.remove(noteId);
                                        titles.remove(noteId);
                                        arrayAdapter.notifyDataSetChanged();

                                        HashSet<String> set = new HashSet(MainActivity.notes);
                                        HashSet<String> set1 = new HashSet(MainActivity.titles);

                                        finalSharedPreferences1.edit().putStringSet("notes", set).apply();
                                        finalSharedPreferences1.edit().putStringSet("titles", set1).apply();
                                        onBackPressed();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }

                });
            }
        });

    }



}
