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

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 07.03.14
 * Time: 15:22
 */
public class FavouriteStreamsList extends TwitchMatchListHolder implements RequestListener<Stream.List> {
    private List<Stream> channels;
    private TwitchGamesAdapter holderAdapter;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);

    public static FavouriteStreamsList newInstance(TwitchGamesAdapter holderAdapter, List<Stream> channels) {
        FavouriteStreamsList fragment = new FavouriteStreamsList();
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
        this.channels = channels != null ? channels : new ArrayList<Stream>();
    }

    @Override
    public void updateList(List<Stream> channels) {
        this.channels = channels;
        onRefresh();
    }

    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Stream stream = getAdapter().getItem(position);
        switch (preferences.getInt("player_type", 0)) {
            case 0: {
                StreamUtils.openActivity(getActivity(), stream);
                break;
            }
            case 1: {
                StreamUtils.openInSpecialApp(getActivity(), stream);
                break;
            }
            default: {
                StreamUtils.openInVideoStreamApp(getActivity(),stream);
            }
        }
    }
    FavStreamsLoadRequest request;

    @Override
    public void onRefresh() {
        setRefreshing(true);
        if(request!=null)
        {
            spiceManager.cancel(request);
        }
        request=new FavStreamsLoadRequest(channels);
        spiceManager.execute(request,this);
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
        setAdapter(new TwitchStreamsAdapter(holderAdapter,streams,channels));
    }
    public class FavStreamsLoadRequest extends TaskRequest<Stream.List>{

        List<Stream> channels;
        TwitchService twitchService=BeanContainer.getInstance().getTwitchService();
        public FavStreamsLoadRequest(List<Stream> channels) {
            super(Stream.List.class);
            this.channels=channels;
        }

        @Override
        public Stream.List loadData() throws Exception {
            if(channels!=null){
                Stream.List list=new Stream.List();
                for (Stream channel : channels) {
                    Stream stream=twitchService.getStream(channel.getChannel());
                    if(stream!=null){
                        list.add(stream);
                    }
                }
                return list;
            }
            return null;
        }
    }
}
