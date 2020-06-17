package com.manasmalla.becoronaready;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int storageRquestCode = 300;
    Bitmap profileBitmap;
    byte[] byteArray = null;
    String username;
    TextInputLayout username_textInputLayout;
    MaterialButton loginButton;
    TextInputEditText username_textInputEditText;
    Intent intent;
    SharedPreferences sharedPreferences;

    OutputStream outputStream;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == storageRquestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent2, 2);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = this.getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE);

        username_textInputLayout = findViewById(R.id.username_inputLayout_settingsActivity);
        username_textInputEditText = findViewById(R.id.username_editText_settingsActivity);
        loginButton = findViewById(R.id.updateButton_settingsActivity);

    }

    public void loginOnClick(View view) {

        username = username_textInputEditText.getText().toString();

        //We have credentials!
        intent = new Intent(this, DashboardActivity.class);
        ///Signup the user
        if (byteArray == null) {
            if ((username == null || username.isEmpty())) {
                Toast.makeText(this, "Please give some data to update!", Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferences.edit().putString("username", username).apply();
                Toast.makeText(this, "Updated Username, " + sharedPreferences.getString("username", null), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        } else {

            File externalStorage = SettingsActivity.this.getExternalFilesDir(null);
            File filePath = new File(externalStorage.getAbsolutePath() + "profile");
            if (filePath.exists()) {
                Log.i("FilePath", "Exists");
            } else {
                if (filePath.mkdir()) {
                    Toast.makeText(this, "Created Folder at " + filePath.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Couldn't create folder at " + filePath.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            File imageSlide = new File(filePath, "profile_image.png");

            try {
                outputStream = new FileOutputStream(imageSlide);
                if ((username != null && !username.isEmpty())) {
                    sharedPreferences.edit().putString("username", username).apply();
                }
                profileBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Updated Profile, " + sharedPreferences.getString("username", null), Toast.LENGTH_SHORT).show();
                outputStream.flush();
                outputStream.close();
                startActivity(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void getProfilePicture(View view) {
        getProfilePhoto();
    }

    private void getProfilePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, storageRquestCode);
            } else {
                Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent2, 2);
            }
        } else {
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent2, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            //   Log.i("URIImageLoader", selectedImage.toString());

            try {

                profileBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                profileBitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);

                byteArray = stream.toByteArray();

                CircleImageView circleImageView = findViewById(R.id.profilePicUploadCircleImageView_settingsActivity);
                circleImageView.setImageBitmap(profileBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
