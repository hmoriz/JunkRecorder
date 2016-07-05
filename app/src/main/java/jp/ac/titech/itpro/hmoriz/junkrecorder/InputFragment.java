package jp.ac.titech.itpro.hmoriz.junkrecorder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hmoriz on 2016/07/05.
 */
public class InputFragment extends JunkRecorderFragment {

    @BindView(R.id.editInputText)
    EditText inputEditText;

    @BindView(R.id.inputDecideButton)
    Button inputDecideButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // xmlからViewを生成、各viewを読み込む
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        ButterKnife.bind(InputFragment.this, view);
        //inputDecideButton = ButterKnife.findById(view, R.id.inputDecideButton);
        //inputEditText = ButterKnife.findById(view, R.id.editInputText);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        inputDecideButton.setOnClickListener(new OnInputDecideButtonClickListner());
        super.onActivityCreated(savedInstanceState);
    }

    // 決定ボタンが押されたときの処理をここに書く
    private class OnInputDecideButtonClickListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // EditTextに書かれた内容をファイルに保存する
            String text = inputEditText.getText().toString();
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 E曜日 H時m分s秒", Locale.JAPANESE);
                OutputStream outputStream = getActivity().openFileOutput(JunkRecorderFragment.fileLocation, Context.MODE_APPEND|Context.MODE_PRIVATE);
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.append(sdf.format(Calendar.getInstance().getTime()) + "のぼやき: " + text+"\n");
                printWriter.close();
                inputEditText.setText("");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
