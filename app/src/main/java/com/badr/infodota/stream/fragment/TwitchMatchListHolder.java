package com.badr.infodota.stream.fragment;

import com.badr.infodota.base.fragment.RecyclerFragment;
import com.badr.infodota.stream.adapter.holder.StreamHolder;
import com.badr.infodota.stream.api.Stream;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 07.03.14
 * Time: 15:28
 */
public abstract class TwitchMatchListHolder extends RecyclerFragment<Stream, StreamHolder> {

    public abstract void updateList(List<Stream> channels);
}
