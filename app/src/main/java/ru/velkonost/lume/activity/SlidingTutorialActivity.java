package ru.velkonost.lume.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cleveroad.slidingtutorial.Direction;
import com.cleveroad.slidingtutorial.IndicatorOptions;
import com.cleveroad.slidingtutorial.PageOptions;
import com.cleveroad.slidingtutorial.TransformItem;
import com.cleveroad.slidingtutorial.TutorialFragment;
import com.cleveroad.slidingtutorial.TutorialOptions;
import com.cleveroad.slidingtutorial.TutorialPageOptionsProvider;

import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.REGISTRATION;

/**
 * @author Velkonost
 * Приветственная активность, дающая краткое понятие о смысле приложения
 */
public class SlidingTutorialActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Свойство - суммарное количество страниц
     */
    private static final int TOTAL_PAGES = 3;

    /**
     * Свойство - количество уникальных страниц
     */
    private static final int ACTUAL_PAGES_COUNT = 3;

    /**
     * Свойство - палитра цветов страниц
     */
    private int[] mPagesColors;


    /**
     * Старт активности
     * @param context
     */
    public static void start(Context context) {
        context.startActivity(new Intent(context, SlidingTutorialActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tutorial);

        mPagesColors = new int[]{
                ContextCompat.getColor(this, R.color.tutorial_page_first),
                ContextCompat.getColor(this, R.color.tutorial_page_second),
                ContextCompat.getColor(this, R.color.tutorial_page_third),
                ContextCompat.getColor(this, android.R.color.holo_blue_dark),
                ContextCompat.getColor(this, android.R.color.holo_purple),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
        };
        if (savedInstanceState == null) {
            replaceTutorialFragment();
        }
    }

    /**
     * Инициализация слайдера
     */
    public void replaceTutorialFragment() {
        final IndicatorOptions indicatorOptions = IndicatorOptions.newBuilder(this)
                .build();
        final TutorialOptions tutorialOptions = TutorialFragment.newTutorialOptionsBuilder(this)
                .setUseAutoRemoveTutorialFragment(true)
                .setUseInfiniteScroll(true)
                .setPagesColors(mPagesColors)
                .setPagesCount(TOTAL_PAGES)
                .setIndicatorOptions(indicatorOptions)
                .setTutorialPageProvider(new TutorialPagesProvider())
                .setOnSkipClickListener(new OnSkipClickListener(this))
                .build();
        final TutorialFragment tutorialFragment = TutorialFragment.newInstance(tutorialOptions);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, tutorialFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {}

    /**
     * Слушатель слайдера
     */
    private static final class TutorialPagesProvider implements TutorialPageOptionsProvider {

        @NonNull
        @Override
        public PageOptions provide(int position) {
            @LayoutRes int pageLayoutResId;
            TransformItem[] tutorialItems;
            position %= ACTUAL_PAGES_COUNT;
            switch (position) {
                case 0: {
                    pageLayoutResId = R.layout.fragment_tutorial_page_first;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.ivFirstImage, Direction.LEFT_TO_RIGHT, 0.20f),
                            TransformItem.create(R.id.ivSecondImage, Direction.RIGHT_TO_LEFT, 0.06f),
                            TransformItem.create(R.id.ivThirdImage, Direction.LEFT_TO_RIGHT, 0.08f),
                            TransformItem.create(R.id.ivFourthImage, Direction.RIGHT_TO_LEFT, 0.1f),
                            TransformItem.create(R.id.ivFifthImage, Direction.RIGHT_TO_LEFT, 0.03f),
                            TransformItem.create(R.id.ivSixthImage, Direction.RIGHT_TO_LEFT, 0.09f),
                            TransformItem.create(R.id.ivSeventhImage, Direction.RIGHT_TO_LEFT, 0.14f),
                            TransformItem.create(R.id.ivEighthImage, Direction.RIGHT_TO_LEFT, 0.07f)
                    };
                    break;
                }
                case 1: {
                    pageLayoutResId = R.layout.fragment_tutorial_page_third;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.ivFirstImage, Direction.RIGHT_TO_LEFT, 0.20f),
                            TransformItem.create(R.id.ivSecondImage, Direction.LEFT_TO_RIGHT, 0.06f),
                            TransformItem.create(R.id.ivThirdImage, Direction.RIGHT_TO_LEFT, 0.08f),
                            TransformItem.create(R.id.ivFourthImage, Direction.LEFT_TO_RIGHT, 0.1f),
                            TransformItem.create(R.id.ivFifthImage, Direction.LEFT_TO_RIGHT, 0.03f),
                            TransformItem.create(R.id.ivSixthImage, Direction.LEFT_TO_RIGHT, 0.09f),
                            TransformItem.create(R.id.ivSeventhImage, Direction.LEFT_TO_RIGHT, 0.14f)
                    };
                    break;
                }
                case 2: {
                    pageLayoutResId = R.layout.fragment_tutorial_page_second;
                    tutorialItems = new TransformItem[]{
                            TransformItem.create(R.id.ivFirstImage, Direction.RIGHT_TO_LEFT, 0.2f),
                            TransformItem.create(R.id.ivSecondImage, Direction.LEFT_TO_RIGHT, 0.06f),
                            TransformItem.create(R.id.ivThirdImage, Direction.RIGHT_TO_LEFT, 0.08f),
                            TransformItem.create(R.id.ivFourthImage, Direction.LEFT_TO_RIGHT, 0.1f),
                            TransformItem.create(R.id.ivFifthImage, Direction.LEFT_TO_RIGHT, 0.03f),
                            TransformItem.create(R.id.ivSixthImage, Direction.LEFT_TO_RIGHT, 0.09f),
                            TransformItem.create(R.id.ivSeventhImage, Direction.LEFT_TO_RIGHT, 0.14f),
                            TransformItem.create(R.id.ivEighthImage, Direction.LEFT_TO_RIGHT, 0.07f)
                    };
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown position: " + position);
                }
            }

            return PageOptions.create(pageLayoutResId, position, tutorialItems);
        }
    }


    /**
     * Слушатель кнопки "Пропустить"
     */
    private static final class OnSkipClickListener implements View.OnClickListener {

        @NonNull
        private final Context mContext;

        OnSkipClickListener(@NonNull Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        public void onClick(View v) {
            Intent profileIntent = new Intent(mContext, ProfileActivity.class);
            profileIntent.putExtra(REGISTRATION, 1);
            profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(profileIntent);
        }
    }

}
