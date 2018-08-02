package com.techlogix.capturetext;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageRequest;
import com.google.api.services.language.v1.CloudNaturalLanguageScopes;
import com.google.api.services.language.v1.model.AnalyzeEntitiesRequest;
import com.google.api.services.language.v1.model.AnalyzeEntitiesResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.api.services.language.v1.model.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class FormActivity extends AppCompatActivity {

    EditText name, company, email, number, website;
    private GoogleCredential mCredential;
    private CloudNaturalLanguage mApi = new CloudNaturalLanguage.Builder(
            new NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    mCredential.initialize(request);
                }
            }).build();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // ***
        // EXTREMELY BAD PRACTICE!!! CHANGE TO ASYNC TASK WHEN DONE WITH APP FUNCTIONALITY
        // ***
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String text = getIntent().getStringExtra("DetectedText");
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

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, textLines);
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

        StringBuilder textEntities = new StringBuilder();
        for (String str : text.split("\n"))
            textEntities.append(str).append(".").append("\n");
        String textNew = textEntities.toString();

        setAccessToken(getAccessToken());
        CloudNaturalLanguageRequest<? extends GenericJson> mRequest = null;
        try {
            mRequest = analyzeEntities(textNew);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            deliverResponse(mRequest.execute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAccessToken() {
        final InputStream stream = FormActivity.this.getResources().openRawResource(R.raw.credential);
        try {
            final GoogleCredential credential = GoogleCredential.fromStream(stream)
                    .createScoped(CloudNaturalLanguageScopes.all());
            credential.refreshToken();
            return (credential.getAccessToken());
        } catch (IOException e) {
            Log.e("Access Token Error", "Failed to obtain access token.", e);
            return null;
        }
    }

    public void setAccessToken(String token) {
        mCredential = new GoogleCredential()
                .setAccessToken(token)
                .createScoped(CloudNaturalLanguageScopes.all());
    }

    public CloudNaturalLanguageRequest<? extends GenericJson> analyzeEntities(String text) throws IOException {
        // Create a new entities API call request and add it to the task queue
        Log.d("Analyze Entities Called", "analyzeEntities: ");
        return (mApi.documents()
                .analyzeEntities(new AnalyzeEntitiesRequest()
                        .setDocument(new Document().setContent(text).setType("PLAIN_TEXT"))));
    }


    private void deliverResponse(GenericJson response) {
        Log.d("Deliver Response Called", "deliverResponse: ");

        final List<Entity> entities = ((AnalyzeEntitiesResponse) response).getEntities();
        final int size = entities.size();
        final EntityInfo[] array = new EntityInfo[size];
        for (int i = 0; i < size; i++) {
            array[i] = new EntityInfo(entities.get(i));
        }
        for (Entity e : entities)
            Log.d("Entity Val", "deliverResponse: Entity Name: " + e.getName() + "\t\tEntity Type: " + e.getType());
    }
}
