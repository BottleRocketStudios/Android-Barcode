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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bottlerocketstudios.barcode.detection.camera.CameraManager;
import com.bottlerocketstudios.barcode.detection.model.ZXingConfiguration;
import com.bottlerocketstudios.barcode.detection.model.ZXingIds;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;

import java.io.IOException;

public class ZXingFacade implements IDecodeHandlerListener {
    private static final String TAG = ZXingFacade.class.getSimpleName();

    private final Context mContext;
    private final AmbientLightManager mAmbientLightManager;
    private final ZXingConfiguration mZXingConfiguration;
    private CameraManager mCameraManager;
    private SurfaceView mSurfaceView;
    private boolean mHasSurface;
    private ZXingFacadeHandler mDecodeHandler;
    private ZXingFacadeListener mZXingFacadeListener;
    private ResultPointCallback mResultPointCallback;

    public ZXingFacade(Context context, ZXingConfiguration ZXingConfiguration, ZXingFacadeListener listener) {
        mContext = context.getApplicationContext();
        mAmbientLightManager = new AmbientLightManager(mContext);
        mZXingConfiguration = ZXingConfiguration;
        mZXingFacadeListener = listener;
    }

    /**
     * Must be called from onCreate lifecycle of host UI
     *
     * @param surfaceView
     */
    public void onCreate(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mHasSurface = false;
    }

    /**
     * Must be called from onResume lifecycle of host UI
     */
    public void onResume() {
        mCameraManager = new CameraManager(mContext, mZXingConfiguration);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(mSurfaceHolderCallback);
        }
        mAmbientLightManager.start(mCameraManager);
    }

    /**
     * Must be called from onPause lifecycle of host UI
     */
    public void onPause() {
        if (mDecodeHandler != null) {
            mDecodeHandler.quitSynchronously();
            mDecodeHandler = null;
        }
        mAmbientLightManager.stop();
        mCameraManager.closeDriver();
        if (!mHasSurface) {
            mSurfaceView.getHolder().removeCallback(mSurfaceHolderCallback);
        }
    }

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if (surfaceHolder == null) {
                Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
            }
            if (!mHasSurface) {
                mHasSurface = true;
                initCamera(surfaceHolder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }
    };

    @Override
    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    @Override
    public Handler getHandler() {
        return mDecodeHandler;
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        mZXingFacadeListener.handleDecode(rawResult, barcode, scaleFactor);
    }

    /**
     * When preview has been stopped due to decode, restart the preview.
     *
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (getHandler() != null) {
            getHandler().sendEmptyMessageDelayed(ZXingIds.RESTART_PREVIEW, delayMS);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (getHandler() == null) {
                mDecodeHandler = new ZXingFacadeHandler(this, mResultPointCallback, mZXingConfiguration);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            mZXingFacadeListener.onZXingException(ioe);
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            mZXingFacadeListener.onZXingException(e);
        }
    }

    public void setResultPointCallback(ResultPointCallback resultPointCallback) {
        mResultPointCallback = resultPointCallback;
    }

    public void toggleFlash() {
        if (mCameraManager != null) {
            boolean flashEnabled = mCameraManager.getTorch();
            mCameraManager.setTorch(!flashEnabled);
        }
    }
}
