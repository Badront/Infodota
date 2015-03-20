package com.badr.infodota.fragment.counter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ActionMenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.badr.infodota.BeanContainer;
import com.badr.infodota.R;
import com.badr.infodota.activity.BaseActivity;
import com.badr.infodota.activity.CounterPickerHeroesSelectActivity;
import com.badr.infodota.activity.HeroInfoActivity;
import com.badr.infodota.activity.ListHolderActivity;
import com.badr.infodota.api.heroes.TruepickerHero;
import com.badr.infodota.service.cosmetic.CounterService;
import com.badr.infodota.service.hero.HeroService;
import com.badr.infodota.util.retrofit.TaskRequest;
import com.badr.infodota.view.FlowLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 20.02.14
 * Time: 17:46
 */
public class CounterPickFilter extends Fragment implements RequestListener<TruepickerHero.List> {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private ScrollView scroll;
    private FlowLayout recommendationsView;
    private View recommendsTitle;
    private View progressBar;
    private ArrayList<Integer> enemies;
    private ArrayList<Integer> allies;
    private Spinner roleSpinner;
    private ImageView[] enemyViews = new ImageView[5];
    private ImageView[] allyViews = new ImageView[4];
    //private boolean[] roles;
    private String[] roleCodes = new String[]{
            "       ",
            "easy triple carry",
            "hard triple carry",
            "easy triple support",
            "hard triple support",
            "jungler",
            "easy double carry",
            "easy double support",
            "hard double carry",
            "hard double support",
            "hard solo",
            "easy solo",
            "mid solo",
            "mid double carry",
            "mid double support"
    };
    private SpiceManager spiceManager=new SpiceManager(UncachedSpiceService.class);
    private View.OnClickListener allyListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CounterPickerHeroesSelectActivity.class);
            intent.putExtra("enemies", enemies);
            intent.putExtra("allies", allies);
            intent.putExtra("mode", CounterPickerHeroesSelectActivity.ALLY);
            startActivityForResult(intent, CounterPickerHeroesSelectActivity.ALLY);
        }
    };

    private View.OnClickListener enemyListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CounterPickerHeroesSelectActivity.class);
            intent.putExtra("enemies", enemies);
            intent.putExtra("allies", allies);
            intent.putExtra("mode", CounterPickerHeroesSelectActivity.ENEMY);
            startActivityForResult(intent, CounterPickerHeroesSelectActivity.ENEMY);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.counter_filters, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        Activity activity = getActivity();
        ActionMenuView actionMenuView = ((ListHolderActivity) activity).getActionMenuView();
        Menu actionMenu = actionMenuView.getMenu();
        actionMenu.clear();
        actionMenuView.setVisibility(View.VISIBLE);

        MenuItem truePicker = actionMenu.add(1, 1011, 1, R.string.truepicker);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View truePickerView = layoutInflater.inflate(R.layout.truepicker_logo, null, false);

        truePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://truepicker.com/"));
                startActivity(intent);
            }
        });
        MenuItemCompat.setActionView(truePicker, truePickerView);
        MenuItemCompat.setShowAsAction(truePicker, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        truePicker.setIcon(R.drawable.truepicker_logo);

        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!prefs.getBoolean("truePickerDialogShowed", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.attention);
            builder.setMessage(R.string.truepicker_attention);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("truePickerDialogShowed", true);
                    editor.commit();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();
        enemies = new ArrayList<Integer>();
        allies = new ArrayList<Integer>();
        View root=getView();
        if(root!=null) {
            recommendationsView = (FlowLayout) root.findViewById(R.id.holder);
            progressBar=root.findViewById(R.id.progressBar);
            recommendsTitle=root.findViewById(R.id.holder_title);
            scroll = (ScrollView) root.findViewById(R.id.scroll);
            roleSpinner = (Spinner) root.findViewById(R.id.role_select);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, roleCodes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roleSpinner.setAdapter(adapter);

		/*root.findViewById(R.id.role_select).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.truepicker_hero_roles);
				builder.setMultiChoiceItems(localeRoles, roles, new DialogInterface.OnMultiChoiceClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked)
					{
						roles[which] = isChecked;
					}
				});
				builder.setPositiveButton(R.string.ready, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						rolesSelected();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(R.string.any_role, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						for (int i = 0; i < roles.length; i++)
						{
							roles[i] = false;
						}
						rolesSelected();
						dialog.dismiss();
					}
				});
				builder.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
					@Override
					public void onCancel(DialogInterface dialog)
					{
						rolesSelected();
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});*/
            enemyViews[0] = (ImageView) root.findViewById(R.id.enemy0);
            enemyViews[0].setOnClickListener(enemyListener);
            enemyViews[1] = (ImageView) root.findViewById(R.id.enemy1);
            enemyViews[1].setOnClickListener(enemyListener);
            enemyViews[2] = (ImageView) root.findViewById(R.id.enemy2);
            enemyViews[2].setOnClickListener(enemyListener);
            enemyViews[3] = (ImageView) root.findViewById(R.id.enemy3);
            enemyViews[3].setOnClickListener(enemyListener);
            enemyViews[4] = (ImageView) root.findViewById(R.id.enemy4);
            enemyViews[4].setOnClickListener(enemyListener);

            allyViews[0] = (ImageView) root.findViewById(R.id.ally0);
            allyViews[0].setOnClickListener(allyListener);
            allyViews[1] = (ImageView) root.findViewById(R.id.ally1);
            allyViews[1].setOnClickListener(allyListener);
            allyViews[2] = (ImageView) root.findViewById(R.id.ally2);
            allyViews[2].setOnClickListener(allyListener);
            allyViews[3] = (ImageView) root.findViewById(R.id.ally3);
            allyViews[3].setOnClickListener(allyListener);
		/*allyViews[4] = (ImageView)root.findViewById(R.id.ally4);
		allyViews[4].setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), CounterPickerHeroesSelectActivity.class);
				intent.putExtra("enemies", enemies);
				intent.putExtra("allies", allies);
				intent.putExtra("id", 4);
				intent.putExtra("mode", CounterPickerHeroesSelectActivity.ALLY);
				startActivityForResult(intent, CounterPickerHeroesSelectActivity.ALLY);
			}
		});*/
            //root.findViewById(R.id.show).setEnabled(false);
            root.findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    recommendsTitle.setVisibility(View.GONE);
                    recommendationsView.removeAllViews();
                    spiceManager.execute(new TruepickerLoadRequest(),CounterPickFilter.this);
                }
            });
            loadImages();
        }
    }

    private void loadImages() {
        final BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null) {
            HeroService heroService = BeanContainer.getInstance().getHeroService();
            for (int i = 0; i < 4; i++) //поправка, т.к. союзников не может быть 5, ведь как же мы?
            {
                if (allies.size() > i) {
                    TruepickerHero hero = heroService.getTruepickerHero(activity, allies.get(i));
                    imageLoader.displayImage("assets://heroes/" + hero.getDotaId() + "/full.png", allyViews[i], options);
                } else {
                    imageLoader.displayImage("assets://default_img.png", allyViews[i], options);
                }
                if (enemies.size() > i) {
                    TruepickerHero hero = heroService.getTruepickerHero(activity, enemies.get(i));
                    imageLoader.displayImage("assets://heroes/" + hero.getDotaId() + "/full.png", enemyViews[i], options);
                } else {
                    imageLoader.displayImage("assets://default_img.png", enemyViews[i], options);
                }
            }
            if (enemies.size() == 5)//та же поправка
            {
                TruepickerHero hero = heroService.getTruepickerHero(activity, enemies.get(4));
                imageLoader.displayImage("assets://heroes/" + hero.getDotaId() + "/full.png", enemyViews[4], options);
            } else {
                imageLoader.displayImage("assets://default_img.png", enemyViews[4], options);
            }
        }
    }

    @Override
    public void onDestroy() {
        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CounterPickerHeroesSelectActivity.ALLY || requestCode == CounterPickerHeroesSelectActivity.ENEMY) {
                enemies = data.getIntegerArrayListExtra("enemies");
                if (enemies == null) {
                    enemies = new ArrayList<Integer>();
                }
                allies = data.getIntegerArrayListExtra("allies");
                if (allies == null) {
                    allies = new ArrayList<Integer>();
                }
                loadImages();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(getActivity(), spiceException.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(TruepickerHero.List truepickerHeros) {
        if (truepickerHeros != null) {
            View root = getView();
            Activity activity=getActivity();
            if (root != null&&activity!=null) {
                progressBar.setVisibility(View.GONE);
                recommendsTitle.setVisibility(View.VISIBLE);
                LayoutInflater inflater = activity.getLayoutInflater();
                for (final TruepickerHero hero : truepickerHeros) {
                    View view = inflater.inflate(R.layout.hero_row, recommendationsView, false);
                    ((TextView) view.findViewById(R.id.name)).setText(hero.getLocalizedName());
                    imageLoader
                            .displayImage("assets://heroes/" + hero.getDotaId() + "/full.png",
                                    (ImageView) view.findViewById(R.id.img), options);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), HeroInfoActivity.class);
                            intent.putExtra("id", hero.getId());
                            startActivity(intent);
                        }
                    });
                    recommendationsView.addView(view);
                }
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    public class TruepickerLoadRequest extends TaskRequest<TruepickerHero.List>{

        public TruepickerLoadRequest() {
            super(TruepickerHero.List.class);
        }

        @Override
        public TruepickerHero.List loadData() throws Exception {
            BeanContainer beanContainer = BeanContainer.getInstance();
            CounterService service = beanContainer.getCounterService();
            List<String> rolesToAdd = new ArrayList<String>();
						/*for(int i=0;i<roles.length;i++){
							if(roles[i]){
								rolesToAdd.add(roleCodes[i]);
							}
						}*/
            Activity activity=getActivity();
            if(activity!=null)
            {
                return service.getCounters(activity, allies, enemies, 1/*roleSpinner.getSelectedItemPosition()*/);
            }
            return null;
        }
    }
}
