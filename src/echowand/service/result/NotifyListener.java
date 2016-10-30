package echowand.service.result;

/**
 *
 * @author ymakino
 */
public class NotifyListener implements ResultListener<NotifyResult> {

    @Override
    public void begin(NotifyResult result) {
    }

    @Override
    public void send(NotifyResult result, ResultFrame resultFrame, boolean success) {
    }

    @Override
    public void send(NotifyResult result, ResultFrame resultFrame, ResultData resultData, boolean success) {
    }

    @Override
    public void send(NotifyResult result, ResultFrame resultFrame, ResultData resultData, boolean success, boolean isSecond) {
    }

    @Override
    public void receive(NotifyResult result, ResultFrame resultFrame) {
    }

    @Override
    public void receive(NotifyResult result, ResultFrame resultFrame, ResultData resultData) {
    }

    @Override
    public void receive(NotifyResult result, ResultFrame resultFrame, ResultData resultData, boolean isSecond) {
    }

    @Override
    public void finish(NotifyResult result) {
    }
}
