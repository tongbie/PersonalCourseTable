package com.example.bietong.personalcoursetable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.util.Random;

/**
 * Created by aaa on 2018/1/12.
 */

public class Course extends View {
    public int X = 0;//横纵坐标
    public int Y = 0;
    private int width = 0;//控件宽高
    private int height = 0;
    private TextPaint textPaint = new TextPaint();
    private Paint backPaint = new Paint();
    private String text;
    private boolean isShow = true;
    public static boolean isSignleColor = false;
    private Context context;

    public Course(Context context, int width, int height, String text) {
        super(context);
        this.width = width;
        this.height = height;
        this.text = text;
        this.context = context;
        initialise();
        ScaleAnimation scaleAnimation
                = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        scaleAnimation.setDuration(300);
        this.startAnimation(scaleAnimation);
    }

    public void setNull() {
        isShow = false;
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
        if (!isShow) {
            return;
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
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dp(11));
        backPaint.setColor(color());
        backPaint.setAntiAlias(true);
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
        if (isSignleColor) {
            return colors[1];
        } else {
            return colors[r];
        }
    }

    private float dp(float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return px * scale + 0.5f;
    }
}
