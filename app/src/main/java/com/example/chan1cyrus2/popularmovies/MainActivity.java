package com.example.chan1cyrus2.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.facebook.stetho.Stetho;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MasterFragment.Callback{
    @Nullable @Bind(R.id.detail_container) FrameLayout detail_container;

    private static final String DetailFragmentTag = "DFTAG";

    private boolean mTwoPane; //tablet mode with 600dp width

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //decide wether it is tablet mode
        if(null != detail_container){
            mTwoPane = true;
            // Make sure that we are not being restored from a previous state,
            // else we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_container, new DetailFragment(), DetailFragmentTag)
                        .commit();
                Stetho.initialize(
                        Stetho.newInitializerBuilder(this)
                                .enableDumpapp(
                                        Stetho.defaultDumperPluginsProvider(this))
                                .enableWebKitInspector(
                                        Stetho.defaultInspectorModulesProvider(this))
                                .build());
            }
        }else{
            mTwoPane = false;
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(
                                    Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(
                                    Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MainSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //MasterFragement.Callback
    @Override
    public void onItemSelected(Movie movie) {
        if(mTwoPane){
            //send Movie details through Parcel and replace detail fragment with the new details
            Bundle args = new Bundle();
            args.putParcelable(Movie.PAR_KEY, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DetailFragmentTag)
                    .commit();
        }else{
            //Start Detail Activity with Movie details through Parcel
            Bundle bundle = new Bundle();
            bundle.putParcelable(Movie.PAR_KEY, movie);
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtras(bundle);
            startActivity(intent);
        }
    }
}
