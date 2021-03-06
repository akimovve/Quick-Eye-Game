package com.example.concentration.game;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.concentration.info.Literals;
import com.example.concentration.data.SharedPreferencesUtil;
import com.example.concentration.models.Theme;
import com.example.concentration.R;
import com.example.concentration.models.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameAlgorithm extends AppCompatActivity {

    protected int numberOfCards;
    private ArrayList<Integer> colorsOfCards = new ArrayList<>();
    protected ArrayList<Button> cards = new ArrayList<>();
    protected Literals literals = new Literals();
    protected QuickEyeGame gameLogic;
    protected Button menuButton, restartButton;
    protected TextView levelNumTextView, flipsCountView, pointsView;
    protected final int convertIdToIndex = R.id.button_00;
    protected int speed = 0, id = -1;

    public GameAlgorithm(){}

    private void setComplexity() {
        int numColors = 1;
        int difficultyLevel = SharedPreferencesUtil.getComplexity(this);
        int[] buttonColors = getResources().getIntArray(R.array.buttoncolors);
        ArrayList<Integer> arrayOfColors = new ArrayList<>();
        for (int a : buttonColors) arrayOfColors.add(a);
        Collections.shuffle(arrayOfColors);
        switch (difficultyLevel) {
            case 1:
                numColors = 2;
                break;
            case 2:
                numColors = 3;
                break;
        }
        for (int i = 0; i < numberOfCards; i++) {
            int randomColor = new Random().nextInt(numColors);
            colorsOfCards.add(i, arrayOfColors.get(randomColor));
        }
    }

    private int getColorOfButtons(int index) {
        return colorsOfCards.get(index);
    }

    private void circleCards() {
        for (int index = 0; index < numberOfCards-1; index++) {
            final int k = index + 1;
            final Button button = cards.get(index);
            button.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.getBackground().setColorFilter(getColorOfButtons(k), PorterDuff.Mode.MULTIPLY);
                    button.setVisibility(View.VISIBLE);
                }
            }, literals.delayForFirstAppearance - 200);
        }
    }

    public void setClick(final Boolean fl, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int index = 0; index < numberOfCards; index++)
                    cards.get(index).setClickable(fl);
            }
        }, delay);
    }

    public void appearanceOfCards() {
        setComplexity();
        for (int index = 0; index < numberOfCards; index++) {
            final Button button = cards.get(index);
            button.getBackground().setColorFilter(getColorOfButtons(index),PorterDuff.Mode.MULTIPLY);
            button.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setVisibility(View.VISIBLE);
                }
            }, literals.delayForFirstAppearance - 200);
            literals.delayForFirstAppearance += literals.delayBetweenAppearance;
        }
        if (SharedPreferencesUtil.getComplexity(this) == 2) circleCards();
    }

    public void openCardsRandomly() {
        Map<Integer,Boolean> checkTheRepeat = new HashMap<>();
        for (int index = 0; index < numberOfCards; index++) {
            checkTheRepeat.put(index, false); // all buttons haven't opened yet => false
        }

        ArrayList<Integer> randArrOfFirstIndexes = new ArrayList<>(); // array of random sequence of cards' indexes
        for (int index = 0; index < numberOfCards; index++)
            randArrOfFirstIndexes.add(index);

        Collections.shuffle(randArrOfFirstIndexes);

        if (numberOfCards == 4) {
            int deleteRandom = (int)(Math.random() * 4);
            randArrOfFirstIndexes.remove(deleteRandom);
        } else {
            int[] secondRandArray = new int[(int) (Math.random() * (numberOfCards / 3) + (numberOfCards / 3))]; // random size [(numberOfCards / 4);(numberOfCards/2)]
            for (int index = 0; index < secondRandArray.length; index++) {
                int randomIndexOfFirstArray;
                do {
                    randomIndexOfFirstArray = (int) (Math.random() * numberOfCards);
                } while (checkTheRepeat.get(randomIndexOfFirstArray));
                secondRandArray[index] = randArrOfFirstIndexes.get(randomIndexOfFirstArray);
                checkTheRepeat.put(randomIndexOfFirstArray, true);
            }

            for (int value : secondRandArray)
                randArrOfFirstIndexes.add(value);

            Collections.shuffle(randArrOfFirstIndexes);

            int index = 0;
            do {
                if (randArrOfFirstIndexes.get(index).equals(randArrOfFirstIndexes.get(index + 1))) {
                    int random;
                    do {
                        random = (int) (Math.random() * randArrOfFirstIndexes.size());
                    } while (random == index || random == index + 1);
                    Integer temp = randArrOfFirstIndexes.get(index + 1);
                    randArrOfFirstIndexes.add(index + 1, randArrOfFirstIndexes.get(random));
                    randArrOfFirstIndexes.add(random, temp);
                }
                index++;
            } while (index < randArrOfFirstIndexes.size() - 1);
        }
        outPutRandomly(randArrOfFirstIndexes);
    }

    public void outPutRandomly(ArrayList<Integer> array) {
        for (int index = 0; index < array.size(); index++) {
            final int randomButtonIndex = array.get(index);
            final Button finalBut = pressedButton(randomButtonIndex);
            finalBut.setTextSize(43);
            finalBut.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finalBut.setText(getEmoji(gameLogic.cards.get(randomButtonIndex))); // opened
                    finalBut.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                }
            }, literals.delayForFirstAppearance + speed); // default. DO NOT TOUCH!
            literals.delayForFirstAppearance += literals.timeCardIsClose; // time the card is being opened
            finalBut.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finalBut.setText(""); // closed
                    finalBut.getBackground().setColorFilter(getColorOfButtons(getIndex(finalBut.getId())),PorterDuff.Mode.MULTIPLY);
                }
            }, literals.delayForFirstAppearance); // default. DO NOT TOUCH!
            literals.delayForFirstAppearance += literals.timeCardIsOpen; // time between closed and next opened card
        }
        if (SharedPreferencesUtil.getComplexity(this) == 2) circleCards();
    }

    public Button pressedButton(int index) {
        return cards.get(index);
    }

    public int getIndex(int index) {
        return index-convertIdToIndex;
    }

    public void updateViewFromModel() {
        for (int index = 0; index < numberOfCards; index++) {
            Card card = gameLogic.cards.get(index);
            functionForPressedButton(cards.get(index), card);
        }
    }

    public void functionForPressedButton(Button button, Card card) {
        button.setTextSize(43);
        if (card.isFaceUp) {
            button.setText(getEmoji(card));
            button.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        } else {
            button.setText("");
            if (!card.isMatched) {
                button.getBackground().setColorFilter(getColorOfButtons(getIndex(button.getId())),PorterDuff.Mode.MULTIPLY);
            } else {
                button.getBackground().setColorFilter(getResources().getColor(R.color.no_color), PorterDuff.Mode.MULTIPLY);
                button.setEnabled(false);
            }
        }
    }

    private Map<Integer, String> emoji = new HashMap<>();
    private String[] animals = {"🐶", "🐱", "🐼", "🦊", "🦁", "🐯", "🐨", "🐮", "🐷", "🐵"};
    private String[] cars = {"🚔", "🏎", "🚕", "🚚", "🚜", "🚛", "🚑", "🚎", "🚙", "🚒"};
    private String[] food = {"🍇", "🍌", "🍔", "🎂", "🌽", "🍉", "🍎", "🥕", "🌶", "🍕"};
    private String[] random = {"🏰","🐨","🐝","🦂","🦖","⛄️","🛸","💻","🏁","💂","💍","🐒",
            "🐊","🎄","🏍","👾","🦁","🐿","🔥","🌘","🍕","⚽️","🥁","🧀","🛩","📸","🎁","🍏",
            "🐩","🐓","🍁", "🌈","🦈","🛏","📚","🗿","🎭","🍿","🥥","🍆","🦔","🎮️","🌶","🐘",
            "🚔","🎡","🏔","🚄","🎬","🐙","🍄","🌵","🐢","👑","🧞","👻","🕶","🎓","🎪","🐶",
            "🐲","🍓","🏆","🎰" };

    private Theme[] themes = {
            new Theme(animals),
            new Theme(cars),
            new Theme(food),
            new Theme(random)
    };

    private int cutEmoji = 0;

    public String getEmoji(Card card) {
        String[] emojiTypes = themes[SharedPreferencesUtil.getTheme(this)].emojis;
        if (emoji.get(card.identifier) == null && emojiTypes.length > 0) {
            int randomIndex = (int)(Math.random()*(emojiTypes.length - cutEmoji));
            emoji.put(card.identifier, emojiTypes[randomIndex]);
            if (emojiTypes.length - 1 - randomIndex >= 0)
                System.arraycopy(emojiTypes, randomIndex + 1, emojiTypes, randomIndex, emojiTypes.length - 1 - randomIndex);
            cutEmoji++;
        }

        if (emoji.get(card.identifier) != null)
            return emoji.get(card.identifier);
        else return "?";
    }
}
