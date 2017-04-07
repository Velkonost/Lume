package ru.velkonost.lume.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hanks.library.AnimateCheckBox;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.velkonost.lume.R;
import ru.velkonost.lume.model.Checkbox;

import static ru.velkonost.lume.Constants.EQUALS;
import static ru.velkonost.lume.Constants.ID;
import static ru.velkonost.lume.Constants.MARQUEE_REPEAT_LIMIT;
import static ru.velkonost.lume.Constants.URL.SERVER_CARD_SET_CHECKBOX_DONE_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_CARD_SET_CHECKBOX_UNDONE_METHOD;
import static ru.velkonost.lume.Constants.URL.SERVER_HOST;
import static ru.velkonost.lume.Constants.URL.SERVER_KANBAN_SCRIPT;
import static ru.velkonost.lume.Constants.URL.SERVER_PROTOCOL;
import static ru.velkonost.lume.net.ServerConnection.getJSON;

/**
 * @author Velkonost
 *
 * Список флажков задач карточки
 */

public class CardCheckboxListAdapter
        extends RecyclerView.Adapter<CardCheckboxListAdapter.CardCheckboxViewHolder> {

    /**
     * Свойство - данные, с которыми необходимо работать
     */
    private List<Checkbox> data;

    private Context context;

    private LayoutInflater inflater;

    private int id;

    public CardCheckboxListAdapter(Context context, List<Checkbox> data) {

        this.data = data;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Checkbox> data) {
        this.data = data;
    }

    @Override
    public CardCheckboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_checkbox, parent, false);
        return new CardCheckboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardCheckboxViewHolder holder, int position) {
        final Checkbox item = data.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            holder.title.setText(Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_LEGACY));
        else
            holder.title.setText(Html.fromHtml(item.getTitle()));

        holder.title.setSelected(true);
        holder.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.title.setHorizontallyScrolling(true);
        holder.title.setMarqueeRepeatLimit(MARQUEE_REPEAT_LIMIT);


        if(item.isDone()) {
            holder.cbDone.setChecked(true);
            holder.title.setTypeface(holder.title.getTypeface(), Typeface.ITALIC);
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorLightGrey));
            holder.line.setVisibility(View.VISIBLE);
        }
        else {
            holder.cbDone.setChecked(false);
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.line.setVisibility(View.INVISIBLE);
        }

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbDone.isChecked()) {
                    holder.cbDone.setChecked(false);
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    holder.line.setVisibility(View.INVISIBLE);

                    id = item.getId();
                    new SetUndone().execute();
                } else {
                    holder.cbDone.setChecked(true);
                    holder.title.setTypeface(holder.title.getTypeface(), Typeface.ITALIC);
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorLightGrey));
                    holder.line.setVisibility(View.VISIBLE);

                    id = item.getId();
                    new SetDone().execute();
                }
            }
        });

        holder.cbDone.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {

                if (isChecked) {
                    holder.title.setTypeface(holder.title.getTypeface(), Typeface.ITALIC);
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorLightGrey));
                    holder.line.setVisibility(View.VISIBLE);

                    id = item.getId();
                    new SetDone().execute();
                } else {
                    holder.title.setTypeface(null, Typeface.NORMAL);
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    holder.line.setVisibility(View.INVISIBLE);

                    id = item.getId();
                    new SetUndone().execute();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CardCheckboxViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.done)
        AnimateCheckBox cbDone;

        @BindView(R.id.line)
        View line;

        CardCheckboxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Изменение состояния флажка для задачи
     */
    private class SetDone extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CARD_SET_CHECKBOX_DONE_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = ID + EQUALS + id;

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
     * Изменение состояния флажка для задачи
     */
    private class SetUndone extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... strings) {

            /**
             * Формирование адреса, по которому необходимо обратиться.
             **/
            String dataURL = SERVER_PROTOCOL + SERVER_HOST + SERVER_KANBAN_SCRIPT
                    + SERVER_CARD_SET_CHECKBOX_UNDONE_METHOD;

            /**
             * Формирование отправных данных.
             */
            @SuppressWarnings("WrongThread") String params = ID + EQUALS + id;

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
