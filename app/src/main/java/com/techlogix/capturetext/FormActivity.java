package com.techlogix.capturetext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class FormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Spinner spinnerName = (Spinner) findViewById(R.id.nameSpinner);
        Spinner spinnerCompany = (Spinner) findViewById(R.id.companySpinner);
        Spinner spinnerEmail = (Spinner) findViewById(R.id.emailSpinner);
        Spinner spinnerNumber = (Spinner) findViewById(R.id.numberSpinner);
        Spinner spinnerWebsite = (Spinner) findViewById(R.id.websiteSpinner);

        final EditText name = (EditText) findViewById(R.id.nameET);
        final EditText company = (EditText) findViewById(R.id.companyET);
        final EditText email = (EditText) findViewById(R.id.emailET);
        final EditText number = (EditText) findViewById(R.id.numberET);
        final EditText website = (EditText) findViewById(R.id.websiteET);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.detectedTextList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerName.setAdapter(adapter);
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCompany.setAdapter(adapter);
        spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                company.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerEmail.setAdapter(adapter);
        spinnerEmail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                email.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerNumber.setAdapter(adapter);
        spinnerNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                number.setText(parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerWebsite.setAdapter(adapter);
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
}
