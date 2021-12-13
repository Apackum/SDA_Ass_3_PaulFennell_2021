package com.example.sdaassign32021;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
 * A simple {@link Fragment} subclass.
 * @author Chris Coughlan 2019
 */
public class ProductList extends Fragment {

    private static final String TAG = "RecyclerViewActivity";
    private ArrayList<FlavorAdapter> mFlavor = new ArrayList<>();

    public ProductList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_product_list, container, false);
        // Create an ArrayList of AndroidFlavor objects
        mFlavor.add(new FlavorAdapter("Red devil T-Shirt", "€15", R.drawable.red_devil));
        mFlavor.add(new FlavorAdapter("Coding T-Shirt", "€12", R.drawable.coding_for_life));
        mFlavor.add(new FlavorAdapter("DCU 2021 T-Shirt", "€125", R.drawable.dcu_2021));
        mFlavor.add(new FlavorAdapter("404", "€12", R.drawable.error_404));
        mFlavor.add(new FlavorAdapter("Sunshine", "€16", R.drawable.hello_sunshine));
        mFlavor.add(new FlavorAdapter("Hello World", "€2", R.drawable.hello_world));
        mFlavor.add(new FlavorAdapter("Ho Ho Ho", "€10", R.drawable.ho_ho_ho));
        mFlavor.add(new FlavorAdapter("Merry Christmas", "€20", R.drawable.merry_christmas));
        mFlavor.add(new FlavorAdapter("SDA ASS 3", "€50", R.drawable.sda_ass_3));
        mFlavor.add(new FlavorAdapter("Smiley", "€5", R.drawable.smiley));

        //start it with the view
        Log.d(TAG, "Starting recycler view");
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView_view);
        FlavorViewAdapter recyclerViewAdapter = new FlavorViewAdapter(getContext(), mFlavor);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }
}
