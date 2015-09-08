package echowand.object;

import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.logic.DefaultRequestProcessor;
import echowand.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Set、Get、SetGet、INF_REQリクエストの処理を実行
 * @author Yoshiki Makino
 */

public class SetGetRequestProcessor extends DefaultRequestProcessor {
    private static final Logger logger = Logger.getLogger(SetGetRequestProcessor.class.getName());
    private static final String className = SetGetRequestProcessor.class.getName();
    
    private LocalObjectManager manager;
    
    /**
     * SetGetRequestProcessorを生成する。
     * ローカルオブジェクトのSetやGetを行うためにLocalObjectManagerを持っている必要がある。
     * @param manager Set、Getの対象となるローカルオブジェクト群
     */
    public SetGetRequestProcessor(LocalObjectManager manager) {
        logger.entering(className, "SetGetRequestProcessor", manager);
        
        this.manager = manager;
        
        logger.exiting(className, "SetGetRequestProcessor");
    }
    
    private void addAllSetFromFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        logger.entering(className, "addAllSetFromFirst", new Object[]{localSetGetAtomic, payload});
        
        int len = payload.getFirstOPC();
        for (int i=0; i<len; i++) {
            localSetGetAtomic.addSet(payload.getFirstPropertyAt(i));
        }
        
        logger.exiting(className, "addAllSetFromFirst");
    }
    
    private void addAllGetFromFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        logger.entering(className, "addAllGetFromFirst", new Object[]{localSetGetAtomic, payload});
        
        int len = payload.getFirstOPC();
        for (int i=0; i<len; i++) {
            localSetGetAtomic.addGet(payload.getFirstPropertyAt(i));
        }
        
        logger.exiting(className, "addAllGetFromFirst");
    }
    
    private void addAllGetFromSecond(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        logger.entering(className, "addAllGetFromSecond", new Object[]{localSetGetAtomic, payload});
        
        int len = payload.getSecondOPC();
        for (int i=0; i<len; i++) {
            localSetGetAtomic.addGet(payload.getSecondPropertyAt(i));
        }
        
        logger.exiting(className, "addAllGetFromSecond");
    }
    
    private void addAllSetToFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        logger.entering(className, "addAllSetToFirst", new Object[]{localSetGetAtomic, payload});
        
        for (Property property : localSetGetAtomic.getSetResult()) {
            payload.addFirstProperty(property);
        }
        
        logger.exiting(className, "addAllSetToFirst");
    }
    
    private void addAllGetToFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        logger.entering(className, "addAllGetToFirst", new Object[]{localSetGetAtomic, payload});
        
        for (Property property : localSetGetAtomic.getGetResult()) {
            payload.addFirstProperty(property);
        }
        
        logger.exiting(className, "addAllGetToFirst");
    }
    
    private void addAllGetToSecond(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        logger.entering(className, "addAllGetToSecond", new Object[]{localSetGetAtomic, payload});
        
        for (Property property : localSetGetAtomic.getGetResult()) {
            payload.addSecondProperty(property);
        }
        
        logger.exiting(className, "addAllGetToSecond");
    }
    
    private boolean doSetAllData(Frame frame, LocalObject object, StandardPayload res) {
        logger.entering(className, "doSetAllData", new Object[]{frame, object, res});
        
        StandardPayload req = frame.getCommonFrame().getEDATA(StandardPayload.class);
        
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        
        addAllSetFromFirst(localSetGetAtomic, req);
        
        localSetGetAtomic.run();
        
        addAllSetToFirst(localSetGetAtomic, res);
        
        boolean success = localSetGetAtomic.isSuccess();
        
        logger.exiting(className, "doSetAllData", success);
        return success;
    }
    
    private boolean doGetAllData(Frame frame, LocalObject object, StandardPayload res, boolean announce) {
        logger.entering(className, "doGetAllData", new Object[]{frame, object, res, announce});
        
        StandardPayload req = frame.getCommonFrame().getEDATA(StandardPayload.class);
        
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        localSetGetAtomic.setAnnounce(announce);
        
        addAllGetFromFirst(localSetGetAtomic, req);
        
        localSetGetAtomic.run();
        
        addAllGetToFirst(localSetGetAtomic, res);
        
        boolean success = localSetGetAtomic.isSuccess();
        
        logger.exiting(className, "doGetAllData", success);
        return success;
    }
    
    private boolean doSetGetAllData(Frame frame, LocalObject object, StandardPayload res) {
        logger.entering(className, "doSetGetAllData", new Object[]{frame, object, res});
        
        StandardPayload req = frame.getCommonFrame().getEDATA(StandardPayload.class);
        
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        
        addAllSetFromFirst(localSetGetAtomic, req);
        addAllGetFromSecond(localSetGetAtomic, req);
        
        localSetGetAtomic.run();
        
        addAllSetToFirst(localSetGetAtomic, res);
        addAllGetToSecond(localSetGetAtomic, res);
        
        boolean success = localSetGetAtomic.isSuccess();
        
        logger.exiting(className, "doSetGetAllData", success);
        return success;
    }
    
    private Frame createResponse(Node sender, Frame frame, LocalObject object, StandardPayload res) {
        logger.entering(className, "createResponse", new Object[]{sender, frame, object, res});
        
        Frame resFrame = createResponse(sender, frame, object, res, false, null);
        
        logger.exiting(className, "createResponse", resFrame);
        return resFrame;
    }
    
    private Frame createResponse(Node sender, Frame frame, LocalObject object, StandardPayload res, boolean useGroup, Subnet subnet) {
        logger.entering(className, "createResponse", new Object[]{sender, frame, object, res, useGroup, subnet});
        
        short tid = frame.getCommonFrame().getTID();
        CommonFrame cf = new CommonFrame();
        cf.setTID(tid);
        cf.setEDATA(res);
        
        StandardPayload req = frame.getCommonFrame().getEDATA(StandardPayload.class); 
        res.setDEOJ(req.getSEOJ());
        res.setSEOJ(object.getEOJ());
        
        Node peer = frame.getSender();
        if (useGroup) {
            peer = subnet.getGroupNode();
        }
        
        Frame resFrame =  new Frame(sender, peer, cf, frame.getConnection());
        
        logger.exiting(className, "createResponse", resFrame);
        return resFrame;
    }
    
    private List<LocalObject> getDestinationObject(Frame frame) {
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = cf.getEDATA(StandardPayload.class); 
        EOJ eoj = payload.getDEOJ();
        if (eoj.isAllInstance()) {
            return manager.getWithClassEOJ(eoj.getClassEOJ());
        } else {
            LinkedList<LocalObject> list = new LinkedList<LocalObject>();
            LocalObject object = manager.get(eoj);
            if (object != null) {
                list.add(object);
            }
            return list;
        }
    }
    
    private void processObjectSetI(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
        logger.entering(className, "processObjectSetI", new Object[]{subnet, frame, object, processed});
        
        StandardPayload res = new StandardPayload();
        if (!doSetAllData(frame, object, res)) {
            res.setESV(ESV.SetI_SNA);
            try {
                subnet.send(createResponse(subnet.getLocalNode(), frame, object, res));
            } catch (SubnetException e) {
                e.printStackTrace();
            }
        }
        
        logger.entering(className, "processObjectSetI");
    }
    
    private void processObjectSetC(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
        logger.entering(className, "processObjectSetC", new Object[]{subnet, frame, object, processed});
        
        StandardPayload res = new StandardPayload();
        if (doSetAllData(frame, object, res)) {
            res.setESV(ESV.Set_Res);
        } else {
            res.setESV(ESV.SetC_SNA);
        }
        try {
            subnet.send(createResponse(subnet.getLocalNode(), frame, object, res));
        } catch (SubnetException e) {
            e.printStackTrace();
        }
        
        logger.exiting(className, "processObjectSetC");
    }
    
    private void processObjectGet(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
        logger.entering(className, "processObjectGet", new Object[]{subnet, frame, object, processed});
        
        StandardPayload res = new StandardPayload();
        if (doGetAllData(frame, object, res, false)) {
            res.setESV(ESV.Get_Res);
        } else {
            res.setESV(ESV.Get_SNA);
        }
        try {
            subnet.send(createResponse(subnet.getLocalNode(), frame, object, res));
        } catch (SubnetException e) {
            e.printStackTrace();
        }
        
        logger.exiting(className, "processObjectGet");
    }

    private void processObjectSetGet(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
        logger.entering(className, "processObjectSetGet", new Object[]{subnet, frame, object, processed});
        
        StandardPayload res = new StandardPayload();
        if (doSetGetAllData(frame, object, res)) {
            res.setESV(ESV.SetGet_Res);
        } else {
            res.setESV(ESV.SetGet_SNA);
        }
        try {
            subnet.send(createResponse(subnet.getLocalNode(), frame, object, res));
        } catch (SubnetException e) {
            e.printStackTrace();
        }
        
        logger.exiting(className, "processObjectSetGet");
    }
    private void processObjectINF_REQ(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
        logger.entering(className, "processObjectINF_REQ", new Object[]{subnet, frame, object, processed});
        
        boolean useGroup;
        
        StandardPayload res = new StandardPayload();
        if (doGetAllData(frame, object, res, true)) {
            res.setESV(ESV.INF);
            useGroup = true;
        } else {
            res.setESV(ESV.INF_SNA);
            useGroup = false;
        }
        
        try {
            subnet.send(createResponse(subnet.getLocalNode(), frame, object, res, useGroup, subnet));
        } catch (SubnetException e) {
            e.printStackTrace();
        }
        
        logger.exiting(className, "processObjectINF_REQ");
    }
    
    private boolean processRequest(Subnet subnet, Frame frame, ESV esv, boolean processed) {
        logger.entering(className, "processRequest", new Object[]{subnet, frame, esv, processed});
        
        if (processed) {
            logger.exiting(className, "processRequest", false);
            return false;
        }
        
        List<LocalObject> objects = getDestinationObject(frame);
        if (objects.isEmpty()) {
            logger.exiting(className, "processRequest", false);
            return false;
        }
        
        for (LocalObject object : new ArrayList<LocalObject>(objects)) {
            switch (esv) {
                case SetI:
                    processObjectSetI(subnet, frame, object, processed);
                    break;
                case SetC:
                    processObjectSetC(subnet, frame, object, processed);
                    break;
                case Get:
                    processObjectGet(subnet, frame, object, processed);
                    break;
                case SetGet:
                    processObjectSetGet(subnet, frame, object, processed);
                    break;
                case INF_REQ:
                    processObjectINF_REQ(subnet, frame, object, processed);
                    break;
                default:
                    logger.exiting(className, "processRequest", false);
                    return false;
            }
        }

        logger.exiting(className, "processRequest", true);
        return true;
    }
    
    /**
     * ESVがSetIであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processSetI(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "processRequest", new Object[]{subnet, frame, processed});
        
        boolean ret = processRequest(subnet, frame, ESV.SetI, processed);
        
        logger.entering(className, "processRequest", ret);
        return ret;
    }
    
    /**
     * ESVがSetCであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processSetC(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "processSetC", new Object[]{subnet, frame, processed});
        
        boolean ret = processRequest(subnet, frame, ESV.SetC, processed);
        
        logger.entering(className, "processSetC", ret);
        return ret;
    }
    
    /**
     * ESVがGetであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processGet(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "processGet", new Object[]{subnet, frame, processed});
        
        boolean ret = processRequest(subnet, frame, ESV.Get, processed);
        
        logger.exiting(className, "processGet", ret);
        return ret;
    }

    /**
     * ESVがSetGetであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processSetGet(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "processSetGet", new Object[]{subnet, frame, processed});
        
        boolean ret = processRequest(subnet, frame, ESV.SetGet, processed);
        
        logger.exiting(className, "processSetGet", ret);
        return ret;
    }
    
    /**
     * ESVがINF_REQであるフレームの処理を行う。
     * @param subnet 受信したフレームの送受信が行なわれたサブネット
     * @param frame 受信したフレーム
     * @param processed 指定されたフレームがすでに処理済みである場合にはtrue、そうでなければfalse
     * @return 処理に成功した場合にはtrue、そうでなければfalse
     */
    @Override
    public boolean processINF_REQ(Subnet subnet, Frame frame, boolean processed) {
        logger.entering(className, "processSetGet", new Object[]{subnet, frame, processed});
        
        boolean ret = processRequest(subnet, frame, ESV.INF_REQ, processed);
        
        logger.exiting(className, "processSetGet", ret);
        return ret;
    }
}
