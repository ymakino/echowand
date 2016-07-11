package echowand.service.result;

/**
 *
 * @author ymakino
 */
public interface ObserveListener {

    public void begin(ObserveResult result);

    public void receive(ObserveResult result, ResultFrame resultFrame);
    
    public void finish(ObserveResult result);
    
}
