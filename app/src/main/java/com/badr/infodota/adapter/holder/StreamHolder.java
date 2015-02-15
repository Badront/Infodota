package com.badr.infodota.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.adapter.OnItemClickListener;

/**
 * Created by Badr on 21.12.2014.
 */
public class StreamHolder extends BaseViewHolder {

    public ImageView img;
    public TextView status;
    public TextView channel;
    public TextView viewers;
    public ImageView favourite;

    public StreamHolder(View itemView, OnItemClickListener listener) {
        super(itemView, listener);
        img = (ImageView) itemView.findViewById(R.id.img);
        channel = (TextView) itemView.findViewById(R.id.channel);
        status = (TextView) itemView.findViewById(R.id.status);
        viewers = (TextView) itemView.findViewById(R.id.viewers);
        favourite = (ImageView) itemView.findViewById(R.id.favourite);
    }
}
