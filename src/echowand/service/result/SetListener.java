package echowand.service.result;

/**
 *
 * @author ymakino
 */
public class SetListener implements ResultListener<SetResult> {

    @Override
    public void begin(SetResult result) {
    }

    @Override
    public void send(SetResult result, ResultFrame resultFrame, boolean success) {
    }

    @Override
    public void send(SetResult result, ResultFrame resultFrame, ResultData resultData, boolean success) {
    }

    @Override
    public void send(SetResult result, ResultFrame resultFrame, ResultData resultData, boolean success, boolean isSecond) {
    }

    @Override
    public void receive(SetResult result, ResultFrame resultFrame) {
    }

    @Override
    public void receive(SetResult result, ResultFrame resultFrame, ResultData resultData) {
    }

    @Override
    public void receive(SetResult result, ResultFrame resultFrame, ResultData resultData, boolean isSecond) {
    }

    @Override
    public void finish(SetResult result) {
    }
}
