package model;

import java.util.HashMap;
import java.util.Map;

public class NoOverWriteMap<K, V> extends HashMap<K, V> implements Map<K, V> {

    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            throw new IllegalArgumentException("No duplicate keys Allowed");
        } else {
            return super.put(key, value);
        }
    }
}
