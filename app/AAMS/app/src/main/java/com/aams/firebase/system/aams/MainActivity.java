/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aams.firebase.system.aams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;

public class  MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private final int PERMISSION_REQUEST_COARSE_LOCATION = 1984;

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplication().getApplicationContext(), MyService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                final AlertDialog.Builder builder    = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        requestPermissions(
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
            };
            private final String[] mFragmentNames = new String[] {

            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else if (i == R.id.action_setLevel) {
            Log.d(TAG, "SET UP A PAGE FOR LEVEL SETTINGS");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
