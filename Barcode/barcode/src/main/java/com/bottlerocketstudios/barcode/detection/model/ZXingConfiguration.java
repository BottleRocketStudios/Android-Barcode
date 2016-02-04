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

package com.bottlerocketstudios.barcode.detection.model;

import android.os.Bundle;

import com.bottlerocketstudios.barcode.detection.camera.FrontLightMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.Map;

public class ZXingConfiguration {

    public static final String KEY_DECODE_1D_PRODUCT = "preferences_decode_1D_product";
    public static final String KEY_DECODE_1D_INDUSTRIAL = "preferences_decode_1D_industrial";
    public static final String KEY_DECODE_QR = "preferences_decode_QR";
    public static final String KEY_DECODE_DATA_MATRIX = "preferences_decode_Data_Matrix";
    public static final String KEY_DECODE_AZTEC = "preferences_decode_Aztec";
    public static final String KEY_DECODE_PDF417 = "preferences_decode_PDF417";

    public static final String KEY_FRONT_LIGHT_MODE = "preferences_front_light_mode";
    public static final String KEY_AUTO_FOCUS = "preferences_auto_focus";
    public static final String KEY_INVERT_SCAN = "preferences_invert_scan";

    public static final String KEY_DISABLE_CONTINUOUS_FOCUS = "preferences_disable_continuous_focus";
    public static final String KEY_DISABLE_EXPOSURE = "preferences_disable_exposure";
    public static final String KEY_DISABLE_METERING = "preferences_disable_metering";
    public static final String KEY_DISABLE_BARCODE_SCENE_MODE = "preferences_disable_barcode_scene_mode";

    private Bundle mZxingConfiguration;
    private Collection<BarcodeFormat> mDecodeFormats;
    private Map<DecodeHintType, ?> mBaseHints;
    private ResultPointCallback mResultPointCallback;
    private String mCharacterSet;

    public ZXingConfiguration() {
        this(null);
    }

    public ZXingConfiguration(Bundle bundle) {
        if (bundle == null) {
            mZxingConfiguration = new Bundle();
        } else {
            mZxingConfiguration = bundle;
        }
    }

    public void setBoolean(String key, boolean value) {
        mZxingConfiguration.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mZxingConfiguration.getBoolean(key, defaultValue);
    }

    public void setString(String key, String value) {
        mZxingConfiguration.putString(key, value);
    }

    public String getString(String key, String defaultValue) {
        return mZxingConfiguration.getString(key, defaultValue);
    }

    public Collection<BarcodeFormat> getDecodeFormats() {
        return mDecodeFormats;
    }

    public void setDecodeFormats(Collection<BarcodeFormat> decodeFormats) {
        mDecodeFormats = decodeFormats;
    }

    public Map<DecodeHintType, ?> getBaseHints() {
        return mBaseHints;
    }

    public void setBaseHints(Map<DecodeHintType, ?> baseHints) {
        mBaseHints = baseHints;
    }

    public ResultPointCallback getResultPointCallback() {
        return mResultPointCallback;
    }

    public void setResultPointCallback(ResultPointCallback resultPointCallback) {
        mResultPointCallback = resultPointCallback;
    }

    public String getCharacterSet() {
        return mCharacterSet;
    }

    public void setCharacterSet(String characterSet) {
        mCharacterSet = characterSet;
    }

    public static ZXingConfiguration createDefaultConfiguration(boolean includeStandard1D, boolean includeStandard2D) {
        ZXingConfiguration zXingConfiguration = new ZXingConfiguration();
        zXingConfiguration.setBoolean(KEY_DECODE_1D_PRODUCT, includeStandard1D);
        zXingConfiguration.setBoolean(KEY_DECODE_1D_INDUSTRIAL, includeStandard1D);
        zXingConfiguration.setBoolean(KEY_DECODE_QR, includeStandard2D);
        zXingConfiguration.setBoolean(KEY_DECODE_DATA_MATRIX, includeStandard2D);
        zXingConfiguration.setBoolean(KEY_DECODE_AZTEC, false);
        zXingConfiguration.setBoolean(KEY_DECODE_PDF417, false);

        zXingConfiguration.setString(KEY_FRONT_LIGHT_MODE, FrontLightMode.OFF.toString());
        zXingConfiguration.setBoolean(KEY_AUTO_FOCUS, true);
        zXingConfiguration.setBoolean(KEY_INVERT_SCAN, false);

        zXingConfiguration.setBoolean(KEY_DISABLE_CONTINUOUS_FOCUS, true);
        zXingConfiguration.setBoolean(KEY_DISABLE_EXPOSURE, true);
        zXingConfiguration.setBoolean(KEY_DISABLE_METERING, true);
        zXingConfiguration.setBoolean(KEY_DISABLE_BARCODE_SCENE_MODE, true);

        return zXingConfiguration;
    }
}
