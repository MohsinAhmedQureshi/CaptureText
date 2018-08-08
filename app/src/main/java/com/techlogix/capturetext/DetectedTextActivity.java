package com.techlogix.capturetext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetectedTextActivity extends AppCompatActivity {

    TextView detectedText, confidenceText, languagesText;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected_text);

        detectedText = (TextView) findViewById(R.id.detectedText);
        text = getIntent().getStringExtra("TranslatedText");
        detectedText.setText(text);

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

    public void onClickForm(View view) {
//        parseText(text);
        Intent intent = new Intent(DetectedTextActivity.this, FormActivityNew.class);
        intent.putExtra("BitmapImageUri", getIntent().getStringExtra("BitmapImageUri"));
        intent.putExtra("DetectedText", text);
        startActivity(intent);
    }

    public void parseText(String text) {
        String number = "", email = "";
        String[] lines = text.split("\n");
        boolean numeric;
        Double num = 0.0;

        for (String line : lines) {

            numeric = true;
            try {
                num = Double.parseDouble(text);
            } catch (NumberFormatException e) {
                numeric = false;
            }

            if (line.contains("@"))
                email = line;
            else if (numeric)
                number = Double.toString(num);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(DetectedTextActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder.setTitle("Results")
                .setMessage("Email: " + email + "\nNumber: " + number)
                .show();

        Log.d("NUMBER FIELD", "Number: " + number);
        Log.d("EMAIL FIELD", "Email: " + email);

    }
}
