package ru.velkonost.lume.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ru.velkonost.lume.R;
import uk.co.senab.photoview.PhotoViewAttacher;

import static ru.velkonost.lume.Constants.AVATAR;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.NAME;
import static ru.velkonost.lume.Constants.PNG;
import static ru.velkonost.lume.Constants.SLASH;
import static ru.velkonost.lume.Constants.URL.SERVER_AVATAR;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_RESOURCE;

public class FullScreenPhotoActivity extends AppCompatActivity {
    private String imageTitle;
    private String profileId;
    private String userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);
        Intent intent = getIntent();

        imageTitle = (String) intent.getExtras().get(NAME);
        profileId = String.valueOf(intent.getExtras().get(ID));
        userAvatar = (String) intent.getExtras().get(AVATAR);




        ImageView imageView = (ImageView) findViewById(R.id.fullImage);


        String avatarURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_RESOURCE
                + SERVER_AVATAR + SLASH + userAvatar
                + SLASH + profileId + PNG;

//        fetchImage(avatarURL, imageView);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));




        upload(avatarURL, imageView);




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
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
    }

    public void upload(String avatarURL, ImageView imageView){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        Picasso.with(this)
                .load(avatarURL)
                .placeholder(R.drawable.noavatar)
                .error(R.drawable.noavatar)
                .resize(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2)
                .centerCrop()
                .into(imageView);
    }
}

