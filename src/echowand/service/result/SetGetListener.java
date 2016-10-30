package echowand.service.result;

/**
 *
 * @author ymakino
 */
public class SetGetListener implements ResultListener<SetGetResult> {

    @Override
    public void begin(SetGetResult result) {
    }

    @Override
    public void send(SetGetResult result, ResultFrame resultFrame, boolean success) {
    }

    @Override
    public void send(SetGetResult result, ResultFrame resultFrame, ResultData resultData, boolean success) {
    }

    @Override
    public void send(SetGetResult result, ResultFrame resultFrame, ResultData resultData, boolean success, boolean isSecond) {
    }

    @Override
    public void receive(SetGetResult result, ResultFrame resultFrame) {
    }

    @Override
    public void receive(SetGetResult result, ResultFrame resultFrame, ResultData resultData) {
    }

    @Override
    public void receive(SetGetResult result, ResultFrame resultFrame, ResultData resultData, boolean isSecond) {
    }

    @Override
    public void finish(SetGetResult result) {
    }
}
