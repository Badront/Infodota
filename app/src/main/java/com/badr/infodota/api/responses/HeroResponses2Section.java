package com.badr.infodota.api.responses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ABadretdinov
 * 19.06.2015
 * 16:28
 */
public class HeroResponses2Section implements Serializable {
    private String code;
    private String name;
    /*not using it by now*/
    private String ability;
    private java.util.List<HeroResponse2> responses;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public java.util.List<HeroResponse2> getResponses() {
        return responses;
    }

    public void setResponses(java.util.List<HeroResponse2> responses) {
        this.responses = responses;
    }

    public static class List extends ArrayList<HeroResponses2Section> {
    }
}
