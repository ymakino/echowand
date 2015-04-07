package echowand.service.result;

/**
 *
 * @author ymakino
 */
public interface Matcher<T> {
    public boolean match(T target);
}
