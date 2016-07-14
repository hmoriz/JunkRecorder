package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.ac.titech.itpro.hmoriz.junkrecorder.R;

/**
 * Created by hmoriz on 2016/07/05.
 */
public class JunkFragment extends JunkRecorderFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    @Override
    public void loadJunk(String filename) {
        super.loadJunk(filename);
    }
}
