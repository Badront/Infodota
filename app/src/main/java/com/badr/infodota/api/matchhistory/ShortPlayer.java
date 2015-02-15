package com.badr.infodota.api.matchhistory;

import java.io.Serializable;

/**
 * User: ABadretdinov
 * Date: 28.08.13
 * Time: 15:18
 */
public class ShortPlayer implements Serializable {
    //the player's 32-bit Steam ID - will be set to "4294967295" if the player has set their account to private.
    private long account_id;
    //an 8-bit unsigned int: if the left-most bit is set, the player was on dire. the two right-most bits represent the player slot (0-4).
    private int player_slot;

    private int hero_id;

    public ShortPlayer(long account_id, int player_slot, int hero_id) {
        this.account_id = account_id;
        this.player_slot = player_slot;
        this.hero_id = hero_id;
    }

    public ShortPlayer() {
        super();
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public int getPlayer_slot() {
        return player_slot;
    }

    public void setPlayer_slot(int player_slot) {
        this.player_slot = player_slot;
    }

    public int getHero_id() {
        return hero_id;
    }

    public void setHero_id(int hero_id) {
        this.hero_id = hero_id;
    }
}
