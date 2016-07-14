package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.ac.titech.itpro.hmoriz.junkrecorder.R;

/**
 * Created by hmoriz on 2016/07/05.
 */
public class OutputFragment extends JunkRecorderFragment {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E曜日 H時m分s秒", Locale.JAPANESE);

    @BindView(R.id.edit_outputbutton)
    Button editButton;

    @BindView(R.id.output_textfield)
    TextView outputView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_output,container, false);
        ButterKnife.bind(this, view);
        editButton.setOnClickListener(new OnEditButtonClickListener());
        if(filename != null) loadJunk(filename);
        return view;
    }

    // フラグメントの情報を引数のboyakiで読み込む
    @Override
    public void loadJunk(final String filename){
        super.loadJunk(filename);
        class LoadTextBoyaki implements Runnable{
            String filename;
            public LoadTextBoyaki(String filename){
                this.filename = filename;
            }
            @Override
            public void run() {
                String text = this.filename + "の内容：\n";
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


    private class OnEditButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mainActivity.moveToInputFragment(filename);
        }
    }
}
