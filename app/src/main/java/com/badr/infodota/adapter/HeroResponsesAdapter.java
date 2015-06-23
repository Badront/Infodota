package com.badr.infodota.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.badr.infodota.R;
import com.badr.infodota.api.responses.HeroResponse2;
import com.badr.infodota.api.responses.HeroResponses2Section;
import com.badr.infodota.util.FileUtils;
import com.badr.infodota.util.Utils;
import com.badr.infodota.view.PinnedSectionListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 05.02.14
 * Time: 18:32
 */
public class HeroResponsesAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter, Filterable {
    protected ImageLoader imageLoader;
    TableRow.LayoutParams otherLayoutParams;
    private Context context;
    private List<HeroResponses2Section> mHeroSectionsResponses;
    private List<Object> filteredHeroResponses;
    private DisplayImageOptions options;
    private int dp2;
    private String holderFolder;
    private List<Integer> playerQueue;
    private List<HeroResponse2> toLocalLoad;
    private boolean editMode = false;

    public HeroResponsesAdapter(Context context, List<HeroResponses2Section> heroSectionsResponses, String holderFolder) {
        editMode = false;
        this.context = context;
        this.holderFolder = holderFolder;
        this.mHeroSectionsResponses = heroSectionsResponses != null ? heroSectionsResponses : new ArrayList<HeroResponses2Section>();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        int dp30 = Utils.dpSize(context, 30);
        dp2 = Utils.dpSize(context, 2);
        otherLayoutParams = new TableRow.LayoutParams(dp30, dp30);
        playerQueue = new ArrayList<>();
        filteredHeroResponses=generateFilteredValue(null);
    }

    private List<Object> generateFilteredValue(String filter) {
        List<Object>result=new ArrayList<>();
        for(HeroResponses2Section section: mHeroSectionsResponses){
            StringBuilder sectionNameB=new StringBuilder("");
            if(!TextUtils.isEmpty(section.getCode())){
                sectionNameB.append(section.getCode()).append(" ");
            }
            sectionNameB.append(section.getName());
            String sectionName=sectionNameB.toString();
            if(section.getResponses()!=null){
                if(TextUtils.isEmpty(filter)||sectionName.toLowerCase().contains(filter)) {
                    result.add(sectionName);
                    result.addAll(section.getResponses());
                }
                else {
                    boolean sectionAdded=false;
                    for(HeroResponse2 response2:section.getResponses()){
                        if(response2.getTitle().toLowerCase().contains(filter)){
                            if(!sectionAdded){
                                result.add(sectionName);
                                sectionAdded=true;
                            }
                            result.add(response2);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public int getCount() {
        return filteredHeroResponses.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredHeroResponses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addToPlayLoading(int position) {
        playerQueue.add(position);
        notifyDataSetChanged();
    }

    public void loaded(int position) {
        playerQueue.remove(Integer.valueOf(position));
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setItemClicked(int position) {
        Object clickedItem=getItem(position);
        if(clickedItem instanceof HeroResponse2) {
            HeroResponse2 heroResponse = (HeroResponse2) clickedItem;
            if (toLocalLoad.contains(heroResponse)) {
                toLocalLoad.remove(heroResponse);
            } else {
                toLocalLoad.add(heroResponse);
            }
            notifyDataSetChanged();
        }
    }

    public void changeEditMode(boolean editMode) {
        this.editMode = editMode;
        toLocalLoad = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void inverseChecked() {
        if (editMode) {
            List<HeroResponse2> notToLoad = toLocalLoad;
            toLocalLoad = new ArrayList<>();
            for(Object object:filteredHeroResponses){
                if(object instanceof HeroResponse2){
                    HeroResponse2 heroResponse2= (HeroResponse2) object;
                    if(notToLoad.contains(heroResponse2)){
                        notToLoad.remove(heroResponse2);
                    }
                    else if(TextUtils.isEmpty(heroResponse2.getLocalUrl())){
                        toLocalLoad.add(heroResponse2);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void startLoadingFiles() {
        new Mp3Loader(toLocalLoad, false).execute();
        changeEditMode(false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Object object = getItem(position);
        if(object instanceof String){
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            vi=inflater.inflate(R.layout.list_section,parent,false);
            ((TextView)vi.findViewById(R.id.section_title)).setText((String)object);
        }else {
            HeroResponseHolder holder;
            final HeroResponse2 response2= (HeroResponse2) object;
            if (vi == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                holder = new HeroResponseHolder();
                vi = inflater.inflate(R.layout.hero_response_row, parent, false);
                holder.othersHeroHolder = (TableLayout) vi.findViewById(R.id.image_holder);
                holder.title = (TextView) vi.findViewById(R.id.title);
                holder.menu = (ImageView) vi.findViewById(R.id.more);
                holder.loading = vi.findViewById(R.id.progressBar);
                holder.saved = vi.findViewById(R.id.saved);
                holder.check = (CheckBox) vi.findViewById(R.id.check);
                vi.setTag(holder);
            } else {
                holder = (HeroResponseHolder) vi.getTag();
            }
            holder.othersHeroHolder.removeAllViews();
            holder.title.setText(response2.getTitle());
            if (playerQueue.contains(position)) {
                holder.loading.setVisibility(View.VISIBLE);
            } else {
                holder.loading.setVisibility(View.GONE);
            }
            if (editMode) {
                holder.check.setVisibility(View.VISIBLE);
                holder.menu.setVisibility(View.GONE);
                holder.check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        if (checkBox.isChecked()) {
                            toLocalLoad.add(response2);
                        } else {
                            toLocalLoad.remove(response2);
                        }
                    }
                });
                holder.check.setChecked(toLocalLoad.contains(object));
            } else {
                holder.menu.setVisibility(View.VISIBLE);
                holder.check.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(response2.getLocalUrl())) {
                holder.saved.setVisibility(View.GONE);
            } else {
                holder.saved.setVisibility(View.VISIBLE);
                holder.check.setVisibility(View.GONE);
            }
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    String[] heroResponseActions = context.getResources().getStringArray(R.array.hero_response_actions);
                    builder.setTitle(R.string.response_dialog_title);
                    builder.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, heroResponseActions), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    loadFile(response2, false);
                                    break;
                                case 1:
                                    if (!TextUtils.isEmpty(response2.getLocalUrl()) && new File(response2.getLocalUrl()).exists()) {
                                        setAsRingtone(response2);
                                    } else {
                                        loadFile(response2, true);
                                    }
                                    break;
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            TableRow row = null;
            int index=0;
            if(response2.getHeroes()!=null){
                List<String> heroes=response2.getHeroes();
                for(int i=0,hSize=heroes.size();i<hSize;i++){
                    String heroName=heroes.get(i);
                    if (index % 3 == 0) {
                        if (row != null) {
                            holder.othersHeroHolder.addView(row);
                        }
                        row = new TableRow(context);
                    }
                    if (row == null) {
                        row = new TableRow(context);
                    }
                    ImageView imageView = new ImageView(context);
                    imageView.setPadding(dp2, dp2, dp2, dp2);
                    imageView.setLayoutParams(otherLayoutParams);
                    row.addView(imageView);
                    imageLoader.displayImage("assets://heroes" + File.separator + heroName + File.separator + "mini.png", imageView, options);
                    index++;
                }
            }
            if(response2.getItems()!=null){
                List<String> items=response2.getItems();
                for(int i=0,iSize=items.size();i<iSize;i++){
                    String itemName=items.get(i);
                    if (index % 3 == 0) {
                        if (row != null) {
                            holder.othersHeroHolder.addView(row);
                        }
                        row = new TableRow(context);
                    }
                    if (row == null) {
                        row = new TableRow(context);
                    }
                    ImageView imageView = new ImageView(context);
                    imageView.setPadding(dp2, dp2, dp2, dp2);
                    imageView.setLayoutParams(otherLayoutParams);
                    row.addView(imageView);
                    imageLoader.displayImage("assets://items" + File.separator + itemName + ".png", imageView, options);
                    index++;
                }
            }
            if(!TextUtils.isEmpty(response2.getRune())){
                System.out.println(response2.getRune());
                //todo
            }
            if (row != null) {
                holder.othersHeroHolder.addView(row);
            }

        }

        return vi;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<Object> filteredHeroResponses =generateFilteredValue(constraint.toString().toLowerCase());
                filterResults.count = filteredHeroResponses.size();
                filterResults.values = filteredHeroResponses;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredHeroResponses = (List<Object>) results.values;
                if (filteredHeroResponses == null) {
                    filteredHeroResponses = new ArrayList<Object>();
                }
                if (results.count >= 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private void setAsRingtone(HeroResponse2 heroResponse) {
        File file = new File(heroResponse.getLocalUrl());
        if (file.exists()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, heroResponse.getTitle());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/oog");
            values.put(MediaStore.MediaColumns.SIZE, file.length());
            String[] folderParts = holderFolder.split(File.separator);
            values.put(MediaStore.Audio.Media.ARTIST, folderParts[folderParts.length - 1]);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
            Uri newUri = context.getContentResolver().insert(uri, values);

            try {
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
                Toast.makeText(context, R.string.ringtone_set, Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                e.printStackTrace();
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadFile(HeroResponse2 heroResponse, boolean saveAsDefault) {
        new Mp3Loader(Arrays.asList(heroResponse), saveAsDefault).execute();
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType==1;
    }
    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof String? 1 : 0;
    }

    private class HeroResponseHolder {
        TableLayout othersHeroHolder;
        TextView title;
        ImageView menu;
        View loading;
        View saved;
        CheckBox check;
    }

    public class Mp3Loader extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        private List<HeroResponse2> heroResponses;
        private boolean setAsTone;

        private Mp3Loader(List<HeroResponse2> heroResponses, boolean asRingtone) {
            this.heroResponses = heroResponses != null ? heroResponses : new ArrayList<HeroResponse2>();
            this.setAsTone = asRingtone;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(R.string.loading);
            if (heroResponses != null && heroResponses.size() > 0) {
                progressDialog.setMessage(heroResponses.get(0).getTitle());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(heroResponses.size());
                progressDialog.setProgress(0);
            }
            progressDialog.show();
        }

        @Override
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {
            try {
                for (HeroResponse2 heroResponse : heroResponses) {
                    publishProgress(heroResponse.getTitle());
                    HttpResponse heroPicture = new DefaultHttpClient().execute(new HttpGet(heroResponse.getUrl()));
                    if (heroPicture.getStatusLine().getStatusCode() == 200) {
                        HttpEntity pictureEntity = heroPicture.getEntity();
                        InputStream input = new BufferedInputStream(pictureEntity.getContent());
                        String[] urlPath = heroResponse.getUrl().split("/");
                        String path = holderFolder + File.separator + urlPath[urlPath.length - 1];
                        if (FileUtils.saveFile(path, input)) {
                            heroResponse.setLocalUrl(path);
                        } else {
                            throw new Exception(context.getString(R.string.file_saving_error));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
            progressDialog.setProgress(progressDialog.getProgress() + 1);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (TextUtils.isEmpty(s)) {
                if (setAsTone && heroResponses != null && heroResponses.size() > 0) {
                    setAsRingtone(heroResponses.get(0));
                }
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }

        }
    }
}
