package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.ac.titech.itpro.hmoriz.junkrecorder.JunkDataStore;
import jp.ac.titech.itpro.hmoriz.junkrecorder.R;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junk.Junk;

/**
 * Created by hmoriz on 2016/07/05.
 */
public class InputFragment extends JunkRecorderFragment {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E曜日 H時m分s秒", Locale.JAPANESE);

    @BindView(R.id.inputtextView)
    TextView inputTextView;

    @BindView(R.id.editInputText)
    EditText inputEditText;

    @BindView(R.id.inputDecideButton)
    Button inputDecideButton;

    @BindView(R.id.inputNewButton)
    Button inputNewButton;

    @BindView(R.id.inputImagebutton)
    Button inputImageButton;

    @BindView(R.id.inputLocationbutton)
    Button inputLocationButton;

    @BindView(R.id.outputTextView)
    TextView outputView;

    boolean mFromMap = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        ButterKnife.bind(InputFragment.this, view);
        initViews();
        inputNewButton.setOnClickListener(new OninputNewButtonClickListner());
        if(filename != null) loadJunk(filename);
        mJunk = null;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        inputDecideButton.setOnClickListener(new OnInputDecideButtonClickListner());
        inputImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputEditText.getText().toString().isEmpty()) {
                    saveJunk();
                }
                mainActivity.moveToImageInputFragment(filename);
            }
        });
        inputLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputEditText.getText().toString().isEmpty()) {
                    saveJunk();
                }
                mainActivity.moveToMapFragment(filename);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    // 各TextView等の内容の初期化
    public void initViews(){
        inputEditText.setText("");
        inputTextView.setText("新しいぼやきの作成");
        outputView.setText("");
        filename=null;
    }

    // フラグメントの情報を引数のboyakiで読み込む
    @Override
    public void loadJunk(String filename) {
        super.loadJunk(filename);
        class LoadTextBoyaki implements Runnable{
            Junk junk;
            String filename;
            public LoadTextBoyaki(String filename){
                if(filename != null) {
                    this.filename = filename;
                    this.junk = JunkDataStore.getInstance().readJunkJson(mainActivity, filename);
                }
            }
            @Override
            public void run() {
                if(filename != null) {
                    inputEditText.setText("");
                    inputEditText.clearFocus();
                    inputTextView.setText(filename + "を編集中");
                    if(junk != null){
                        mJunk = junk;
                        String text = "";
                        text = text + sdf.format(junk.date)+"のつぶやき: "+junk.text + "\n";
                        Junk junk1 = junk;
                        while(junk1.hasNextJunk()){
                            try {
                                junk1 = junk1.getNextJunk();
                                text = text + sdf.format(junk1.date)+"のつぶやき: "+junk1.text + "\n";
                            } catch (JSONException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                        if(mJunk.getLocation() != null){
                            inputLocationButton.setText("MAPで位置表示");
                        }
                        if(mJunk.getImageAddress() != null){
                            inputImageButton.setText("画像の表示・編集");
                        }
                        outputView.setText(text);
                    }
                }else{
                    inputEditText.setText("");
                    inputTextView.setText("新規編集中");
                }
            }
        }
        class LoadThread implements Runnable{
            String filename;
            public LoadThread(String filename){
                this.filename = filename;
            }
            @Override
            public void run() {
                // 強引に、viewができるまでは処理は行わない
                while(getView() == null){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mainActivity.runOnUiThread(new LoadTextBoyaki(filename));
            }
        }
        mainActivity.runOnUiThread(new LoadThread(filename));
    }

    // とりあえずファイルに保存
    public void saveJunk(){
        // EditTextに書かれた内容をファイルに保存する
        String filename;
        if(InputFragment.this.filename != null && !InputFragment.this.filename.isEmpty()){
            filename = InputFragment.this.filename;
        }else{
            filename = JunkDataStore.makeFilename(getActivity());
        }
        String text = inputEditText.getText().toString();
        if(mJunk != null) {
            try {
                Junk oldJunk = mJunk;
                mJunk = mJunk.addPreviousJunk(new Junk());
                mJunk.setImageAddress(oldJunk.getImageAddress());
                mJunk.setLocation(oldJunk.getLocation());
                oldJunk.setImageAddress(null);
                oldJunk.setLocation(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            mJunk = new Junk();
        }
        mJunk.text = text;
        JunkDataStore.getInstance().writeJunk(getActivity(), mJunk, filename);
        inputEditText.setText("");
        loadJunk(filename);
    }

    public void enableMapCallback() {
        mFromMap = true;
        inputLocationButton.setEnabled(false);
    }

    public void setupLocation(LatLng latLng) {
        mFromMap = true;
        inputLocationButton.setEnabled(false);
        if(mJunk != null){
            mJunk.setLocation(latLng);
        }
    }

    // 決定ボタンが押されたときの処理をここに書く
    private class OnInputDecideButtonClickListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            saveJunk();
            if(mFromMap){
                mainActivity.moveToMapFragment(filename);
            }
        }
    }


    // 新規作成ボタンが押されたときの処理をここに書く
    private class OninputNewButtonClickListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            initViews();
            filename = null;
        }
    }



}
