package com.aaburov.tedrssviewer;

/**
 * Created by Giorgio on 15.05.2015.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PostItemAdapter extends ArrayAdapter<RSSPostData> {
    private Activity myContext;
    private ArrayList<RSSPostData> datas;
    private TCImageLoader myTCImageLoader;

    public PostItemAdapter(Context context, int textViewResourceId, ArrayList<RSSPostData> objects) {
        super(context, textViewResourceId, objects);
        myContext = (Activity) context;
        datas = objects;
        myTCImageLoader= new TCImageLoader(context);
    }

    static class ViewHolder {
        TextView postTitleView;
        TextView postDateView;
        ImageView postThumbView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.rss_item,null);

            viewHolder = new ViewHolder();
            viewHolder.postThumbView = (ImageView) convertView.findViewById(R.id.ivPicture);
            viewHolder.postTitleView = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.postDateView = (TextView) convertView.findViewById(R.id.tvInfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        myTCImageLoader.display(datas.get(position).postThumbUrl,viewHolder.postThumbView,R.drawable.no_image);
        viewHolder.postTitleView.setText(datas.get(position).postTitle);
        viewHolder.postDateView.setText(datas.get(position).postInfo);
        return convertView;
    }

}