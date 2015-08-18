package com.badr.infodota.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.activity.ItemInfoActivity;
import com.badr.infodota.api.dotabuff.Unit;
import com.badr.infodota.api.heroes.Hero;
import com.badr.infodota.api.matchdetails.AdditionalUnit;
import com.badr.infodota.api.matchdetails.Player;
import com.badr.infodota.util.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.badr.infodota.util.Utils.PHONE;
import static com.badr.infodota.util.Utils.TABLET_LANDSCAPE;
import static com.badr.infodota.util.Utils.TABLET_PORTRAIT;

/**
 * User: ABadretdinov
 * Date: 21.01.14
 * Time: 14:44
 */
public class MatchDetailsAdapter extends BaseAdapter {
    int state;
    private List<Player> players;
    private LayoutInflater inflater;
    private Context context;

    public MatchDetailsAdapter(Context context, List<Player> players) {
        this.context = context;
        this.players = players != null ? players : new ArrayList<Player>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        state = Utils.getDeviceState(context);
    }

    @Override
    public void notifyDataSetChanged() {
        state = Utils.getDeviceState(context);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Player getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return players.get(position).getAccount_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        MatchDetailsHolder holder;
        if (vi == null) {
            vi = inflater.inflate(R.layout.match_player_row, parent, false);
            holder = new MatchDetailsHolder();
            holder.heroImg = (ImageView) vi.findViewById(R.id.hero_img);
            holder.heroName = (TextView) vi.findViewById(R.id.hero_name);
            holder.nick = (TextView) vi.findViewById(R.id.nick);
            holder.playerLvl = (TextView) vi.findViewById(R.id.player_lvl);
            holder.leaver = (TextView) vi.findViewById(R.id.leaver);
            holder.kills = (TextView) vi.findViewById(R.id.kills);
            holder.deaths = (TextView) vi.findViewById(R.id.death);
            holder.assists = (TextView) vi.findViewById(R.id.assists);
            holder.gold = (TextView) vi.findViewById(R.id.gold);
            holder.lastHits = (TextView) vi.findViewById(R.id.last_hits);
            holder.denies = (TextView) vi.findViewById(R.id.denies);
            holder.gpm = (TextView) vi.findViewById(R.id.gpm);
            holder.xpm = (TextView) vi.findViewById(R.id.xpm);

            holder.item0 = (ImageView) vi.findViewById(R.id.item0);
            holder.item1 = (ImageView) vi.findViewById(R.id.item1);
            holder.item2 = (ImageView) vi.findViewById(R.id.item2);
            holder.item3 = (ImageView) vi.findViewById(R.id.item3);
            holder.item4 = (ImageView) vi.findViewById(R.id.item4);
            holder.item5 = (ImageView) vi.findViewById(R.id.item5);

            holder.additionalUnitItem0 = (ImageView) vi.findViewById(R.id.additional_unit_item0);
            holder.additionalUnitItem1 = (ImageView) vi.findViewById(R.id.additional_unit_item1);
            holder.additionalUnitItem2 = (ImageView) vi.findViewById(R.id.additional_unit_item2);
            holder.additionalUnitItem3 = (ImageView) vi.findViewById(R.id.additional_unit_item3);
            holder.additionalUnitItem4 = (ImageView) vi.findViewById(R.id.additional_unit_item4);
            holder.additionalUnitItem5 = (ImageView) vi.findViewById(R.id.additional_unit_item5);

            holder.additionalUnitHolder = (LinearLayout) vi.findViewById(R.id.additional_unit_holder);
            holder.unitHolder = (LinearLayout) vi.findViewById(R.id.unit_holder);
            vi.setTag(holder);
        } else {
            holder = (MatchDetailsHolder) vi.getTag();
        }
        switch (state) {
            case TABLET_LANDSCAPE:
                holder.unitHolder.setOrientation(LinearLayout.VERTICAL);
                holder.additionalUnitHolder.setOrientation(LinearLayout.VERTICAL);
                ((LinearLayout) vi.findViewById(R.id.item_holder)).setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) vi).setOrientation(LinearLayout.HORIZONTAL);
                break;
            case TABLET_PORTRAIT:
                holder.unitHolder.setOrientation(LinearLayout.VERTICAL);
                holder.additionalUnitHolder.setOrientation(LinearLayout.VERTICAL);
                ((LinearLayout) vi.findViewById(R.id.item_holder)).setOrientation(LinearLayout.VERTICAL);
                ((LinearLayout) vi).setOrientation(LinearLayout.HORIZONTAL);
                break;
            case PHONE:
                holder.unitHolder.setOrientation(LinearLayout.HORIZONTAL);
                holder.additionalUnitHolder.setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) vi.findViewById(R.id.item_holder)).setOrientation(LinearLayout.VERTICAL);
                ((LinearLayout) vi).setOrientation(LinearLayout.VERTICAL);
                break;
        }
        Player player = getItem(position);

        Unit account = player.getAccount();
        if (account != null) {
            holder.nick.setText(account.getName());
            holder.nick.setVisibility(View.VISIBLE);
        } else {
            holder.nick.setVisibility(View.INVISIBLE);
        }
        Integer leaver = player.getLeaverStatus();
        if (leaver == null) {
            holder.leaver.setText(context.getString(R.string.bot));
            holder.leaver.setVisibility(View.VISIBLE);
        } else switch (leaver) {
            case 1: {
                holder.leaver.setText(context.getString(R.string.disconnected));
                holder.leaver.setVisibility(View.VISIBLE);
                break;
            }
            case 2: {
                holder.leaver.setText(context.getString(R.string.abandoned));
                holder.leaver.setVisibility(View.VISIBLE);
                break;
            }
            case 3: {
                holder.leaver.setText(context.getString(R.string.leaved));
                holder.leaver.setVisibility(View.VISIBLE);
                break;
            }
            case 4: {
                holder.leaver.setText(context.getString(R.string.afk));
                holder.leaver.setVisibility(View.VISIBLE);
                break;
            }
            case 5: {
                holder.leaver.setText(context.getString(R.string.never_connected));
                holder.leaver.setVisibility(View.VISIBLE);
                break;
            }
            case 6: {
                holder.leaver.setText(context.getString(R.string.never_connected_too_long));
                holder.leaver.setVisibility(View.VISIBLE);
                break;
            }
            default: {
                holder.leaver.setVisibility(View.INVISIBLE);
            }
        }
        final Hero hero = player.getHero();
        if (hero != null) {
            Glide.with(context).load(Utils.getHeroFullImage(hero.getDotaId())).placeholder(R.drawable.default_img).into(holder.heroImg);
           /* holder.heroImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HeroInfoActivity.class);
                    intent.putExtra("id", (long) hero.getId());
                    context.startActivity(intent);
                }
            });*/
            holder.heroName.setText(hero.getLocalizedName());
        } else {
            holder.heroImg.setImageResource(R.drawable.default_img);
            holder.heroName.setText("");
        }
        if (player.getItem0dotaId() != null) {
            Glide.with(context).load(Utils.getItemImage(player.getItem0dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.item0);
            holder.item0.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(player.getItem0()));
        } else {
            holder.item0.setImageResource(R.drawable.emptyitembg);
        }
        if (player.getItem1dotaId() != null) {
            Glide.with(context).load(Utils.getItemImage(player.getItem1dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.item1);
            holder.item1.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(player.getItem1()));
        } else {
            holder.item1.setImageResource(R.drawable.emptyitembg);
        }

        if (player.getItem2dotaId() != null) {
            Glide.with(context).load(Utils.getItemImage(player.getItem2dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.item2);
            holder.item2.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(player.getItem2()));
        } else {
            holder.item2.setImageResource(R.drawable.emptyitembg);
        }

        if (player.getItem3dotaId() != null) {
            Glide.with(context).load(Utils.getItemImage(player.getItem3dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.item3);
            holder.item3.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(player.getItem3()));
        } else {
            holder.item3.setImageResource(R.drawable.emptyitembg);
        }

        if (player.getItem4dotaId() != null) {
            Glide.with(context).load(Utils.getItemImage(player.getItem4dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.item4);
            holder.item4.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(player.getItem4()));
        } else {
            holder.item4.setImageResource(R.drawable.emptyitembg);
        }

        if (player.getItem5dotaId() != null) {
            Glide.with(context).load(Utils.getItemImage(player.getItem5dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.item5);
            holder.item5.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(player.getItem5()));
        } else {
            holder.item5.setImageResource(R.drawable.emptyitembg);
        }
        if (player.getAdditionalUnits() != null && player.getAdditionalUnits().size() > 0) {
            AdditionalUnit unit = player.getAdditionalUnits().get(0);
            holder.additionalUnitHolder.setVisibility(View.VISIBLE);

            if (unit.getItem0dotaId() != null) {
                Glide.with(context).load(Utils.getItemImage(unit.getItem0dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.additionalUnitItem0);
                holder.additionalUnitItem0.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(unit.getItem0()));
            } else {
                holder.additionalUnitItem0.setImageResource(R.drawable.emptyitembg);
            }

            if (unit.getItem1dotaId() != null) {
                Glide.with(context).load(Utils.getItemImage(unit.getItem1dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.additionalUnitItem1);
                holder.additionalUnitItem1.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(unit.getItem1()));
            } else {
                holder.additionalUnitItem1.setImageResource(R.drawable.emptyitembg);
            }

            if (unit.getItem2dotaId() != null) {
                Glide.with(context).load(Utils.getItemImage(unit.getItem2dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.additionalUnitItem2);
                holder.additionalUnitItem2.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(unit.getItem2()));
            } else {
                holder.additionalUnitItem2.setImageResource(R.drawable.emptyitembg);
            }

            if (unit.getItem3dotaId() != null) {
                Glide.with(context).load(Utils.getItemImage(unit.getItem3dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.additionalUnitItem3);
                holder.additionalUnitItem3.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(unit.getItem3()));
            } else {
                holder.additionalUnitItem3.setImageResource(R.drawable.emptyitembg);
            }

            if (unit.getItem4dotaId() != null) {
                Glide.with(context).load(Utils.getItemImage(unit.getItem4dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.additionalUnitItem4);
                holder.additionalUnitItem4.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(unit.getItem4()));
            } else {
                holder.additionalUnitItem4.setImageResource(R.drawable.emptyitembg);
            }

            if (unit.getItem5dotaId() != null) {
                Glide.with(context).load(Utils.getItemImage(unit.getItem5dotaId()))/*.placeholder(R.drawable.default_img)*/.into(holder.additionalUnitItem5);
                holder.additionalUnitItem5.setOnClickListener(new ItemInfoActivity.OnDotaItemClickListener(unit.getItem5()));
            } else {
                holder.additionalUnitItem5.setImageResource(R.drawable.emptyitembg);
            }
        } else {
            holder.additionalUnitHolder.setVisibility(View.GONE);
        }

        holder.playerLvl.setText(context.getString(R.string.level) + ": " + player.getLevel());
        holder.kills.setText(Html.fromHtml(context.getString(R.string.kills) + " " + player.getKills()));
        holder.deaths.setText(Html.fromHtml(context.getString(R.string.deaths) + " " + player.getDeaths()));
        holder.assists.setText(Html.fromHtml(context.getString(R.string.assists) + " " + player.getAssists()));
        holder.gold.setText(Html.fromHtml(context.getString(R.string.gold) + " " + player.getGold()));
        holder.lastHits.setText(Html.fromHtml(context.getString(R.string.last_hits) + " " + player.getLastHits()));
        holder.denies.setText(Html.fromHtml(context.getString(R.string.denies) + " " + player.getDenies()));
        holder.gpm.setText(Html.fromHtml(context.getString(R.string.gpm) + " " + player.getGoldPerMin()));
        holder.xpm.setText(Html.fromHtml(context.getString(R.string.xpm) + " " + player.getXpPerMin()));
        return vi;
    }

    private class MatchDetailsHolder {
        ImageView heroImg;
        TextView heroName;
        TextView playerLvl;
        TextView leaver;
        TextView nick;
        TextView kills;
        TextView deaths;
        TextView assists;
        TextView gold;
        TextView lastHits;
        TextView denies;
        TextView gpm;
        TextView xpm;

        ImageView item0;
        ImageView item1;
        ImageView item2;
        ImageView item3;
        ImageView item4;
        ImageView item5;
        LinearLayout unitHolder;

        ImageView additionalUnitItem0;
        ImageView additionalUnitItem1;
        ImageView additionalUnitItem2;
        ImageView additionalUnitItem3;
        ImageView additionalUnitItem4;
        ImageView additionalUnitItem5;
        LinearLayout additionalUnitHolder;

    }
}
