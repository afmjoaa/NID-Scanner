package io.joaa.myapplication.util;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.pdf417.PDF417Reader;

import java.awt.image.BufferedImage;

public class ScannerUtils {

    public static String processImage(BufferedImage image) throws ReaderException {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        PDF417Reader reader = new PDF417Reader();

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = reader.decode(bitmap);
        return result.getText();

    }

    public static String processImage(Bitmap bMap) throws ReaderException {

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(),intArray);
        PDF417Reader reader = new PDF417Reader();

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = reader.decode(bitmap);
        return result.getText();

    }
}
