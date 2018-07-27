package com.techlogix.capturetext;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;

public class FormActivity extends AppCompatActivity {

    EditText name, company, email, number, website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        String text = getIntent().getStringExtra("DetectedText");
//        String[] textLines = text.split("\n");
        ArrayList<String> textLines = new ArrayList<>();
        textLines.add("");
        Collections.addAll(textLines, text.split("\n"));

        final ImageView capturedImage = findViewById(R.id.capturedImage);
        final Uri imageUri = Uri.parse(getIntent().getStringExtra("BitmapImageUri"));
        final Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            capturedImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Spinner spinnerName = findViewById(R.id.nameSpinner);
        Spinner spinnerCompany = findViewById(R.id.companySpinner);
        Spinner spinnerEmail = findViewById(R.id.emailSpinner);
        Spinner spinnerNumber = findViewById(R.id.numberSpinner);
        Spinner spinnerWebsite = findViewById(R.id.websiteSpinner);

        name = findViewById(R.id.nameET);
        company = findViewById(R.id.companyET);
        email = findViewById(R.id.emailET);
        number = findViewById(R.id.numberET);
        website = findViewById(R.id.websiteET);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.detectedTextList, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, textLines);
        //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerName.setAdapter(spinnerArrayAdapter);
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCompany.setAdapter(spinnerArrayAdapter);
        spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                company.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerEmail.setAdapter(spinnerArrayAdapter);
        spinnerEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                email.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerNumber.setAdapter(spinnerArrayAdapter);
        spinnerNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                number.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerWebsite.setAdapter(spinnerArrayAdapter);
        spinnerWebsite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                website.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (!name.isFocused() && !company.isFocused() && !email.isFocused() && !number.isFocused() && !website.isFocused()) {
            super.onBackPressed();
        } else {
            name.clearFocus();
            company.clearFocus();
            email.clearFocus();
            number.clearFocus();
            website.clearFocus();
        }
    }

}
