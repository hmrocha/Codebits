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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Codebits Talk
 * 
 * @author Henrique Rocha <hmrocha@gmail.com>
 * 
 */
public class Talk implements Parcelable {

    private static final String UNRATED = "none";
    
    private String id;
    private String title;
    private String author;
    private String description;
    private String proposed;
    private int upVotes;
    private int downVotes;
    private String rate;

    public static final Parcelable.Creator<Talk> CREATOR = new Parcelable.Creator<Talk>() {

        public Talk createFromParcel(Parcel source) {
            return new Talk(source);
        }

        public Talk[] newArray(int size) {
            return new Talk[size];
        }
    
    };
    
    /**
     * Create a Talk from a JSONObject
     * 
     * @param jsonObject
     *            the JSONObject to parse the talk from.
     * @throws JSONException
     */
    public Talk(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.title = jsonObject.getString("title");
        this.author = jsonObject.getString("user");
        this.description = jsonObject.getString("description");
        this.proposed = jsonObject.getString("regdate");
        this.upVotes = Integer.parseInt(jsonObject.getString("up"));
        this.downVotes = Integer.parseInt(jsonObject.getString("down"));
        this.rate = jsonObject.getString("rated");
    }

    private Talk(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.author = source.readString();
        this.description = source.readString();
        this.proposed = source.readString();
        this.upVotes = source.readInt();
        this.downVotes = source.readInt();
        this.rate = source.readString();
    }
    
    /**
     * Talk ID.
     * @return the id for this talk.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Talk title.
     * 
     * @return the title for this talk.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Talk author.
     * 
     * @return the author of this talk.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Talk description.
     * @return the description of this talk.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Talk's proposed date.
     * @return the date when this talk has been proposed.
     */
    public String getProposed() {
        return proposed;
    }
    
    /**
     * Number of votes.
     * 
     * @return the number of votes for this talk.
     */
    public int getVotes() {
        return upVotes + downVotes;
    }

    /**
     * Number of up votes.
     * 
     * @return the number of up votes for this talk.
     */
    public int getUpVotes() {
        return upVotes;
    }

    /**
     * Number of down votes.
     * 
     * @return the number of down votes for this talk.
     */
    public int getDownVotes() {
        return downVotes;
    }

    /**
     * Is this talk rated?
     * @return true if the talk was rated by the user, false otherwise.
     */
    public boolean isRated() {
        return !UNRATED.equals(rate);
    }
    
    /**
     * The rate for this talk.
     * @return "up" or "down" if isRated(), "none" otherwise
     */
    public String getRate() {
        return rate;
    }
    
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.description);
        dest.writeString(this.proposed);
        dest.writeInt(this.upVotes);
        dest.writeInt(this.downVotes);
        dest.writeString(this.rate);
    }
}