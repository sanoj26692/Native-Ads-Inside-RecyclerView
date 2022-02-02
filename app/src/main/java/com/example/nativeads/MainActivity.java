/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.nativeads;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    RecyclerViewAdapter adapter;
    private NativeAd nativeAd;
    private AdLoader adLoader;
    int NUMBER_OF_ADS = 3;

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";

    ArrayList<Object> arrayList = new ArrayList<>();
    private final List<NativeAd> mNativeAds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {}
        });

        // data to populate the RecyclerView with

        arrayList.add("Horse");
        arrayList.add("Cow");
        arrayList.add("Camel");
        arrayList.add("Sheep");
        arrayList.add("Goat");

        refreshAd();
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, arrayList);
        recyclerView.setAdapter(adapter);
    }

    private void refreshAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);
        adLoader = builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            // OnLoadedListener implementation.
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                mNativeAds.add(nativeAd);
                if (!adLoader.isLoading()) {
                    insertAdsInMenuItems();
                }
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                if (!adLoader.isLoading()) {
                    insertAdsInMenuItems();
                }
            }
            @Override
            public void onAdClicked() {
                // Log the click event or other custom behavior.
            }
        }).build();
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }
        //int offset = (FolderVideoPath.size() / mNativeAds.size()) + 1;
        int offset = 2;
        int index = 1;
        for (NativeAd ad : mNativeAds) {
            arrayList.add(index, ad);
            index = index + offset;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

}
