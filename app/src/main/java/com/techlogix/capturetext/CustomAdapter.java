package com.techlogix.capturetext;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Map;


public class CustomAdapter extends ArrayAdapter<String> {

    private String[] mDataset;
    private ArrayList<String> mTextLines;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private Context mContext;
    private DataTransferInterface mCallback;
    private ViewHolder viewHolder;
    private Map<String, String> mEntitiesMap;
    private int lastPosition = -1;

    public CustomAdapter(Context context, String[] myDataset, ArrayList<String> myTextLines, DataTransferInterface callback, Map<String, String> entitiesMap) {
        super(context, R.layout.form_field, myDataset);
        mDataset = myDataset;
        mTextLines = myTextLines;
        mContext = context;
        mCallback = callback;
        mEntitiesMap = new ArrayMap<>();
        mEntitiesMap.putAll(entitiesMap);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.form_field, parent, false);
            viewHolder.inputLayout = convertView.findViewById(R.id.inputLayout);
            viewHolder.editText = convertView.findViewById(R.id.formET);
            viewHolder.spinner = convertView.findViewById(R.id.formSpinner);

            viewHolder.inputLayout.setHint(mDataset[position]);
            String value = mEntitiesMap.get(mDataset[position]);
            Log.d("Form Value: ", mDataset[position] + ": " + value);
            viewHolder.editText.setText(value);
            Log.d("Setting Tag", "Position: " + position);
            int id = getContext().getResources().getIdentifier("et" + position, "id", getContext().getPackageName());
            viewHolder.editText.setId(id);
            Log.d("Setting Tag", "onBindViewHolder: " + id);

//            mCallback.setValues(id);

            spinnerArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mTextLines);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            viewHolder.spinner.setAdapter(spinnerArrayAdapter);
            viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                    if (spinnerPosition != 0)
                        viewHolder.editText.setText(parent.getItemAtPosition(spinnerPosition).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {

        TextInputLayout inputLayout;
        TextInputEditText editText;
        Spinner spinner;
    }
}
