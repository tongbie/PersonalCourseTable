package com.example.bietong.personalcoursetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.Random;

/**
 * Created by aaa on 2018/1/12.
 */

public class Course extends android.support.v7.widget.AppCompatTextView {
    public int X = 0;//横纵坐标
    public int Y = 0;
    private int width = 0;//控件宽高
    private int height = 0;
    private int currentWidth;//当前宽高
    private int currentHeight;
    private TextPaint textPaint = new TextPaint();
    private Paint backPaint = new Paint();
    private float scale;//用以dp化px
    private String text;
    private boolean isShow = true;
    private float canvasScaleUp = 0.3f;//放大动画缩放倍数
//    private float canvasScaleDown=1f;
//    private boolean isFirstHide=false;

    public Course(Context context, int width, int height, String text) {
        super(context);
        this.width = width;
        this.height = height;
        this.text = text;
        scale = context.getResources().getDisplayMetrics().density;
        initialise();
    }

    public void setHeight(int height) {
        this.height = height;
        invalidate();
    }

    public void setNull() {
        isShow = false;
//        isFirstHide=true;
        invalidate();
    }

    public void setPosition(int X, int Y) {
        this.X = X;
        this.Y = Y;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*if(isFirstHide){
            if(canvasScaleDown>0f){
                canvas.scale(canvasScaleDown,canvasScaleDown);
                canvasScaleDown-=canvasScaleDown/8;
                postInvalidateDelayed(1);
                return;
            }else {
                isFirstHide=false;
            }
        }*/
        if (!isShow) {
            return;
        }
        if (canvasScaleUp <1f) {
            canvas.scale(canvasScaleUp, canvasScaleUp);
            canvasScaleUp += canvasScaleUp /8;
            postInvalidateDelayed(1);
        } else {
            canvas.scale(1f, 1f);
        }
        canvas.drawRoundRect(0, 0, width, height, 15, 15, backPaint);
        StaticLayout staticLayout = new StaticLayout(
                text,
                textPaint,
                getWidth(),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                true);
        staticLayout.draw(canvas);
    }

    private void initialise() {
        {//画笔设置
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(11 * scale + 0.5f);

            backPaint.setColor(color());
            backPaint.setAntiAlias(true);
        }
        currentWidth = (int) width / 4;
        currentHeight = (int) height / 4;
    }

    private int color() {
        int[] colors = new int[]{
                Color.parseColor("#98d262"),//浅绿
                Color.parseColor("#177cb0"),//蓝
                Color.parseColor("#38b48b"),//绿
                Color.parseColor("#64c6b9"),
                Color.parseColor("#ee827c"),//粉红
                Color.parseColor("#db8449"),
                Color.parseColor("#867ba9"),
        };
        Random random = new Random();
        int r = random.nextInt(colors.length - 1);
        return colors[r];
    }
}
