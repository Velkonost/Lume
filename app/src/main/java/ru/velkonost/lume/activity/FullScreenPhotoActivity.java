package ru.velkonost.lume.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.JPG;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;
import static ru.velkonost.lume.Managers.ImageManager.fetchImage;

/**
 * @author Velkonost
 *
 * Класс, описывающий активность полноэкранной фотографии.
 *
 */
public class FullScreenPhotoActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_full_screen_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        Intent intent = getIntent();

        /**
         * Заголовок - полное имя владельца аватарки.
         */
        String imageTitle = (String) intent.getExtras().get(NAME);

        /**
         * Идентификатор владельца аватарки.
         */
        String profileId = String.valueOf(intent.getExtras().get(ID));

        /**
         * Папка на сервере, в которой находится открытый аватар.
         */
        String userAvatar = (String) intent.getExtras().get(AVATAR);


        ImageView imageView = (ImageView) findViewById(R.id.fullImage);

        /**
         * Составляем адрес, по которому следует загрузить картинку.
         */
        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + userAvatar
                + SLASH + profileId + JPG;

        /**
         * Загружаем картинку с интернета.
         * {@link fetchImage(java.lang.String, android.widget.ImageView, boolean, boolean)}
         */
        fetchImage(avatarURL, imageView, false, true);

        /**
         * Инициализация тулбара.
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(imageTitle);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}

