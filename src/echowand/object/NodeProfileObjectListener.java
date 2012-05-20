package echowand.object;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.*;
import java.util.LinkedList;

/**
 * 他ノードから送信されたインスタンスリストのレスポンスを受信し、RemoteObjectを生成する。
 * @author Yoshiki Makino
 */
public class NodeProfileObjectListener implements TransactionListener {
    private final EPC INSTANCE_LIST_S = EPC.xD6;
    private RemoteObjectManager manager;
    private TransactionManager transactionManager;
    
    /**
     * NodeProfileObjectListenerを生成する。
     * @param manager 生成したRemoteObjectの登録を行うRemoteObjectManager
     * @param transactionManager 生成したRemoteObjectに登録するTransactionManager
     */
    public NodeProfileObjectListener(RemoteObjectManager manager, TransactionManager transactionManager) {
        this.manager = manager;
        this.transactionManager = transactionManager;
    }
    
    /**
     * トランザクションが開始された時に呼び出される。　
     * @param t 開始したトランザクション
     */
    @Override
    public void begin(Transaction t) {
    }
    
    /**
     * 指定されたインスタンスリストプロパティを解析し、EOJのリストを返す。
     * @param property 受信したプロパティの情報
     * @return プロパティの情報から抽出したEOJのリスト
     */
    private LinkedList<EOJ> parseInstanceListS(Property property) {
        LinkedList<EOJ> eojs = new LinkedList<EOJ>();
        Data data = property.getEDT();
        int len = data.size();
        for (int i=0; 3*(i+1)<len; i++) {
            byte[] eojBytes = data.toBytes(3*i+1, 3);
            eojs.add(new EOJ(eojBytes));
        }
        return eojs;
    }

    /**
     * 指定されたインスタンスリストプロパティで指定されたEOJのRemoteObjectを生成する。
     * @param subnet 生成するRemoteObjectの存在するサブネット
     * @param node 生成するRemoteObjectを管理しているノード
     * @param property インスタンスリストプロパティ
     */
    private void addRemoteObjects(Subnet subnet, Node node, Property property) {
        LinkedList<EOJ> eojs = parseInstanceListS(property);
        for (EOJ eoj : eojs) {
            RemoteObject object = new RemoteObject(subnet, node, eoj, transactionManager);
            manager.add(object);
        }
    }

    /**
     * 指定されたフレーム中のインスタンスリストプロパティで指定されたRemoteObjectを生成する。
     * @param t レスポンスが所属するトランザクション
     * @param subnet レスポンスが送受信されたサブネット
     * @param frame レスポンスのフレーム
     */
    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload) cf.getEDATA();
        int len = payload.getFirstOPC();
        for (int i = 0; i < len; i++) {
            Property property = payload.getFirstPropertyAt(i);
            if (property.getEPC() == INSTANCE_LIST_S) {
                addRemoteObjects(subnet, frame.getSender(), property);
            }
        }
    }

    /**
     * トランザクションが終了した時に呼び出される。
     * @param t 終了したトランザクション
     */
    @Override
    public void finish(Transaction t) {
    }
}
