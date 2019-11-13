package com.sty.ne.boat.loading.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class LoadingView extends View {
    private Path path;
    private Path dst;
    private Paint paint;
    private PathMeasure pathMeasure;
    private float length;
    private float mAnimatorValue;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        //path
        path = new Path();
        path.addCircle(500, 500, 100, Path.Direction.CW);

        pathMeasure = new PathMeasure(path, true);
        dst = new Path();
        //路径长度
        length = pathMeasure.getLength();
        //动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = animation.getAnimatedFraction();
                invalidate();
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dst.reset();
        float distance = mAnimatorValue * length;
        float start = (float) (distance - ((0.5 - Math.abs(mAnimatorValue - 0.5)) *  length));
        pathMeasure.getSegment(start, distance, dst, true);
        canvas.drawPath(dst, paint);
    }
}
