package com.manasmalla.becoronaready;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {

    CircleImageView userImageView;
    TextView userNameTextView, userScoreTextView;
    SharedPreferences sharedPreferences;

    File imageSlide;

    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = this.getSharedPreferences("com.manasmalla.becoronaready", MODE_PRIVATE);

        userNameTextView = findViewById(R.id.usernameTextView);
        userScoreTextView = findViewById(R.id.scoreTextView);
        userImageView = findViewById(R.id.user_profile_imgView);
        Log.i("score", String.valueOf(sharedPreferences.getInt("score", 20)));

        File externalStorage = DashboardActivity.this.getExternalFilesDir(null);
        File filePath = new File(externalStorage.getAbsolutePath() + "profile");
        imageSlide = new File(filePath, "profile_image.png");
    }

    @Override
    protected void onResume() {
        super.onResume();
        userNameTextView.setText(sharedPreferences.getString("username", "User"));
        userScoreTextView.setText((sharedPreferences.getInt("totScore", 20)) + " SP");
        Bitmap userIcon = BitmapFactory.decodeFile(imageSlide.getAbsolutePath());
        userImageView.setImageBitmap(userIcon);
    }

    public void playGameOnClick(View view) {
        Intent playGame = new Intent(this, MainActivity.class);
        startActivity(playGame);
    }

    public void settingsOnClick(View view) {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    public void infoOnClick(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void graphOnClick(View view) {
        Intent graph = new Intent(this, COVIDUpdatesActivity.class);
        startActivity(graph);
    }

    public void shareOnClick(View view) {
        int drawable = R.drawable.banner_share;
        Bitmap bitmapBanner = BitmapFactory.decodeResource(getResources(),
                R.drawable.banner_share);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapBanner.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "appShareBanner.png");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        String imgBitmapPath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmapBanner, "title", null);
        Uri appBannerUri = Uri.parse(imgBitmapPath);
*/

        int score = sharedPreferences.getInt("totScore", 20);

        //Intent.EXTRA_STREAM,

        Uri path = FileProvider.getUriForFile(this, "com.manasmalla.becoronaready.provider", f);

        int uid = Binder.getCallingUid();
        String callingPackage = getPackageManager().getNameForUid(uid);
        this.grantUriPermission(callingPackage, path, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Intent sendIntent = ShareCompat.IntentBuilder.from(this)
                .setStream(path)
                .getIntent();
        Log.i("URIContent", path.toString());
        sendIntent.setDataAndType(path, "image/*");
        //sendIntent.putExtra(Intent.EXTRA_TITLE, "Be Corona Ready");
        //sendIntent.putExtra(Intent.EXTRA_STREAM, path);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Share Knowledge, not the virus! \uD83D\uDE09\uD83D\uDC4D Let's compete for something good! My score is " + score + "! Download Be Corona Ready from Google Play store today! https://play.google.com/store/apps/developer?id=Manas+Malla");

        //sendIntent.setType("image/png");

        sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
// Show the Sharesheet
        startActivity(Intent.createChooser(sendIntent, "Share Be Corona Ready!"));

    }

}
