//package ru.velkonost.lume.Activities;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//import ru.velkonost.lume.R;
//
//public class SettingsActivity extends AppCompatActivity {
//
//    //Объявляем используемые переменные:
//    private ImageView imageView;
//    private final int Pick_image = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//
//        //Связываемся с нашим ImageView:
//        imageView = (ImageView)findViewById(R.id.imageView);
//
//        //Связываемся с нашей кнопкой Button:
//        Button PickImage = (Button) findViewById(R.id.button);
//        //Настраиваем для нее обработчик нажатий OnClickListener:
//        PickImage.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                //Тип получаемых объектов - image:
//                photoPickerIntent.setType("image/*");
//                //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
//                startActivityForResult(photoPickerIntent, Pick_image);
//            }
//        });
//    }
//
//    //Обрабатываем результат выбора в галерее:
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//        switch(requestCode) {
//            case Pick_image:
//                if(resultCode == RESULT_OK){
//                    try {
//
//                        //Получаем URI изображения, преобразуем его в Bitmap
//                        //объект и отображаем в элементе ImageView нашего интерфейса:
//                        final Uri imageUri = imageReturnedIntent.getData();
//                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                        imageView.setImageBitmap(selectedImage);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//        }
//    }
//
//
//
//    private class MyAsyncTask extends AsyncTask<URL, Void, Bitmap> {
//
//        CircleImageView ivLogo;
//
//        public MyAsyncTask(CircleImageView iv) {
//            ivLogo = iv;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        ///Загружаем
//        @Override
//        protected Bitmap doInBackground(URL... urls) {
//            Bitmap networkBitmap = null;
//
//            URL networkUrl = urls[0];
//            try {
//                networkBitmap = BitmapFactory.decodeStream(networkUrl
//                        .openConnection().getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return networkBitmap;
//        }
//
//        //Выводим изображения
//        protected void onPostExecute(Bitmap result) {
//            if(result!=null) {
//                Bitmap bm = Bitmap.createScaledBitmap(result, ivLogo.getHeight(), ivLogo.getHeight(), false);
//                ivLogo.setImageBitmap(bm);
//            }else{
//                ivLogo.setImageResource(R.drawable.unkava);
//            }
//        }
//    }
//}
