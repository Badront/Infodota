package com.badr.infodota.base.task;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.streams.Stream;
import com.badr.infodota.base.service.twitch.TwitchService;
import com.badr.infodota.util.retrofit.TaskRequest;

import java.util.List;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 16:50
 */
public class TwitchStreamsLoadRequest extends TaskRequest<Stream.List> {
    private List<Stream> mFavs;

    public TwitchStreamsLoadRequest(List<Stream> favourites) {
        super(Stream.List.class);
        mFavs = favourites;
    }

    @Override
    public Stream.List loadData() throws Exception {
        TwitchService twitchService = BeanContainer.getInstance().getTwitchService();
        Stream.List result = twitchService.getGameStreams();
        if (result != null && mFavs != null) {
            for (Stream stream : result) {
                stream.setFavourite(mFavs.contains(stream));
            }
        }
        return result;
    }
}