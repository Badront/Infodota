package com.badr.infodota.api.twitch;

import com.badr.infodota.util.HasId;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: Histler
 * Date: 25.02.14
 */
public class Stream implements HasId {
    @SerializedName("_id")
    private long id;
    private Map<String, String> _links;
    private Channel channel;
    private String game;
    private Map<String, String> preview;
    private int viewers;

    public Stream() {
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public Map<String, String> get_links() {
        return _links;
    }

    public void set_links(Map<String, String> _links) {
        this._links = _links;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Map<String, String> getPreview() {
        return preview;
    }

    public void setPreview(Map<String, String> preview) {
        this.preview = preview;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    @Override
    public String toString() {
        if (channel != null) {
            return channel.toString();
        }
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stream stream = (Stream) o;

        if (id != stream.id) return false;
        if (!channel.getName().equals(stream.channel.getName())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + channel.hashCode();
        return result;
    }
    public static class List extends ArrayList<Stream>{
    }
}
