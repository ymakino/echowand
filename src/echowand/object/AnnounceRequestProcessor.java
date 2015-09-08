package echowand.object;

import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.logic.DefaultRequestProcessor;
import echowand.net.CommonFrame;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * INF、INFCリクエストの処理を実行
 * @author Yoshiki Makino
 */
public class AnnounceRequestProcessor extends DefaultRequestProcessor {
    private static final Logger logger = Logger.getLogger(AnnounceRequestProcessor.class.getName());
    private static final String className = AnnounceRequestProcessor.class.getName();
    
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
        logger.entering(className, "AnnounceRequestProcessor", new Object[]{localManager, remoteManager});
        
        this.localManager = localManager;
        this.remoteManager = remoteManager;
        
        logger.exiting(className, "AnnounceRequestProcessor");
    }
    
    
    private StandardPayload updateINForINFC(Frame frame) {
        logger.entering(className, "updateINForINFC", frame);
        
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        
        RemoteObject object = remoteManager.get(frame.getSender(), payload.getSEOJ());
        
        CommonFrame replyCommonFrame = new CommonFrame(payload.getDEOJ(), payload.getSEOJ(), ESV.INFC_Res);
        replyCommonFrame.setTID(commonFrame.getTID());
        StandardPayload replyPayload = replyCommonFrame.getEDATA(StandardPayload.class);

        int len = payload.getFirstOPC();
        for (int i = 0; i < len; i++) {
            Property property = payload.getFirstPropertyAt(i);
            replyPayload.addFirstProperty(new Property(property.getEPC()));
            if (object != null) {
                object.notifyData(property.getEPC(), new ObjectData(property.getEDT()));
            }
        }

        logger.exiting(className, "updateINForINFC", replyPayload);
        return replyPayload;
    }
    
    private void replyINForINFC(Subnet subnet, Frame frame, StandardPayload replyPayload, LocalObject object) {
        logger.entering(className, "replyINForINFC", new Object[]{subnet, frame, replyPayload, object});
        
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);

        replyPayload.setDEOJ(payload.getSEOJ());
        CommonFrame replyCommonFrame = new CommonFrame();
        replyCommonFrame.setEDATA(replyPayload);
        replyCommonFrame.setTID(commonFrame.getTID());

        Frame replyFrame = new Frame(subnet.getLocalNode(), frame.getSender(), replyCommonFrame, frame.getConnection());
        try {
            subnet.send(replyFrame);
        } catch (SubnetException e) {
            e.printStackTrace();
        }
        
        logger.exiting(className, "replyINForINFC");
    }

    private boolean processINForINFC(Subnet subnet, Frame frame, boolean needsReply) {
        logger.entering(className, "processINForINFC", new Object[]{subnet, frame, needsReply});
        
        CommonFrame commonFrame = frame.getCommonFrame();
        StandardPayload payload = commonFrame.getEDATA(StandardPayload.class);
        
        if (payload == null) {
            logger.exiting(className, "processINForINFC", false);
            return false;
        }
        
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

        logger.exiting(className, "processINForINFC", true);
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
        logger.entering(className, "processINF", new Object[]{subnet, frame, processed});
        
        if (processed) {
            logger.exiting(className, "processINF", false);
            return false;
        }
        
        boolean ret = processINForINFC(subnet, frame, false);
        
        logger.exiting(className, "processINF", ret);
        return ret;
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
        logger.entering(className, "processINFC", new Object[]{subnet, frame, processed});
        
        if (processed) {
            logger.exiting(className, "processINFC", false);
            return false;
        }
        
        boolean ret = processINForINFC(subnet, frame, true);
        
        logger.exiting(className, "processINFC", ret);
        return ret;
    }
}
