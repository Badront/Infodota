package com.badr.infodota.api.responses;

import java.util.List;

/**
 * User: ABadretdinov
 * Date: 05.02.14
 * Time: 17:19
 */
public class HeroResponsesResult {
    List<HeroResponse> responses;

    public HeroResponsesResult() {
    }

    public List<HeroResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<HeroResponse> responses) {
        this.responses = responses;
    }
}
