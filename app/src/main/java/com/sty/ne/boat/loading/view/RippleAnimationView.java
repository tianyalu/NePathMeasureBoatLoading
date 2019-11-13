package com.sty.ne.boat.loading.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.sty.ne.boat.loading.R;
import com.sty.ne.boat.loading.ui.UIUtils;

import java.util.ArrayList;

public class RippleAnimationView extends RelativeLayout {
    private static final int RIPPLE_COUNT = 10;
    public Paint paint;

    private int rippleColor;
    private int radius;
    private int strokeWidth;
    private ArrayList<RippleCircle> viewList = new ArrayList<>();
    private AnimatorSet animatorSet;
    private boolean animationRunning = false;

    public RippleAnimationView(Context context) {
        this(context, null);
    }

    public RippleAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //属性初始化
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleAnimationView);
        int ripple_anim_type = typedArray.getInt(R.styleable.RippleAnimationView_ripple_anim_type, 0);
        if(ripple_anim_type == 0) {
            paint.setStyle(Paint.Style.FILL);
        }else {
            paint.setStyle(Paint.Style.STROKE);
        }
        radius = typedArray.getInteger(R.styleable.RippleAnimationView_radius, 54);
        strokeWidth = typedArray.getInteger(R.styleable.RippleAnimationView_strokeWidth, 54);
        rippleColor = typedArray.getColor(R.styleable.RippleAnimationView_ripple_anim_color,
                ContextCompat.getColor(context, R.color.rippleColor));
        paint.setStrokeWidth(UIUtils.getInstance().getWidth(strokeWidth));
        paint.setColor(rippleColor);
        typedArray.recycle();

        //所有的控件按照中心位置排好
        LayoutParams layoutParams = new LayoutParams(UIUtils.getInstance().getWidth(radius),
                UIUtils.getInstance().getWidth(radius));
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        float maxScale = 10;
        //延迟时间
        int rippleDuration = 3500;

        int singleDelay = rippleDuration / RIPPLE_COUNT; //间隔时间，上一个波纹和下一个波纹间隔时间
        ArrayList<Animator> animatorList = new ArrayList<>();
        for (int i = 0; i < RIPPLE_COUNT; i++) {
            //添加水波
            RippleCircle rippleCircle = new RippleCircle(this);
            addView(rippleCircle, layoutParams);
            viewList.add(rippleCircle);
            //添加动画
            //            //X
//            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleCirCle, "scaleX", 1, maxScale);
//            scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            scaleXAnimator.setRepeatMode(ValueAnimator.RESTART);
//            scaleXAnimator.setStartDelay(i * singleDelay);
//            scaleXAnimator.setDuration(rippleDuration);
//            animatorList.add(scaleXAnimator);
//            //Y
//            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleCirCle, "scaleY", 1, maxScale);
//            scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            scaleYAnimator.setRepeatMode(ValueAnimator.RESTART);
//            scaleYAnimator.setStartDelay(i * singleDelay);
//            scaleYAnimator.setDuration(rippleDuration);
//            animatorList.add(scaleYAnimator);
//            //alpha
//            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleCirCle, "scaleY", 1, maxScale);
//            scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            scaleYAnimator.setRepeatMode(ValueAnimator.RESTART);
//            scaleYAnimator.setStartDelay(i * singleDelay);
//            scaleYAnimator.setDuration(rippleDuration);
//            animatorList.add(scaleYAnimator);
            PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("scaleX", maxScale, 1);
            PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleY", maxScale, 1);
            PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("alpha", 0, 1);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(rippleCircle, holder1, holder2, holder3);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setStartDelay(i * singleDelay);
            animator.setDuration(rippleDuration);
            animatorList.add(animator);
        }
        animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playSequentially(animatorList);
    }

    //启动动画
    public void startRippleAnimator() {
        if(!animationRunning) {
            ArrayList<Animator> childAnimations = animatorSet.getChildAnimations();
            for (Animator childAnimation : childAnimations) {
                ((ObjectAnimator) childAnimation).setRepeatCount(ValueAnimator.INFINITE);
            }
            for (RippleCircle rippleCircle : viewList) {
                rippleCircle.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning = true;
        }
    }
    //停止动画
    public void stopRippleAnimator() {
        if(animationRunning) {
            final ArrayList<Animator> childAnimations = animatorSet.getChildAnimations();
            final int[] count = new int[1];
            count[0] = 0;
            for (Animator childAnimation : childAnimations) {
                ((ObjectAnimator) childAnimation).setRepeatCount(0);
                childAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        count[0]++;
                        if(count[0] == childAnimations.size()){
                            animatorSet.end();
                            animationRunning = false;
                        }
                    }
                });
            }
        }
    }

    public boolean isAnimationRunning() {
        return animationRunning;
    }
}
