package com.badr.infodota.fragment.trackdota.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.TwitchPlayActivity;
import com.badr.infodota.adapter.TwitchStreamsAdapter;
import com.badr.infodota.adapter.holder.StreamHolder;
import com.badr.infodota.api.streams.Stream;
import com.badr.infodota.api.streams.twitch.TwitchAccessToken;
import com.badr.infodota.api.trackdota.core.CoreResult;
import com.badr.infodota.api.trackdota.live.LiveGame;
import com.badr.infodota.fragment.RecyclerFragment;
import com.badr.infodota.fragment.twitch.TwitchGamesAdapter;
import com.badr.infodota.service.twitch.TwitchService;
import com.badr.infodota.util.DialogUtils;
import com.badr.infodota.util.ProgressTask;
import com.badr.infodota.util.Refresher;
import com.badr.infodota.util.Updatable;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.parser.Element;
import com.parser.Playlist;

import java.util.List;

/**
 * Created by Badr on 18.04.2015.
 */
public class StreamList extends RecyclerFragment<Stream, StreamHolder> implements RequestListener<Stream.List>,TwitchGamesAdapter,Updatable<Pair<CoreResult,LiveGame>> {
    private List<Stream> channels;
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    private CoreResult coreResult;
    private LiveGame liveGame;
    private Refresher refresher;
    public static StreamList newInstance(Refresher refresher,CoreResult coreResult,LiveGame liveGame){
        StreamList fragment=new StreamList();
        fragment.refresher=refresher;
        fragment.coreResult=coreResult;
        fragment.liveGame=liveGame;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
            if(coreResult!=null) {
                spiceManager.execute(new StreamsLoadRequest(), this);
            }
        }
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onRefresh() {
        if(refresher!=null){
            setRefreshing(true);
            refresher.onRefresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayoutId(R.layout.trackdota_streams);
        View view=super.onCreateView(inflater, container, savedInstanceState);
        actionMenuView= (ActionMenuView) view.findViewById(R.id.actionMenuView);
        return view;
    }

    private ActionMenuView actionMenuView;
    public static final int PLAYER_TYPE = 1403;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (Build.VERSION.SDK_INT < 14) {
            preferences.edit().putInt("player_type", 1).commit();
            actionMenuView.setVisibility(View.GONE);
        } else {
            actionMenuView.setVisibility(View.VISIBLE);
            int currentPlayer = preferences.getInt("player_type", 0);
            Menu actionMenu = actionMenuView.getMenu();
            MenuItem player = actionMenu.add(1, PLAYER_TYPE, 1, getResources().getStringArray(R.array.player_types)[currentPlayer]);
            MenuItemCompat.setShowAsAction(player, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
            actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    final MenuItem player=menuItem;
                    PopupMenu popup = new PopupMenu(getActivity(), getActivity().findViewById(menuItem.getItemId()));
                    final Menu menu = popup.getMenu();
                    String[] playerTypes = getResources().getStringArray(R.array.player_types);
                    for (int i = 0; i < playerTypes.length; i++) {
                        menu.add(2, i, 0, playerTypes[i]);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            player.setTitle(menuItem.getTitle());
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            preferences.edit().putInt("player_type", menuItem.getItemId()).commit();
                            return true;
                        }
                    });
                    popup.show();
                    return true;
                }
            });
        }
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
                            if (atResult != null) {
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
        setRefreshing(false);
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestSuccess(Stream.List streams) {
        setRefreshing(false);
        TwitchStreamsAdapter adapter = new TwitchStreamsAdapter(this, streams, channels);
        setAdapter(adapter);
    }

    @Override
    public void updateList() {
        onRefresh();
    }

    @Override
    public void onUpdate(Pair<CoreResult, LiveGame> entity) {
        coreResult=entity.first;
        liveGame=entity.second;
        setRefreshing(true);
        if(coreResult!=null) {
            spiceManager.execute(new StreamsLoadRequest(), this);
        }
    }

    public class StreamsLoadRequest extends TaskRequest<Stream.List> {
        private TwitchService twitchService=BeanContainer.getInstance().getTwitchService();

        public StreamsLoadRequest() {
            super(Stream.List.class);
        }

        @Override
        public Stream.List loadData() throws Exception {
            if(coreResult.getStreams()!=null){
                channels=twitchService.getFavouriteStreams(getActivity());
                Stream.List list=new Stream.List();
                for (Stream channel : coreResult.getStreams()) {
                    if("twitch".equals(channel.getProvider())) {
                        Stream stream = twitchService.getStream(channel.getChannel());
                        if (stream != null) {
                            list.add(stream);
                        }
                    }
                }
                return list;
            }
            return null;
        }
    }
}
