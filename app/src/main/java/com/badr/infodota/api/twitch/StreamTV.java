package com.badr.infodota.api.twitch;

import java.util.Map;

/**
 * User: Histler
 * Date: 25.02.14
 */
public class StreamTV {
    private Map<String, String> _links;
    private Stream stream;

    public StreamTV() {
    }

    public Map<String, String> get_links() {
        return _links;
    }

    public void set_links(Map<String, String> _links) {
        this._links = _links;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
