package echowand.object;

import echowand.net.Frame;
import echowand.net.SubnetException;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.CommonFrame;
import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.logic.RequestProcessor;
import java.util.LinkedList;
import java.util.List;

/**
 * INF、INFCリクエストの処理を実行
 * @author Yoshiki Makino
 */
public class AnnounceRequestProcessor extends RequestProcessor {
    private LocalObjectManager localManager;
    private RemoteObjectManager remoteManager;
    
    /**
     * AnnounceRequestProcessorを生成する。
     * ローカルオブジェクトへの通知を可能にするためLocalObjectManagerを持っている必要がある。
     * リモートオブジェクトの通知を伝えるためにRemoteObjectManagerを持っている必要がある。
     * @param localManager 通知の宛先となるローカルオブジェクト群
     * @param remoteManager データの通知を伝えるためのリモートオブジェクト群
     */
    public AnnounceRequestProcessor(LocalObjectManager localManager, RemoteObjectManager remoteManager) {
        this.localManager = localManager;
        this.remoteManager = remoteManager;
    }
    
    
    private StandardPayload updateINForINFC(Frame frame) {
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload)commonFrame.getEDATA();
        
        RemoteObject object = remoteManager.get(frame.getSender(), payload.getSEOJ());
        
        CommonFrame replyCommonFrame = new CommonFrame(payload.getDEOJ(), payload.getSEOJ(), ESV.INFC_Res);
        replyCommonFrame.setTID(commonFrame.getTID());
        StandardPayload replyPayload = (StandardPayload)replyCommonFrame.getEDATA();

        int len = payload.getFirstOPC();
        for (int i = 0; i < len; i++) {
            Property property = payload.getFirstPropertyAt(i);
            replyPayload.addFirstProperty(new Property(property.getEPC()));
            if (object != null) {
                object.notifyData(property.getEPC(), new ObjectData(property.getEDT()));
            }
        }

        return replyPayload;
    }
    private void replyINForINFC(Subnet subnet, Frame frame, StandardPayload replyPayload, LocalObject object) {
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload) commonFrame.getEDATA();

        replyPayload.setDEOJ(payload.getSEOJ());
        CommonFrame replyCommonFrame = new CommonFrame();
        replyCommonFrame.setEDATA(replyPayload);
        replyCommonFrame.setTID(commonFrame.getTID());

        Frame replyFrame = new Frame(subnet.getLocalNode(), frame.getSender(), replyCommonFrame);
        try {
            subnet.send(replyFrame);
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }

    private boolean processINForINFC(Subnet subnet, Frame frame, boolean needsReply) {
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload)commonFrame.getEDATA();
        EOJ eoj = payload.getDEOJ();
        List<LocalObject> objects;
        
        StandardPayload replyPayload = updateINForINFC(frame);
        
        if (eoj.isAllInstance()) {
            objects = localManager.getWithClassEOJ(eoj.getClassEOJ());
        } else {
            objects = new LinkedList<LocalObject>();
            LocalObject object = localManager.get(eoj);
            if (object != null) {
                objects.add(object);
            }
        }

        if (needsReply) {
            for (LocalObject object : objects) {
                replyINForINFC(subnet, frame, replyPayload, object);
            }
        }

        return true;
    }
    
    /**
     * ESVがINFであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processINF(Subnet subnet, Frame frame, boolean processed) {
        if (processed) {
            return false;
        }
        return processINForINFC(subnet, frame, false);
    }

    /**
     * ESVがINFCであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processINFC(Subnet subnet, Frame frame, boolean processed) {
        if (processed) {
            return false;
        }
        return processINForINFC(subnet, frame, true);
    }
}
