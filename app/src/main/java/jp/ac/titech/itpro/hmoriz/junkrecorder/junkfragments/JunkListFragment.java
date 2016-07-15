package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.ac.titech.itpro.hmoriz.junkrecorder.JunkDataStore;
import jp.ac.titech.itpro.hmoriz.junkrecorder.R;

/**
 * Created by hmoriz on 2016/07/08.
 */
public class JunkListFragment extends JunkRecorderFragment{

    @BindView(R.id.boyakiListView)
    ListView boyakiList;

    @BindView(R.id.newButton)
    Button newButton;

    @BindView(R.id.refreshListButton)
    Button refreshButton;

    @BindView(R.id.deleteListButton)
    Button deleteButton;

    @BindView(R.id.mapButton)
    Button mapButton;

    ArrayAdapter<String> arrayAdapter;
    HashMap<String, File> mFileMap = new HashMap<>();

    private final static String TAG_ADD = "のつぶやき";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_junklist, container, false);
        ButterKnife.bind(this, view);
        arrayAdapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_expandable_list_item_1);
        updateList();
        boyakiList.setAdapter(arrayAdapter);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateList();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });
        boyakiList.setOnItemClickListener(new OnListClickListener());
        boyakiList.setOnItemLongClickListener(new OnListLongClickListener());
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.moveToInputFragment();
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.moveToMapFragment();
            }
        });
        return view;
    }

    public void updateList() {
        arrayAdapter.clear();
        mFileMap.clear();
        int id = 1;
        List<File> fileList = JunkDataStore.getInstance().getAllTextFiles(getActivity());
        if (fileList != null){
            for (File file : fileList) {
                String filename = "Junk" + id + "(" +file.getName() + ")";
                arrayAdapter.add(filename);
                mFileMap.put(filename, file);
                id++;
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    private void clearList(){
        JunkDataStore.getInstance().deleteAllFiles(getActivity());
        updateList();
    }

    @Override
    public void loadJunk(String filename) {

    }

    private class OnListClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String filename =  filename = mFileMap.get(parent.getItemAtPosition(position)).getName().split("\\.")[0];
            mainActivity.moveToInputFragment(filename);
        }
    }

    private class OnListLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            filename = mFileMap.get(parent.getItemAtPosition(position)).getName().split("\\.")[0];
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            AlertDialog alertDialog = builder.setTitle("削除の確認")
                    .setMessage(filename + "を削除してもよろしいですか？")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JunkDataStore.getInstance().deleteFile(getActivity(), filename);
                            updateList();
                        }
                    }).setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DO NOTHING
                        }
                    }).create();
            return false;
        }
    }
}
