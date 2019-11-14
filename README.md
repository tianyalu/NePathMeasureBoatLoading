## NePathMeasureBoatLoading 网易云音乐听歌识曲水波纹特效
### 一、PathMeasure Loading动画
#### 1.1概念：
##### 1.1.1 getSegment(float startD, float stopD, Path dst, boolean startWithMoveTo)
![image](https://github.com/tianyalu/NePathMeasureBoatLoading/blob/master/show/get_segment.png)
##### 1.1.2 getPosTan(float distance, float[] pos, float[] tan)
![image](https://github.com/tianyalu/NePathMeasureBoatLoading/blob/master/show/get_pos_tan.png)  

** 注意：角A为圆切线和x轴正方向夹角（圆切线方向与圆绘制方向有关）**  

#### 1.2 核心代码
```android 
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
        //起点在终点转半圈之后开始以两倍的速度追赶终点，最后在初始点追上
        float start = (float) (distance - ((0.5 - Math.abs(mAnimatorValue - 0.5)) *  length));
        pathMeasure.getSegment(start, distance, dst, true);
        canvas.drawPath(dst, paint);
    }
```

#### 1.3 演示示例
![image](https://github.com/tianyalu/NePathMeasureBoatLoading/blob/master/show/loading.gif)   
 
### 二、听歌识曲水波纹动画
#### 2.1 代码
```android
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
//        animatorSet.playSequentially(animatorList);
        animatorSet.playTogether(animatorList);
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
//                            animatorSet.end();
                            animationRunning = false;
                        }
                    }
                });
            }
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //保险起见在这里也设置状态
                    animationRunning = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }    
```

#### 2.1 演示效果
![image](https://github.com/tianyalu/NePathMeasureBoatLoading/blob/master/show/ripple.gif)   


