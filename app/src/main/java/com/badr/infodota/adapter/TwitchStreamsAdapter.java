package com.badr.infodota.adapter;

import android.content.Context;
import android.text.TextUtils;
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
import com.bumptech.glide.Glide;

import java.text.MessageFormat;
import java.util.List;

/**
 * User: Histler
 * Date: 28.02.14
 */
public class TwitchStreamsAdapter extends BaseRecyclerAdapter<Stream, StreamHolder> {
    private TwitchGamesAdapter holderAdapter;
    private List<Stream> favStreams;

    public TwitchStreamsAdapter(TwitchGamesAdapter holderAdapter, Stream.List streams, List<Stream> favStreams) {
        super(streams);
        this.holderAdapter = holderAdapter;
        this.favStreams = favStreams;
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
        Context context = holder.img.getContext();
        if (TextUtils.isEmpty(stream.getImage())) {
            Glide.with(context).load(MessageFormat.format(Constants.TwitchTV.PREVIEW_URL, stream.getChannel())).placeholder(R.drawable.default_game).into(holder.img);
        } else {
            Glide.with(context).load(stream.getImage()).placeholder(R.drawable.default_game).into(holder.img);
        }
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
        if ("douyu".equals(stream.getProvider())) {
            holder.provider.setImageResource(R.drawable.douyu);
        } else {
            holder.provider.setImageResource(R.drawable.twitch);
        }
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).getId();
    }
}
