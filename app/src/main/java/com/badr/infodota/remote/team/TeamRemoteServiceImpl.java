package com.badr.infodota.remote.team;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.R;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.team.LogoDataHolder;
import com.badr.infodota.remote.BaseRemoteServiceImpl;

import java.text.MessageFormat;

/**
 * User: ABadretdinov
 * Date: 15.05.14
 * Time: 16:53
 */
public class TeamRemoteServiceImpl extends BaseRemoteServiceImpl implements TeamRemoteService {
    @Override
    public Pair<String, String> getTeamLogo(Context context, Long logoId) throws Exception {
        String url = MessageFormat.format(Constants.Team.SUBURL, context.getString(R.string.api), String.valueOf(logoId));
        Pair<LogoDataHolder, String> result = basicRequestSend(context, url, LogoDataHolder.class);
        if (result.first != null) {
            String logoUrl = result.first.getUrl();
            return Pair.create(logoUrl, result.second);
        }
        return Pair.create(null, result.second);
    }
}
