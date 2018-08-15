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
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class FormActivity extends AppCompatActivity {

    private EditText fName, lName, company, email, website, mPhone, wPhone, address;
    private Spinner fNameSpinner, lNameSpinner, companySpinner, emailSpinner, websiteSpinner, mPhoneSpinner, wPhoneSpinner, addressSpinner;

    private ArrayList<EditText> ETs;
    private ArrayList<Spinner> Spinners;

    private Pattern p = Pattern.compile("(?:\\(\\d{3}\\)|\\d{3}[-]*)\\d{3}[-]*\\d{4}");
    private ArrayList<String> domains = new ArrayList<>();
    private GoogleCredential mCredential;
    private CloudNaturalLanguage mApi;
    private ArrayList<String> textLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initialize();

        Log.d("Form Activity", "onCreate: Spinners List Size: " + Spinners.size());

        String text = getIntent().getStringExtra("TranslatedText");
        textLines.add("");
        Collections.addAll(textLines, text.split("\n"));

        for (int i = 0; i < Spinners.size(); i++)
            initializeSpinner(ETs.get(i), Spinners.get(i), textLines);

        final ImageView capturedImage = findViewById(R.id.capturedImage);
        final Uri imageUri = Uri.parse(getIntent().getStringExtra("BitmapImageUri"));
        final Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            capturedImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ///////////////////////////////////////////////////////////////////////////

        boolean phoneNumberFlag = true;
        StringBuilder textEntities = new StringBuilder();
        for (String str : text.split("\n")) {
            String checkStr = str.replaceAll("[a-zA-Z+]", "");
            checkStr = checkStr.replace(" ", "");
            if (phoneNumberFlag && checkStr.matches("\\d+")) {
                Log.d("NumberCheck", "onCreate: " + checkStr);
                phoneNumberFlag = false;
                mPhone.setText(str);
            }
            textEntities.append(str).append(".").append("\n");
        }
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

    private void initialize() {
        ETs = new ArrayList<>();
        Spinners = new ArrayList<>();
        textLines = new ArrayList<>();

        fName = findViewById(R.id.firstNameET);
        fNameSpinner = findViewById(R.id.firstNameSpinner);

        ETs.add(fName);
        Spinners.add(fNameSpinner);

        lName = findViewById(R.id.lastNameET);
        lNameSpinner = findViewById(R.id.lastNameSpinner);

        ETs.add(lName);
        Spinners.add(lNameSpinner);

        company = findViewById(R.id.companyET);
        companySpinner = findViewById(R.id.companySpinner);

        ETs.add(company);
        Spinners.add(companySpinner);

        email = findViewById(R.id.emailET);
        emailSpinner = findViewById(R.id.emailSpinner);

        ETs.add(email);
        Spinners.add(emailSpinner);

        website = findViewById(R.id.websiteET);
        websiteSpinner = findViewById(R.id.websiteSpinner);

        ETs.add(website);
        Spinners.add(websiteSpinner);

        mPhone = findViewById(R.id.mobilePhoneET);
        mPhoneSpinner = findViewById(R.id.mobilePhoneSpinner);

        ETs.add(mPhone);
        Spinners.add(mPhoneSpinner);

        wPhone = findViewById(R.id.workPhoneET);
        wPhoneSpinner = findViewById(R.id.workPhoneSpinner);

        ETs.add(wPhone);
        Spinners.add(wPhoneSpinner);

        address = findViewById(R.id.addressET);
        addressSpinner = findViewById(R.id.addressSpinner);

        ETs.add(address);
        Spinners.add(addressSpinner);

        mApi = new CloudNaturalLanguage.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        mCredential.initialize(request);
                    }
                }).build();

        domains.add(".com");
        domains.add(".org");
        domains.add(".net");
        domains.add(".us");
        domains.add(".ca");
        domains.add(".fr");
        domains.add(".in");
        domains.add(".pk");
        domains.add(".nl");
        domains.add(".uk");
        domains.add(".ru");
        domains.add(".br");
        domains.add(".es");
        domains.add(".cn");
        domains.add(".no");
        domains.add(".co");
        domains.add(".int");
        domains.add(".mil");
        domains.add(".edu");
        domains.add(".gov");
        domains.add(".biz");
        domains.add(".info");
        domains.add(".mobi");
        domains.add(".ly");
        domains.add(".name");
        domains.add(".jobs");
        domains.add(".tech");
    }

    private void initializeSpinner(EditText editText, Spinner spinner, ArrayList<String> menuItems) {

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(FormActivity.this, android.R.layout.simple_spinner_item, menuItems);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final EditText et = editText;

        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    et.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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


        boolean nameFlag = true, organizationFlag = true, locationFlag = true, websiteFlag = true, emailFlag = true;

        for (EntityInfo e : array)
            if (nameFlag && e.type.equalsIgnoreCase("Person")) {

                if (e.name.contains(" ")) {
                    String names[] = e.name.split(" ");
                    fName.setText(names[0]);
                    lName.setText(names[names.length - 1]);
                } else {
                    fName.setText(e.name);
                }

                nameFlag = false;
            } else if (organizationFlag && e.type.equalsIgnoreCase("Organization")) {
                company.setText(e.name);
                organizationFlag = false;
            } else if (emailFlag && e.name.contains("@") && e.type.equalsIgnoreCase("Other")) {
                email.setText(e.name);
                emailFlag = false;
            } else if (websiteFlag && !e.name.contains("@") && e.type.equalsIgnoreCase("Other")) {
                for (String d : domains)
                    if (e.name.contains(d)) {
                        websiteFlag = false;
                        website.setText(e.name);
                        break;
                    }
            } else if (locationFlag && e.type.equalsIgnoreCase("Location")) {
                address.setText(e.name);
                locationFlag = false;
            }
    }

}
