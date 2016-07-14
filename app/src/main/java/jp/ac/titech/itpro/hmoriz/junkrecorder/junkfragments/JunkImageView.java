package jp.ac.titech.itpro.hmoriz.junkrecorder.junkfragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by hmoriz on 2016/07/08.
 */
public class JunkImageView extends View {

    private Bitmap bitmap;
    private Paint paint;
    private Canvas canvas;
    private ArrayList<Bitmap> records;
    private Path path;

    public JunkImageView(Context context) {
        super(context);
        canvas = new Canvas();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        records = new ArrayList<>();
    }

    public JunkImageView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        canvas = new Canvas();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        records = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        if(records.size()>=1){
            // 画面のサイズが変わったときは最後の画面を復元する
            clearBitmap();
            loadBitmap(records.get(records.size()-1));
        }else{
            records.add(bitmap.copy(Bitmap.Config.ARGB_8888, false));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                path.moveTo(event.getX(), event.getY());
                canvas.drawPoint(event.getX(), event.getY(), paint);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                canvas.drawPath(path, paint);
                break;
            case MotionEvent.ACTION_UP:
                Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                records.add(bitmap2);
        }
        invalidate();
        return true;
    }

    public void clearBitmap(){
        if(canvas != null) canvas.drawColor(Color.WHITE);
        invalidate();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }


    public void loadBitmap(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float wratio = ((float)this.canvas.getWidth()) / w;
        float hratio = ((float)this.canvas.getHeight()) / h;
        Matrix matrix = new Matrix();
        if(wratio < hratio){
            matrix.postScale(wratio, wratio);
        }else{
            matrix.postScale(hratio, hratio);
        }
        canvas.drawBitmap(bitmap, matrix, paint);
        invalidate();
        if(records.size() == 0){
            records.add(bitmap.copy(Bitmap.Config.ARGB_8888, false));
        }
    }

    public void redoBitmap(){
        if(records.size() >=2){
            clearBitmap();
            loadBitmap(records.get(records.size()-2));
            records.remove(records.size()-1);
        }else if(records.size() == 1){
            clearBitmap();
            loadBitmap(records.get(0));
        }
    }

    public void deleteCache(){
        records.clear();
    }

}
