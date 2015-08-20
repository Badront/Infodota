package com.badr.infodota.base.fragment.twitch;

import com.badr.infodota.base.adapter.holder.StreamHolder;
import com.badr.infodota.base.api.streams.Stream;
import com.badr.infodota.base.fragment.RecyclerFragment;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 07.03.14
 * Time: 15:28
 */
public abstract class TwitchMatchListHolder extends RecyclerFragment<Stream, StreamHolder> {

    public abstract void updateList(List<Stream> channels);
}
