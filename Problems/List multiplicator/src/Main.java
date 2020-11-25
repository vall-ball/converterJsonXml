import java.util.ArrayList;
import java.util.List;

/**
    Class to modify
*/
class ListMultiplicator {

    /**
        Repeats original list content provided number of times   
        @param list list to repeat
        @param n times to repeat, should be zero or greater
    */
	public static void multiply(List<?> list, int n) {
        if (n == 0) {
            list.clear();
        } else if (n > 1) {
            nCopies(list, n);
        }
    }

    private static <T> void nCopies(List<T> list, int n) {
        List<T> original = new ArrayList<>(list);
        for (int i = 1; i < n; i++) {
            list.addAll(original);
        }
    }
}