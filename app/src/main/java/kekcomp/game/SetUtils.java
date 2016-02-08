package kekcomp.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by svyatoslav on 1/15/16.
 */
public class SetUtils {

    public SetUtils(){}

    // объединение двух множеств
    private static Set<Integer> union(Set<Integer> a, Set<Integer> b) {
        Set<Integer> result = new HashSet<Integer>(a);
        result.addAll(b);
        return result;
    }

    // пересечение двух множеств
    private static Set<Integer> intersection(Set<Integer> a, Set<Integer> b) {
        Set<Integer> result = new HashSet<Integer>(a);
        result.retainAll(b);
        return result;
    }

    // разность двух множеств
    public static Set<Integer> complement(Set<Integer> a, Set<Integer> b) {
        Set<Integer> result = new HashSet<Integer>();
        for(Integer i : a) {
            if(!b.contains(i)) {
                result.add(i);
            }
        }
        return result;
    }

    public static int[] toInt(Set<Integer> set) {
        int[] a = new int[set.size()];
        int i = 0;
        for (Integer val : set) a[i++] = val;
        return a;
    }
}
