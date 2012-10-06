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

import net.henriquerocha.android.codebits.api.Methods;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LauncherActivity extends Activity {

    private static final String TAG = "Launcher";

    private String mEmail;
    private String mPassword;
    private String mToken;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
        mEmail = settings.getString(Constants.KEY_EMAIL, "");
        mPassword = settings.getString(Constants.KEY_PASSWORD, "");
        Log.d(TAG, "username: " + mEmail + " password: " + mPassword);
        if (mEmail == null || mPassword == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            new DownloadTokenTask().execute();
        }
    }

    private class DownloadTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            try {
                result = Methods.getToken(mEmail, mPassword);
            } catch (IOException e) {
                Log.d(TAG, "DownloadTokenTask::doInBackground: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = null;
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    mToken = json.getString("token");
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                    mToken = null;
                }
            }
            if (mToken == null) {
                intent = new Intent(mContext, LoginActivity.class);
                Log.d(TAG, "Going to login activity.");
            } else {
                intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(Constants.AUTH_TOKEN, mToken);
                Log.d(TAG, "Going to main activity.");
            }
            startActivity(intent);
        }
    }

}
