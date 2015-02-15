package com.badr.infodota.util.web;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: Histler
 * Date: 21.04.14
 */
public class URLImageParser implements Html.ImageGetter {
    Context context;
    TextView container;

    /**
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param holder
     * @param context
     */
    public URLImageParser(TextView holder, Context context) {
        this.context = context;
        this.container = holder;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(result.getBounds());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            URLImageParser.this.container.invalidate();

            // For ICS
            URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight()
                    + result.getBounds().bottom));

            // Pre ICS
            URLImageParser.this.container.setEllipsize(null);
        }

        /**
         * Get the Drawable from URL
         *
         * @param urlString
         * @return
         */
        @SuppressWarnings("deprecation")
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");

                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                int width;
                int height;
                if (Build.VERSION.SDK_INT >= 13) {
                    Point size = new Point();
                    display.getSize(size);
                    height = size.y;
                    width = size.x;
                } else {
                    width = display.getWidth();
                    height = display.getHeight();
                }
                int scale = Math.min(width, height);
                drawable.setBounds(0, 0, scale, drawable.getIntrinsicWidth() != 0 ? drawable.getIntrinsicHeight() * scale / drawable.getIntrinsicWidth() : drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }
}
