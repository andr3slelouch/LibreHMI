package andrade.luis.librehmi.models;

import java.util.*;

public class RecentUsedFilesCache {
    Set<String> cache;

    public int getCapacity() {
        return capacity;
    }

    int capacity;

    public RecentUsedFilesCache(int capacity) {
        this.cache = new LinkedHashSet<>(capacity);
        this.capacity = capacity;
    }

    public boolean get(String key) {
        if (!cache.contains(key)) {
            return false;
        }
        cache.remove(key);
        cache.add(key);
        return true;
    }

    public void refer(String key) {
        if (!get(key)) {
            put(key);
        }
    }

    public ArrayList<String> exportArrayList() {
        LinkedList<String> list = new LinkedList<>(cache);
        Iterator<String> itr = list.descendingIterator();
        ArrayList<String> arrayList = new ArrayList<>();
        while (itr.hasNext()) {
            arrayList.add(itr.next());
        }
        return arrayList;
    }

    public void importArrayList(ArrayList<String> arrayList) {
        LinkedList<String> list = new LinkedList<>(arrayList);
        Iterator<String> itr = list.descendingIterator();
        while (itr.hasNext()) {
            refer(itr.next());
        }
    }

    public void put(String key) {
        if (cache.size() == capacity) {
            String firstKey = cache.iterator().next();
            cache.remove(firstKey);
        }
        cache.add(key);
    }
}
