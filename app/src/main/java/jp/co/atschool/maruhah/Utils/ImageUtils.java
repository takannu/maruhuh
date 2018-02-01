package jp.co.atschool.maruhah.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import jp.co.atschool.maruhah.Network.NetworkUtilListener;
import jp.co.atschool.maruhah.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by goooshiete on 2017/12/01.
 */

public class ImageUtils {

    public static Bitmap adjustBitmap(Activity activity, int requestCode, Intent data, Bitmap bitmap) {
        return adjustBitmap(activity, data, data.getData(), bitmap);
    }

    public static Bitmap adjustBitmap(Activity activity, Intent data, Uri imageUri, Bitmap bitmap) {
        ContentResolver resolver = activity.getContentResolver();
        try {
            bitmap = BitmapFactory.decodeStream(resolver.openInputStream(imageUri));
        } catch (Exception e) {
            bitmap = (Bitmap) data.getExtras().get("data");
        }

        if (null != bitmap) {
            while (2 * 1024 * 1024 < bitmap.getByteCount() && 3840*2160 < bitmap.getWidth()*bitmap.getHeight()) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.6), (int)(bitmap.getHeight()*0.6), true);
            }
        }
        try {
            Matrix matrix = new Matrix();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(imageUri, projection, null, null, null);

            try {
                if (cursor.moveToFirst()) {
                    try {
                        String path = cursor.getString(0);

                        ExifInterface exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                                    matrix.postScale(-1, 1);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postScale(-1, -1);
                                    break;
                                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                                    matrix.postScale(1, -1);
                                    break;
                                case ExifInterface.ORIENTATION_TRANSPOSE:
                                    matrix.postRotate(90);
                                    matrix.postScale(1, -1);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case ExifInterface.ORIENTATION_TRANSVERSE:
                                    matrix.postRotate(270);
                                    matrix.postScale(1, -1);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }
                        }
                    } catch (IOException e) {
                    } finally {
                        cursor.close();
                    }
                }
                if (!matrix.isIdentity()) {
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            } catch (NullPointerException e) {
            }
        } catch (IllegalArgumentException e) {
        }

        return bitmap;
    }

    public static ImageView setCornerRadiusImageView(ImageView imageView, Bitmap result, float cornerRadius) {
        // 取得した画像をImageViewに設定します。
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        // 画像を角丸に見せます。
        result = getCroppedBitmap(result, cornerRadius);
        imageView.setImageBitmap(result);

        return imageView;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, float cornerRadius) {

        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();

        final Rect rect   = new Rect(0, 0, width, height);
        final RectF rectf = new RectF(0, 0, width, height);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawRoundRect(rectf, width / cornerRadius, height / cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getTextSynthesisImage(Context context, Resources resources, String text) {

        // Bitmap合成
        // Bitmapの枠を作成
        Bitmap baseBitmap = Bitmap.createBitmap(400, 280, Bitmap.Config.ARGB_8888);
        // BaseになるBitmapの生成
        Canvas canvas = new Canvas(baseBitmap);

        Bitmap baseImage = BitmapFactory.decodeResource(resources, R.mipmap.text_frame_2);
        baseImage = Bitmap.createScaledBitmap(baseImage, 400, 280, false);
        canvas.drawBitmap(baseImage, 0, 0, null);

//        Bitmap frameBitmap = textCreateBitmap(context, resources,"新しいフォントを探す旅に出ることにします。");
//        canvas.drawBitmap(frameBitmap, 30, 30, null);



        TextPaint textPaint = new TextPaint();
        textPaint.setARGB(150, 0, 0, 0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        AssetManager as = resources.getAssets();
        Typeface typeface = Typeface.createFromAsset(as, "SmartFont.ttf");
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(20);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        StaticLayout staticLayout = new StaticLayout(text, textPaint, canvas.getWidth() * 4 / 5, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        canvas.save();
        canvas.translate(xPos, (canvas.getHeight() / 2) - ((staticLayout.getHeight() / 2)));
        staticLayout.draw(canvas);
        canvas.restore();
//        canvas.drawText("新しいフォントを探す旅に出ることにします。", xPos, yPos, textPaint);





        return baseBitmap;
    }

    public static Bitmap textCreateBitmap(Context context, Resources resources, String text) {

        TextView tv = new TextView(context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(340, 220);
        tv.setTextSize(8);
//        tv.setLayoutParams(layoutParams);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.TRANSPARENT);
//        tv.setBackgroundColor(Color.BLUE);

        AssetManager as = resources.getAssets();
        Typeface typeface = Typeface.createFromAsset(as, "SmartFont.ttf");
        tv.setTypeface(typeface);

        Bitmap testB;

        testB = Bitmap.createBitmap(340, 220, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(testB);
        tv.layout(0, 0, 340, 220);
        tv.draw(c);

        return testB;
    }

    public static void setMediaImagesUpload(final Activity activity ,final Uri imageUri, final NetworkUtilListener networkUtilListener){
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(imageUri, projection, null, null, null);

        File file = new File("");

        if (cursor.moveToFirst()) {

            String path = cursor.getString(0);

            file = new File(path);
        }

//                        File file = new File(imageUri.getPath());
//                        file = new File("/storage/emulated/0/DCIM/100ANDRO/DSC_0591.JPG");

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("media", "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpg"), file))
                .build();

        Request request = new Request.Builder()
                .url("https://upload.twitter.com/1.1/media/upload.json")
                .post(formBody)
                .build();

        Log.d("log", "mmmmmmmmmmmmmkkkkkkkkkkkkktin");

        Log.d("log", "mmmmmmmmmmmmmkkkkkkkkkkkkktin!!!!!!!!!!!!");

//        ApiUtils.post(request, networkUtilListener);
    }
}
