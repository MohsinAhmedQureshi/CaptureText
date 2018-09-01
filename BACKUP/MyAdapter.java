package com.techlogix.capturetext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] mDataset;
    private ArrayList<String> mTextLines;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private Context mContext;
    private DataTransferInterface mCallback;

    // Provide a suitable constructor (depends on the kind of dataset)
    MyAdapter(Context context, String[] myDataset, ArrayList<String> myTextLines, DataTransferInterface callback) {
        mDataset = myDataset;
        mTextLines = myTextLines;
        mContext = context;
        mCallback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.form_field, parent, false);

        return (new ViewHolder(v));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.inputLayout.setHint(mDataset[position]);
        int generatedID = View.generateViewId();
        holder.editText.setId(generatedID);
        Log.d("Setting Tag", "onBindViewHolder: " + generatedID);

        mCallback.setValues(generatedID);

        spinnerArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mTextLines);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        holder.spinner.setAdapter(spinnerArrayAdapter);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    holder.editText.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextInputEditText editText;
        TextInputLayout inputLayout;
        Spinner spinner;

        ViewHolder(View itemView) {
            super(itemView);

            editText = itemView.findViewById(R.id.formET);
            spinner = itemView.findViewById(R.id.formSpinner);
            inputLayout = itemView.findViewById(R.id.inputLayout);
        }
    }
}