package com.badr.infodota.remote;

import com.badr.infodota.api.twitch.AccessToken;
import com.badr.infodota.api.twitch.GameStreams;
import com.badr.infodota.api.twitch.StreamTV;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Badr on 23.03.2015.
 */
public interface TwitchRestService {
    @GET("/kraken/streams?game=Dota%202&hls=true")
    GameStreams getGameStreams();
    @GET("/kraken/streams/{name}")
    StreamTV getStream(@Path("name")String channelName);
    @GET("/api/channels/{name}/access_token")
    AccessToken getAccessToken(@Path("name")String channelName);
}
