package com.badr.infodota.api.matchhistory;

/**
 * User: ABadretdinov
 * Date: 28.08.13
 * Time: 15:16
 */
public class MatchHistory {
    Result result;

    public MatchHistory(Result result) {
        this.result = result;
    }

    public MatchHistory() {
        super();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
