package com.badr.infodota.fragment.hero;

import android.app.Activity;
import android.support.v4.app.ListFragment;

import com.badr.infodota.R;
import com.badr.infodota.adapter.SkillsAdapter;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.heroes.Skill;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.retrofit.LocalSpiceService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * User: ABadretdinov
 * Date: 16.01.14
 * Time: 15:57
 */
public class HeroSkills extends ListFragment implements RequestListener<Skill.List> {
    private SpiceManager spiceManager=new SpiceManager(LocalSpiceService.class);


    private Hero hero;

    public static HeroSkills newInstance(Hero hero) {
        HeroSkills fragment = new HeroSkills();
        fragment.hero = hero;
        return fragment;
    }
    private boolean initialized=false;
    @Override
    public void onStart() {
        if(!spiceManager.isStarted()) {
            spiceManager.start(getActivity());
            if(!initialized){
                spiceManager.execute(new SkillsLoadRequest(),this);
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        initialized=false;
        super.onDestroy();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        initialized=true;
    }

    @Override
    public void onRequestSuccess(Skill.List skills) {
        initialized=true;
        setListAdapter(new SkillsAdapter(getActivity(), skills));

    }

    public class SkillsLoadRequest extends TaskRequest<Skill.List>{

        public SkillsLoadRequest() {
            super(Skill.List.class);
        }

        @Override
        public Skill.List loadData() throws Exception {
            Activity activity=getActivity();
            if(activity!=null){
                String locale = activity.getString(R.string.language);
                String heroDotaId = hero.getDotaId();
                String json = FileUtils
                        .getTextFromAsset(activity, "heroes/" + heroDotaId + "/skills_" + locale + ".json");
                return new Gson().fromJson(json, Skill.List.class);
            }
            return null;
        }
    }
}
