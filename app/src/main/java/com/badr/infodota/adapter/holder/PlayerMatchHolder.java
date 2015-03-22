package com.badr.infodota.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.badr.infodota.R;
import com.badr.infodota.adapter.OnItemClickListener;

/**
 * Created by Badr on 22.03.2015.
 */
public class PlayerMatchHolder extends BaseViewHolder {
    public ImageView heroImg;
    public TextView gameStartTime;
    public TextView heroName;
    public TextView gameType;
    public PlayerMatchHolder(View itemView, OnItemClickListener listener) {
        super(itemView, listener);
        heroImg = (ImageView) itemView.findViewById(R.id.hero_img);
        heroName = (TextView) itemView.findViewById(R.id.hero_name);
        gameStartTime = (TextView) itemView.findViewById(R.id.game_start_time);
        gameType = (TextView) itemView.findViewById(R.id.game_type);
    }
}
