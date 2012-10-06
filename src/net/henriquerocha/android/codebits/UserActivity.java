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
import java.lang.ref.WeakReference;

import net.henriquerocha.android.codebits.api.Methods;
import net.henriquerocha.android.codebits.api.User;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends CodebitsActivity {
    private static final String TAG = "UserActivity";

    private String token;
    private String id;

    private TextView mTvName;
    private TextView mTvKarmaPoints;
    private TextView mTvBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setUpActivity();
        setProgressBarIndeterminateVisibility(false);
        SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
        id = settings.getString(Constants.KEY_USER_ID, "");
        mActionBar.setSelectedNavigationItem(1);
        new DownloadUserTask().execute(Methods.USER);
    }

    private void setUpActivity() {
        token = getIntent().getStringExtra(Constants.AUTH_TOKEN);
        mTvName = (TextView) findViewById(R.id.name);
        mTvKarmaPoints = (TextView) findViewById(R.id.karma_points);
        mTvBio = (TextView) findViewById(R.id.bio);
    }

    public void showUser(User user) {
        mTvName.setText(user.getName());
        mTvKarmaPoints.setText(user.getKarma());
        mTvBio.setText(user.getBio());
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (mMenu[itemPosition].equals("SCAN USER")) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } else if (mMenu[itemPosition].equals("CALL FOR TALKS")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.AUTH_TOKEN, mToken);
            startActivity(intent);
        }
        return true;
    }

    private class DownloadUserTask extends AsyncTask<String, Void, String> {

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
                    url += "/" + id + "?token=" + token;
                }
                result = NetworkUtils.downloadUrl(url);
            } catch (IOException e) {
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                showUser(new User(new JSONObject(result)));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            setProgressBarIndeterminateVisibility(false);
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
