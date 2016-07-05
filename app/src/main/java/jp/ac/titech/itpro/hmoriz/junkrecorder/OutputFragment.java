package jp.ac.titech.itpro.hmoriz.junkrecorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hmoriz on 2016/07/05.
 */
public class OutputFragment extends JunkRecorderFragment {

    @BindView(R.id.output_textfield)
    TextView outputView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_output,container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void updateOutputView(){
        // ファイルを読み込み、outputViewに突っ込む
        String text = "";
        try{
            InputStream inputStream = getActivity().openFileInput(JunkRecorderFragment.fileLocation);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String text2;
            while((text2 = bufferedReader.readLine()) != null){
                text = text + "\n\n"+ text2;
            }
            bufferedReader.close();
            outputView.setText(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
