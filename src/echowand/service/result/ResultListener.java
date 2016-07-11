package echowand.service.result;

/**
 *
 * @author ymakino
 * @param <ResultType>
 */
public interface ResultListener<ResultType extends ResultBase> {

    public void begin(ResultType result);

    public void send(ResultType result, ResultFrame resultFrame, boolean success);

    public void receive(ResultType result, ResultFrame resultFrame);
    
    public void finish(ResultType result);
    
}
