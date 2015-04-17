package com.badr.infodota.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.adapter.holder.StreamHolder;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.streams.Stream;
import com.badr.infodota.fragment.twitch.TwitchGamesAdapter;
import com.badr.infodota.service.twitch.TwitchService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;
import java.util.List;

/**
 * User: Histler
 * Date: 28.02.14
 */
public class TwitchStreamsAdapter extends BaseRecyclerAdapter<Stream, StreamHolder> {
    DisplayImageOptions options;
    private ImageLoader imageLoader;
    private TwitchGamesAdapter holderAdapter;
    private List<Stream> favStreams;

    public TwitchStreamsAdapter(TwitchGamesAdapter holderAdapter, Stream.List streams, List<Stream> favStreams) {
        super(streams);
        this.holderAdapter = holderAdapter;
        this.favStreams = favStreams;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_game)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
    }

    public void addStream(Stream stream) {
        if (!mData.contains(stream)) {
            mData.add(0, stream);
        }
        notifyDataSetChanged();
    }

    @Override
    public StreamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.twitch_stream_row, parent, false);
        return new StreamHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(StreamHolder holder, int position) {
        final Stream stream = getItem(position);

        imageLoader.displayImage(MessageFormat.format(Constants.TwitchTV.PREVIEW_URL, stream.getChannel()), holder.img, options);
        holder.channel.setText(stream.getChannel());
        holder.status.setText(stream.getTitle());
        holder.viewers.setText(String.valueOf(stream.getViewers()));
        if (favStreams.contains(stream)) {
            holder.favourite.setImageResource(R.drawable.favourite_on);
            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitchService twitchService = BeanContainer.getInstance().getTwitchService();
                    twitchService.deleteStream(v.getContext(), stream);
//                    notifyDataSetChanged();
                    holderAdapter.updateList();
                }
            });
        } else {
            holder.favourite.setImageResource(R.drawable.favourite_off);
            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TwitchService twitchService = BeanContainer.getInstance().getTwitchService();
                    twitchService.addStream(v.getContext(), stream);
                    //notifyDataSetChanged();
                    holderAdapter.updateList();
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).getId();
    }
}
