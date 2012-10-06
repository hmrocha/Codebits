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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends Activity {
    private static final String TAG = "UserActivity";
    
    private String token;
    private String id;
    
    private TextView tvName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_user);
        setProgressBarIndeterminateVisibility(false);
        this.token = getIntent().getStringExtra(Constants.AUTH_TOKEN);
        this.tvName = (TextView) findViewById(R.id.name);
        SharedPreferences settings = getSharedPreferences(Constants.LOGIN_INFO, 0);
        id = settings.getString(Constants.KEY_USER_ID, "");
        new DownloadUserTask().execute(Methods.USER);
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
                Log.d(TAG, result);
                tvName.setText(new JSONObject(result).getString("name"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setProgressBarIndeterminateVisibility(false);
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return null; //decodeSampledBitmapFromResource(getResources(), data, 100, 100));
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
