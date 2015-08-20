package com.badr.infodota.base.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.hero.api.Skill;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.Utils;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;

import java.util.List;

/**
 * User: Histler
 * Date: 17.01.14
 */
public class SkillsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Skill> mSkills;
    private DrawableImageGetter imageGetter;

    public SkillsAdapter(Context context, List<Skill> skills) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSkills = skills;
        imageGetter = new DrawableImageGetter(context);
    }

    @Override
    public int getCount() {
        return mSkills != null ? mSkills.size() : 0;
    }

    @Override
    public Skill getItem(int position) {
        return mSkills.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSkills.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        SkillHolder holder;
        Context context = parent.getContext();
        if (convertView == null) {
            vi = mInflater.inflate(R.layout.hero_skill_row, parent, false);
            holder = new SkillHolder();
            holder.image = (ImageView) vi.findViewById(R.id.skill_img);
            holder.name = (TextView) vi.findViewById(R.id.skill_name);
            holder.affects = (TextView) vi.findViewById(R.id.skill_affects);

            holder.loreHolder = (LinearLayout) vi.findViewById(R.id.skill_lore_layout);
            holder.paramsHolder = (LinearLayout) vi.findViewById(R.id.skill_params_layout);
            holder.youtube = (TextView) vi.findViewById(R.id.youtube);
            vi.setTag(holder);
        } else {
            holder = (SkillHolder) vi.getTag();
        }
        Skill skill = getItem(position);
        holder.name.setText(Html.fromHtml(skill.getDotaName()));
        holder.affects.setText(Html.fromHtml(skill.getAffects()));
        Glide.with(context)
                .load(Utils.getSkillImage(skill.getName()))
                .into(holder.image);
        holder.loreHolder.removeAllViews();
        holder.paramsHolder.removeAllViews();
        if (!TextUtils.isEmpty(skill.getCmb())) {
            TextView tv = new TextView(context);
            tv.setText(Html.fromHtml(skill.getCmb(), imageGetter, null));
            holder.paramsHolder.addView(tv);
        }

        if (!TextUtils.isEmpty(skill.getDmg())) {
            TextView tv = new TextView(context);
            tv.setText(Html.fromHtml(skill.getDmg(), imageGetter, null));
            holder.paramsHolder.addView(tv);
        }

        if (!TextUtils.isEmpty(skill.getAttrib())) {
            TextView tv = new TextView(context);
            tv.setText(Html.fromHtml(skill.getAttrib(), imageGetter, null));
            holder.paramsHolder.addView(tv);
        }
        if (!TextUtils.isEmpty(skill.getDesc())) {
            TextView tv = new TextView(context);
            tv.setText(Html.fromHtml(skill.getDesc(), imageGetter, null));
            holder.loreHolder.addView(tv);
        }

        if (!TextUtils.isEmpty(skill.getNotes())) {
            TextView tv = new TextView(context);
            tv.setText(Html.fromHtml(skill.getNotes(), imageGetter, null));
            holder.loreHolder.addView(tv);
        }
        if (TextUtils.isEmpty(skill.getYoutube())) {
            holder.youtube.setVisibility(View.GONE);
        } else {
            holder.youtube.setVisibility(View.VISIBLE);
            final String youtubeUrl = skill.getYoutube();
            holder.youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(context).equals(YouTubeInitializationResult.SUCCESS)) {
                        Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(context, youtubeUrl, false, false);
                        context.startActivity(intent);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeUrl)));
                    }
                }
            });
        }
        return vi;
    }

    public static class SkillHolder {
        ImageView image;
        TextView name;
        TextView affects;
        LinearLayout loreHolder;
        LinearLayout paramsHolder;
        TextView youtube;
    }

    public class DrawableImageGetter implements Html.ImageGetter {
        private Context mContext;

        public DrawableImageGetter(Context context) {
            super();
            this.mContext = context;
        }

        @Override
        public Drawable getDrawable(String source) {
            String[] parts = source.split("/");
            String realFileName = parts[parts.length - 1];
            return FileUtils.getDrawableFromAsset(mContext, realFileName);
        }
    }
}
