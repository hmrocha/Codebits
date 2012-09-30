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

import net.henriquerocha.android.codebits.api.Comment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentsArrayAdapter extends ArrayAdapter<Comment> {

    private Context context;
    private List<Comment> comments;

    public CommentsArrayAdapter(Context context, List<Comment> comments) {
        super(context, R.layout.list_comment_item, comments);
        this.context = context;
        this.comments = comments;
    }

    static class ViewHolder {
        TextView author;
        TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.list_comment_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.author = (TextView) rowView.findViewById(R.id.comment_author);
            viewHolder.text = (TextView) rowView.findViewById(R.id.comment_text);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Comment comment = this.comments.get(position);
        holder.author.setText(comment.getAuthor());
        holder.text.setText(String.valueOf(comment.getText()));
        return rowView;
    }
}
