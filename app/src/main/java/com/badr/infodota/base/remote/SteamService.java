package com.badr.infodota.base.remote;

import com.badr.infodota.base.api.cosmetics.icon.ItemIconHolderResult;
import com.badr.infodota.base.api.cosmetics.player.PlayerCosmeticItem;
import com.badr.infodota.base.api.cosmetics.price.PricesResult;
import com.badr.infodota.base.api.cosmetics.store.StoreResult;
import com.badr.infodota.base.api.matchdetails.MatchDetails;
import com.badr.infodota.base.api.matchhistory.MatchHistoryResultResponse;
import com.badr.infodota.base.api.news.AppNewsResult;
import com.badr.infodota.base.api.playersummaries.PlayersResult;
import com.badr.infodota.base.api.playersummaries.friends.FriendsResult;
import com.badr.infodota.base.api.team.LogoDataHolder;
import com.badr.infodota.base.api.ti4.PrizePoolHolder;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ABadretdinov
 * 16.03.2015
 * 14:17
 */
public interface SteamService {

    @GET("/ISteamUser/getplayersummaries/v0002/")
    PlayersResult getPlayers(@Query("steamids") String steamids);

    @GET("/ISteamUser/GetFriendList/v0001/")
    FriendsResult getFriends(@Query("steamid") String steamId);

    @GET("/ISteamRemoteStorage/GetUGCFileDetails/v1/?appid=570")
    LogoDataHolder getTeamLogo(@Query("ugcid") long logoId);

    @GET("/IEconDOTA2_570/GetTournamentPrizePool/v1")
//ti4 = 600
    PrizePoolHolder getLeaguePrizePool(@Query("leagueid") long leagueId);

    @GET("/ISteamNews/GetNewsForApp/v0002/?appid=570&count=50&format=json")
    AppNewsResult getNews(@Query("enddate") Long enddate);

    @GET("/IDOTA2Match_570/GetMatchDetails/V001/")
    MatchDetails getMatchDetails(@Query("match_id") String matchId);

    @GET("/IDOTA2Match_570/GetMatchHistory/V001/")
    MatchHistoryResultResponse getMatchHistory(
            @Query("account_id") Long accountId,
            @Query("start_at_match_id") Long startMatchId,
            @Query("hero_id") Long heroId
    );

    @GET("/IEconItems_570/GetSchemaURL/v1/")
    StoreResult getCosmeticItems(@Query("language") String language);

    @GET("/ISteamEconomy/GetAssetPrices/v0001?language=en&appid=570")
    PricesResult getCosmeticItemsPrices();

    @GET("/IEconItems_570/GetPlayerItems/v0001/")
    List<PlayerCosmeticItem> getPlayerCosmeticItems(@Query("steamId") long steamId);

    @GET("/IEconDOTA2_570/GetItemIconPath/v1/?format=json")
    ItemIconHolderResult getItemIconPath(@Query("iconname") String iconname);
}