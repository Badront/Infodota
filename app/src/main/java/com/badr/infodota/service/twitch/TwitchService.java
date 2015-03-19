package com.badr.infodota.service.twitch;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.InitializingBean;
import com.badr.infodota.api.twitch.AccessToken;
import com.badr.infodota.api.twitch.Channel;
import com.parser.Playlist;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 16.05.14
 * Time: 17:55
 */
public interface TwitchService extends InitializingBean {
    AccessToken getAccessToken(Context context, String channelName);

    Pair<Playlist, String> getPlaylist(Context context, String channelName, AccessToken accessToken);

    boolean isStreamFavourite(Context context, Channel channel);

    void addStream(Context context, Channel channel);

    void deleteStream(Context context, Channel channel);

    List<Channel> getFavouriteStreams(Context context);
}
