package edu.msu.hujiahui.team13;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * TODO: document your custom view class.
 */
public class GhostView extends View {
    public GhostView(Context context) {
        super(context);
    }

    public GhostView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GhostView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Context myContext;

    private void init(Context context){
        myContext = context;
    }

    private String[] captured = new String[5];

    public void setCaptured(String[] captured) {
        this.captured = captured;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int i=0;
        while(captured[i]!=null){
            Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.ic_android_black_24dp);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);;
            canvas.drawBitmap(image, 1, 1, paint);
            i++;
        }
    }
}