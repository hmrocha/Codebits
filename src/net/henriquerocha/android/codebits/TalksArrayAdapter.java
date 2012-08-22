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

import java.util.List;

import net.henriquerocha.android.codebits.api.Talk;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TalksArrayAdapter extends ArrayAdapter<Talk> {

    private Context context;
    private List<Talk> talks;
    private boolean loggedIn;

    public TalksArrayAdapter(Context context, List<Talk> talks, boolean loggedIn) {
        super(context, R.layout.list_talk_item, talks);
        this.context = context;
        this.talks = talks;
        this.loggedIn = loggedIn;
    }

    static class ViewHolder {
        TextView title;
        TextView votes;
        TextView author;
        TextView number;
        TextView rated;
        TextView description;
//        View background;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.list_talk_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.talk_title);
            viewHolder.votes = (TextView) rowView.findViewById(R.id.talk_votes);
            viewHolder.author = (TextView) rowView.findViewById(R.id.talk_author);
            viewHolder.number = (TextView) rowView.findViewById(R.id.talk_number);
            viewHolder.rated = (TextView) rowView.findViewById(R.id.talk_rated);
            viewHolder.description = (TextView) rowView.findViewById(R.id.talk_description);
//            viewHolder.background = rowView.findViewById(R.id.talk_background);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

//        int color = position % 2 == 0 ? 0xff030501 : 0xff031006;
//        holder.background.setBackgroundColor(color);
//
        Talk talk = this.talks.get(position);
        holder.title.setText(talk.getTitle().toUpperCase());
        holder.votes.setText(String.valueOf(talk.getVotes()));
        holder.author.setText(talk.getAuthor());
        holder.description.setText(talk.getDescription());
        if (holder.number != null) {
            holder.number.setText(String.valueOf(position + 1));
        }
        Resources res = context.getResources();
        if (this.loggedIn && holder.rated != null) {
            holder.rated.setTextColor(talk.isRated() ? res.getColor(R.color.green_rated) : res
                    .getColor(R.color.red_unrated));
            holder.rated.setText(talk.isRated() ? res.getString(R.string.rated) : res
                    .getString(R.string.unrated));
            holder.rated.setVisibility(View.VISIBLE);
        } 
        else {
            if (holder.rated != null) {
                holder.rated.setVisibility(View.GONE);
            }
        }
        return rowView;
    }
}
