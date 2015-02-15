package com.badr.infodota.api.heroes;

/**
 * User: ABadretdinov
 * Date: 28.08.13
 * Time: 16:33
 */
public class GetHeroes {
    private Result result;

    public GetHeroes(Result result) {
        this.result = result;
    }

    public GetHeroes() {
        super();
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
