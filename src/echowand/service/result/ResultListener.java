package echowand.service.result;

/**
 *
 * @author ymakino
 * @param <ResultType>
 */
public interface ResultListener<ResultType extends ResultBase> {

    public void begin(ResultType result);

    public void send(ResultType result, ResultFrame resultFrame, boolean success);

    public void send(ResultType result, ResultFrame resultFrame, ResultData resultData, boolean success);

    public void send(ResultType result, ResultFrame resultFrame, ResultData resultData, boolean success, boolean isSecond);

    public void receive(ResultType result, ResultFrame resultFrame);

    public void receive(ResultType result, ResultFrame resultFrame, ResultData resultData);

    public void receive(ResultType result, ResultFrame resultFrame, ResultData resultData, boolean isSecond);
    
    public void finish(ResultType result);
    
}
