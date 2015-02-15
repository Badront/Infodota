package com.badr.infodota.api.heroes;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 28.08.13
 * Time: 16:32
 */
public class Result {
    private List<Hero> heroes;

    public Result(List<Hero> heroes) {
        this.heroes = heroes;
    }

    public Result() {
        super();
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Hero> heroes) {
        this.heroes = heroes;
    }
}
