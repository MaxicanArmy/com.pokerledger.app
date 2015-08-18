package com.pokerledger.app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;

import java.util.Calendar;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityActiveSession extends ActivitySession {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);

        Calendar calender = Calendar.getInstance();
        ((Button) findViewById(R.id.start_date)).setHint(String.format("%04d-%02d-%02d", calender.get(Calendar.YEAR), calender.get(Calendar.MONTH)+1, calender.get(Calendar.DAY_OF_MONTH)));
        ((Button) findViewById(R.id.start_time)).setHint(String.format("%02d:%02d", calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE)));
    }

    public void saveActiveSession(View v) {

        //begin error checking and capturing the values to be saved in the db
        String buyinText = ((EditText) findViewById(R.id.buy_in)).getText().toString();

        if (buyinText.equals("")) {
            Toast.makeText(this, "You must enter a buy in amount.", Toast.LENGTH_SHORT).show();
            findViewById(R.id.buy_in).requestFocus();
            return;
        }
        else {
            this.activeSession.setBuyIn(Integer.parseInt(buyinText));
        }

        Spinner formatSpinner = (Spinner) findViewById(R.id.game_formats);

        if (formatSpinner.getSelectedItem() != null) {
            this.activeSession.setGameFormat((GameFormat) formatSpinner.getSelectedItem());

            if (this.activeSession.getGameFormat().getBaseFormatId() == 2) {
                String entrantsText = ((EditText) findViewById(R.id.entrants)).getText().toString();

                if (!entrantsText.equals("")) {
                    this.activeSession.setEntrants(Integer.parseInt(entrantsText));
                }
            }
            else {
                Spinner blinds = (Spinner) findViewById(R.id.blinds);

                if (blinds.getSelectedItem() != null) {
                    this.activeSession.setBlinds((Blinds) blinds.getSelectedItem());
                } else {
                    Toast.makeText(this, "You must enter the blinds.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(this, "You must select a format.", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = ((Button) findViewById(R.id.start_date)).getHint().toString();

        if (startDate.equals("Start Date")) {
            Toast.makeText(this, "Select a start date for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        String startTime = ((Button) findViewById(R.id.start_time)).getHint().toString();

        if (startTime.equals("Start Time")) {
            Toast.makeText(this, "Select a start time for this session.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.activeSession.setStartDate(startDate);
        this.activeSession.setStartTime(startTime);

        String note = ((EditText) findViewById(R.id.note)).getText().toString();

        if (!note.equals("")) {
            this.activeSession.setNote(note);
        }

        this.activeSession.setGame((Game) ((Spinner) findViewById(R.id.game)).getSelectedItem());

        Spinner location = (Spinner) findViewById(R.id.location);

        if (location.getSelectedItem() != null) {
            this.activeSession.setLocation((Location) location.getSelectedItem());
        } else {
            Toast.makeText(this, "You must enter the location.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.activeSession.setState(1);
        new AddSession().execute(this.activeSession);
        setResult(RESULT_OK);
        finish();
    }
}