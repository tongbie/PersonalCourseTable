package com.example.bietong.personalcoursetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import java.util.Random;

/**
 * Created by aaa on 2018/1/12.
 */

public class Course extends View {
    public int X = 0;//横纵坐标
    public int Y = 0;
    private int width = 0;//控件宽高
    private int height = 0;
    private int currentWidth;//当前宽高
    private int currentHeight;
    private TextPaint textPaint = new TextPaint();
    private Paint backPaint = new Paint();
    private String text;
    private boolean isShow = true;
    private float canvasScaleUp = 0.2f;//放大动画缩放倍数
    public static boolean isSignleColor=false;
    private Context context;
    private float canvasScaleDown=0.99f;
    private boolean isFirstHide=false;

    public Course(Context context, int width, int height, String text) {
        super(context);
        this.width = width;
        this.height = height;
        this.text = text;
        this.context=context;
        initialise();
    }

    public void setHeight(int height) {
        this.height = height;
        invalidate();
    }

    public void setNull() {
        isShow = false;
        invalidate();
    }

    public void setFirstHide(boolean isFirstHide){
        this.isFirstHide=isFirstHide;
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
        if(isFirstHide){
            if(canvasScaleDown>0f){
                canvas.scale(canvasScaleDown,canvasScaleDown);
                canvasScaleDown-=(1f-canvasScaleDown+0.2)/8;
                postInvalidateDelayed(1);
                return;
            }else {
                isFirstHide=false;
                setNull();
            }
        }
        if (!isShow) {
            return;
        }
        if (canvasScaleUp <1f) {
            canvas.translate((1f-canvasScaleUp)*width/2,(1f-canvasScaleUp)*height/2);
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
        canvas.save();
        canvas.translate((width-(_dp(11))*4)/2,0);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void initialise() {
        {//画笔设置
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(_dp(11));

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
        if(isSignleColor){
            return colors[1];
        }else {
            return colors[r];
        }
    }

    private float _dp(float px){
        float scale = context.getResources().getDisplayMetrics().density;
        return px*scale+0.5f;
    }
}
