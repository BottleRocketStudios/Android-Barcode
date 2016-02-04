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

package com.bottlerocketstudios.barcodedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bottlerocketstudios.barcode.detection.controller.ZXingFacade;
import com.bottlerocketstudios.barcode.detection.controller.ZXingFacadeListener;
import com.bottlerocketstudios.barcode.detection.model.ZXingConfiguration;
import com.google.zxing.Result;

import java.util.concurrent.TimeUnit;

public class ScanningActivity extends Activity {
    private static final String TAG = ScanningActivity.class.getSimpleName();

    private TextView mStatusText;
    private SurfaceView mPreview;
    private ZXingFacade mZXingFacade;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.scanning_activity);

        mPreview = (SurfaceView) findViewById(R.id.zxing_preview_surface);
        mStatusText = (TextView) findViewById(R.id.zxing_status_text);

        initFacade();
    }

    private void initFacade() {
        ZXingConfiguration zXingConfiguration = ZXingConfiguration.createDefaultConfiguration(true, true);
        mZXingFacade = new ZXingFacade(this, zXingConfiguration, mZXingFacadeListener);
        mZXingFacade.onCreate(mPreview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mZXingFacade.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mZXingFacade.onPause();
    }

    private ZXingFacadeListener mZXingFacadeListener = new ZXingFacadeListener() {
        @Override
        public void handleDecode(Result result, Bitmap barcode, float scaleFactor) {
            Log.d(TAG, "Decoded: " + result.getText());
            mStatusText.setText(result.getText());
            mZXingFacade.restartPreviewAfterDelay(TimeUnit.SECONDS.toMillis(1));
        }

        @Override
        public void onZXingException(Throwable t) {
            Log.e(TAG, "Exception thrown by ZXing", t);
        }
    };
}
