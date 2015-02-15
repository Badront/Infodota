package com.badr.infodota.fragment.twitch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.activity.TwitchPlayActivity;
import com.badr.infodota.adapter.TwitchStreamsAdapter;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.twitch.AccessToken;
import com.badr.infodota.api.twitch.Channel;
import com.badr.infodota.api.twitch.GameStreams;
import com.badr.infodota.api.twitch.Stream;
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
import java.util.List;

/**
 * User: Histler
 * Date: 25.02.14
 */
public class StreamsList extends TwitchMatchListHolder {
    private TwitchGamesAdapter holderAdapter;
    private List<Channel> channels;

    public static StreamsList newInstance(TwitchGamesAdapter holderAdapter, List<Channel> channels) {
        StreamsList fragment = new StreamsList();
        fragment.setHolderAdapter(holderAdapter);
        fragment.setChannels(channels);
        return fragment;
    }

    public void setHolderAdapter(TwitchGamesAdapter holderAdapter) {
        this.holderAdapter = holderAdapter;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadSteams();
    }

    private void loadSteams() {
        final ActionBarActivity activity = (ActionBarActivity) getActivity();
        setRefreshing(true);
        if (activity != null) {
            activity.setSupportProgressBarIndeterminateVisibility(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DefaultHttpClient client = new DefaultHttpClient();
                    String url = Constants.TwitchTV.DOTA_GAMES;
                    try {
                        HttpResponse response = client.execute(new HttpGet(url));
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String entity = EntityUtils.toString(response.getEntity());
                            GameStreams streams = new Gson().fromJson(entity, GameStreams.class);
                            List<Stream> streamsList = streams.getStreams();
                            final TwitchStreamsAdapter adapter = new TwitchStreamsAdapter(holderAdapter, streamsList, channels);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setAdapter(adapter);
                                    setRefreshing(false);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.setSupportProgressBarIndeterminateVisibility(false);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        super.onDestroy();
    }

    @Override
    public void updateList(List<Channel> channels) {
        this.channels = channels;
        loadSteams();
    }

    @Override
    public void onRefresh() {
        loadSteams();
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
}
