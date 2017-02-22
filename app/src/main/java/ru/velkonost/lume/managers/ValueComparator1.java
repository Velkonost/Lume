package ru.velkonost.lume.managers;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Сортирует Map по значению.
 *
 * @param <K> Тип ключа.
 * @param <V> Тип значения.
 */
public class ValueComparator1<K, V extends Comparable<V>> implements Comparator<K> {

    HashMap<K, V> map = new HashMap<>();

    /** Конструктор */
    public ValueComparator1(HashMap<K, V> map){
        this.map.putAll(map);
    }

    /** Сортировка по значению сверху вниз */
    @Override
    public int compare(K s1, K s2) {
        return -map.get(s1).compareTo(map.get(s2));
    }
}

