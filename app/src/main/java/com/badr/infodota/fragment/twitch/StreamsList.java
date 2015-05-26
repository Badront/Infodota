package com.badr.infodota.fragment.twitch;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.adapter.TwitchStreamsAdapter;
import com.badr.infodota.api.streams.Stream;
import com.badr.infodota.service.twitch.TwitchService;
import com.badr.infodota.util.StreamUtils;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

/**
 * User: Histler
 * Date: 25.02.14
 */
public class StreamsList extends TwitchMatchListHolder implements RequestListener<Stream.List> {
    private TwitchGamesAdapter holderAdapter;
    private List<Stream> channels;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);

    public static StreamsList newInstance(TwitchGamesAdapter holderAdapter, List<Stream> channels) {
        StreamsList fragment = new StreamsList();
        fragment.setHolderAdapter(holderAdapter);
        fragment.setChannels(channels);
        return fragment;
    }

    private boolean initialized=false;
    @Override
    public void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
            if(!initialized) {
                onRefresh();
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        initialized=false;
        super.onDestroy();
    }

    public void setHolderAdapter(TwitchGamesAdapter holderAdapter) {
        this.holderAdapter = holderAdapter;
    }

    public void setChannels(List<Stream> channels) {
        this.channels = channels;
    }

    @Override
    public void updateList(List<Stream> channels) {
        this.channels = channels;
        onRefresh();
    }

    @Override
    public void onRefresh() {
        setRefreshing(true);
        spiceManager.execute(new StreamsLoadRequest(),this);
    }

    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Stream stream = getAdapter().getItem(position);
        switch (preferences.getInt("player_type", 0)) {
            case 0: {
                StreamUtils.openActivity(getActivity(),stream);
                break;
            }
            case 1: {
                StreamUtils.openInSpecialApp(getActivity(),stream);
                break;
            }
            default: {
                StreamUtils.openInVideoStreamApp(getActivity(),stream);
            }
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
        setRefreshing(false);
        Toast.makeText(getActivity(),spiceException.getLocalizedMessage(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestSuccess(Stream.List streams) {
        initialized=true;
        setRefreshing(false);
        TwitchStreamsAdapter adapter = new TwitchStreamsAdapter(holderAdapter, streams, channels);
        setAdapter(adapter);
    }

    public class StreamsLoadRequest extends TaskRequest<Stream.List>{
        private TwitchService twitchService=BeanContainer.getInstance().getTwitchService();
        public StreamsLoadRequest() {
            super(Stream.List.class);
        }

        @Override
        public Stream.List loadData() throws Exception {
            return twitchService.getGameStreams();
        }
    }
}
