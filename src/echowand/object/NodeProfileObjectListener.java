package echowand.object;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 他ノードから送信されたインスタンスリストのレスポンスを受信し、RemoteObjectを生成する。
 * @author Yoshiki Makino
 */
public class NodeProfileObjectListener implements TransactionListener {
    private static final Logger logger = Logger.getLogger(NodeProfileObjectListener.class.getName());
    private static final String className = NodeProfileObjectListener.class.getName();
    
    private final EPC INSTANCE_LIST_S = EPC.xD6;
    private RemoteObjectManager manager;
    private TransactionManager transactionManager;
    
    /**
     * NodeProfileObjectListenerを生成する。
     * @param manager 生成したRemoteObjectの登録を行うRemoteObjectManager
     * @param transactionManager 生成したRemoteObjectに登録するTransactionManager
     */
    public NodeProfileObjectListener(RemoteObjectManager manager, TransactionManager transactionManager) {
        logger.entering(className, "NodeProfileObjectListener", new Object[]{manager, transactionManager});
        
        this.manager = manager;
        this.transactionManager = transactionManager;
        
        logger.exiting(className, "NodeProfileObjectListener", manager);
    }
    
    /**
     * トランザクションが開始された時に呼び出される。　
     * @param t 開始したトランザクション
     */
    @Override
    public void begin(Transaction t) {
    }

    /**
     * フレームが送信された時に呼び出される。
     * @param t フレームを送信したトランザクション
     * @param subnet フレームを送信したサブネット
     * @param frame 送信したフレーム
     * @param success 送信に成功した場合にはtrue、失敗した場合にはfalse
     */
    @Override
    public void send(Transaction t, Subnet subnet, Frame frame, boolean success) {
    }
    
    /**
     * 指定されたインスタンスリストプロパティを解析し、EOJのリストを返す。
     * @param property 受信したプロパティの情報
     * @return プロパティの情報から抽出したEOJのリスト
     */
    private LinkedList<EOJ> parseInstanceListS(Property property) {
        logger.entering(className, "parseInstanceListS", property);
        
        LinkedList<EOJ> eojs = new LinkedList<EOJ>();
        Data data = property.getEDT();

        if (data == null) {
            logger.exiting(className, "parseInstanceListS", eojs);
            return eojs;
        }

        int len = data.size();
        for (int i=0; 3*(i+1)<len; i++) {
            byte[] eojBytes = data.toBytes(3*i+1, 3);
            eojs.add(new EOJ(eojBytes));
        }
        
        logger.exiting(className, "parseInstanceListS", eojs);
        return eojs;
    }

    /**
     * 指定されたインスタンスリストプロパティで指定されたEOJのRemoteObjectを生成する。
     * 生成したRemoteObjectはRemoteObjectManagerに登録される。
     * @param subnet 生成するRemoteObjectの存在するサブネット
     * @param node 生成するRemoteObjectを管理しているノード
     * @param property インスタンスリストプロパティ
     */
    private void addRemoteObjects(Subnet subnet, Node node, Property property) {
        logger.entering(className, "addRemoteObjects", new Object[]{subnet, node, property});
        
        LinkedList<EOJ> eojs = parseInstanceListS(property);
        for (EOJ eoj : eojs) {
            if (manager.get(node, eoj) == null) {
                RemoteObject object = new RemoteObject(subnet, node, eoj, transactionManager);
                manager.add(object);
            }
        }
        
        logger.exiting(className, "addRemoteObjects");
    }

    /**
     * 指定されたフレーム中のインスタンスリストプロパティで指定されたRemoteObjectを生成する。
     * また、送信したノードのノードプロファイルのRemoteObjectも生成する。
     * 生成したRemoteObjectはRemoteObjectManagerに登録される。
     * @param t レスポンスが所属するトランザクション
     * @param subnet レスポンスが送受信されたサブネット
     * @param frame レスポンスのフレーム
     */
    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        logger.entering(className, "receive", new Object[]{t, subnet, frame});
        
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = cf.getEDATA(StandardPayload.class);
        
        if (payload == null) {
            logger.logp(Level.WARNING, className, "receive", "invalid frame: ", frame);
            return;
        }
        
        if (manager.get(frame.getSender(), new EOJ("0ef001")) == null) {
            manager.add(new RemoteObject(subnet, frame.getSender(), new EOJ("0ef001"), transactionManager));
        }
        
        int len = payload.getFirstOPC();
        for (int i = 0; i < len; i++) {
            Property property = payload.getFirstPropertyAt(i);
            if (property.getEPC() == INSTANCE_LIST_S) {
                addRemoteObjects(subnet, frame.getSender(), property);
            }
        }
        
        logger.exiting(className, "receive");
    }

    /**
     * トランザクションが終了した時に呼び出される。
     * @param t 終了したトランザクション
     */
    @Override
    public void finish(Transaction t) {
    }
}