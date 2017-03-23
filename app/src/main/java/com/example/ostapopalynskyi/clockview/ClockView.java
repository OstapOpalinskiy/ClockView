package com.example.ostapopalynskyi.clockview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

/**
 * Created by ostap.opalynskyi on 06.03.2017.
 */

public class ClockView extends View {

    private Paint secondArrowPaint;
    private Paint minuteArrowPaint;
    private Paint hourArrowPaint;
    private Paint backgroundPaint;
    private float xCenter;
    private float yCenter;
    private float r;
    private float xEnd;
    private float yEnd;
    private int width;
    private int height;
    private int smallerSide;
    private float stepAngle;
    private float currentAngle;
    private int currentAnimationValue = 0;
    private Bitmap clockBackground;
    private Bitmap background;
    private Rect destinationRect;
    private float circleRound;
    private Handler handler;
    private int timeTemp = 0;
    private boolean isStoped = false;
    private double fullCircleAngle = Math.PI * 2;
    private Point secondsArrowPoint;
    private Point minutesArrowPoint;
    private Point hoursArrowPoint;
    private float secondArrowLength;
    private float minutesArrowLength;
    private float hoursArrowLength;

    private Calendar calendar;

    public ClockView(Context context) {
        super(context);
        init(context);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        int smallerSide = Math.min(width, height);
        r = smallerSide / 2;
        xCenter = width / 2;
        yCenter = height / 2;
//        xEnd = (float) (xCenter + (r * Math.cos(currentAngle)));
//        yEnd = (float) (yCenter + (r * Math.sin(currentAngle)));
        destinationRect = new Rect(0, 0, 100, 100);
        background = Bitmap.createScaledBitmap(clockBackground, width, height, false);

        secondArrowLength = r;
        minutesArrowLength = r - r / 4;
        hoursArrowLength = r - r / 3;
    }

    public boolean isStoped() {
        return isStoped;
    }

    public void setStoped(boolean stoped) {
        isStoped = stoped;
    }

    private void init(final Context context) {
        clockBackground = BitmapFactory.decodeResource(getResources(), R.drawable.clock_dial);
        stepAngle = (float) ((Math.PI * 2) / 60);
        currentAngle = (float) (-Math.PI / 2);
        circleRound = (float) (2 * Math.PI);

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);

        secondArrowPaint = getPaint(Color.GRAY, 10);
        secondArrowPaint.setTextSize(25);

        minuteArrowPaint = getPaint(Color.GRAY, 15);
        hourArrowPaint = getPaint(Color.GRAY, 20);

        calendar = Calendar.getInstance();
        handler = new Handler();
        updateTime();
        handler.postDelayed(updateTimeTask, 1000);
        long time = System.currentTimeMillis();

        Log.d("TAG1", "Hours: " + getHours(time));
        Log.d("TAG1", "Minutes: " + getMinutes(time));
        Log.d("TAG1", "Seconds: " + getSeconds(time));
    }

    private Paint getPaint(int color, float stroke) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(stroke);
        paint.setAntiAlias(true);
        return paint;
    }

    private void updateTime() {
        long currentTime = System.currentTimeMillis();
        timeTemp = (int) (System.currentTimeMillis() / 1000);
        secondsArrowPoint = getPointForArrow(60, getSeconds(currentTime), secondArrowLength);
        minutesArrowPoint = getPointForArrow(60, getMinutes(currentTime), minutesArrowLength);
        hoursArrowPoint = getPointForArrow(12, getHours(currentTime), hoursArrowLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(background, 0, 0, null);

        canvas.drawLine(xCenter, yCenter, secondsArrowPoint.x, secondsArrowPoint.y, secondArrowPaint);
        canvas.drawLine(xCenter, yCenter, minutesArrowPoint.x, minutesArrowPoint.y, minuteArrowPaint);
        canvas.drawLine(xCenter, yCenter, hoursArrowPoint.x, hoursArrowPoint.y, hourArrowPaint);

        canvas.drawCircle(xCenter, yCenter, r / 16, backgroundPaint);

//        Log.d("TAG1", "animated value:" + xEnd);
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            updateTime();
            invalidate();
            handler.postDelayed(this, 1000);
        }
    };

    private Point getPointForArrow(int numberOfSteps, float timeUnits, float arrowLength) {
        float x;
        float y;
        double arrowAngle = fullCircleAngle / numberOfSteps * timeUnits;
        x = (float) (xCenter + arrowLength * Math.cos(arrowAngle - fullCircleAngle/4));
        y = (float) (xCenter + arrowLength * Math.sin(arrowAngle - fullCircleAngle/4));
        return new Point(x, y);

    }


    private int getSeconds(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.SECOND);
    }

    private int getMinutes(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.MINUTE);
    }

    private float getHours(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.HOUR);
    }

    private class Point {
        float x;
        float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

//    public void move() {
//        ValueAnimator va = ValueAnimator.ofFloat(0f, circleRound);
//        int mDuration = 60000; //in millis
//        va.setDuration(mDuration);
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float newValue = (float) animation.getAnimatedValue();
//                Log.d("TAG1", "value:" + animation.getAnimatedValue());
//
//                currentAngle += stepAngle;
//                invalidate();
//            }
//        });
//        va.start();
//    }

}
