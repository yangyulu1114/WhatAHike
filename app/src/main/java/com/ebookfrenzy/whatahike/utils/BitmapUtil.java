package com.ebookfrenzy.whatahike.utils;

import static android.graphics.ImageDecoder.decodeBitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.ui.activity.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class BitmapUtil {
    public static File saveBitmapToFile(String url) throws Exception {
        Bitmap bitmap = createBitmap(url);
        File outputDir = MyApplication.getAppContext().getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile("FireBaseUpload", ".png", outputDir);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputFile;
    }

    public static Bitmap createBitmap(String url) throws Exception {
        int degree = getBitmapDegree(url);
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        int[] screenSize = DisplayUtil.getScreenSize();
        Log.v("bush", "screen width " + screenSize[0] + "screen height " + screenSize[1]);
        Bitmap bitmap = decodeBitmap(url, screenSize[0] / 2, screenSize[1] / 2);
        Log.v("bush", "bitmap size before rotate" + bitmap.getWidth() + " " + bitmap.getHeight());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Log.v("bush", "bitmap size after rotate" + bitmap.getWidth() + " " + bitmap.getHeight());
        Log.v("bush", "add new cache");
        return bitmap;
    }

    private static int getBitmapDegree(String url) throws Exception {
        InputStream inputStream = createInputStream(url);
        int degree = 0;
        ExifInterface exifInterface = new ExifInterface(inputStream);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        inputStream.close();
        return degree;
    }

    private static InputStream createInputStream(String url) throws Exception {
        if (url.startsWith("http")) {
            return createInputStreamFromUrl(url);
        } else if (url.startsWith("content") || url.startsWith("file")) {
            return createInputStreamFromUri(url);
        } else {
            throw new IllegalArgumentException(url);
        }
    }

    private static InputStream createInputStreamFromUrl(String url) throws Exception {
        return new URL(url).openStream();
    }

    private static InputStream createInputStreamFromUri(String uri) throws Exception {
        return MyApplication.getAppContext().getContentResolver().openInputStream(Uri.parse(uri));
    }

    public static Bitmap decodeBitmap(String url, int reqWidth, int reqHeight) throws Exception {
        InputStream inputStream = createInputStream(url);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        options.inSampleSize = calculateInSampleSize(url, options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(createInputStream(url), null, options);
    }

    public static int calculateInSampleSize(String url, BitmapFactory.Options options, int reqWidth, int reqHeight) throws Exception {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int degree = getBitmapDegree(url) % 180;

        if (height > reqHeight || width > reqWidth) {

            final int needHeight = degree != 90 ? height : width;
            final int needWeight = degree != 90 ? width : height;

            while ((needHeight / inSampleSize) > reqHeight
                    || (needWeight / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
