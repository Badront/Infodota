package com.badr.infodota.adapter;

import android.view.View;
import android.widget.ListAdapter;

/**
 * Created by Badr on 16.02.2015.
 */
public interface PinnedSectionListAdapter extends ListAdapter {
    /** This method shall return 'true' if views of given type has to be pinned. */
    boolean isItemViewTypePinned(int viewType);
}

/** Wrapper class for pinned section view and its position in the list. */
class PinnedSection {
    public View view;
    public int position;
    public long id;
}