package com.badr.infodota.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.badr.infodota.api.heroes.Skill;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.Utils;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * User: Histler
 * Date: 17.01.14
 */
public class SkillsAdapter extends BaseAdapter {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private LayoutInflater mInflater;
    private List<Skill> mSkills;
    private DrawableImageGetter imageGetter;
    private Context mContext;

    public SkillsAdapter(Context context, List<Skill> skills) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSkills = skills;
        imageGetter = new DrawableImageGetter();
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
        holder.name.setText(Html.fromHtml(skill.getDname()));
        holder.affects.setText(Html.fromHtml(skill.getAffects()));
        imageLoader.displayImage(
                Utils.getSkillImage(skill.getName()),
                holder.image,
                options);
        holder.loreHolder.removeAllViews();
        holder.paramsHolder.removeAllViews();
        if (!TextUtils.isEmpty(skill.getCmb())) {
            TextView tv = new TextView(mContext);
            tv.setText(Html.fromHtml(skill.getCmb(), imageGetter, null));
            holder.paramsHolder.addView(tv);
        }

        if (!TextUtils.isEmpty(skill.getDmg())) {
            TextView tv = new TextView(mContext);
            tv.setText(Html.fromHtml(skill.getDmg(), imageGetter, null));
            holder.paramsHolder.addView(tv);
        }

        if (!TextUtils.isEmpty(skill.getAttrib())) {
            TextView tv = new TextView(mContext);
            tv.setText(Html.fromHtml(skill.getAttrib(), imageGetter, null));
            holder.paramsHolder.addView(tv);
        }
        if (!TextUtils.isEmpty(skill.getDesc())) {
            TextView tv = new TextView(mContext);
            tv.setText(Html.fromHtml(skill.getDesc(), imageGetter, null));
            holder.loreHolder.addView(tv);
        }


        if (!TextUtils.isEmpty(skill.getNotes())) {
            TextView tv = new TextView(mContext);
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
                    if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(mContext).equals(YouTubeInitializationResult.SUCCESS)) {
                        Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(mContext, youtubeUrl, false, false);
                        mContext.startActivity(intent);

                    } else {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeUrl)));
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

        @Override
        public Drawable getDrawable(String source) {
            String[] parts = source.split("/");
            String realFileName = parts[parts.length - 1];
            return FileUtils.getDrawableFromAsset(mContext, realFileName);
        }
    }
}
