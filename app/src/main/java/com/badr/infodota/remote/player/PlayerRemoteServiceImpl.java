package com.badr.infodota.remote.player;

import android.content.Context;
import android.util.Pair;

import com.badr.infodota.R;
import com.badr.infodota.api.Constants;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.playersummaries.Player;
import com.badr.infodota.api.playersummaries.PlayerResponse;
import com.badr.infodota.api.playersummaries.PlayerResults;
import com.badr.infodota.api.playersummaries.friends.Friend;
import com.badr.infodota.api.playersummaries.friends.FriendsList;
import com.badr.infodota.api.playersummaries.friends.FriendsResult;
import com.badr.infodota.remote.BaseRemoteServiceImpl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 15:51
 */
public class PlayerRemoteServiceImpl extends BaseRemoteServiceImpl implements PlayerRemoteService {

    public static final long STEAM64ID = 76561197960265728L;

    @Override
    public Pair<List<Unit>, String> getAccounts(Context context, List<Long> ids) throws Exception {
        StringBuilder steamIds = new StringBuilder("");
        for (int i = 0; i < ids.size(); i++) {
            if (i != 0) {
                steamIds.append(",");
            }
            Long steam64id = STEAM64ID + ids.get(i);
            steamIds.append(String.valueOf(steam64id));
        }
        String url = MessageFormat.format(Constants.Players.SUBURL, context.getString(R.string.api), steamIds.toString());
        Pair<PlayerResults, String> result = basicRequestSend(context, url, PlayerResults.class);
        if (result.first != null && result.first.getResponse() != null) {
            PlayerResponse response = result.first.getResponse();
            List<Player> players = response.getPlayers();
            List<Unit> units = new ArrayList<Unit>();
            if (players != null && players.size() > 0) {
                for (Player player : players) {
                    Unit unit = new Unit();
                    unit.setName(player.getPersonaname());
                    Long steam64id = Long.valueOf(player.getSteamid());
                    unit.setAccountId(steam64id - STEAM64ID);
                    unit.setIcon(player.getAvatarfull());
                    units.add(unit);
                }
            }
            return Pair.create(units, result.second);
        }
        return Pair.create(null, result.second);
    }

    @Override
    public Pair<Unit, String> getAccount(Context context, long id) throws Exception {
        long steam64id = STEAM64ID + id;
        String url = MessageFormat.format(Constants.Players.SUBURL, context.getString(R.string.api), String.valueOf(steam64id));
        Pair<PlayerResults, String> result = basicRequestSend(context, url, PlayerResults.class);
        if (result.first != null && result.first.getResponse() != null) {
            PlayerResponse response = result.first.getResponse();
            List<Player> players = response.getPlayers();
            Unit unit = null;
            if (players != null && players.size() > 0) {
                unit = new Unit();
                Player player = players.get(0);
                unit.setName(player.getPersonaname());
                unit.setAccountId(id);
                unit.setIcon(player.getAvatarfull());
            }
            return Pair.create(unit, result.second);
        }
        return Pair.create(null, result.second);
    }

    @Override
    public Pair<List<Unit>, String> getAccounts(Context context, String name) throws Exception {
        String url = Constants.DotaBuff.SEARCH_URL + URLEncoder.encode(name, "UTF-8");
        Document document = Jsoup.connect(url).get();    //document.location()
        String location = document.location();
        if (!url.equals(location)) {
            String[] parts = location.split("/");
            Long accountId = Long.valueOf(parts[parts.length - 1]);
            return getAccounts(context, Arrays.asList(accountId));
        } else {
            Elements elements = document.select("div[class=record player]");
            List<Unit> units = new ArrayList<Unit>();
            for (Element element : elements) {
                Element img = element.select("img").first();
                Unit unit = new Unit();
                unit.setIcon(img.attr("src"));
                Element nameElement = element.select("div[class=name]").first();
                Element dotaBuffUrl = nameElement.select("a").first();
                String accountUrl = dotaBuffUrl.attr("href");
                unit.setUrl(accountUrl);
                unit.setName(dotaBuffUrl.html());
                unit.setType("Player");
                units.add(unit);
            }
            return Pair.create(units, null);
        }
    }

    @Override
    public Pair<List<Unit>, String> getFriends(Context context, long id) throws Exception {
        long steam64id = STEAM64ID + id;
        String url = MessageFormat.format(Constants.Players.FRIENDS, context.getString(R.string.api), String.valueOf(steam64id));
        Pair<FriendsResult, String> result = basicRequestSend(context, url, FriendsResult.class);
        if (result.first != null && result.first.getFriendslist() != null) {
            FriendsList response = result.first.getFriendslist();
            List<Friend> friends = response.getFriends();
            if (friends != null && friends.size() > 0) {
                List<Long> ids = new ArrayList<Long>();
                for (Friend friend : friends) {
                    ids.add(Long.valueOf(friend.getSteamid()) - STEAM64ID);
                }
                return getAccounts(context, ids);
            }
        }
        return Pair.create(null, result.second);
    }


}
