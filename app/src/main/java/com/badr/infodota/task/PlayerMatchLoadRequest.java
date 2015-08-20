package com.badr.infodota.task;

import android.content.Context;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.matchhistory.PlayerMatchResult;
import com.badr.infodota.service.match.MatchService;
import com.badr.infodota.util.retrofit.TaskRequest;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 16:09
 */
public class PlayerMatchLoadRequest extends TaskRequest<PlayerMatchResult> {
    private BeanContainer container = BeanContainer.getInstance();
    private MatchService matchService = container.getMatchService();

    private long mFromMatchId;
    private long mAccountId;
    private Long mHeroId;
    private boolean mRecreate;
    private Context mContext;

    public PlayerMatchLoadRequest(Context context, boolean recreate, long accountId, long fromMatchId, Long heroId) {
        super(PlayerMatchResult.class);
        mContext = context;
        this.mRecreate = recreate;
        this.mFromMatchId = fromMatchId;
        this.mAccountId = accountId;
        this.mHeroId = heroId;
    }

    @Override
    public PlayerMatchResult loadData() throws Exception {
        PlayerMatchResult result = matchService.getMatches(mContext, mAccountId, mFromMatchId, mHeroId);
        if (result != null) {
            result.setRecreate(mRecreate);
        }
        return result;

    }
}
