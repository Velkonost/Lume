package ru.velkonost.lume.Managers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.SearchActivity;

import static android.graphics.Color.WHITE;
import static ru.velkonost.lume.Constants.SEARCH;

/**
 * @author Velkonost
 *
 * Инициализаторы
 */
public class InitializationsManager {

    /**
     * Инициализатор диалога с одной кнопкой
     *
     * @param context - контекст
     * @param header заголовок диалога
     * @param description - описание диалога
     * @param btnName - название кнопки
     *
     */
    public static void inititializeAlertDialog(Context context, String header,
                                               String description, String btnName){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(header)
                .setMessage(description)
                .setCancelable(false)
                .setNegativeButton(btnName,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Инициализация диалога с обновлением активности по нажатию на кнопку
     *
     * @param context - контекст
     * @param header - заголовок диалога
     * @param description - описание диалога
     * @param btnName - название кнопки
     * @param activity - активность, которую необходимо обновить
     *
     */
    public static void inititializeAlertDialogWithRefresh(Context context, String header,
                                                          String description, String btnName,
                                                          final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(header)
                .setMessage(description)
                .setCancelable(false)
                .setNegativeButton(btnName,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                changeActivityCompat(activity);

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Инициализация тулбара
     *
     * @param activity - активность
     * @param toolbar - тулбар переданной активности
     * @param title - заголовок тулбара
     */
    public static void initToolbar(AppCompatActivity activity, Toolbar toolbar, int title) {

        toolbar.setTitle(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);

        activity.setSupportActionBar(toolbar);
    }

    /**
     * Инициализация тулбара
     *
     * @param activity - активность
     * @param toolbar - тулбар переданной активности
     * @param title - заголовок тулбара
     */
    public static void initToolbar(FragmentActivity activity, Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(WHITE);
    }

    /**
     * Инициализация тулбара
     *
     * @param activity - активность
     * @param toolbar - тулбар переданной активности
     * @param title - заголовок тулбара
     *
     */
    public static void initToolbar(AppCompatActivity activity, Toolbar toolbar, String title) {

        toolbar.setTitle(title);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);

        activity.setSupportActionBar(toolbar);
    }

    /**
     * Обновление активности
     *
     * @param a - активность
     *
     */
    public static void changeActivityCompat(final Activity a) {
        final Intent intent = a.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        a.finish();
        a.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_diagonaltranslate);

        a.startActivity(intent);
        a.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_diagonaltranslate);
    }

    /**
     * Переход на новую активность
     *
     * @param a - старая активность
     * @param nextIntent - намерение с новой активностью
     *
     */
    public static void changeActivityCompat(final Activity a, Intent nextIntent) {
        final Intent currentIntent = a.getIntent();
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


        if (currentIntent.filterEquals(nextIntent)) {
            a.finish();
        }
        a.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_diagonaltranslate);

        a.startActivity(nextIntent);
        a.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_diagonaltranslate);
    }

    /**
     * Инициализация элемента поиска
     *
     * @param activity - активность
     * @param searchView - элемент поиска переданной активности
     *
     */
    public static void initSearch(final Activity activity, MaterialSearchView searchView) {

        searchView.setEllipsize(true);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent nextIntent;

                /**
                 * Получение данных, по которым пользователь хочет найти информацию.
                 * Сохранение этих данных в файл на данном устройстве.
                 **/
//                saveText(activity, SEARCH, query);

                /**
                 * Переход на страницу поиска, где выоводится результат.
                 * {@link SearchActivity}
                 **/
                nextIntent = new Intent(activity, SearchActivity.class);

                /**
                 * Получение данных, по которым пользователь хочет найти информацию.
                 * Сохранение этих данных в файл на данном устройстве.
                 **/
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                nextIntent.putExtra(SEARCH, query);

                /**
                 * Переход на следующую активность.
                 * {@link InitializationsManager#changeActivityCompat(Activity, Intent)}
                 **/
                changeActivityCompat(activity, nextIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }
}
