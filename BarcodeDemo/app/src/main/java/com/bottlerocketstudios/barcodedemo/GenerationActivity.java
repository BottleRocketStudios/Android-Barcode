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

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.bottlerocketstudios.barcode.generation.ui.BarcodeView;

public class GenerationActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private EditText mBarcodeText;
    private BarcodeView mBarcodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generation_activity);

        mBarcodeText = (EditText) findViewById(R.id.generation_barcode_text);
        mBarcodeText.addTextChangedListener(mBarcodeTextWatcher);

        mBarcodeImage = (BarcodeView) findViewById(R.id.generation_barcode_image);
    }

    private TextWatcher mBarcodeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mHandler.removeCallbacks(mUpdateBarcodeRunnable);
            mHandler.postDelayed(mUpdateBarcodeRunnable, 500);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private Runnable mUpdateBarcodeRunnable = new Runnable() {
        @Override
        public void run() {
            mBarcodeImage.setBarcodeText(mBarcodeText.getText().toString());
        }
    };
}
