package com.pokerledger.app;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.helper.SessionSet;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityMain extends ActivityBase {
    TextView profit;
    TextView timePlayed;
    TextView hourlyWage;
    TextView listHeader;
    ListView list;
    private static final int ACTIVE_RESULT = 1;
    private static final int FINISHED_RESULT = 2;
    private ProgressDialog pdia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listHeader = (TextView) findViewById(R.id.active_games_header);
        list = (ListView)findViewById(R.id.list);

        new LoadActiveSessions().execute();

        this.profit = (TextView) findViewById(R.id.profit);
        this.timePlayed = (TextView) findViewById(R.id.time_played);
        this.hourlyWage = (TextView) findViewById(R.id.hourly_wage);
        new LoadStatistics().execute();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager manager = getFragmentManager();
                Gson gson = new Gson();

                Bundle b = new Bundle();
                b.putString("SESSION_JSON", gson.toJson(parent.getAdapter().getItem(position)));

                FragmentEditActiveSession dialog = new FragmentEditActiveSession();
                dialog.setArguments(b);
                dialog.show(manager, "EditSession");
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if ((pdia != null) && pdia.isShowing())
            pdia.dismiss();
        pdia = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            new LoadActiveSessions().execute();

            if (requestCode == FINISHED_RESULT) {
                new LoadStatistics().execute();
            }
        }
    }

    protected void notifyListChange() {
        //this method is necessary because i cant get fragments to call async tasks
        new LoadActiveSessions().execute();
    }

    public class LoadActiveSessions extends AsyncTask<Void, Void, Void> {
        ArrayList<Session> sessions = new ArrayList<>();

        public LoadActiveSessions() {}

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            sessions = dbHelper.getSessions(1);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SessionListAdapter adapter = new SessionListAdapter(ActivityMain.this, sessions);
            if (sessions.size() > 0) {
                listHeader.setVisibility(View.VISIBLE);
            } else {
                listHeader.setVisibility(View.GONE);
            }

            list.setAdapter(adapter);
        }
    }

    public class LoadStatistics extends AsyncTask<Void, Void, SessionSet> {

        @Override
        protected SessionSet doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            return new SessionSet(dbHelper.getSessions(0));
        }

        @Override
        protected void onPostExecute(SessionSet stats) {
            if (stats.getProfit() < 0 ) {
                ActivityMain.this.profit.setTextColor(Color.parseColor("#ff0000"));
                ActivityMain.this.hourlyWage.setTextColor(Color.parseColor("#ff0000"));
            }

            ActivityMain.this.profit.setText(stats.profitFormatted());
            ActivityMain.this.timePlayed.setText(stats.timeFormatted());
            ActivityMain.this.hourlyWage.setText(stats.wageFormatted());
        }
    }
}
