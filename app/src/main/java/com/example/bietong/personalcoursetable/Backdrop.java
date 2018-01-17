package com.example.bietong.personalcoursetable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by aaa on 2018/1/13.
 */

public class Backdrop extends Drawable {

    private int color = Color.BLUE;
    private int radian = 15;
    private int width = 0;
    private int height = 0;
    private Paint paint = new Paint();
    private int currentWidth = 0;
    private int currentHeight = 0;
    private Canvas canvas;

    public Backdrop() {
        radian = 10;
        initialise();
    }

    public Backdrop(int width, int height, int color) {
        this.width = width;
        this.height = height;
        this.color = color;
        initialise();
    }

    public Backdrop(int width, int height, int radian, int color) {
        this.width = width;
        this.height = height;
        this.radian = radian;
        this.color = color;
        initialise();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
//        this.canvas = canvas;
//        if (currentWidth < width) {
//            Log.e("height", String.valueOf(currentHeight));
//            canvas.drawRoundRect(0, 0, currentWidth, currentHeight, 15, 15, paint);
//            currentWidth += 20;
//            currentHeight += 20;
//        } else {
            canvas.drawRoundRect(0, 0, width, height, 15, 15, paint);
//        }
//        onDraw();
    }

//    private void onDraw() {
//        canvas.drawText("",0,0,paint);
//    }


    /* 初始化 */
    private void initialise() {
        {//当前宽高
            currentWidth = (int) width / 4;
            currentHeight = (int) height / 4;
        }
        {//初始化画笔
            paint.setColor(color);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
        }
    }


    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
