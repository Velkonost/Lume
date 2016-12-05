package ru.velkonost.lume.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.velkonost.lume.R;

import static ru.velkonost.lume.Constants.HYPHEN;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
        }
    }



    @Override
    public int getItemCount() {
        return 0;
    }

    class PlaceLivingViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView userPlaceLiving;

        public PlaceLivingViewHolder (View itemView){
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.cardViewPlaceLiving);
            userPlaceLiving = (TextView) itemView.findViewById(R.id.descriptionCardPlaceLiving);
        }
    }

    class BirthdayViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView userBirthday;

        public BirthdayViewHolder (View itemView){
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.cardViewBirthday);
            userBirthday = (TextView) itemView.findViewById(R.id.descriptionCardBirthday);
        }
    }

    class PlaceStudyViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView userPlaceStudy;

        public PlaceStudyViewHolder (View itemView){
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.cardViewPlaceStudy);
            userPlaceStudy = (TextView) itemView.findViewById(R.id.descriptionCardPlaceStudy);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("TYPE", String.valueOf(super.getItemViewType(position)));
        return super.getItemViewType(position);

    }


    /**
     * Форматирование даты из вида, полученного с сервер - YYYY-MM-DD
     *                в вид, необходимый для отображения - DD-MM-YYYY
     **/
    public String formatDate(String dateInStr) {

        String day, month, year;

        /** Разделяем строку на три ключевый строки */
        day = String.valueOf(dateInStr.charAt(dateInStr.length() - 2)) +
                dateInStr.charAt(dateInStr.length() - 1);

        month = String.valueOf(dateInStr.charAt(dateInStr.length() - 5)) +
                dateInStr.charAt(dateInStr.length() - 4);

        year = String.valueOf(dateInStr.charAt(dateInStr.length() - 10)) +
                dateInStr.charAt(dateInStr.length() - 9) +
                dateInStr.charAt(dateInStr.length() - 8) +
                dateInStr.charAt(dateInStr.length() - 7);

        /** Соединяем все воедино */
        return day
                + HYPHEN + month
                + HYPHEN + year;
    }
}
