package com.techlogix.capturetext;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Map;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class FormActivityNew extends AppCompatActivity implements DataTransferInterface {

    String phoneNumber = "";
    Pattern p = Pattern.compile("(?:\\(\\d{3}\\)|\\d{3}[-]*)\\d{3}[-]*\\d{4}");
    private ArrayList<Integer> formIDs;
    //    private RecyclerView form;
    private ListView form;
    //    private RecyclerView.Adapter mAdapter;
    private CustomAdapter mAdapter;
    //    private RecyclerView.LayoutManager mLayoutManager;
    private String[] formHints;
    private ArrayList<String> domains = new ArrayList<>();
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
    private String person;
    private String organization;
    private String email;
    private String location;
    private String website;
    private Map<String, String> entitiesMap = new ArrayMap<>();
    private ArrayList<String> textLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_old);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String text = getIntent().getStringExtra("TranslatedText");
        textLines = new ArrayList<>();
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

        formIDs = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            formIDs.add(getResources().getIdentifier("et" + i, "id", getPackageName()));

        formHints = getResources().getStringArray(R.array.formHints);

        form = findViewById(R.id.formListView);
//        form.setHasFixedSize(true);

//        mLayoutManager = new LinearLayoutManager(this);
//        form.setLayoutManager(mLayoutManager);

        ///////////////////////////////////////////////////////////////////////////

        boolean phoneNumberFlag = true;
        StringBuilder textEntities = new StringBuilder();
        for (String str : text.split("\n")) {
            String checkStr = str.replaceAll("[a-zA-Z+]", "");
            checkStr = checkStr.replace(" ", "");
            if (phoneNumberFlag && checkStr.matches("\\d+")) {
                Log.d("NumberCheck", "onCreate: " + checkStr);
                phoneNumberFlag = false;
                phoneNumber = str;
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

    private String getAccessToken() {
        final InputStream stream = FormActivityNew.this.getResources().openRawResource(R.raw.credential);
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

        boolean nameFlag = true, organizationFlag = true, locationFlag = true, websiteFlag = true;
        person = "";
        organization = "";
        email = "";
        location = "";
        website = "";
        for (EntityInfo e : array)
            if (nameFlag && e.type.equalsIgnoreCase("Person")) {
                person = e.name;
                nameFlag = false;
            } else if (organizationFlag && e.type.equalsIgnoreCase("Organization")) {
                organization = e.name;
                organizationFlag = false;
            } else if (locationFlag && e.type.equalsIgnoreCase("Location")) {
                location = e.name;
                locationFlag = false;
            } else if (websiteFlag && !e.name.contains("@") && e.type.equalsIgnoreCase("Other")) {
                for (String d : domains)
                    if (e.name.contains(d)) {
                        websiteFlag = false;
                        website = e.name;
                        break;
                    }
            } else if (e.name.contains("@") && e.type.equalsIgnoreCase("Other"))
                email = e.name;

//        final View listView = findViewById(R.id.formListView);
//        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                //At this point the layout is complete and the
//                //dimensions of recyclerView and any child views are known.
//            }
//        });

        Log.d("Person Name: ", person);
        if (!person.isEmpty()) {
            if (person.contains(" ")) {
                String names[] = person.split(" ");
                final TextInputEditText firstNameET = findViewById(formIDs.get(0));
                final TextInputEditText lastNameET = findViewById(formIDs.get(1));

//                        firstNameET.setText(names[0]);
//                        lastNameET.setText(names[names.length - 1]);
                entitiesMap.put(formHints[0], names[0]);
                entitiesMap.put(formHints[1], names[names.length - 1]);
            } else {
                final TextInputEditText personET = findViewById(formIDs.get(0));
                Log.d("Person ET Text", "onGlobalLayout: " + person);
//                        personET.setText(person);
                entitiesMap.put(formHints[0], person);
                entitiesMap.put(formHints[1], "");
            }
        } else {
            entitiesMap.put(formHints[0], "");
            entitiesMap.put(formHints[1], "");
        }
        Log.d("Organization Name: ", organization);
        if (!organization.isEmpty()) {
            final TextInputEditText companyET = findViewById(formIDs.get(2));
//                    companyET.setText(organization);
            entitiesMap.put(formHints[2], organization);
        } else {
            entitiesMap.put(formHints[2], "");
        }
        Log.d("Email: ", email);
        if (!email.isEmpty()) {
            final TextInputEditText emailET = findViewById(formIDs.get(3));
//                    emailET.setText(email);
            entitiesMap.put(formHints[3], email);
        } else {
            entitiesMap.put(formHints[3], "");
        }
        Log.d("Website: ", website);
        if (!website.isEmpty()) {
            final TextInputEditText websiteET = findViewById(formIDs.get(4));
//                    websiteET.setText(website);
            entitiesMap.put(formHints[4], website);
        } else {
            entitiesMap.put(formHints[4], "");
        }
        Log.d("Phone Number: ", phoneNumber);
        if (!phoneNumber.isEmpty()) {
            final TextInputEditText phoneET = findViewById(formIDs.get(5));
//                    phoneET.setText(phoneNumber);
            entitiesMap.put(formHints[5], phoneNumber);
        } else {
            entitiesMap.put(formHints[5], "");
        }
        Log.d("Location: ", location);
        if (!location.isEmpty()) {
            final TextInputEditText addressET = findViewById(formIDs.get(8));
//                    addressET.setText(location);
            entitiesMap.put(formHints[6], location);
        } else {
            entitiesMap.put(formHints[6], location);
        }

        mAdapter = new CustomAdapter(FormActivityNew.this, formHints, textLines, FormActivityNew.this, entitiesMap);
        form.setAdapter(mAdapter);
    }


    @Override
    public void setValues(int id) {
        formIDs.add(id);
        Log.d("Interface: ", "setValues: Function Called");
    }
}