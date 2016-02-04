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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bottlerocketstudios.barcode.detection.camera.CameraManager;
import com.bottlerocketstudios.barcode.detection.model.ZXingConfiguration;
import com.bottlerocketstudios.barcode.detection.model.ZXingIds;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ZXingFacadeHandler extends Handler {

    private static final String TAG = ZXingFacadeHandler.class.getSimpleName();

    private final ZXingFacade mZXingFacade;
    private final DecodeThread decodeThread;
    private final CameraManager mCameraManager;
    private State state;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    ZXingFacadeHandler(ZXingFacade ZXingFacade,
                       ResultPointCallback resultPointCallback,
                       ZXingConfiguration ZXingConfiguration) {
        this.mZXingFacade = ZXingFacade;
        decodeThread = new DecodeThread(ZXingFacade, resultPointCallback, ZXingConfiguration);
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        mCameraManager = ZXingFacade.getCameraManager();
        mCameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case ZXingIds.RESTART_PREVIEW:
                restartPreviewAndDecode();
                break;
            case ZXingIds.DECODE_SUCCEEDED:
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = null;
                float scaleFactor = 1.0f;
                if (bundle != null) {
                    byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                    if (compressedBitmap != null) {
                        barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                        // Mutable copy:
                        barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                    }
                    scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
                }
                mZXingFacade.handleDecode((Result) message.obj, barcode, scaleFactor);
                break;
            case ZXingIds.DECODE_FAILED:
                // We're decoding as fast as possible, so when one decode fails, start another.
                state = State.PREVIEW;
                mZXingFacade.getCameraManager().requestPreviewFrame(decodeThread.getHandler(), ZXingIds.DECODE);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        mZXingFacade.getCameraManager().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), ZXingIds.QUIT);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(ZXingIds.DECODE_SUCCEEDED);
        removeMessages(ZXingIds.DECODE_FAILED);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            mZXingFacade.getCameraManager().requestPreviewFrame(decodeThread.getHandler(), ZXingIds.DECODE);
        }
    }

}
