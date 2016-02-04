/*
 * Copyright (c) 2016 Bottle Rocket LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bottlerocketstudios.barcode.generation.controller;

import android.graphics.Bitmap;
import android.util.Log;

import com.bottlerocketstudios.barcode.generation.model.BarcodeRequest;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class BarcodeBitmapGenerator {
    private static final String TAG = BarcodeBitmapGenerator.class.getSimpleName();

    public Bitmap generate(BarcodeRequest barcodeRequest) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, barcodeRequest.getCharacterSet());
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(
                    barcodeRequest.getBarcodeText(),
                    barcodeRequest.getBarcodeFormat(),
                    barcodeRequest.getWidth(),
                    barcodeRequest.getHeight(),
                    hints);
            return convertBitMatrixToBitmap(bitMatrix, barcodeRequest.getForegroundColor(), barcodeRequest.getBackgroundColor());
        } catch (WriterException e) {
            Log.e(TAG, "Caught com.google.zxing.WriterException", e);
        }
        return null;
    }

    private Bitmap convertBitMatrixToBitmap(BitMatrix bitMatrix, int foregroundColor, int backgroundColor) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        int pixels[] = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] =  bitMatrix.get(x, y) ? foregroundColor : backgroundColor;
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }
}
