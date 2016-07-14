package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.ac.titech.itpro.hmoriz.junkrecorder.JunkDataStore;
import jp.ac.titech.itpro.hmoriz.junkrecorder.R;
import jp.ac.titech.itpro.hmoriz.junkrecorder.junk.Junk;

/**
 * Created by hmoriz on 2016/07/05.
 */
public class ImageInputFragment extends JunkRecorderFragment {

    @BindView(R.id.inputDecideButton)
    Button inputDecideButton;

    @BindView(R.id.imageView)
    JunkImageView imageView;

    @BindView(R.id.inputRedoButton)
    Button inputRedoButton;

    @BindView(R.id.inputResetButton)
    Button inputClearButton;

    @BindView(R.id.inputCloseButton)
    Button inputCloseButton;

    @BindView(R.id.imageInputTextView)
    TextView inputTextView;

    Bitmap bitmapCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_imageinput, container, false);
        ButterKnife.bind(ImageInputFragment.this, view);

        //mJunk = new Junk();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        inputRedoButton.setOnClickListener(new OnInputRedoButtonClickListener());
        inputClearButton.setOnClickListener(new OnInputClearButtonClickListener());
        inputDecideButton.setOnClickListener(new OnInputDecideButtonClickListner());
        inputCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.closeThisSection();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    // フラグメントの情報を引数のJunkで読み込む
    // viewが生成される前に情報を書き換えようとしてしまうとぬるぽが発生するので
    // Threadを用いて、viewが生成されるまで待ってから書き換えている
    @Override
    public void loadJunk(@Nullable String filename){
        super.loadJunk(filename);
        class LoadImageBoyaki implements Runnable{
            Bitmap bitmap;
            Junk junk;
            String filename;
            public LoadImageBoyaki(String filename){
                if(filename != null) {
                    this.filename = filename;
                    this.junk = JunkDataStore.getInstance().readJunkJson(mainActivity, filename);
                    mJunk = this.junk;
                    if(mJunk != null) bitmap = JunkDataStore.getInstance().readJunkBitmap(mainActivity, this.junk);
                }
            }
            @Override
            public void run() {
                if(filename!= null){
                    inputTextView.setText("編集中："+filename);
                }else{
                    inputTextView.setText("新規編集中");
                }
                if(bitmap != null) {
                    imageView.clearBitmap();
                    imageView.deleteCache();
                    imageView.loadBitmap(bitmap);
                }else{
                    imageView.clearBitmap();
                    imageView.deleteCache();
                    if(bitmapCache != null)imageView.loadBitmap(bitmapCache);
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
                mainActivity.runOnUiThread(new LoadImageBoyaki(filename));
            }
        }
        mainActivity.runOnUiThread(new LoadThread(filename));

    }

    // 決定ボタンが押されたときの処理をここに書く
    private class OnInputDecideButtonClickListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // EditTextに書かれた内容をファイルに保存する
            Bitmap bitmap = imageView.getBitmap();
            String filename = "";
            if(ImageInputFragment.this.filename != null){
                filename = ImageInputFragment.this.filename;
                if(mJunk != null){
                    // 現状なにもしない
                }else {
                    Junk junk = new Junk();
                    try {
                        mJunk = junk.addNextJunk(mJunk);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                filename = JunkDataStore.makeFilename(getActivity());
            }
            mJunk.date = Calendar.getInstance().getTime();
            JunkDataStore.getInstance().writeJunkBitmap(getActivity(), mJunk, bitmap, filename);
            JunkDataStore.getInstance().writeJunk(getActivity(), mJunk, filename);
        }
    }

    // 1手戻すボタン
    private class OnInputRedoButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            imageView.redoBitmap();
        }
    }

    // クリアするボタン
    private class OnInputClearButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            imageView.clearBitmap();
        }
    }

    @Override
    public void onDestroyView() {
        // Viewがnullになる前にviewを一時退避しとく
        bitmapCache = imageView.getBitmap().copy(Bitmap.Config.ARGB_8888, false);
        super.onDestroyView();
    }
}
