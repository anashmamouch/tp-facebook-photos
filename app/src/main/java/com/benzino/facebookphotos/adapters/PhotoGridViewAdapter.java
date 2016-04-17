package com.benzino.facebookphotos.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.model.Photo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/4/16.
 *
 * @author Anas
 */
public class PhotoGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Photo> data = new ArrayList<>();

    private SparseBooleanArray mCheckStates;


    public PhotoGridViewAdapter(Context context, int layoutResourceId, List<Photo> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        mCheckStates = new SparseBooleanArray(data.size());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null){
            LayoutInflater inflater = LayoutInflater.from(context) ;

            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkbox);
            holder.fullscreenIcon = (ImageView) row.findViewById(R.id.fullscreen);

            row.setTag(holder);

        }else{

            holder = (ViewHolder) row.getTag();
        }

        Photo photo = data.get(position);

        Picasso.with(context).load(photo.getUrl()).resize(100, 100).centerCrop().into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                mCheckStates.put(position, holder.checkBox.isChecked());
            }
        });

        /**

        holder.fullscreenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to details page
            }
        });

        **/

        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(mCheckStates.get(position, true));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mCheckStates.put((Integer)buttonView.getTag(), isChecked);
                data.get(position).setChecked(mCheckStates.get(position));
            }
        });

        return row;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    static class ViewHolder {
        ImageView image;
        ImageView fullscreenIcon;
        CheckBox checkBox;
    }
}
