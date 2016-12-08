package ru.velkonost.lume.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import ru.velkonost.lume.R;

public class DialogsActivity extends AppCompatActivity {

    String[] data = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};

    GridView gvMain;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dialogs);

        adapter = new ArrayAdapter<>(this, R.layout.item_dialog, R.id.tvText, data);
        gvMain = (GridView) findViewById(R.id.gvMain);
        gvMain.setAdapter(adapter);
        adjustGridView();
    }

    private void adjustGridView() {
        gvMain.setColumnWidth(200);
        gvMain.setNumColumns(GridView.AUTO_FIT);

//        gvMain.setVerticalSpacing(5);
        gvMain.setHorizontalSpacing(5);
        gvMain.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
    }
}
