package echowand.service.result;

/**
 *
 * @author ymakino
 */
public class GetListener implements ResultListener<GetResult> {

    @Override
    public void begin(GetResult result) {
    }

    @Override
    public void send(GetResult result, ResultFrame resultFrame, boolean success) {
    }

    @Override
    public void send(GetResult result, ResultFrame resultFrame, ResultData resultData, boolean success) {
    }

    @Override
    public void send(GetResult result, ResultFrame resultFrame, ResultData resultData, boolean success, boolean isSecond) {
    }

    @Override
    public void receive(GetResult result, ResultFrame resultFrame) {
    }

    @Override
    public void receive(GetResult result, ResultFrame resultFrame, ResultData resultData) {
    }

    @Override
    public void receive(GetResult result, ResultFrame resultFrame, ResultData resultData, boolean isSecond) {
    }

    @Override
    public void finish(GetResult result) {
    }
}
