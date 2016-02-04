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

package com.bottlerocketstudios.barcode;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.test.AndroidTestCase;
import android.util.Log;

import com.bottlerocketstudios.barcode.generation.controller.AsyncBarcodeBitmapGenerator;
import com.bottlerocketstudios.barcode.generation.model.BarcodeRequest;

public class TestBarcodeGeneration extends AndroidTestCase {

    private static final String TAG = TestBarcodeGeneration.class.getSimpleName();

    private static final int TEST_WIDTH = 300;
    private static final int TEST_HEIGHT = TEST_WIDTH;
    public static final int MAX_TIME_MS = 1000;


    public void testBarcodeGeneration() {
        AsyncBarcodeBitmapGenerator asyncBarcodeBitmapGenerator = new AsyncBarcodeBitmapGenerator(true);
        BarcodeRequest.BarcodeRequestBuilder barcodeRequestBuilder = new BarcodeRequest.BarcodeRequestBuilder();
        barcodeRequestBuilder.barcodeText("This is a test");
        barcodeRequestBuilder.width(TEST_WIDTH);
        barcodeRequestBuilder.height(TEST_HEIGHT);
        long startTime = SystemClock.uptimeMillis();
        final Container<Bitmap> bitmapContainer = new Container<>(null);

        asyncBarcodeBitmapGenerator.startGeneration(barcodeRequestBuilder.build(), new AsyncBarcodeBitmapGenerator.BarcodeGenerationListener() {
            @Override
            public void onGenerationComplete(Bitmap bitmap) {
                bitmapContainer.setValue(bitmap);
            }
        });

        try {
            while (bitmapContainer.getValue() == null && SystemClock.uptimeMillis() - startTime < MAX_TIME_MS) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Caught java.lang.InterruptedException", e);
        }

        Log.d(TAG, "Generation took " + (SystemClock.uptimeMillis() - startTime));

        assertNotNull("Generation failed in allowed time limit", bitmapContainer.getValue());
        assertEquals("Wrong width bitmap", TEST_WIDTH, bitmapContainer.getValue().getWidth());
        assertEquals("Wrong height bitmap", TEST_HEIGHT, bitmapContainer.getValue().getHeight());
    }


    private class Container<T> {
        T value;

        public Container(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

}
