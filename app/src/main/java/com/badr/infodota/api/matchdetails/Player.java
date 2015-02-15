package com.badr.infodota.api.matchdetails;

import com.badr.infodota.api.AbilityUpgrade;
import com.badr.infodota.api.matchhistory.ShortPlayer;

import java.io.Serializable;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 28.08.13
 * Time: 14:57
 */
public class Player extends ShortPlayer implements Serializable {
    public static final long HIDDEN_ID = 4294967295L;
    //the numeric ID of the item that player finished with in their top-left slot.
    private int item_0;
    //top-center
    private int item_1;
    //top-right
    private int item_2;
    //bottom-left
    private int item_3;
    //bottom-center
    private int item_4;
    //bottom-right
    private int item_5;
    private int kills;
    private int deaths;
    private int assists;
    /*
    * NULL - player is a bot.
    * 3 - по ходу не пикнул.
    * 2 - player abandoned game.
    * 1 - player left game after the game has become safe to leave.
    * 0 - Player stayed for the entire match.
    * */
    private Integer leaver_status;
    //the amount of gold the player had left at the end of the match
    private long gold;
    private int last_hits;
    private int denies;
    private int gold_per_min;
    private int xp_per_min;
    private long gold_spent;
    private long hero_damage;
    private long tower_damage;
    private long hero_healing;
    // the player's final level
    private long level;
    //an array detailing the order in which a player's ability points were spent.
    private List<AbilityUpgrade> ability_upgrades;

    private List<AdditionalUnit> additional_units;

    public Player() {
        super();
    }

    public int getItem_0() {
        return item_0;
    }

    public void setItem_0(int item_0) {
        this.item_0 = item_0;
    }

    public int getItem_1() {
        return item_1;
    }

    public void setItem_1(int item_1) {
        this.item_1 = item_1;
    }

    public int getItem_2() {
        return item_2;
    }

    public void setItem_2(int item_2) {
        this.item_2 = item_2;
    }

    public int getItem_3() {
        return item_3;
    }

    public void setItem_3(int item_3) {
        this.item_3 = item_3;
    }

    public int getItem_4() {
        return item_4;
    }

    public void setItem_4(int item_4) {
        this.item_4 = item_4;
    }

    public int getItem_5() {
        return item_5;
    }

    public void setItem_5(int item_5) {
        this.item_5 = item_5;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public Integer getLeaver_status() {
        return leaver_status;
    }

    public void setLeaver_status(Integer leaver_status) {
        this.leaver_status = leaver_status;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public int getLast_hits() {
        return last_hits;
    }

    public void setLast_hits(int last_hits) {
        this.last_hits = last_hits;
    }

    public int getDenies() {
        return denies;
    }

    public void setDenies(int denies) {
        this.denies = denies;
    }

    public int getGold_per_min() {
        return gold_per_min;
    }

    public void setGold_per_min(int gold_per_min) {
        this.gold_per_min = gold_per_min;
    }

    public int getXp_per_min() {
        return xp_per_min;
    }

    public void setXp_per_min(int xp_per_min) {
        this.xp_per_min = xp_per_min;
    }

    public long getGold_spent() {
        return gold_spent;
    }

    public void setGold_spent(long gold_spent) {
        this.gold_spent = gold_spent;
    }

    public long getHero_damage() {
        return hero_damage;
    }

    public void setHero_damage(long hero_damage) {
        this.hero_damage = hero_damage;
    }

    public long getTower_damage() {
        return tower_damage;
    }

    public void setTower_damage(long tower_damage) {
        this.tower_damage = tower_damage;
    }

    public long getHero_healing() {
        return hero_healing;
    }

    public void setHero_healing(long hero_healing) {
        this.hero_healing = hero_healing;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public List<AbilityUpgrade> getAbility_upgrades() {
        return ability_upgrades;
    }

    public void setAbility_upgrades(List<AbilityUpgrade> ability_upgrades) {
        this.ability_upgrades = ability_upgrades;
    }

    public List<AdditionalUnit> getAdditional_units() {
        return additional_units;
    }

    public void setAdditional_units(List<AdditionalUnit> additional_units) {
        this.additional_units = additional_units;
    }
}
