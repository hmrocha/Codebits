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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String DEBUG_TAG = NetworkUtils.class.getSimpleName();

    public static String downloadUrl(String urlString) throws IOException {
        URL url = null;
        String result = null;
        InputStream is = null;
        try {
            url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            // Convert the InputStream into a string
            StringBuffer contentAsString = new StringBuffer();
            String s = null;
            BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((s = r.readLine()) != null) {
                contentAsString.append(s);
            }
            result = contentAsString.toString();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return result;
    }
}
