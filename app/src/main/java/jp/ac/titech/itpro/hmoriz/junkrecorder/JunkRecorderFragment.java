package jp.ac.titech.itpro.hmoriz.junkrecorder;

/**
 * Created by hmoriz on 2016/07/05.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */

public abstract class JunkRecorderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";
    static String fileLocation = "test1.txt";

    public JunkRecorderFragment() {
    }

    // Factory Method
    public static JunkRecorderFragment newInstance(int sectionNumber) {
        JunkRecorderFragment fragment;
        switch(sectionNumber){
            case 1:
                fragment = new InputFragment();
                break;
            case 2:
                fragment = new OutputFragment();
                break;
            default:
                fragment = new JunkFragment();

        }
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

}