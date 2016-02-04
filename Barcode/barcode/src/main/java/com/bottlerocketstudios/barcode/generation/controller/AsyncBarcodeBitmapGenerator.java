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
import android.os.AsyncTask;

import com.bottlerocketstudios.barcode.generation.model.BarcodeRequest;

import java.lang.ref.WeakReference;

public class AsyncBarcodeBitmapGenerator {

    private final boolean mSingleOperation;
    private BarcodeGenerationTask mBarcodeGenerationTask;

    public AsyncBarcodeBitmapGenerator(boolean singleOperation) {
        mSingleOperation = singleOperation;
    }

    public void startGeneration(BarcodeRequest barcodeRequest, BarcodeGenerationListener listener) {
        if (mBarcodeGenerationTask != null && mSingleOperation) {
            mBarcodeGenerationTask.cancel(false);
        }
        mBarcodeGenerationTask = new BarcodeGenerationTask(listener);
        mBarcodeGenerationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, barcodeRequest);
    }

    private static class BarcodeGenerationTask extends AsyncTask<BarcodeRequest, Void, Bitmap> {

        private final WeakReference<BarcodeGenerationListener> mTaskListenerRef;

        public BarcodeGenerationTask(BarcodeGenerationListener listener) {
            mTaskListenerRef = new WeakReference<>(listener);
        }

        @Override
        protected Bitmap doInBackground(BarcodeRequest... params) {
            return (new BarcodeBitmapGenerator()).generate(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            BarcodeGenerationListener listener = mTaskListenerRef.get();
            if (listener != null) {
                listener.onGenerationComplete(bitmap);
            }
        }
    }

    public interface BarcodeGenerationListener {
        void onGenerationComplete(Bitmap bitmap);
    }
}
