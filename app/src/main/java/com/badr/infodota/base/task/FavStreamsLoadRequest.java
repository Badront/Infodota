package com.badr.infodota.base.task;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.streams.Stream;
import com.badr.infodota.base.service.twitch.TwitchService;
import com.badr.infodota.util.retrofit.TaskRequest;

import java.util.List;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 15:14
 */
public class FavStreamsLoadRequest extends TaskRequest<Stream.List> {

    List<Stream> channels;

    public FavStreamsLoadRequest(List<Stream> channels) {
        super(Stream.List.class);
        this.channels = channels;
    }

    @Override
    public Stream.List loadData() throws Exception {
        TwitchService twitchService = BeanContainer.getInstance().getTwitchService();
        if (channels != null) {
            Stream.List list = new Stream.List();
            for (Stream channel : channels) {
                Stream stream = twitchService.getStream(channel.getChannel());
                if (stream != null) {
                    stream.setFavourite(true);
                    list.add(stream);
                }
            }
            return list;
        }
        return null;
    }
}
