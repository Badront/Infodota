package com.badr.infodota.service.team;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.api.matchdetails.Team;
import com.badr.infodota.api.team.LogoDataHolder;
import com.badr.infodota.dao.DatabaseManager;
import com.badr.infodota.dao.TeamDao;

/**
 * User: ABadretdinov
 * Date: 15.05.14
 * Time: 17:06
 */
public class TeamServiceImpl implements TeamService {
    private TeamDao teamDao;

    @Override
    public Pair<String, String> getTeamLogo(Context context, long id) {
        try {
            LogoDataHolder result = BeanContainer.getInstance().getSteamService().getTeamLogo(id);
            if (result == null) {
                String message= "Failed to get team logo";
                Log.e(TeamServiceImpl.class.getName(), message);
                return Pair.create(null,message);
            }
            else {
                return Pair.create(result.getUrl(),null);
            }
        } catch (Exception e) {
            String message = "Failed to get team logo, cause: " + e.getMessage();
            Log.e(TeamServiceImpl.class.getName(), message, e);
            return Pair.create(null, message);
        }
    }

    @Override
    public void saveTeam(Context context, Team team) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            teamDao.saveOrUpdate(database, team);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public Team getTeamById(Context context, long id) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase database = manager.openDatabase();
        try {
            return teamDao.getById(database, id);
        } finally {
            manager.closeDatabase();
        }
    }

    @Override
    public void initialize() {
        BeanContainer container = BeanContainer.getInstance();
        teamDao = container.getTeamDao();
    }
}
