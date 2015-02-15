package com.badr.infodota.api.abilities;

import java.util.List;

/**
 * User: Histler
 * Date: 22.01.14
 */
public class AbilityResult {
    private List<Ability> abilities;

    public AbilityResult() {
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }
}
