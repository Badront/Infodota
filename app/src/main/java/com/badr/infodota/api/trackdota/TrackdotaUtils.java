package com.badr.infodota.api.trackdota;

import android.text.TextUtils;

import com.badr.infodota.api.trackdota.game.Team;

/**
 * Created by ABadretdinov
 * 16.04.2015
 * 12:11
 */
public class TrackdotaUtils {
    public static final int RADIANT=0;
    public static final int DIRE=1;
    public static String getTeamName(Team team,int align){
        return team!=null&& !TextUtils.isEmpty(team.getName())?team.getName():align==RADIANT?"Radiant":"Dire";
    }
    public static String getTeamTag(Team team,int align){
        return team!=null&& !TextUtils.isEmpty(team.getTag())?team.getTag():align==RADIANT?"Radiant":"Dire";
    }
    public static String getTeamImageUrl(Team team){
        return "http://www.trackdota.com/data/images/teams/"+team.getId()+".png";
    }
}
