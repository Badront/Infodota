package com.badr.infodota.api.twitch;

import java.util.List;
import java.util.Map;

/**
 * User: Histler
 * Date: 25.02.14
 */
public class GameStreams {
    private Map<String, String> _links;
    private List<Stream> streams;

    public GameStreams() {
    }

    public Map<String, String> get_links() {
        return _links;
    }

    public void set_links(Map<String, String> _links) {
        this._links = _links;
    }

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }
}
