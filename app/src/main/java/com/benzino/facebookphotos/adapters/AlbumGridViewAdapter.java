package com.benzino.facebookphotos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.model.Album;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 15/4/16.
 *
 * @author Anas
 */
public class AlbumGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;

    private List<Album> data = new ArrayList<>();

    public AlbumGridViewAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        this.context = context;
        this.layoutResourceId = layoutResourceId;

    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param layoutResourceId The resource ID for a layout file containing a TextView to use when
     */
    public AlbumGridViewAdapter(Context context, int layoutResourceId, List<Album> data) {
        super(context, layoutResourceId);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null){
            LayoutInflater inflater = LayoutInflater.from(context);

            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();

            holder.textView  = (TextView) row.findViewById(R.id.album_title);
            holder.coverImageView = (ImageView) row.findViewById(R.id.album_cover_image);

            row.setTag(holder);
        }else {
            holder = (ViewHolder) row.getTag();
        }

        /*Sorting data by name*/
        Collections.sort(data, ComparatorByName);

        final Album album = data.get(position);

        holder.textView.setText(album.getTitle());

        Picasso.with(context).load(album.getCoverPhotoUrl()).resize(180, 180).centerCrop().into(holder.coverImageView);

        holder.coverImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = album.getId();
            }
        });


        return row;
    }

    public Comparator<Album> ComparatorByName = new Comparator<Album>() {
        @Override
        public int compare(Album album1, Album album2) {
            if (album1.getTitle().equals(album2.getTitle())){
                return 0;
            }
            return album1.getTitle().compareTo(album2.getTitle());
        }
    };

    @Override
    public int getCount() {
        return data.size();
    }

    public static class ViewHolder{
        TextView textView;
        ImageView coverImageView;
    }
}
