package com.example.proiectmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Sampler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class SplashActivity extends AppCompatActivity {
    //Initialize variable
    ImageView ivTop, ivHeart, ivBeat, ivBottom;
    TextView textView;
    CharSequence charSequence;
    int index;
    long delay = 200;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Assign variable

        ivTop= findViewById(R.id.iv_top);
        ivHeart= findViewById(R.id.iv_heart);
        ivBeat= findViewById(R.id.iv_beat);
        ivBottom= findViewById(R.id.iv_bottom);
        textView=findViewById(R.id.text_view);

        //set full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
        ,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initialize top animation

        Animation animation1 = AnimationUtils.loadAnimation(this
    ,R.anim.top_wave);

        //start top animation
        ivTop.setAnimation(animation1);

        //initialize object animatir

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                ivHeart,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        );

        //set duration
        objectAnimator.setDuration(500);
        //set repeat count
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //set repeat mode
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        //start animator
        objectAnimator.start();

        //set animate text
        animatText("4EVER65");
        //load gif
        Glide.with(this).load("icon-1.1s-47px.gif")
        .asGif()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(ivBeat);

        //initialize bottom animation
        Animation animation2 = AnimationUtils.loadAnimation(this
                ,R.anim.bottom_wave);

        //start bottom animation
        ivBottom.setAnimation(animation2);

        //initialize handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //redirect to main activity
                startActivity(new Intent(SplashActivity.this
                ,LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //Finish activity
                finish();
            }
        },4000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //when runnable is run
            //set text
            textView.setText(charSequence.subSequence(0,index++));
            //check condition
            if(index <= charSequence.length()){
                //when index is equal to text length
                //run handler
                handler.postDelayed(runnable, delay);
            }

        }
    };

    //create animatex text method

    public void animatText(CharSequence cs){
        //set text
        charSequence = cs;
        //clear index
        index=0;
        //clear text
        textView.setText("");
        //remove call back
        handler.removeCallbacks(runnable);
        //run handler
        handler.postDelayed(runnable,delay);
    }
}
