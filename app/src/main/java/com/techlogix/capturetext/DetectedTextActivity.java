package com.techlogix.capturetext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DetectedTextActivity extends AppCompatActivity {

    TextView detectedText, confidenceText, languagesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected_text);

        detectedText = (TextView) findViewById(R.id.detectedText);
        detectedText.setText(getIntent().getStringExtra("TranslatedText"));

        confidenceText = (TextView) findViewById(R.id.confidenceLevel);
        confidenceText.setText(getIntent().getStringExtra("ConfidenceText"));

        languagesText = (TextView) findViewById(R.id.detectedLanguages);
        languagesText.setText(getIntent().getStringExtra("LanguagesText"));
    }

    public void onClickCopy(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Detected Text", detectedText.getText().toString().trim());
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, "Text Copied.", Toast.LENGTH_SHORT).show();
    }
}
