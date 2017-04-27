package ru.velkonost.lume.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.Managers.LinkMarkerLongClickListener;
import ru.velkonost.lume.Managers.TypefaceUtil;
import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.AMPERSAND;
import static ru.velkonost.lume.Constants.CARD_ID;
import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.IDS;
import static ru.velkonost.lume.Constants.LATITUDE;
import static ru.velkonost.lume.Constants.LONGITUDE;
import static ru.velkonost.lume.Constants.TITLE;
import static ru.velkonost.lume.Constants.URL.SERVER_ADD_MAP_MARKER_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_GET_MAP_MARKERS_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.Constants.URL.SERVER_REMOVE_MAP_MARKER_METHOD;
import static ru.velkonost.lume.Managers.InitializationsManager.initToolbar;
import static ru.velkonost.lume.R.id.map;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_map;

    /**
     * Свойство - описание верхней панели инструментов приложения
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Свойство - google карта
      */
    private GoogleMap mMap;

    /**
     * Свойство - идентификатор карточки
     */
    private int cardId;

    /**
     * Свойство - список идентфикаторов маркеров карты
     */
    private ArrayList<String> ids;

    /**
     * Свойство - название нового маркера
     */
    private String title;

    /**
     * Свойство - широта нового маркера
     */
    private double latitude;

    /**
     * Свойство - долгота нового маркера
     */
    private double longitude;

    /**
     * Свойство - название удаляемого маркера
     */
    private String removeTitle;

    /**
     * Свойство - широта удаляемого маркера
     */
    private double removeLatitude;

    /**
     * Свойство - долгота удаляемого маркера
     */
    private double removeLongitude;

    /**
     * Свойство - список маркером карты
     */
    private ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBase();
        getData();
        initialize();


        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Установка первоначальных настроек активности
     */
    private void setBase() {

        setContentView(LAYOUT);
        ButterKnife.bind(this);
        setTheme(R.style.AppTheme_Cursor);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
        setSystemBarBackground();

    }

    /**
     * Установка цвета дли системной панели
     */
    private void setSystemBarBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorDarkPrimary));
        }
    }

    /**
     * Инициализация основных элементов
     */
    private void initialize() {

        ids = new ArrayList<>();
        markers = new ArrayList<>();

        initToolbar(MapActivity.this, toolbar, getString(R.string.map_activity_name));

    }

    /**
     * Получение данных (отсутствует получение с интернета)
     */
    private void getData() {
        getExtras();
    }

    /**
     * Получение данных из предыдущей активности
     */
    private void getExtras() {
        Intent intent = getIntent();
        cardId = intent.getExtras().getInt(CARD_ID);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new GetMarkers().execute();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {


                LinearLayout layout = new LinearLayout(MapActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams  params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dp2px(5), dp2px(20), dp2px(5), dp2px(20));

                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle(getResources().getString(R.string.create_marker));

                final EditText inputName
                        = (EditText) getLayoutInflater().inflate(R.layout.item_edittext_style, null);

                setInputNameParams(inputName, params);

                layout.addView(inputName);

                builder.setView(layout)
                        .setPositiveButton(getResources().getString(R.string.create), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                title = inputName.getText().toString();

                                if (title.length() != 0) {
                                    new AddMarker().execute();

                                    latitude = latLng.latitude;
                                    longitude = latLng.longitude;

                                    LatLng s = new LatLng(latitude, longitude);
                                    markers.add(mMap.addMarker(new MarkerOptions().position(s).title(title)));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(s));

                                    mMap.setOnMarkerDragListener(new LinkMarkerLongClickListener(markers) {
                                        @Override
                                        public void onLongClickListener(Marker marker) {
                                            removeTitle = marker.getTitle();
                                            removeLatitude = marker.getPosition().latitude;
                                            removeLongitude = marker.getPosition().longitude;

                                            new RemoveMarker().execute();

                                            markers.remove(marker);
                                            marker.remove();
                                        }
                                    });

                                } else dialog.cancel();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    /**
     * Настройка поля для редактирования названия доски
     * @param inputName - поле для редактирования названия доски
     * @param params - layout-параметры для установки
     */
    private void setInputNameParams(EditText inputName, LinearLayout.LayoutParams  params) {

        inputName.setTextColor(ContextCompat.getColor(MapActivity.this, R.color.colorBlack));
        inputName.setLayoutParams(params);
        inputName.setHint(getResources().getString(R.string.enter_board_name));
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);

    }

    /**
     * Конвертер из dp в px
     *
     * @param dp - значения в dp
     * @return - значение в px
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                MapActivity.this.getResources().getDisplayMetrics());
    }

    /**
     * Получение данных о маркерах
     */
    private class GetMarkers extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_GET_MAP_MARKERS_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            /* Свойство - полученный JSON–объект */
            JSONObject dataJsonObj;

            try {

                /*
                  Получение JSON-объекта по строке.
                 */
                dataJsonObj = new JSONObject(strJson);

                /**
                 * Получение идентификаторов найденных пользователей.
                 */
                JSONArray idsJSON = dataJsonObj.getJSONArray(IDS);

                for (int i = 0; i < idsJSON.length(); i++) {
                    ids.add(idsJSON.getString(i));
                }

                for (int i = 0; i < ids.size(); i++) {
                    String participantId = ids.get(i);

                    JSONObject markerInfo = dataJsonObj.getJSONObject(participantId);

                    String title = markerInfo.getString(TITLE);
                    int latitude = markerInfo.getInt(LATITUDE);
                    int longitude = markerInfo.getInt(LONGITUDE);

                    LatLng newMarker = new LatLng(latitude, longitude);

                    markers.add(mMap.addMarker(new MarkerOptions().position(newMarker).title(title)));
                }



                mMap.setOnMarkerDragListener(new LinkMarkerLongClickListener(markers) {
                    @Override
                    public void onLongClickListener(Marker marker) {
                        removeTitle = marker.getTitle();
                        removeLatitude = marker.getPosition().latitude;
                        removeLongitude = marker.getPosition().longitude;

                        new RemoveMarker().execute();

                        markers.remove(marker);
                        marker.remove();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Добавление нового маркера
     */
    private class AddMarker extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_ADD_MAP_MARKER_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + LATITUDE + EQUALS + latitude
                    + AMPERSAND + LONGITUDE + EQUALS + longitude
                    + AMPERSAND + TITLE + EQUALS + title;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }

    /**
     * Удаление маркера
     */
    private class RemoveMarker extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_REMOVE_MAP_MARKER_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = CARD_ID + EQUALS + cardId
                    + AMPERSAND + LATITUDE + EQUALS + removeLatitude
                    + AMPERSAND + LONGITUDE + EQUALS + removeLongitude
                    + AMPERSAND + TITLE + EQUALS + removeTitle;

            /** Свойство - код ответа, полученных от сервера */
            String resultJson = "";

            /**
             * Соединяется с сервером, отправляет данные, получает ответ.
             * {@link ru.velkonost.lume.net.ServerConnection#getJSON(String, String)}
             **/
            try {
                resultJson = getJSON(dataURL, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultJson;
        }
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
        }
    }
}
