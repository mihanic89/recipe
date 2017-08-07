package xyz.yapapa.recipe.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import xyz.yapapa.recipe.R;
import xyz.yapapa.recipe.ui.component.RecyclerViewAdapter;


public class MainActivityRecycler extends AppCompatActivity {



    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        int[] gaggeredList = getListItemData();

        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(MainActivityRecycler.this, gaggeredList);
        recyclerView.setAdapter(rcAdapter);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2888343178529026~2653479392");


        mAdView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                .build();
        mAdView.loadAd(adRequest);



    }

    private int[] getListItemData(){

            int[] mDataset = new int[]{
                    R.mipmap.c001,
                    R.mipmap.c002,
                    R.mipmap.c003,
                    R.mipmap.c004,
                    R.mipmap.c005,
                    R.mipmap.c006,
                    R.mipmap.c007,
                    R.mipmap.c008,
                    R.mipmap.c009,
                    R.mipmap.d01,
                    R.mipmap.d02,
                    R.mipmap.d03,
                    R.mipmap.d04,
                    R.mipmap.d05,
                    R.mipmap.d06,
                    R.mipmap.d07,
                    R.mipmap.d08,
                    R.mipmap.d09,
                    R.mipmap.d10,
                    R.mipmap.d11,
                    R.mipmap.d12,
                    R.mipmap.d13,
                    R.mipmap.d14,
                    R.mipmap.d15,
                    R.mipmap.d16,
                    R.mipmap.d17,
                    R.mipmap.d18,
                    R.mipmap.d19,
                    R.mipmap.d20,
                    R.mipmap.d21,
                    R.mipmap.d22,
                    R.mipmap.d23,
                    R.mipmap.d24,
                    R.mipmap.d25,
                    R.mipmap.d26,
                    R.mipmap.d27,
                    R.mipmap.b01,
                    R.mipmap.b02,
                    R.mipmap.b03,
                    R.mipmap.b04,
                    R.mipmap.b05,
                    R.mipmap.b06,
                    R.mipmap.b07,
                    R.mipmap.b08,
                    R.mipmap.d28,
                    R.mipmap.d29,
                    R.mipmap.d30,
                    R.mipmap.d31,
                    R.mipmap.d32,
                    R.mipmap.d33,
                    R.mipmap.d34,
                    R.mipmap.d35,
            };


       return mDataset;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


}
