package com.badr.infodota.fragment.twitch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.activity.TwitchPlayActivity;
import com.badr.infodota.adapter.TwitchStreamsAdapter;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.twitch.AccessToken;
import com.badr.infodota.api.twitch.Channel;
import com.badr.infodota.api.twitch.Stream;
import com.badr.infodota.api.twitch.StreamTV;
import com.badr.infodota.remote.twitch.TwitchRemoteService;
import com.badr.infodota.service.twitch.TwitchService;
import com.badr.infodota.util.DialogUtils;
import com.badr.infodota.util.ProgressTask;
import com.google.gson.Gson;
import com.parser.Element;
import com.parser.Playlist;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 07.03.14
 * Time: 15:22
 */
public class FavouriteStreamsList extends TwitchMatchListHolder {
    Thread favsThread;
    private List<Channel> channels;
    private TwitchGamesAdapter holderAdapter;

    public static FavouriteStreamsList newInstance(TwitchGamesAdapter holderAdapter, List<Channel> channels) {
        FavouriteStreamsList fragment = new FavouriteStreamsList();
        fragment.setHolderAdapter(holderAdapter);
        fragment.setChannels(channels);
        return fragment;
    }

    public void setHolderAdapter(TwitchGamesAdapter holderAdapter) {
        this.holderAdapter = holderAdapter;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels != null ? channels : new ArrayList<Channel>();
    }

    @Override
    public void updateList(List<Channel> channels) {
        this.channels = channels;
        TwitchStreamsAdapter adapter = new TwitchStreamsAdapter(holderAdapter, null, channels);
        setAdapter(adapter);
        getFavourites();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TwitchStreamsAdapter adapter = new TwitchStreamsAdapter(holderAdapter, null, channels);
        setAdapter(adapter);
        getFavourites();
    }

    public void getFavourites() {
        setRefreshing(true);
        final Activity activity = getActivity();
        if (favsThread != null) {
            favsThread.interrupt();
        }
        favsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (channels != null) {
                    for (Channel channel : channels) {
                        DefaultHttpClient client = new DefaultHttpClient();
                        try {
                            HttpGet get = new HttpGet(Constants.TwitchTV.STREAM_SUBURL + URLEncoder.encode(channel.getName(), "UTF-8"));
                            HttpResponse response = client.execute(get);
                            if (response.getStatusLine().getStatusCode() == 200) {
                                String entity = EntityUtils.toString(response.getEntity());
                                StreamTV streamTV = new Gson().fromJson(entity, StreamTV.class);
                                if (streamTV.getStream() != null) {
                                    final Stream stream = streamTV.getStream();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRefreshing(false);
                                            ((TwitchStreamsAdapter) getAdapter()).addStream(stream);
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        favsThread.start();
    }

    @Override
    public void onItemClick(View view, int position) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Stream stream = getAdapter().getItem(position);
        Channel channel = stream.getChannel();
        switch (preferences.getInt("player_type", 0)) {
            case 0: {
                Intent intent;
                String channelName = channel.getName();
                intent = new Intent(getActivity(), TwitchPlayActivity.class);
                intent.putExtra("channelName", channelName);
                intent.putExtra("channelTitle", channel.getStatus());
                startActivity(intent);
                break;
            }
            case 1: {
                Intent intent;
                String url = channel.getUrl();
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;
            }
            default: {
                final String channelName = channel.getName();
                final Activity activity = getActivity();
                if (activity != null) {
                    DialogUtils.showLoaderDialog(getFragmentManager(), new ProgressTask<String>() {
                        BeanContainer container = BeanContainer.getInstance();
                        TwitchService service = container.getTwitchService();
                        TwitchRemoteService remoteService = container.getTwitchRemoteService();

                        @Override
                        public String doTask(OnPublishProgressListener listener) throws Exception {
                            Pair<AccessToken, String> atResult = service.getAccessToken(activity, channelName);
                            if (atResult.first != null) {
                                Pair<Playlist, String> playlistResult = service.getPlaylist(activity, channelName, atResult.first);
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
    public void onRefresh() {
        updateList(channels);
    }
}
