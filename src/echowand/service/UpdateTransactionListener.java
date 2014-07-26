package echowand.service;

import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.net.Frame;
import echowand.net.Subnet;
import echowand.service.result.ResultUpdate;

/**
 *
 * @author ymakino
 */
public class UpdateTransactionListener implements TransactionListener {
    private ResultUpdate resultUpdate;
    
    public UpdateTransactionListener(ResultUpdate resultUpdate) {
        this.resultUpdate = resultUpdate;
    }

    @Override
    public void begin(Transaction t) {
        // doNothing
    }

    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        resultUpdate.addFrame(frame);
    }

    @Override
    public void finish(Transaction t) {
        // doNothing;
    }
    
}
