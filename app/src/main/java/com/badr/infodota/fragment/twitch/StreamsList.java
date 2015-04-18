package com.badr.infodota.fragment.twitch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.activity.TwitchPlayActivity;
import com.badr.infodota.adapter.TwitchStreamsAdapter;
import com.badr.infodota.api.streams.Stream;
import com.badr.infodota.api.streams.twitch.TwitchAccessToken;
import com.badr.infodota.service.twitch.TwitchService;
import com.badr.infodota.util.DialogUtils;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.parser.Element;
import com.parser.Playlist;

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
                Intent intent;
                intent = new Intent(getActivity(), TwitchPlayActivity.class);
                intent.putExtra("channelName", stream.getChannel());
                intent.putExtra("channelTitle", stream.getTitle());
                startActivity(intent);
                break;
            }
            case 1: {
                Intent intent;
                String url = "http://www.twitch.tv/"+stream.getChannel();
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            }
            default: {
                final String channelName = stream.getChannel();
                final Activity activity = getActivity();
                if (activity != null) {
                    DialogUtils.showLoaderDialog(getFragmentManager(), new ProgressTask<String>() {
                        BeanContainer container = BeanContainer.getInstance();
                        TwitchService service = container.getTwitchService();

                        @Override
                        public String doTask(OnPublishProgressListener listener) throws Exception {
                            TwitchAccessToken atResult = service.getAccessToken(channelName);
                            if (atResult!= null) {
                                Pair<Playlist, String> playlistResult = service.getPlaylist(activity, channelName, atResult);
                                Playlist playlist = playlistResult.first;
                                List<Element> elements = playlist.getElements();
                                if (elements != null && elements.size() > 0) {
                                    return elements.get(0).getURI().toString();
                                }
                            }
                            return "";
                        }

                        @Override
                        public void doAfterTask(String result) {
                            if (!TextUtils.isEmpty(result)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(result), "application/x-mpegURL");//"video/m3u8");  //
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void handleError(String error) {

                        }

                        @Override
                        public String getName() {
                            return null;
                        }
                    });
                }
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
