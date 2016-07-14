package jp.ac.titech.itpro.hmoriz.junkrecorder;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.ImageInputFragment;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.InputFragment;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.InputMapFragment;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.JunkListFragment;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.JunkRecorderFragment;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.OutputFragment;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private JunkViewPager mViewPager;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!getFilesDir().exists())getFilesDir().mkdirs();
        JunkDataStore.initFilename_id(this, getSharedPreferences("data_id", MODE_PRIVATE).getLong("id", 0));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.add(SectionsPagerAdapter.TAG_LIST);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (JunkViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeThisSection();
            }
        });
        mAppBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        Log.d("MainActivity", "onCreate");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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
        // automatically handle clicks on the Home/Up refreshButton, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d("MainActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy");
        super.onDestroy();
    }

    public void moveViewerPage(int position){
        mViewPager.setCurrentItem(position, true);
    }

    public void moveToInputFragment(){
        moveToInputFragment(null);
    }
    public void moveToInputFragment(String filename){
        JunkRecorderFragment junkRecorderFragment = mSectionsPagerAdapter.findJunkRecorderFragment(InputFragment.class);
        if(junkRecorderFragment == null){
            junkRecorderFragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_INPUT_NORMAL);
            mSectionsPagerAdapter.add(SectionsPagerAdapter.TAG_INPUT);
        }
        int position = mSectionsPagerAdapter.getPosition(SectionsPagerAdapter.TAG_INPUT);
        ((JunkRecorderFragment)mSectionsPagerAdapter.getFragment(SectionsPagerAdapter.TAG_INPUT, position)).loadJunk(filename);
        mViewPager.setCurrentItem(position, true);
    }
    public void moveToInputFragment(String filename, LatLng latLng){
        moveToInputFragment(filename);
        int position = mSectionsPagerAdapter.getPosition(SectionsPagerAdapter.TAG_INPUT);
        ((InputFragment)mSectionsPagerAdapter.getFragment(SectionsPagerAdapter.TAG_INPUT, position)).setupLocation(latLng);
    }
    public void moveToOutputFragment(){
        int position = mSectionsPagerAdapter.findJunkRecorderFragmentPosition(OutputFragment.class);
        mViewPager.setCurrentItem(position, true);
    }
    public void moveToOutputFragment(String filename){
        mSectionsPagerAdapter.findJunkRecorderFragment(OutputFragment.class).loadJunk(filename);
        moveToOutputFragment();
    }
    public void moveToImageInputFragment(){
        moveToImageInputFragment(null);
    }
    public void moveToImageInputFragment(String filename){
        mAppBarLayout.setExpanded(false, true);
        JunkRecorderFragment junkRecorderFragment = mSectionsPagerAdapter.findJunkRecorderFragment(ImageInputFragment.class);
        if(junkRecorderFragment == null){
            junkRecorderFragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_INPUT_IMAGE);
            mSectionsPagerAdapter.add(SectionsPagerAdapter.TAG_IMAGE);
        }
        int position = mSectionsPagerAdapter.getPosition(SectionsPagerAdapter.TAG_IMAGE);
        ((JunkRecorderFragment)mSectionsPagerAdapter.getFragment(SectionsPagerAdapter.TAG_IMAGE, position)).loadJunk(filename);
        closeInputMethod();
        mViewPager.setCurrentItem(position, true);
    }
    public void moveToJunkListFragment(){
        int position = mSectionsPagerAdapter.findJunkRecorderFragmentPosition(JunkListFragment.class);
        mViewPager.setCurrentItem(position, true);
    }
    public void moveToMapFragment(){
        closeInputMethod();
        mAppBarLayout.setExpanded(false, true);
        JunkRecorderFragment junkRecorderFragment = mSectionsPagerAdapter.findJunkRecorderFragment(InputMapFragment.class);
        if(junkRecorderFragment == null){
            junkRecorderFragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_INPUT_MAP);
            mSectionsPagerAdapter.add(SectionsPagerAdapter.TAG_MAP);
        }
        int position = mSectionsPagerAdapter.getPosition(SectionsPagerAdapter.TAG_MAP);
        mViewPager.setCurrentItem(position, true);
    }

    public void moveToMapFragment(String filename){
        closeInputMethod();
        mAppBarLayout.setExpanded(false, true);
        JunkRecorderFragment junkRecorderFragment = mSectionsPagerAdapter.findJunkRecorderFragment(InputMapFragment.class);
        if(junkRecorderFragment == null){
            mSectionsPagerAdapter.add(SectionsPagerAdapter.TAG_MAP);
        }
        int position = mSectionsPagerAdapter.getPosition(SectionsPagerAdapter.TAG_MAP);
        ((InputMapFragment)mSectionsPagerAdapter.getFragment(SectionsPagerAdapter.TAG_MAP, position)).startAddJunkMode(filename);
        mViewPager.setCurrentItem(position, true);
    }

    public void closeThisSection(){
        int position = mViewPager.getCurrentItem();
        if(position == 0){
            Toast.makeText(this, "Sorry! You can't close List Fragment!", Toast.LENGTH_LONG).show();
            return;
        }
        mViewPager.setCurrentItem(position-1, true);
        mSectionsPagerAdapter.remove(position);
    }

    public void closeInputMethod(){
        //ソフトキーボードを閉じる
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ButterKnife.findById(this, android.R.id.content);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
