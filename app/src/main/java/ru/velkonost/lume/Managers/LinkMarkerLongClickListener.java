package ru.velkonost.lume.Managers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Слушатель для обработки долгого нажатия на маркер карты
 */
public abstract class LinkMarkerLongClickListener implements GoogleMap.OnMarkerDragListener {

    /**
     * Свойство - предыдущая позиция
     */
    private int previousIndex = -1;

    /**
     * Свойство - выбранный маркер
     */
    private Marker cachedMarker = null;

    /**
     * Свойство - позиция выбранного маркера
     */
    private LatLng cachedDefaultPostion = null;

    /**
     * Свойство - список маркеров
     */
    private List<Marker> markerList;

    /**
     * Свойство - список позиций маркеров
     */
    private List<LatLng> defaultPostions;

    public LinkMarkerLongClickListener(List<Marker> markerList){
        this.markerList = new ArrayList<>(markerList);
        defaultPostions = new ArrayList<>(markerList.size());
        for (Marker marker : markerList) {
            defaultPostions.add(marker.getPosition());
            marker.setDraggable(true);
        }
    }

    public abstract void onLongClickListener(Marker marker);

    @Override
    public void onMarkerDragStart(Marker marker) {
        onLongClickListener(marker);
        setDefaultPostion(markerList.indexOf(marker));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        setDefaultPostion(markerList.indexOf(marker));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        setDefaultPostion(markerList.indexOf(marker));
    }


    private void setDefaultPostion(int markerIndex) {
        if(previousIndex == -1 || previousIndex != markerIndex){
            cachedMarker = markerList.get(markerIndex);
            cachedDefaultPostion = defaultPostions.get(markerIndex);
            previousIndex = markerIndex;
        }
        cachedMarker.setPosition(cachedDefaultPostion);
    }
}