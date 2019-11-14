package com.sty.ne.boat.loading;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sty.ne.boat.loading.ui.UIUtils;
import com.sty.ne.boat.loading.ui.ViewCalculateUtil;
import com.sty.ne.boat.loading.view.RippleAnimationView;

public class MainActivity extends AppCompatActivity {
    private RippleAnimationView rippleAnimationView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.getInstance(this);
//        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_ripple);

        rippleAnimationView = findViewById(R.id.ripple_view);
        imageView = findViewById(R.id.image);
        ViewCalculateUtil.setViewRelativeLayoutParam(imageView, 400,
                400, 0, 0, 0,0);


        addListener();

    }

    private void addListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rippleAnimationView.isAnimationRunning()) {
                    rippleAnimationView.stopRippleAnimator();
                }else {
                    rippleAnimationView.startRippleAnimator();
                }
            }
        });
    }

}
