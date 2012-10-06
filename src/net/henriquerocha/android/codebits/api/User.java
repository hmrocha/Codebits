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

package net.henriquerocha.android.codebits.api;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String name;
    private String bio;
    private String karma;

    public User(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.bio = jsonObject.getString("bio");
        this.karma = jsonObject.getString("karma");
    }

    public String getName() {
        return name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public String getKarma() {
        return karma;
    }
}
