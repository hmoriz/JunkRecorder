package jp.ac.titech.itpro.hmoriz.junkrecorder.junk;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hmoriz on 2016/07/13.
 */
public class Junk {
    private JSONObject jsonObject;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.JAPANESE);

    public Date date;
    public String text;
    Junk next;
    String imageLocation;
    LatLng location;

    private static final String TAG_TYPE = "type";
    private static final String TAG_DATE = "date";
    private static final String TAG_TEXT = "text";
    private static final String TAG_NEXT = "next";
    private static final String TAG_IMAGELOCATION = "imagelocation";
    private static final String TAG_LOCATIONLA = "location_la";
    private static final String TAG_LOCATIONLO = "location_lo";

    // 空のオブジェクトを生成
    public Junk(){
        jsonObject = new JSONObject();
        this.date = Calendar.getInstance().getTime();
        this.text = "";
        try {
            updateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 普通にオブジェクトを生成
    public Junk(Date date, String text) throws JSONException{
        this.date = date;
        this.text = text;
        jsonObject = new JSONObject();
        updateJson();
    }

    // json文からオブジェクトを生成
    public Junk(String jsonText) throws JSONException {
        jsonObject = new JSONObject(jsonText);
        readJsonFromText(true);
    }

    // Junkをthisの後ろに加える
    public Junk addNextJunk(Junk junk) throws JSONException {
        this.next = junk;
        updateJson();
        return this;
    }

    // Junkをthisの前に加える
    public Junk addPreviousJunk(Junk junk) throws JSONException{
        junk.addNextJunk(this);
        updateJson();
        return junk;
    }

    // 次のぼやきを見つける
    public Junk getNextJunk() throws JSONException {
        if(this.next != null){
            String jsonText = jsonObject.getString(TAG_NEXT);
            next = new Junk(jsonText);
        }
        return this.next;
    }

    // イメージファイルのアドレスを登録
    public void setImageAddress(String imageLocation){
        this.imageLocation = imageLocation;
        try {
            updateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // イメージファイルのアドレスを取得
    public String getImageAddress(){
        readJsonFromText(false);
        return this.imageLocation;
    }

    // 位置情報を登録
    public void setLocation(LatLng location){
        this.location = location;
        try {
            updateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 位置情報を取得
    public LatLng getLocation(){
        readJsonFromText(false);
        return this.location;
    }

    public boolean hasNextJunk(){
        readJsonFromText(true);
        return this.next != null;
    }

    public void updateJson() throws JSONException{
        if(date != null)jsonObject.put(TAG_DATE, simpleDateFormat.format(date));
        if(text != null)jsonObject.put(TAG_TEXT, text);
        if(next != null)jsonObject.put(TAG_NEXT, next.getJsonObject().toString());
        if(imageLocation != null)jsonObject.put(TAG_IMAGELOCATION, imageLocation);
        if(location != null){
            jsonObject.put(TAG_LOCATIONLA, location.latitude);
            jsonObject.put(TAG_LOCATIONLO, location.longitude);
        }
    }

    // JsonObjectから要素を取り出す
    // 登録されていない要素が出てくることは容易に想像ができるので、ひたすら無視していく
    public void readJsonFromText(boolean readNext){
        try {
            date = simpleDateFormat.parse(jsonObject.getString(TAG_DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // ignore
            // e.printStackTrace();
        }
        try {
            text = jsonObject.getString(TAG_TEXT);
        } catch (JSONException e) {
            // ignore
            //e.printStackTrace();
        }
        try {
            imageLocation = jsonObject.getString(TAG_IMAGELOCATION);
        } catch (JSONException e) {
            //e.printStackTrace();
        }

        try {
            double latitude = jsonObject.getDouble(TAG_LOCATIONLA);
            double longitude = jsonObject.getDouble(TAG_LOCATIONLO);
            location = new LatLng(latitude, longitude);
        } catch (JSONException e) {
            //e.printStackTrace();
        }


        if(readNext) {
            try {
                String jsonObject1= jsonObject.getString(TAG_NEXT);
                next = new Junk(jsonObject1);
            } catch (JSONException e) {
                //e.printStackTrace();
            }
        }
    }

    public String parseJunk() throws JSONException {
        updateJson();
        return jsonObject.toString();
    }

    private JSONObject getJsonObject(){
        try {
            updateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.jsonObject;
    }

}
