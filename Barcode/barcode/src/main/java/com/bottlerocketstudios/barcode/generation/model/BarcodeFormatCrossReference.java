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

package com.bottlerocketstudios.barcode.generation.model;

import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;

public enum BarcodeFormatCrossReference {
    EAN_8("ean_8", 1, BarcodeFormat.EAN_8),
    UPC_E("upc_e", 2, BarcodeFormat.UPC_E),
    EAN_13("ean_13", 3, BarcodeFormat.EAN_13),
    UPC_A("upc_a", 4, BarcodeFormat.UPC_A),
    QR_CODE("qr_code", 5, BarcodeFormat.QR_CODE),
    CODE_39("code_39", 6, BarcodeFormat.CODE_39),
    CODE_128("code_128", 7, BarcodeFormat.CODE_128),
    ITF("itf", 8, BarcodeFormat.ITF),
    PDF_417("pdf_417", 9, BarcodeFormat.PDF_417),
    CODABAR("codabar", 10, BarcodeFormat.CODABAR),
    DATA_MATRIX("data_matrix", 11, BarcodeFormat.DATA_MATRIX),
    AZTEC("aztec", 12, BarcodeFormat.AZTEC)
    ;

    private final String mStyleableEnumString;
    private final int mStyleableEnumInt;
    private final BarcodeFormat mBarcodeFormat;

    BarcodeFormatCrossReference(String styleableEnumString, int styleableEnumInt, BarcodeFormat barcodeFormat) {
        mStyleableEnumString = styleableEnumString;
        mStyleableEnumInt = styleableEnumInt;
        mBarcodeFormat = barcodeFormat;
    }

    public static BarcodeFormatCrossReference lookupByStyleableEnumInt(int styleableEnumInt) {
        for (BarcodeFormatCrossReference barcodeFormatCrossReference : BarcodeFormatCrossReference.values()) {
            if (barcodeFormatCrossReference.getStyleableEnumInt() == styleableEnumInt) {
                return barcodeFormatCrossReference;
            }
        }
        throw new IllegalArgumentException("Styleable Enumeration Integer value out of range");
    }

    public static BarcodeFormatCrossReference lookupByStyleableEnumString(String styleableEnumString) {
        for (BarcodeFormatCrossReference barcodeFormatCrossReference : BarcodeFormatCrossReference.values()) {
            if (TextUtils.equals(barcodeFormatCrossReference.getStyleableEnumString(), styleableEnumString)) {
                return barcodeFormatCrossReference;
            }
        }
        throw new IllegalArgumentException("Styleable Enumeration String unknown " + styleableEnumString);
    }

    public int getStyleableEnumInt() {
        return mStyleableEnumInt;
    }

    public BarcodeFormat getBarcodeFormat() {
        return mBarcodeFormat;
    }

    public String getStyleableEnumString() {
        return mStyleableEnumString;
    }
}
