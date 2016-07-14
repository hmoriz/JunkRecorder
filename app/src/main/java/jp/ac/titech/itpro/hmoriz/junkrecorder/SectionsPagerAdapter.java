package jp.ac.titech.itpro.hmoriz.junkrecorder;

/**
 * Created by hmoriz on 2016/07/05.
 */

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakama.arraypageradapter.ArrayFragmentPagerAdapter;
import com.nakama.arraypageradapter.ArrayFragmentStatePagerAdapter;
import com.nakama.arraypageradapter.ArrayViewPagerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.JunkFragment;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments.JunkRecorderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends ArrayFragmentStatePagerAdapter<String> {

    HashMap<String, JunkRecorderFragment> fragmentHashMap = new HashMap<>();

    static final String TAG_LIST = "LIST";
    static final String TAG_INPUT = "INPUT";
    static final String TAG_IMAGE = "IMAGE";
    static final String TAG_MAP = "MAP";

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        List<Fragment> fragments = fm.getFragments();
    }


    @Override
    public Fragment getFragment(String item, int position) {
        if(fragmentHashMap.get(item) != null){
            return fragmentHashMap.get(item);
        }
        Fragment fragment = null;
        switch (item){
            case TAG_LIST:
                fragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_OUTPUT_LIST);
                fragmentHashMap.put(item, (JunkRecorderFragment) fragment);
                return fragment;
            case TAG_INPUT:
                fragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_INPUT_NORMAL);
                fragmentHashMap.put(item, (JunkRecorderFragment) fragment);
                return fragment;
            case TAG_IMAGE:
                fragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_INPUT_IMAGE);
                fragmentHashMap.put(item, (JunkRecorderFragment) fragment);
                return fragment;
            case TAG_MAP:
                fragment = JunkRecorderFragment.newInstance(JunkRecorderFragment.FRAGMENT_INPUT_MAP);
                fragmentHashMap.put(item, (JunkRecorderFragment) fragment);
                return fragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position);
    }


    @Override
    public void add(String item) {
        getFragment(item, fragmentHashMap.size());
        super.add(item);
    }

    @Override
    public void remove(int position) {
        fragmentHashMap.remove(getItem(position));
        super.remove(position);
    }

    @Override
    public void clear() {
        super.clear();
        fragmentHashMap.clear();
    }

    public <T extends JunkRecorderFragment> T findJunkRecorderFragment(Class<T> clazz){
        for(String fragmentItem : fragmentHashMap.keySet()){
            if(fragmentHashMap.get(fragmentItem).getClass().equals(clazz))return (T)fragmentHashMap.get(fragmentItem);
        }
        return null;
    }
    public <T extends JunkRecorderFragment> int findJunkRecorderFragmentPosition(Class<T> clazz){
        for(String  fragmentItem : fragmentHashMap.keySet()){
            if(fragmentHashMap.get(fragmentItem).getClass().equals(clazz)){
                return getItemPosition(fragmentHashMap.get(fragmentItem));
            }
        }
        return -1;
    }

}