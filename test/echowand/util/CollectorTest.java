package echowand.util;

import echowand.util.Selector;
import echowand.util.Collector;
import java.util.Collection;
import java.util.LinkedList;
import org.junit.*;
import static org.junit.Assert.*;

class DummySelector<T> implements Selector<T> {
    private T object;
    public DummySelector(T object) {
        this.object = object;
    }

    @Override
    public boolean match(T object) {
        return this.object.equals(object);
    }
};

/**
 *
 * @author Yoshiki Makino
 */
public class CollectorTest {

    /**
     * Test of collect method, of class Collector.
     */
    @Test
    public void testCollect() {
        DummySelector<String> selector = new DummySelector<String>("MATCH");
        LinkedList<String> list = new LinkedList<String>();
        list.add("MATCH");
        list.add("NO MATCH");
        list.add("MATCH");
        list.add("NO MATCH");
        list.add("MATCH");
        list.add("NO MATCH");
        list.add("MATCH");
        list.add("MATCH");
        Collector<String> collector = new Collector<String>(selector);
        assertEquals(5, collector.collect(list).size());
    }
}
