package com.badr.infodota.base.task;

/**
 * Created by ABadretdinov
 * 20.08.2015
 * 14:48
 */

import com.badr.infodota.BeanContainer;
import com.badr.infodota.base.api.joindota.LiveStream;
import com.badr.infodota.base.service.joindota.JoinDotaService;
import com.badr.infodota.util.retrofit.TaskRequest;

import java.util.List;

public class ChannelLoadRequest extends TaskRequest<String> {
    private List<LiveStream> mLiveStreams;

    public ChannelLoadRequest(List<LiveStream> liveStreams) {
        super(String.class);
        this.mLiveStreams = liveStreams;
    }

    @Override
    public String loadData() throws Exception {
        BeanContainer container = BeanContainer.getInstance();
        JoinDotaService service = container.getJoinDotaService();
        return service.fillChannelName(mLiveStreams);
    }
}
