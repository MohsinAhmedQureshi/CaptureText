package com.mohsin_ahmed.capturetext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DetectedTextActivity extends AppCompatActivity {

    TextView detectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected_text);

        detectedText = (TextView) findViewById(R.id.detectedText);
        detectedText.setText(getIntent().getStringExtra("TranslatedText"));
    }

    public void onClickCopy(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Detected Text", detectedText.getText().toString().trim());
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, "Text Copied.", Toast.LENGTH_SHORT).show();
    }
}
