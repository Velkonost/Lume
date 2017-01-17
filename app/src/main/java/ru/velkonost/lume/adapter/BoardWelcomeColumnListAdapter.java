package ru.velkonost.lume.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.velkonost.lume.R;
import ru.velkonost.lume.activity.BoardColumnsActivity;
import ru.velkonost.lume.descriptions.BoardColumn;

public class BoardWelcomeColumnListAdapter extends ArrayAdapter {
    private List<BoardColumn> data;
    private Context mContext;

    public BoardWelcomeColumnListAdapter(Context context, List<BoardColumn> data) {
        super(context, R.layout.item_board_column, data);
        mContext = context;
        this.data = data;
    }

    public void setData(List<BoardColumn> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull final ViewGroup parent) {
        final BoardColumn boardColumn = data.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_board_column, null);
        }

        ((TextView) convertView.findViewById(R.id.columnName)).setText(boardColumn.getName());


        (convertView.findViewById(R.id.item_board_column)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mContext, BoardColumnsActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra(DIALOG_ID, Integer.parseInt(dialogContact.getDialogId()));
//                        intent.putExtra(ID, Integer.parseInt(dialogContact.getId()));
                        mContext.startActivity(intent);
                    }
                }, 350);
            }
        });

        return convertView;
    }

}
