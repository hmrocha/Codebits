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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity {
    private static final String DEBUG_TAG = LoginActivity.class.getSimpleName();

    private String token;
    private String id;
    
    private TextView tvEmail;
    private TextView tvPassword;
    private TextView tvLoginFailed;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);
        setProgressBarIndeterminateVisibility(false);
        this.context = this;
        
        this.tvEmail = (TextView) findViewById(R.id.et_email);
        this.tvPassword = (TextView) findViewById(R.id.et_password);
        this.tvLoginFailed = (TextView) findViewById(R.id.tv_login_failed);
        
        SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
        this.tvEmail.setText(settings.getString(Constants.KEY_EMAIL, ""));
        this.tvPassword.setText(settings.getString(Constants.KEY_PASSWORD, ""));
    }

    public void onLogin(View v) {
        tvLoginFailed.setVisibility(View.GONE);
        new DownloadTokenTask().execute();

    }

    private class DownloadTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            try {
                result = Methods.getToken(tvEmail.getText().toString(), tvPassword.getText()
                        .toString());
                Log.d(DEBUG_TAG, "Methods.getToken: " + result);
            } catch (IOException e) {
                Log.d(DEBUG_TAG, "DownloadTokenTask::doInBackground: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                NetworkUtils.checkConnection(context);
                setProgressBarIndeterminateVisibility(false);
                return;
            }
            try {
                JSONObject json = new JSONObject(result);
                token = json.getString("token");
                id = json.getString("uid");
            } catch (JSONException e) {
                Log.d(DEBUG_TAG, e.getMessage());
                token = null;
            }
            setProgressBarIndeterminateVisibility(false);
            if (token == null) {
                tvLoginFailed.setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Constants.AUTH_TOKEN, token);
                SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.KEY_EMAIL, tvEmail.getText().toString());
                editor.putString(Constants.KEY_PASSWORD, tvPassword.getText().toString());
                editor.putString(Constants.KEY_USER_ID, id);
                editor.commit();
                startActivity(intent);
            }
        }
    }

}
