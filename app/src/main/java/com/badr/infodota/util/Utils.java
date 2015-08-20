package com.badr.infodota.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

import com.badr.infodota.R;
import com.badr.infodota.base.api.dotabuff.Unit;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * User: Histler
 * Date: 16.01.14
 */
public class Utils {
    public static final int PHONE = 0;
    public static final int TABLET_PORTRAIT = 1;
    public static final int TABLET_LANDSCAPE = 2;
    private static final long STEAM64ID=76561197960265728L;
    public static long steam32to64(long steam32){
        return STEAM64ID + steam32;
    }

    public static long steam64to32(long steam64){
        return steam64-STEAM64ID;
    }

    public static int dpSize(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    public static int getDeviceState(Context context){
        Resources resources=context.getResources();
        int state;
        if (!resources.getBoolean(R.bool.is_tablet)) {
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                state = Utils.TABLET_PORTRAIT;
            } else {
                state = Utils.PHONE;
            }
        } else {
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                state = Utils.TABLET_LANDSCAPE;
            } else {
                state = Utils.TABLET_PORTRAIT;
            }
        }
        return state;
    }

    public static boolean IsPackageInstalled(Context context,String PackageUri) {
        final PackageManager pm = context.getPackageManager();
        boolean IsPackageInstalled;
        try {
            pm.getPackageInfo(PackageUri, PackageManager.GET_ACTIVITIES);
            IsPackageInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            IsPackageInstalled = false;
        }
        return IsPackageInstalled;
    }

    public static void addPlayerToListDialog(final Context context, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_player_title);
        String[] list = context.getResources().getStringArray(R.array.match_history_title);

        builder.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, Arrays.copyOfRange(list, 1, 3)), listener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void deletePlayerFromListDialog(final Context context, Unit unit, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_player_title);
        builder.setMessage(MessageFormat.format(context.getString(R.string.delete_player_msg), unit.getName()));
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.delete, listener);
        builder.show();
    }

    public static Bitmap getBitmap(Drawable drawable){
        Bitmap bitmap;
        if(drawable instanceof BitmapDrawable){
            bitmap=((BitmapDrawable) drawable).getBitmap();
        }else {
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public static Bitmap drawableToGrayScale(Drawable drawable){
        Bitmap bitmap=getBitmap(drawable);
        if(bitmap!=null) {
            return toGrayScale(bitmap);
        }
        else {
            return null;
        }
    }

    public static Bitmap toGrayScale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static String getItemImage(String itemDotaId){
        return "file:///android_asset/items/" + itemDotaId + ".png";
    }

    public static String getHeroFullImage(String heroDotaId){
        return "file:///android_asset/heroes/" + heroDotaId + "/full.png";
    }

    public static String getHeroPortraitImage(String heroDotaId){
        return "file:///android_asset/heroes/" + heroDotaId + "/vert.jpg";
    }

    public static String getHeroMiniImage(String heroDotaId){
        return "file:///android_asset/heroes/" + heroDotaId + "/mini.png";
    }
    public static String getSkillImage(String skillName){
        return "file:///android_asset/skills/"+skillName+".png";
    }
}
