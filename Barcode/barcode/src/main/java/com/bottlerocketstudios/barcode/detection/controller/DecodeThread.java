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

package com.bottlerocketstudios.barcode.detection.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bottlerocketstudios.barcode.detection.model.DecodeFormatManager;
import com.bottlerocketstudios.barcode.detection.model.ZXingConfiguration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread<T> extends Thread {
    private static final String TAG = DecodeThread.class.getSimpleName();

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private final IDecodeHandlerListener mDecodeHandlerListener;
    private final Map<DecodeHintType, Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(IDecodeHandlerListener decodeHandlerListener,
                 ResultPointCallback resultPointCallback,
                 ZXingConfiguration ZXingConfiguration) {

        mDecodeHandlerListener = decodeHandlerListener;
        handlerInitLatch = new CountDownLatch(1);

        hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        if (ZXingConfiguration.getBaseHints() != null) {
            hints.putAll(ZXingConfiguration.getBaseHints());
        }

        Collection<BarcodeFormat> decodeFormats = ZXingConfiguration.getDecodeFormats();

        // The zxingConfiguration can't change while the thread is running, so pick them up once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {

            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
            if (ZXingConfiguration.getBoolean(ZXingConfiguration.KEY_DECODE_1D_PRODUCT, true)) {
                decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
            }
            if (ZXingConfiguration.getBoolean(ZXingConfiguration.KEY_DECODE_1D_INDUSTRIAL, true)) {
                decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
            }
            if (ZXingConfiguration.getBoolean(ZXingConfiguration.KEY_DECODE_QR, true)) {
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            }
            if (ZXingConfiguration.getBoolean(ZXingConfiguration.KEY_DECODE_DATA_MATRIX, true)) {
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            }
            if (ZXingConfiguration.getBoolean(ZXingConfiguration.KEY_DECODE_AZTEC, false)) {
                decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
            }
            if (ZXingConfiguration.getBoolean(ZXingConfiguration.KEY_DECODE_PDF417, false)) {
                decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
            }
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (ZXingConfiguration.getCharacterSet() != null) {
            hints.put(DecodeHintType.CHARACTER_SET, ZXingConfiguration.getCharacterSet());
        }
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
        Log.i(TAG, "Hints: " + hints);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(mDecodeHandlerListener, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
