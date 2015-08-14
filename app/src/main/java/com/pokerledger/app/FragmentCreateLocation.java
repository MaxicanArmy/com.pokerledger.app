package com.pokerledger.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Catface Meowmers on 8/16/15.
 */
public class FragmentCreateLocation extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_location, null, false);
        getDialog().setTitle("Create Location");

        Button createLocation = (Button) view.findViewById(R.id.create_location);
        createLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String locationName = ((EditText) getView().findViewById(R.id.location_name)).getText().toString();

                if (locationName.equals("")) {
                    Toast.makeText(getActivity(), "You must enter a location name or press cancel.", Toast.LENGTH_SHORT).show();
                } else {
                    ((ActivityBase) getActivity()).notifyCreateLocation(locationName);
                    dismiss();
                }
            }
        });

        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
