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

package com.bottlerocketstudios.barcode.generation.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bottlerocketstudios.barcode.R;
import com.bottlerocketstudios.barcode.generation.controller.AsyncBarcodeBitmapGenerator;
import com.bottlerocketstudios.barcode.generation.model.BarcodeFormatCrossReference;
import com.bottlerocketstudios.barcode.generation.model.BarcodeRequest;
import com.google.zxing.BarcodeFormat;

public class BarcodeView extends ImageView {

    private static final BarcodeFormatCrossReference DEFAULT_BARCODE_FORMAT_STYLEABLE_VALUE = BarcodeFormatCrossReference.QR_CODE;

    BarcodeFormat mBarcodeFormat = BarcodeFormat.QR_CODE;
    private String mBarcodeText;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private int mBarcodeForegroundColor = BarcodeRequest.DEFAULT_FOREGROUND_COLOR;
    private int mBarcodeBackgroundColor = BarcodeRequest.DEFAULT_BACKGROUND_COLOR;
    private String mBarcodeCharacterSet = BarcodeRequest.DEFAULT_CHARACTERSET;

    private AsyncBarcodeBitmapGenerator mAsyncBarcodeBitmapGenerator = new AsyncBarcodeBitmapGenerator(true);
    private UpdateRunnable mUpdateRunnable = new UpdateRunnable(this);

    public BarcodeView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public BarcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public BarcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarcodeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        BarcodeFormatCrossReference barcodeFormatCrossReference = DEFAULT_BARCODE_FORMAT_STYLEABLE_VALUE;
        if (attrs != null) {
            TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BarcodeView,
                    defStyleAttr,
                    defStyleRes);
            try {
                if (isInEditMode()) {
                    //For some reason the layout preview uses the string value of the enum instead of the integer which is used at runtime.
                    barcodeFormatCrossReference = BarcodeFormatCrossReference.lookupByStyleableEnumString(styledAttributes.getString(R.styleable.BarcodeView_barcode_format));
                } else {
                    barcodeFormatCrossReference = BarcodeFormatCrossReference.lookupByStyleableEnumInt(styledAttributes.getInt(R.styleable.BarcodeView_barcode_format, 0));
                }
                mBarcodeForegroundColor = styledAttributes.getColor(R.styleable.BarcodeView_barcode_foreground_color, mBarcodeForegroundColor);
                mBarcodeBackgroundColor = styledAttributes.getColor(R.styleable.BarcodeView_barcode_background_color, mBarcodeBackgroundColor);
                mBarcodeText = styledAttributes.getString(R.styleable.BarcodeView_barcode_text);

                String characterSet = styledAttributes.getString(R.styleable.BarcodeView_barcode_character_set);
                if (!TextUtils.isEmpty(characterSet)) {
                    mBarcodeCharacterSet = characterSet;
                }
            } finally {
                styledAttributes.recycle();
            }
        }
        mBarcodeFormat = barcodeFormatCrossReference.getBarcodeFormat();
    }

    /**
     * Change the text of the barcode to the supplied value.
     */
    public void setBarcodeText(String barcodeText) {
        if (!TextUtils.equals(mBarcodeText, barcodeText)) {
            mBarcodeText = barcodeText;
            postUpdateBarcode();
        }
    }

    /**
     * Change the barcode foreground color. Typically black for barcodes.
     */
    public void setBarcodeForegroundColor(int foregroundColor) {
        if (foregroundColor != mBarcodeForegroundColor) {
            mBarcodeForegroundColor = foregroundColor;
            postUpdateBarcode();
        }
    }

    /**
     * Change the barcode foreground color. Typically white for barcodes.
     */
    public void setBarcodeBackgroundColor(int backgroundColor) {
        if (backgroundColor != mBarcodeBackgroundColor) {
            mBarcodeBackgroundColor = backgroundColor;
            postUpdateBarcode();
        }
    }

    /**
     * Change the barcode format to be drawn.
     */
    public void setBarcodeFormat(BarcodeFormat barcodeFormat) {
        if (!mBarcodeFormat.equals(barcodeFormat)) {
            mBarcodeFormat = barcodeFormat;
            postUpdateBarcode();
        }
    }

    /**
     * Change the character set encoded by the barcode generator.
     */
    public void setBarcodeCharacterSet(String characterSet) {
        if (!TextUtils.equals(mBarcodeCharacterSet, characterSet)) {
            mBarcodeCharacterSet = characterSet;
            postUpdateBarcode();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = w - (getPaddingLeft() + getPaddingRight());
        int height = h - (getPaddingTop() + getPaddingBottom());
        if (mDrawableWidth != width || mDrawableHeight != height) {
            mDrawableWidth = width;
            mDrawableHeight = height;
            postUpdateBarcode();
        }
    }

    /**
     * Post a runnable to the UI Handler to start updating the barcode. You should only need this if
     * you have called setImageBitmap/Resource/etc manually.
     */
    public void postUpdateBarcode() {
        removeCallbacks(mUpdateRunnable);
        post(mUpdateRunnable);
    }

    /**
     * Prevent hammering the update with back-to-back calls to setForeground/Text/Etc by running it
     * all through this runnable that will only be posted once per UI loop.
     */
    private static class UpdateRunnable implements Runnable {
        private BarcodeView mBarcodeView;

        public UpdateRunnable(BarcodeView barcodeView) {
            mBarcodeView = barcodeView;
        }

        @Override
        public void run() {
            mBarcodeView.updateBarcode();
        }
    }

    /**
     * Call to force the barcode to be regenerated asynchronously. This should not be necessary unless
     * setImageResource/Bitmap/etc has been called from outside of this class.
     */
    private void updateBarcode() {
        if (mDrawableHeight > 0 && mDrawableWidth > 0 && !TextUtils.isEmpty(mBarcodeText)) {
            BarcodeRequest barcodeRequest = (new BarcodeRequest.BarcodeRequestBuilder())
                    .barcodeText(mBarcodeText)
                    .barcodeFormat(mBarcodeFormat)
                    .width(mDrawableWidth)
                    .height(mDrawableHeight)
                    .foregroundColor(mBarcodeForegroundColor)
                    .backgroundColor(mBarcodeBackgroundColor)
                    .characterSet(mBarcodeCharacterSet)
                    .build();
            mAsyncBarcodeBitmapGenerator.startGeneration(barcodeRequest, mBarcodeGenerationListener);
        }
    }

    private AsyncBarcodeBitmapGenerator.BarcodeGenerationListener mBarcodeGenerationListener = new AsyncBarcodeBitmapGenerator.BarcodeGenerationListener() {
        @Override
        public void onGenerationComplete(Bitmap bitmap) {
            displayNewBarcode(bitmap);
        }
    };

    private void displayNewBarcode(Bitmap bitmap) {
        setImageBitmap(bitmap);
    }
}
