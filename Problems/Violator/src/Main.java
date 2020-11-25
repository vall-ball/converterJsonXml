import java.util.ArrayList;
import java.util.List;

/**
 * Class to work with
 */
class Violator {

    public static List<Box<? extends Bakery>> defraud() {

        List<?> i = new ArrayList();
        boolean a = i instanceof List;
        List<Box<? extends Bakery>> list = new ArrayList<>();
        Box box = new Box();
        box.put(new Paper());
        list.add(box);
        return list;
    }

}