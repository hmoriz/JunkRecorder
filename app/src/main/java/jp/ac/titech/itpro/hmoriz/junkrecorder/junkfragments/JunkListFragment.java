package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;

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

    ArrayAdapter<File> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_junklist, container, false);
        ButterKnife.bind(this, view);
        arrayAdapter = new ArrayAdapter<File>(container.getContext(), android.R.layout.simple_expandable_list_item_1);
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

    public void updateList(){
        arrayAdapter.clear();
        arrayAdapter.addAll(JunkDataStore.getInstance().getAllTextFiles(getActivity()));
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
            Log.d("onListClickListner", String.valueOf(position));
            Log.d("onListClickListner", view.toString());
            Log.d("onListClickListner", parent.getItemAtPosition(position).getClass().toString() + "  " +parent.getItemAtPosition(position).toString());
            String filename = parent.getItemAtPosition(position).toString();
            int len = filename.split("/").length;
            String filename2 = filename.split("/")[len-1].split("\\W")[0];
            mainActivity.moveToInputFragment(filename2);
        }
    }
}
