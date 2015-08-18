package com.pokerledger.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pokerledger.app.helper.DatabaseHelper;
import com.pokerledger.app.model.Blinds;
import com.pokerledger.app.model.Game;
import com.pokerledger.app.model.GameFormat;
import com.pokerledger.app.model.Location;
import com.pokerledger.app.model.Session;

import java.util.ArrayList;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public class ActivityBase extends Activity {
    Session activeSession = new Session();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.add_session :
                FragmentManager manager = getFragmentManager();

                FragmentAddSession dialog = new FragmentAddSession();
                dialog.show(manager, "AddSession");
                break;
            case R.id.history :
                Intent history = new Intent(this, ActivityHistory.class);
                this.startActivity(history);
                break;
            /*
            case R.id.statistics :
                Intent statistics = new Intent(this, StatisticsActivity.class);
                this.startActivity(statistics);
                break;
            case R.id.filter :
                Intent filter = new Intent(this, FilterActivity.class);
                this.startActivity(filter);
                break;
            case R.id.backup :
                Intent backup = new Intent(this, BackupActivity.class);
                this.startActivity(backup);
                break;
                */
        }

        return super.onOptionsItemSelected(item);
    }

    protected void notifyCreateLocation(String value) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateLocation().execute(new Location(0, value, 0));
    }

    protected void notifyCreateGame(String value) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateGame().execute(new Game(0, value, 0));
    }

    protected void notifyCreateGameFormat(String value) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateLocation().execute(new Location(0, value, 0));
    }

    protected void notifyCreateBlinds(int sb, int bb, int straddle, int bringIn, int ante, int perPoint) {
        //this method is necessary because i cant get fragments to call async tasks
        new CreateBlinds().execute(new Blinds(sb, bb, straddle, bringIn, ante, perPoint, 0));
    }

    public void showCreateLocationDialog(View v) {
        FragmentManager manager = getFragmentManager();
        FragmentCreateLocation fcl = new FragmentCreateLocation();
        fcl.show(manager, "CreateLocation");

        /*
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add Location");

        //Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint("Location Name");
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                new CreateLocation().execute(new Location(0, value, 0));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        */
    }

    public void showCreateGameDialog(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Add Game");

        //Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint("Game Description");
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                new CreateGame().execute(new Game(0, value, 0));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void showCreateGameFormatDialog(View v) {

    }

    public void showCreateBlindsDialog(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View blindEntryView = factory.inflate(R.layout.fragment_add_blinds, null);
        alert.setView(blindEntryView);

        alert.setTitle("Add Blinds");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String sbText = ((EditText) blindEntryView.getRootView().findViewById(R.id.small_blind)).getText().toString();
                String bbText = ((EditText) blindEntryView.getRootView().findViewById(R.id.big_blind)).getText().toString();
                String strText = ((EditText) blindEntryView.getRootView().findViewById(R.id.straddle)).getText().toString();
                String biText = ((EditText) blindEntryView.getRootView().findViewById(R.id.bring_in)).getText().toString();
                String anteText = ((EditText) blindEntryView.getRootView().findViewById(R.id.ante)).getText().toString();
                String ppText = ((EditText) blindEntryView.getRootView().findViewById(R.id.points)).getText().toString();

                int sb = 0, bb = 0, straddle = 0, bringIn = 0, ante = 0, perPoint = 0;

                if (!bbText.equals("") && sbText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Blinds not added. If there is only one blind enter it in SB.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!ppText.equals("") && (!sbText.equals("") || !bbText.equals("") || !strText.equals("") || !biText.equals("") || !anteText.equals(""))) {
                    Toast.makeText(getApplicationContext(), "Blinds not added. You cannot enter other blinds when using points.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!strText.equals("") && (sbText.equals("") || bbText.equals(""))) {
                    Toast.makeText(getApplicationContext(), "Blinds not added. You must enter a SB and BB to enter a straddle.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!sbText.equals("")) {
                    sb = Integer.parseInt(sbText);
                }
                if (!bbText.equals("")) {
                    bb = Integer.parseInt(bbText);
                }
                if (!strText.equals("")) {
                    straddle = Integer.parseInt(strText);
                }
                if (!biText.equals("")) {
                    bringIn = Integer.parseInt(biText);
                }
                if (!anteText.equals("")) {
                    ante = Integer.parseInt(anteText);
                }
                if (!ppText.equals("")) {
                    perPoint = Integer.parseInt(ppText);
                }

                new CreateBlinds().execute(new Blinds(sb, bb, straddle, bringIn, ante, perPoint, 0));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = alert.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public class CreateLocation extends AsyncTask<Location, Void, Location> {
        @Override
        protected Location doInBackground(Location... loc) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.createLocation(loc[0]);
        }

        @Override
        protected void onPostExecute(Location result) {
            activeSession.setLocation(result);
            new LoadLocations().execute();
        }
    }

    public class LoadLocations extends AsyncTask<Void, Void, ArrayList<Location>> {
        @Override
        protected ArrayList<Location> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.getAllLocations("frequency");
        }

        @Override
        protected void onPostExecute(ArrayList<Location> result) {
            Spinner locationSpinner = (Spinner) findViewById(R.id.location);
            ArrayAdapter locationAdapter = new ArrayAdapter(ActivityBase.this, android.R.layout.simple_spinner_item, result);
            locationAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            locationSpinner.setAdapter(locationAdapter);
            setLocation();
        }
    }

    public void setLocation() {
        Spinner locationSpinner = (Spinner) findViewById(R.id.location);

        if (this.activeSession.getLocation().getId() > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < locationSpinner.getCount()) {
                Location currentLocation = (Location) locationSpinner.getItemAtPosition(count);
                if (this.activeSession.getLocation().getId() == currentLocation.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            locationSpinner.setSelection(spinnerPos);
        }
    }

    public class CreateGame extends AsyncTask<Game, Void, Game> {
        @Override
        protected Game doInBackground(Game... loc) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.createGame(loc[0]);
        }

        @Override
        protected void onPostExecute(Game result) {
            activeSession.setGame(result);
            new LoadGames().execute();
        }
    }

    public class LoadGames extends AsyncTask<Void, Void, ArrayList<Game>> {
        @Override
        protected ArrayList<Game> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.getAllGames(null);
        }

        @Override
        protected void onPostExecute(ArrayList<Game> result) {
            Spinner gameSpinner = (Spinner) findViewById(R.id.game);
            ArrayAdapter gameAdapter = new ArrayAdapter(ActivityBase.this, android.R.layout.simple_spinner_item, result);
            gameAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            gameSpinner.setAdapter(gameAdapter);
            setGame();
        }
    }

    public void setGame() {
        Spinner gameSpinner = (Spinner) findViewById(R.id.game);

        if (this.activeSession.getGame().getId() > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < gameSpinner.getCount()) {
                Game currentGame = (Game) gameSpinner.getItemAtPosition(count);
                if (this.activeSession.getGame().getId() == currentGame.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            gameSpinner.setSelection(spinnerPos);
        }
    }

    public class CreateGameFormat extends AsyncTask<GameFormat, Void, GameFormat> {
        @Override
        protected GameFormat doInBackground(GameFormat... g) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.createGameFormat(g[0]);
        }

        @Override
        protected void onPostExecute(GameFormat result) {
            activeSession.setGameFormat(result);
            new LoadGameFormats().execute();
        }
    }

    public class LoadGameFormats extends AsyncTask<Void, Void, ArrayList<GameFormat>> {
        @Override
        protected ArrayList<GameFormat> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.getAllGameFormats(null);
        }

        @Override
        protected void onPostExecute(ArrayList<GameFormat> result) {
            Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_formats);
            ArrayAdapter gameFormatAdapter = new ArrayAdapter(ActivityBase.this, android.R.layout.simple_spinner_item, result);
            gameFormatAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            gameFormatSpinner.setAdapter(gameFormatAdapter);
            setGameFormat();
        }
    }

    public void setGameFormat() {
        Spinner gameFormatSpinner = (Spinner) findViewById(R.id.game_formats);

        if (this.activeSession.getGameFormat().getId() > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < gameFormatSpinner.getCount()) {
                GameFormat currentGameFormat = (GameFormat) gameFormatSpinner.getItemAtPosition(count);
                if (this.activeSession.getGameFormat().getId() == currentGameFormat.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            gameFormatSpinner.setSelection(spinnerPos);
        }
    }

    public class CreateBlinds extends AsyncTask<Blinds, Void, Blinds> {
        @Override
        protected Blinds doInBackground(Blinds... set) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.createBlinds(set[0]);
        }

        @Override
        protected void onPostExecute(Blinds result) {
            activeSession.setBlinds(result);
            new LoadBlinds().execute();
        }
    }

    public class LoadBlinds extends AsyncTask<Void, Void, ArrayList<Blinds>> {
        @Override
        protected ArrayList<Blinds> doInBackground(Void... params) {
            DatabaseHelper db;
            db = new DatabaseHelper(getApplicationContext());
            return db.getAllBlinds(null);
        }

        @Override
        protected void onPostExecute(ArrayList<Blinds> result) {
            Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);
            ArrayAdapter blindsAdapter = new ArrayAdapter(ActivityBase.this, android.R.layout.simple_spinner_item, result);
            blindsAdapter.setDropDownViewResource(R.layout.spinner_item_view);
            blindsSpinner.setAdapter(blindsAdapter);
            setBlinds();
        }
    }

    public void setBlinds() {
        Spinner blindsSpinner = (Spinner) findViewById(R.id.blinds);

        if (activeSession.getBlinds().getId() > 0) {
            int count = 0;
            int spinnerPos = -1;
            while (spinnerPos == -1 && count < blindsSpinner.getCount()) {
                Blinds currentBlinds = (Blinds) blindsSpinner.getItemAtPosition(count);
                if (this.activeSession.getBlinds().getId() == currentBlinds.getId()) {
                    spinnerPos = count;
                }
                count++;
            }
            blindsSpinner.setSelection(spinnerPos);
        }
    }

}
