package com.badr.infodota.hero.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.hero.R;
import com.badr.infodota.hero.entity.FullAbility;
import com.badr.infodota.hero.util.AbilityHttpImageGetter;
import com.badr.infodota.hero.util.HeroUtils;
import com.badr.infodota.hero.util.OnYoutubeClickListener;
import com.bumptech.glide.Glide;

/**
 * Created by ABadretdinov
 * 15.03.2016
 * 15:47
 */
public class HeroAbilityView extends LinearLayout {

    private AbilityHttpImageGetter mImageGetter;

    private FullAbility mAbility;
    private ImageView mImage;
    private TextView mName;
    private TextView mAffects;
    private LinearLayout mLoreHolder;
    private LinearLayout mParamsHolder;
    private View mYoutube;

    public HeroAbilityView(Context context, FullAbility ability) {
        this(context);
        setAbility(ability);
    }

    public HeroAbilityView(Context context) {
        this(context, (AttributeSet) null);
    }

    public HeroAbilityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeroAbilityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeroAbilityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        inflate(context, R.layout.hero_ability_view, this);
        mImageGetter = new AbilityHttpImageGetter(getContext());
        mImage = (ImageView) findViewById(R.id.ability_img);
        mName = (TextView) findViewById(R.id.ability_name);
        mAffects = (TextView) findViewById(R.id.ability_affects);

        mLoreHolder = (LinearLayout) findViewById(R.id.ability_lore_layout);
        mParamsHolder = (LinearLayout) findViewById(R.id.ability_params_layout);
        mYoutube = findViewById(R.id.youtube);
    }

    public FullAbility getAbility() {
        return mAbility;
    }

    public void setAbility(FullAbility ability) {
        this.mAbility = ability;

        if (mAbility != null) {
            mName.setText(Html.fromHtml(ability.getDotaName()));
            mAffects.setText(Html.fromHtml(ability.getAffects()));
            Glide.with(getContext())
                    .load(HeroUtils.getSkillImage(ability.getName()))
                    .into(mImage);
            mLoreHolder.removeAllViews();
            mParamsHolder.removeAllViews();
            if (!TextUtils.isEmpty(ability.getCmb())) {
                TextView tv = new TextView(getContext());
                tv.setText(Html.fromHtml(ability.getCmb(), mImageGetter, null));
                mParamsHolder.addView(tv);
            }

            if (!TextUtils.isEmpty(ability.getDmg())) {
                TextView tv = new TextView(getContext());
                tv.setText(Html.fromHtml(ability.getDmg(), mImageGetter, null));
                mParamsHolder.addView(tv);
            }

            if (!TextUtils.isEmpty(ability.getAttrib())) {
                TextView tv = new TextView(getContext());
                tv.setText(Html.fromHtml(ability.getAttrib(), mImageGetter, null));
                mParamsHolder.addView(tv);
            }
            if (!TextUtils.isEmpty(ability.getDesc())) {
                TextView tv = new TextView(getContext());
                tv.setText(Html.fromHtml(ability.getDesc(), mImageGetter, null));
                mLoreHolder.addView(tv);
            }

            if (!TextUtils.isEmpty(ability.getNotes())) {
                TextView tv = new TextView(getContext());
                tv.setText(Html.fromHtml(ability.getNotes(), mImageGetter, null));
                mLoreHolder.addView(tv);
            }
            if (TextUtils.isEmpty(ability.getYoutube())) {
                mYoutube.setVisibility(View.GONE);
                mYoutube.setOnClickListener(null);
            } else {
                mYoutube.setVisibility(View.VISIBLE);
                String youtubeUrl = ability.getYoutube();
                mYoutube.setOnClickListener(new OnYoutubeClickListener(youtubeUrl));
            }
        }
    }
}
