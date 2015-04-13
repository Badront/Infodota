package com.badr.infodota.api.trackdota.live;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by ABadretdinov
 * 13.04.2015
 * 16:22
 */
public class LiveGame implements Serializable {
    @Expose
    @SerializedName("is_paused")
    private boolean isPaused;

    @Expose
    @SerializedName("api_downtime")
    private long apiDowntime;

    @Expose
    private long spectators;

    @Expose
    @SerializedName("v")
    private long version;

    @Expose
    private int status;

    @Expose
    @SerializedName("tower_state")
    private long towerState;

    @Expose
    @SerializedName("barracks_state")
    private long barracksState;

    @Expose
    private long updated;

    @Expose
    private long duration;

    @Expose
    @SerializedName("roshan_respawn_timer")
    private long roshanRespawnTimer;

    @Expose
    private Team dire;
    @Expose
    private Team radiant;
    @Expose
    private List<LogEvent> log;

    @Expose
    private Map<String,long[]> stats;

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public long getApiDowntime() {
        return apiDowntime;
    }

    public void setApiDowntime(long apiDowntime) {
        this.apiDowntime = apiDowntime;
    }

    public long getSpectators() {
        return spectators;
    }

    public void setSpectators(long spectators) {
        this.spectators = spectators;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTowerState() {
        return towerState;
    }

    public void setTowerState(long towerState) {
        this.towerState = towerState;
    }

    public long getBarracksState() {
        return barracksState;
    }

    public void setBarracksState(long barracksState) {
        this.barracksState = barracksState;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getRoshanRespawnTimer() {
        return roshanRespawnTimer;
    }

    public void setRoshanRespawnTimer(long roshanRespawnTimer) {
        this.roshanRespawnTimer = roshanRespawnTimer;
    }

    public Team getDire() {
        return dire;
    }

    public void setDire(Team dire) {
        this.dire = dire;
    }

    public Team getRadiant() {
        return radiant;
    }

    public void setRadiant(Team radiant) {
        this.radiant = radiant;
    }

    public List<LogEvent> getLog() {
        return log;
    }

    public void setLog(List<LogEvent> log) {
        this.log = log;
    }

    public Map<String, long[]> getStats() {
        return stats;
    }

    public void setStats(Map<String, long[]> stats) {
        this.stats = stats;
    }
}
