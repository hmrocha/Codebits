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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends CodebitsActivity {
    private static final String TAG = "MainActivity";

    // private TextView textView;
    private ArrayList<Talk> talks;

    private ListView mLvTalksList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminateVisibility(false);

        this.talks = new ArrayList<Talk>();
        // textView = (TextView) findViewById(R.id.talks);
        NetworkUtils.checkConnection(this);
        new DownloadJsonTalksTask().execute(Methods.CALL_FOR_TALKS);
        mLvTalksList = (ListView) findViewById(R.id.talks_list);
        mLvTalksList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DisplayTalkActivity.class);
                intent.putExtra("talk", talks.get(position));
                intent.putExtra(Constants.AUTH_TOKEN, mToken);
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
                if (mToken != null) {
                    url += "?token=" + mToken;
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
            mLvTalksList
                    .setAdapter(new TalksArrayAdapter(MainActivity.this, talks, mToken != null));
            setProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (mMenu[itemPosition].equals("SCAN USER")) {
            scanQrCode();
        } else if (mMenu[itemPosition].equals("PROFILE")) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(Constants.AUTH_TOKEN, mToken);
            startActivity(intent);
        }
        // else if (mMenu[itemPosition].equals("CALL FOR TALKS")) {
        // Intent intent = new Intent(this, MainActivity.class);
        // intent.putExtra(Constants.AUTH_TOKEN, mToken);
        // startActivity(intent);
        // }
        return true;
    }
}
