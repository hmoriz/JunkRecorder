package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

/**
 * Created by hmoriz on 2016/07/05.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import jp.ac.titech.itpro.hmoriz.junkrecorder.MainActivity;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junk.Junk;

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
    protected String filename; // 現在フォーカスを当てているファイルの名前 使い方は子クラスにまかせる
    protected Junk mJunk; // 現在編集中のjunkのインスタンス

    protected MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        Log.d("JunkRecorderFragment","onAttach:"+context.toString());
        super.onAttach(context);
        if(context.getClass().equals(MainActivity.class)) mainActivity = (MainActivity)context;
    }

    public JunkRecorderFragment() {
    }

    public String getFilename(){
        return filename;
    }

    public void loadJunk(String filename){
        this.filename = filename;
    }

    public static final int FRAGMENT_INPUT_NORMAL = 1;
    public static final int FRAGMENT_OUTPUT_NORMAL = 2;
    public static final int FRAGMENT_INPUT_IMAGE = 3;
    public static final int FRAGMENT_OUTPUT_LIST = 4;
    public static final int FRAGMENT_INPUT_MAP = 5;

    // Factory Method
    public static JunkRecorderFragment newInstance(int sectionNumber) {
        JunkRecorderFragment fragment;
        switch(sectionNumber){
            case FRAGMENT_INPUT_NORMAL:
                fragment = new InputFragment();
                break;
            case FRAGMENT_OUTPUT_NORMAL:
                fragment = new OutputFragment();
                break;
            case FRAGMENT_INPUT_IMAGE:
                fragment = new ImageInputFragment();
                break;
            case FRAGMENT_OUTPUT_LIST:
                fragment = new JunkListFragment();
                break;
            case FRAGMENT_INPUT_MAP:
                fragment = new InputMapFragment();
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