package com.example.sdaassign32021;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
/*      Copyright [2021] [Paul fennell]

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
        */
/*
 * This is a viewPager adapter.
 * @author Paul fennell
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context nContext) {
        super(fm, behavior);
        context = nContext;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Fragment();

        //finds the tab position (note array starts at 0)
        position = position+1;

        //finds the fragment
        switch (position)
        {
            case 1:
                //code
                fragment = new WelcomeScreen();
                break;
            case 2:
                //code
                fragment = new ProductList();
                break;
            case 3:
                //code
                fragment = new OrderTshirt();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        position = position+1;

        CharSequence tabTitle = "";

        //finds the fragment
        switch (position)
        {
            case 1:
                //code
                tabTitle = "Welcome";
                break;
            case 2:
                //code
                tabTitle = "Product List";
                break;
            case 3:
                //code
                tabTitle = "Order T Shirt";
                break;

        }

        return tabTitle;
    }
}
