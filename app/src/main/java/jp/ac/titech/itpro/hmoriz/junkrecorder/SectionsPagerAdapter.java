package jp.ac.titech.itpro.hmoriz.junkrecorder;

/**
 * Created by hmoriz on 2016/07/05.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private JunkRecorderFragment fragments[] = new JunkRecorderFragment[3];

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(fragments[position] != null){
            return fragments[position];
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a JunkRecorderFragment (defined as a static inner class below).
        JunkRecorderFragment fragment = JunkRecorderFragment.newInstance(position+1);
        fragments[position] = fragment;
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}