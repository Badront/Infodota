package com.badr.infodota.api.matchdetails;

import com.badr.infodota.api.matchhistory.Match;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * User: ABadretdinov
 * Date: 28.08.13
 * Time: 15:19
 */
public class Result extends Match implements Serializable {
    //true if radiant won, false otherwise
    @SerializedName("radiant_win")
    private boolean radiantWin;
    //the total time in seconds the match ran for
    private long duration;
    //an 11-bit unsinged int: see http://wiki.teamfortress.com/wiki/WebAPI/GetMatchDetails#Tower_Status
    @SerializedName("tower_status_radiant")
    private int towerStatusRadiant;
    //an 11-bit unsinged int: see http://wiki.teamfortress.com/wiki/WebAPI/GetMatchDetails#Tower_Status
    @SerializedName("tower_status_dire")
    private int towerStatusDire;
    //a 6-bit unsinged int: see http://wiki.teamfortress.com/wiki/WebAPI/GetMatchDetails#Barracks_Status
    @SerializedName("barracks_status_radiant")
    private int barracksStatusRadiant;
    //a 6-bit unsinged int: see http://wiki.teamfortress.com/wiki/WebAPI/GetMatchDetails#Barracks_Status
    @SerializedName("barracks_status_dire")
    private int barracksStatusDire;
    //for replays
    private long cluster;
    //the time in seconds at which first blood occurred
    @SerializedName("first_blood_time")
    private long firstBloodTime;
    //the number of human players in the match
    @SerializedName("human_players")
    private int humanPlayers;
    //the leauge this match is from (see GetMatchHistory)
    private long leagueid;
    //the number of thumbs up the game has received
    @SerializedName("positive_votes")
    private long positiveVotes;
    //the number of thumbs up the game has received
    @SerializedName("negative_votes")
    private long negativeVotes;
    /*
    a number representing the game mode of this match
    * '1' : 'All Pick',
    * '2' : "Captains Mode",
    * '3' : 'Random Draft',
    * '4' : 'Single Draft',
    * '5' : 'All Random',
    * '6' : '?? INTRO/DEATH ??',
    * '7' : 'The Diretide',
    * '8' : "Reverse Captains Mode",
    * '9' : 'Greeviling',
    * '10' : 'Tutorial',
    * '11' : 'Mid Only',
    * '12' : 'Least Played',
    * '13' : 'New Player Pool'
    * */
    private long game_mode;
    private String season;

    /*
    * The following fields are only included if there were
    * teams applied to radiant and dire
    * (i.e. this is a league match in a private lobby)
    * */
    //the name of the radiant team
    @SerializedName("radiant_name")
    private String radiantName;
    @SerializedName("radiant_logo")
    private Long radiantLogo;
    @SerializedName("radiant_team_id")
    private Long radiantTeamId;
    //true if all players on radiant belong to this team, false otherwise (i.e. are the stand-ins {false} or not {true})
    @SerializedName("radiant_team_complete")
    private Integer radiantTeamComplete;
    //he name of the dire team
    @SerializedName("dire_name")
    private String direName;
    @SerializedName("dire_logo")
    private Long direLogo;
    @SerializedName("dire_team_id")
    private Long direTeamId;
    //true if all players on dire belong to this team, false otherwise (i.e. are the stand-ins {false} or not {true})
    @SerializedName("dire_team_complete")
    private Integer direTeamComplete;

    private List<PickBan> picks_bans;


    private boolean section = false;

    public Result() {
        super();
    }

    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    public boolean isRadiantWin() {
        return radiantWin;
    }

    public void setRadiantWin(boolean radiantWin) {
        this.radiantWin = radiantWin;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getTowerStatusRadiant() {
        return towerStatusRadiant;
    }

    public void setTowerStatusRadiant(int towerStatusRadiant) {
        this.towerStatusRadiant = towerStatusRadiant;
    }

    public int getTowerStatusDire() {
        return towerStatusDire;
    }

    public void setTowerStatusDire(int towerStatusDire) {
        this.towerStatusDire = towerStatusDire;
    }

    public int getBarracksStatusRadiant() {
        return barracksStatusRadiant;
    }

    public void setBarracksStatusRadiant(int barracksStatusRadiant) {
        this.barracksStatusRadiant = barracksStatusRadiant;
    }

    public int getBarracksStatusDire() {
        return barracksStatusDire;
    }

    public void setBarracksStatusDire(int barracksStatusDire) {
        this.barracksStatusDire = barracksStatusDire;
    }

    public long getCluster() {
        return cluster;
    }

    public void setCluster(long cluster) {
        this.cluster = cluster;
    }

    public long getFirstBloodTime() {
        return firstBloodTime;
    }

    public void setFirstBloodTime(long firstBloodTime) {
        this.firstBloodTime = firstBloodTime;
    }

    public int getHumanPlayers() {
        return humanPlayers;
    }

    public void setHumanPlayers(int humanPlayers) {
        this.humanPlayers = humanPlayers;
    }

    public long getLeagueid() {
        return leagueid;
    }

    public void setLeagueid(long leagueid) {
        this.leagueid = leagueid;
    }

    public long getPositiveVotes() {
        return positiveVotes;
    }

    public void setPositiveVotes(long positiveVotes) {
        this.positiveVotes = positiveVotes;
    }

    public long getNegativeVotes() {
        return negativeVotes;
    }

    public void setNegativeVotes(long negativeVotes) {
        this.negativeVotes = negativeVotes;
    }

    public long getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(long game_mode) {
        this.game_mode = game_mode;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getRadiantName() {
        return radiantName;
    }

    public void setRadiantName(String radiantName) {
        this.radiantName = radiantName;
    }

    public Long getRadiantLogo() {
        return radiantLogo;
    }

    public void setRadiantLogo(Long radiantLogo) {
        this.radiantLogo = radiantLogo;
    }

    public Integer getRadiantTeamComplete() {
        return radiantTeamComplete;
    }

    public void setRadiantTeamComplete(Integer radiantTeamComplete) {
        this.radiantTeamComplete = radiantTeamComplete;
    }

    public String getDireName() {
        return direName;
    }

    public void setDireName(String direName) {
        this.direName = direName;
    }

    public Long getDireLogo() {
        return direLogo;
    }

    public void setDireLogo(Long direLogo) {
        this.direLogo = direLogo;
    }

    public Integer getDireTeamComplete() {
        return direTeamComplete;
    }

    public void setDireTeamComplete(Integer direTeamComplete) {
        this.direTeamComplete = direTeamComplete;
    }

    public List<PickBan> getPicks_bans() {
        return picks_bans;
    }

    public void setPicks_bans(List<PickBan> picks_bans) {
        this.picks_bans = picks_bans;
    }

    public Long getRadiantTeamId() {
        return radiantTeamId;
    }

    public void setRadiantTeamId(Long radiantTeamId) {
        this.radiantTeamId = radiantTeamId;
    }

    public Long getDireTeamId() {
        return direTeamId;
    }

    public void setDireTeamId(Long direTeamId) {
        this.direTeamId = direTeamId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (section != result.section) return false;
        if (section) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            dateFormat.setTimeZone(tz);
            long rightTimestamp = result.getStart_time() * 1000;
            long leftTimestamp = getStart_time() * 1000;
            if (!dateFormat.format(new Date(rightTimestamp)).equals(dateFormat.format(new Date(leftTimestamp)))) {
                return false;
            }
            /*rightTimestamp/=3600; //hours
            leftTimestamp/=3600;
            rightTimestamp/=24; //days
            leftTimestamp/=24;
            if(rightTimestamp!=leftTimestamp)return false;*/
        } else {
            if (barracksStatusDire != result.barracksStatusDire) return false;
            if (barracksStatusRadiant != result.barracksStatusRadiant) return false;
            if (cluster != result.cluster) return false;
            if (duration != result.duration) return false;
            if (firstBloodTime != result.firstBloodTime) return false;
            if (game_mode != result.game_mode) return false;
            if (humanPlayers != result.humanPlayers) return false;
            if (leagueid != result.leagueid) return false;
            if (negativeVotes != result.negativeVotes) return false;
            if (positiveVotes != result.positiveVotes) return false;
            if (radiantWin != result.radiantWin) return false;
            if (towerStatusDire != result.towerStatusDire) return false;
            if (towerStatusRadiant != result.towerStatusRadiant) return false;
            if (direLogo != null ? !direLogo.equals(result.direLogo) : result.direLogo != null)
                return false;
            if (direName != null ? !direName.equals(result.direName) : result.direName != null)
                return false;
            if (direTeamComplete != null ? !direTeamComplete.equals(result.direTeamComplete) : result.direTeamComplete != null)
                return false;
            if (direTeamId != null ? !direTeamId.equals(result.direTeamId) : result.direTeamId != null)
                return false;
            if (picks_bans != null ? !picks_bans.equals(result.picks_bans) : result.picks_bans != null)
                return false;
            if (radiantLogo != null ? !radiantLogo.equals(result.radiantLogo) : result.radiantLogo != null)
                return false;
            if (radiantName != null ? !radiantName.equals(result.radiantName) : result.radiantName != null)
                return false;
            if (radiantTeamComplete != null ? !radiantTeamComplete.equals(result.radiantTeamComplete) : result.radiantTeamComplete != null)
                return false;
            if (radiantTeamId != null ? !radiantTeamId.equals(result.radiantTeamId) : result.radiantTeamId != null)
                return false;
            if (season != null ? !season.equals(result.season) : result.season != null)
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (radiantWin ? 1 : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (towerStatusRadiant ^ (towerStatusRadiant >>> 32));
        result = 31 * result + (int) (towerStatusDire ^ (towerStatusDire >>> 32));
        result = 31 * result + (int) (barracksStatusRadiant ^ (barracksStatusRadiant >>> 32));
        result = 31 * result + (int) (barracksStatusDire ^ (barracksStatusDire >>> 32));
        result = 31 * result + (int) (cluster ^ (cluster >>> 32));
        result = 31 * result + (int) (firstBloodTime ^ (firstBloodTime >>> 32));
        result = 31 * result + humanPlayers;
        result = 31 * result + (int) (leagueid ^ (leagueid >>> 32));
        result = 31 * result + (int) (positiveVotes ^ (positiveVotes >>> 32));
        result = 31 * result + (int) (negativeVotes ^ (negativeVotes >>> 32));
        result = 31 * result + (int) (game_mode ^ (game_mode >>> 32));
        result = 31 * result + (season != null ? season.hashCode() : 0);
        result = 31 * result + (radiantName != null ? radiantName.hashCode() : 0);
        result = 31 * result + (radiantLogo != null ? radiantLogo.hashCode() : 0);
        result = 31 * result + (radiantTeamId != null ? radiantTeamId.hashCode() : 0);
        result = 31 * result + (radiantTeamComplete != null ? radiantTeamComplete.hashCode() : 0);
        result = 31 * result + (direName != null ? direName.hashCode() : 0);
        result = 31 * result + (direLogo != null ? direLogo.hashCode() : 0);
        result = 31 * result + (direTeamId != null ? direTeamId.hashCode() : 0);
        result = 31 * result + (direTeamComplete != null ? direTeamComplete.hashCode() : 0);
        result = 31 * result + (picks_bans != null ? picks_bans.hashCode() : 0);
        return result;
    }
}
