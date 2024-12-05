package es.caib.pinbal.core.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UtilsHelper {

    private static final int DEFAULT_PARTITIONS_SIZE = 5;
    private static final int MAX_PARTITION_ELEMENTS = 999;

    public static <T> List<List<T>> listSplit(List<T> list) {

        if (list == null || list.isEmpty()) {
            return initializeSplits();
        } else {
            List<List<T>> sublists = new ArrayList<>(ListUtils.partition(list, MAX_PARTITION_ELEMENTS));

            while (sublists.size() < DEFAULT_PARTITIONS_SIZE) {
                sublists.add(new ArrayList<T>());
            }
            return sublists;
        }
    }

    public static <T> List<T> getListPartition(List<List<T>> list, int index) {
        if (list == null) {
            return null;
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + ". L'índex no pot ser negatiu.");
        }
        if (index > list.size()) {
            throw new IndexOutOfBoundsException("Index " + index + ". La llista només té " + list.size() + " elements.");
        }
        return getNullIfEmpty(list.get(index));
    }

    private static <T> List<List<T>> initializeSplits() {
        List<List<T>> newSplits = new ArrayList<>(DEFAULT_PARTITIONS_SIZE);
        for (int i = 0; i < DEFAULT_PARTITIONS_SIZE; i++) {
            newSplits.add(new ArrayList<T>());
        }
        return newSplits;
    }

    public static <T> List<T> getNullIfEmpty(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return null;
        } else {
            return objects;
        }
    }

}
