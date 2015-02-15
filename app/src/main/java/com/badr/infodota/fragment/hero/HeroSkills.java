package com.badr.infodota.fragment.hero;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.badr.infodota.R;
import com.badr.infodota.adapter.SkillsAdapter;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.heroes.Skill;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.LoaderProgressTask;
import com.badr.infodota.util.ProgressTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 16.01.14
 * Time: 15:57
 */
public class HeroSkills extends ListFragment {

    private Hero hero;

    public static HeroSkills newInstance(Hero hero) {
        HeroSkills fragment = new HeroSkills();
        fragment.hero = hero;
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Activity activity = getActivity();
        new LoaderProgressTask<List<Skill>>(new ProgressTask<List<Skill>>() {
            @Override
            public List<Skill> doTask(OnPublishProgressListener listener) throws Exception {
                String locale = activity.getString(R.string.language);
                String heroDotaId = hero.getDotaId();
                String json = FileUtils
                        .getTextFromAsset(activity, "heroes/" + heroDotaId + "/skills_" + locale + ".json");
                Type skillListType = new TypeToken<List<Skill>>() {
                }.getType();
                return new Gson().fromJson(json, skillListType);
            }

            @Override
            public void doAfterTask(List<Skill> result) {
                setListAdapter(new SkillsAdapter(activity, result));
            }

            @Override
            public void handleError(String error) {

            }

            @Override
            public String getName() {
                return null;
            }
        }, null).execute();
    }
}
