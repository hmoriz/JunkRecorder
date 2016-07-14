package jp.ac.titech.itpro.hmoriz.junkrecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jp.ac.titech.itpro.hmoriz.junkrecorder.junk.Junk;

/**
 * Created by hmoriz on 2016/07/06.
 */

// ルール ここのクラスに送られる引数filenameはパスは含めず、かつ拡張子もいれない！
// 他のクラスに送る段階で拡張子や絶対パスを加える

public class JunkDataStore {

    // Singleton class
    private final static JunkDataStore __instance = new JunkDataStore();
    private static long filename_id = 10000;

    private JunkDataStore(){

    }

    public static JunkDataStore getInstance(){
        return __instance;
    }

    // きわめて適当なファイル名を生成
    public static String makeFilename(Context context){
        ++filename_id;
        context.getSharedPreferences("data_id", Context.MODE_PRIVATE).edit().putLong("id", filename_id).commit();
        return "test" + filename_id;
    }

    // filename_idをidで初期化
    public static void initFilename_id(Context context, long id){
        filename_id = id;
        context.getSharedPreferences("data_id", Context.MODE_PRIVATE).edit().putLong("id", filename_id).commit();
    }

    // Junkを読み込む
    public Junk readJunkJson(Context context, String filename){
        String text = "";
        String text1 = "";
        try{
            InputStream inputStream = context.openFileInput(filename+".txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while((text1 = bufferedReader.readLine()) != null){
                text = text + text1;
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Junk junk = null;
        try{
            junk = new Junk(text);
            Log.d("JunkStore", "Load Complete! Junk from " + filename + ": "+junk.parseJunk());
            Toast.makeText(context, filename + "のつぶやきを読み込みました", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return junk;
    }

    public HashMap<String, Junk> readAllJunks(Context context){
        List<File> files = getAllTextFiles(context);
        if(files == null || files.isEmpty())return null;
        HashMap<String, Junk> junks = new HashMap<>();
        for(File file : files){
            String text = "";
            String text1 = "";
            try{
                InputStream inputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while((text1 = bufferedReader.readLine()) != null){
                    text = text + text1;
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                Junk junk = new Junk(text);
                String[] filenames = file.toString().split("/");
                String filename = filenames[filenames.length-1].split("\\.")[0];
                Log.d("JunkStore", "Load Complete! Junk from "+ filename + ": "+junk.parseJunk());
                junks.put(filename, junk);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return junks;
    }

    // Junk内の画像を読み込む
    public Bitmap readJunkBitmap(Context context, Junk junk){
        String filelocation = junk.getImageAddress();
        if(filelocation == null)return null;
        try {
            FileInputStream inputStream = new FileInputStream(filelocation);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            String filenames[] = filelocation.split("/");
            String filename = filenames[filenames.length-1];
            Toast.makeText(context, filename + "の画像を読み込みました", Toast.LENGTH_LONG).show();
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Junkを書き込む
    public boolean writeJunk(Context context, Junk junk, String filename){
        // 画像の保存したパスを添えて、imageBoyakiをtxtに保存
        try{
            OutputStream outputStream = context.openFileOutput(filename+".txt", Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.append(junk.parseJunk());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        Log.d("JunkDataStore", "File was saved to " + filename + ".txt");
        Toast.makeText(context, filename + "につぶやきを保存しました", Toast.LENGTH_LONG).show();
        return true;
    }

    // junkの画像を書き込む
    public boolean writeJunkBitmap(Context context, Junk junk, Bitmap bitmap, String filename){
        String location = context.getFilesDir().toString()+"/"+filename;
        File file = new File(location + ".png");
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        junk.setImageAddress(location+".png");
        Toast.makeText(context, filename + ".pngに画像を保存しました", Toast.LENGTH_LONG).show();
        return true;
    }

    public List<File> getAllFiles(Context context){
        File dir = context.getFilesDir();
        List<File> files = new ArrayList<>();
        for(File file : dir.listFiles()){
            if(file.isDirectory()){
                continue;
            }
            files.add(file);
        }
        return files;
    }

    public List<File> getAllTextFiles(Context context){
        File dir = context.getFilesDir();
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.getName().endsWith(".txt");
            }
        })));
        return files;
    }

    public List<File> getAllImageFiles(Context context){
        File dir = context.getFilesDir();
        List<File> files = new ArrayList<>();
        for(File file : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".bmp"))return true;
                return false;
            }
        })){
            files.add(file);
        }
        return files;
    }

    public void deleteAllFiles(Context context){
        File dir = context.getFilesDir();
        for(File file : dir.listFiles()){
            if(!file.isDirectory()) file.delete();
        }
        initFilename_id(context, 0);
    }
}
