package com.badr.infodota.api.heroes;

import java.util.Map;

/**
 * User: Histler
 * Date: 16.01.14
 */
public class GetHeroesSkills {
    Map<String, Skill> abilitydata;

    public GetHeroesSkills() {
    }

    public Map<String, Skill> getAbilitydata() {
        return abilitydata;
    }

    public void setAbilitydata(Map<String, Skill> abilitydata) {
        this.abilitydata = abilitydata;
    }
}
