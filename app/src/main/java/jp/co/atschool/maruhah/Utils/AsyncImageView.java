package jp.co.atschool.maruhah.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nttr on 2018/01/15.
 */

public class AsyncImageView extends AsyncTask<String, Void, Bitmap> {
    private ImageView image;

    public AsyncImageView(ImageView _image) {
        image = _image;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap image;
        try {
            URL imageUrl = new URL(params[0]);
            InputStream imageIs;
            imageIs = imageUrl.openStream();
            image = BitmapFactory.decodeStream(imageIs);
            return image;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        // 取得した画像をImageViewに設定します。
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setPadding(7,7,7,7);
        image = (ImageView) ImageUtils.setCornerRadiusImageView(image, result, 2);
    }
}
