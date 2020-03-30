package com.example.concentration.Game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.annotation.Nullable;

import com.example.concentration.Activities.HomeActivity;
import com.example.concentration.Activities.LevelUpActivity;
import com.example.concentration.DataSave.PreferencesUtil;
import com.example.concentration.R;

public class MainGameActivity extends GameAlgorithm {

    OnClickListener buttonClicks;
    private int flipCount = 0;
    private static int levelNumber;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameplay_layout);

        numberOfCards = 16;
        gameLogic = new QuickEyeGame((numberOfCards + 1) / 2);

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        init();
        levelNumber = PreferencesUtil.getUserLevel(this);
        levelNumTextView.setText(getResources().getText(R.string.lvl) + " " + levelNumber);

        setClick(false,1); // time for becoming cards not clickable
        appearanceOfCards(); // cards start to appear one by one
        openCardsRandomly(); // cards start opening randomly
        setClick(true, literals.delayForFirstAppearance); // delay of start of the game

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(MainGameActivity.this, HomeActivity.class);
                overridePendingTransition(R.anim.activity_down_up_enter, R.anim.slow_appear);
                startActivity(intent);
            }
        });

        buttonClicks = new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if (id != v.getId()) {
                    flipCount += 1;
                    id = v.getId();
                }
                flipsCountView.setText("Flips: " + flipCount);
                pointsView.setText("Points: " + gameLogic.mistakePoints);
                gameLogic.chooseCard(getIndex(v.getId()));
                System.out.println(gameLogic.mistakePoints);
                updateViewFromModel();

                if (gameLogic.checkForAllMatchedCards()) {
                    levelNumber += 1;
                    PreferencesUtil.saveUserLevel(MainGameActivity.this, levelNumber);
                    Intent intent = new Intent(MainGameActivity.this, LevelUpActivity.class);
                    intent.putExtra("flips", flipCount);
                    intent.putExtra("points", gameLogic.mistakePoints);
                    intent.putExtra("activity", true);
                    overridePendingTransition(R.anim.activity_down_up_enter, R.anim.slow_appear);
                    startActivity(intent);
                }
            }
        };

        for (int index = 0; index < numberOfCards; index++) {
            Button btn = cards.get(index);
            if (btn.getId() - convertIdToIndex == index)
                btn.setOnClickListener(buttonClicks);
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        menuButton = findViewById(R.id.menuButton);
        restartButton = findViewById(R.id.restartButton);
        levelNumTextView = findViewById(R.id.levelTextView);
        flipsCountView = findViewById(R.id.flipsCountView);
        pointsView = findViewById(R.id.pointsView);
        flipsCountView.setText(getResources().getText(R.string.flips_0) + " 0");
        pointsView.setText(getResources().getText(R.string.points_0) + " 0");
        cards.add((Button)findViewById(R.id.button_00));
        cards.add((Button)findViewById(R.id.button_01));
        cards.add((Button)findViewById(R.id.button_02));
        cards.add((Button)findViewById(R.id.button_03));
        cards.add((Button)findViewById(R.id.button_04));
        cards.add((Button)findViewById(R.id.button_05));
        cards.add((Button)findViewById(R.id.button_06));
        cards.add((Button)findViewById(R.id.button_07));
        cards.add((Button)findViewById(R.id.button_08));
        cards.add((Button)findViewById(R.id.button_09));
        cards.add((Button)findViewById(R.id.button_10));
        cards.add((Button)findViewById(R.id.button_11));
        cards.add((Button)findViewById(R.id.button_12));
        cards.add((Button)findViewById(R.id.button_13));
        cards.add((Button)findViewById(R.id.button_14));
        cards.add((Button)findViewById(R.id.button_15));
        cards.add((Button)findViewById(R.id.button_16));
        cards.add((Button)findViewById(R.id.button_17));
        cards.add((Button)findViewById(R.id.button_18));
        cards.add((Button)findViewById(R.id.button_19));
        restartButton.setVisibility(View.INVISIBLE);
    }
}