package com.badr.infodota.fragment.twitch;

import com.badr.infodota.adapter.holder.StreamHolder;
import com.badr.infodota.api.twitch.Channel;
import com.badr.infodota.api.twitch.Stream;
import com.badr.infodota.fragment.RecyclerFragment;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 07.03.14
 * Time: 15:28
 */
public abstract class TwitchMatchListHolder extends RecyclerFragment<Stream, StreamHolder> {
    public abstract void updateList(List<Channel> channels);
}
