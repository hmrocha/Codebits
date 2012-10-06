/*  
 *  Codebits
 *  Copyright (C) 2012 Henrique Rocha <hmrocha@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.henriquerocha.android.codebits;

import java.io.IOException;
import java.util.ArrayList;

import net.henriquerocha.android.codebits.api.Methods;
import net.henriquerocha.android.codebits.api.Talk;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockListActivity implements OnNavigationListener {
    private static final String TAG = DownloadJsonTalksTask.class.getSimpleName();

    // private TextView textView;
    private ArrayList<Talk> talks;
    private ListActivity context;
    private String token;
    private String[] menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminateVisibility(false);
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(this, R.array.menu,
                R.layout.sherlock_spinner_item);
        this.menu = getResources().getStringArray(R.array.menu);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(list, this);
        actionBar.setDisplayShowTitleEnabled(false);

        this.context = this;
        this.talks = new ArrayList<Talk>();
        this.token = getIntent().getStringExtra(Constants.AUTH_TOKEN);

        // textView = (TextView) findViewById(R.id.talks);
        NetworkUtils.checkConnection(this);
        new DownloadJsonTalksTask().execute(Methods.CALL_FOR_TALKS);
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DisplayTalkActivity.class);
                intent.putExtra("talk", talks.get(position));
                intent.putExtra(Constants.AUTH_TOKEN, token);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_sign_out:
            SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(Constants.KEY_EMAIL);
            editor.remove(Constants.KEY_PASSWORD);
            editor.commit();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.talks = new ArrayList<Talk>();
        new DownloadJsonTalksTask().execute(Methods.CALL_FOR_TALKS);
    }

    private class DownloadJsonTalksTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            try {
                String url = urls[0];
                if (token != null) {
                    url += "?token=" + token;
                }
                result = NetworkUtils.downloadUrl(url);
            } catch (IOException e) {
                result = "Unable to retrieve web page. URL may be invalid.";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonTalks = new JSONArray(result);
                for (int i = 0; i < jsonTalks.length(); i++) {
                    talks.add(new Talk(jsonTalks.getJSONObject(i)));
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
            context.setListAdapter(new TalksArrayAdapter(context, talks, token != null));
            setProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (this.menu[itemPosition].equals("SCAN USER")) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } else if (this.menu[itemPosition].equals("PROFILE")) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(Constants.AUTH_TOKEN, token);
            startActivity(intent);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format,
                        Toast.LENGTH_LONG);
                toast.show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.show();

            }
        }
    }
}
