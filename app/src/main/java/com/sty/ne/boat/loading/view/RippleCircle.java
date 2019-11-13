package com.sty.ne.boat.loading.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class RippleCircle extends View {
    RippleAnimationView rippleAnimationView;

    public RippleCircle(RippleAnimationView rippleAnimationView) {
        this(rippleAnimationView.getContext(), null);
        this.rippleAnimationView = rippleAnimationView;
        //设置不可见
        this.setVisibility(INVISIBLE);
    }

    public RippleCircle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画一个水波
        int center = (Math.min(getWidth(), getHeight())) / 2;
        canvas.drawCircle(center, center, center, rippleAnimationView.paint);
    }
}
