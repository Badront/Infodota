package com.badr.infodota.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badr.infodota.base.R;
import com.badr.infodota.base.adapter.BaseRecyclerAdapter;
import com.badr.infodota.base.adapter.OnItemClickListener;
import com.badr.infodota.base.adapter.holder.BaseViewHolder;
import com.badr.infodota.base.util.DividerItemDecoration;

/**
 * Created by ABadretdinov
 * 24.08.2015
 * 11:37
 */
public abstract class RecyclerFragment<T, VIEW_HOLDER extends BaseViewHolder> extends Fragment implements OnItemClickListener {
    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<T, VIEW_HOLDER> mAdapter;
    private View mProgressBar;
    private View mEmptyView;

    protected int getLayoutId() {
        return R.layout.recycler_content;
    }

    public RecyclerView getRecyclerView() {
        if (mRecyclerView == null) {
            ensureList(getView());
        }
        return mRecyclerView;
    }

    protected boolean isNeedDivider() {
        return true;
    }

    public RecyclerView.LayoutManager getLayoutManager(Context context) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }

    private void ensureList(View root) {
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) root;
        } else {
            mRecyclerView = (RecyclerView) root.findViewById(android.R.id.list);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(getLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setVerticalScrollBarEnabled(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
        if (isNeedDivider()) {
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(root.getContext(), LinearLayoutManager.VERTICAL);
            mRecyclerView.addItemDecoration(itemDecoration);
        }
        mEmptyView = root.findViewById(R.id.internalEmpty);
        mProgressBar = root.findViewById(R.id.progressBar);
        if (mAdapter != null) {
            setAdapter(mAdapter);
        }
    }

    public BaseRecyclerAdapter<T, VIEW_HOLDER> getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseRecyclerAdapter<T, VIEW_HOLDER> adapter) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        mAdapter = adapter;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
        }
        if (mEmptyView != null) {
            if (mAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ensureList(view);
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
    }
}