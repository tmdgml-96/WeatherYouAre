package com.example.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationActivity extends AppCompatActivity {
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView title,subTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image = findViewById(R.id.iv_startImage);
        title = findViewById(R.id.tv_logotitle);
        subTitle = findViewById(R.id.tv_subTitle);

        image.setAnimation(topAnim);
        title.setAnimation(bottomAnim);
        subTitle.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AnimationActivity.this,MainActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(image,"startImage");
                pairs[1] = new Pair<View,String>(title,"titleText");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AnimationActivity.this,pairs);
                startActivity(intent,options.toBundle());

            }
        },3000);
    }
}