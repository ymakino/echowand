package echowand.object;

import echowand.common.EOJ;
import echowand.common.ESV;
import echowand.logic.RequestProcessor;
import echowand.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set、Get、SetGet、INF_REQリクエストの処理を実行
 * @author Yoshiki Makino
 */

public class SetGetRequestProcessor extends RequestProcessor {
    private LocalObjectManager manager;
    
    /**
     * SetGetRequestProcessorを生成する。
     * ローカルオブジェクトのSetやGetを行うためにLocalObjectManagerを持っている必要がある。
     * @param manager Set、Getの対象となるローカルオブジェクト群
     */
    public SetGetRequestProcessor(LocalObjectManager manager) {
        this.manager = manager;
    }
    
    private void addAllSetFromFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        int len = payload.getFirstOPC();
        for (int i=0; i<len; i++) {
            localSetGetAtomic.addSet(payload.getFirstPropertyAt(i));
        }
    }
    
    private void addAllGetFromFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        int len = payload.getFirstOPC();
        for (int i=0; i<len; i++) {
            localSetGetAtomic.addGet(payload.getFirstPropertyAt(i));
        }
    }
    
    private void addAllGetFromSecond(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        int len = payload.getSecondOPC();
        for (int i=0; i<len; i++) {
            localSetGetAtomic.addGet(payload.getSecondPropertyAt(i));
        }
    }
    
    private void addAllSetToFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        for (Property property : localSetGetAtomic.getSetResult()) {
            payload.addFirstProperty(property);
        }
    }
    
    private void addAllGetToFirst(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        for (Property property : localSetGetAtomic.getGetResult()) {
            payload.addFirstProperty(property);
        }
    }
    
    private void addAllGetToSecond(LocalSetGetAtomic localSetGetAtomic, StandardPayload payload) {
        for (Property property : localSetGetAtomic.getGetResult()) {
            payload.addSecondProperty(property);
        }
    }
    
    private boolean doSetAllData(Frame frame, LocalObject object, StandardPayload res) {
        StandardPayload req = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        
        addAllSetFromFirst(localSetGetAtomic, req);
        
        localSetGetAtomic.run();
        
        addAllSetToFirst(localSetGetAtomic, res);
        
        return localSetGetAtomic.isSuccess();
    }
    
    private boolean doGetAllData(Frame frame, LocalObject object, StandardPayload res, boolean announce) {
        StandardPayload req = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        localSetGetAtomic.setAnnounce(announce);
        
        addAllGetFromFirst(localSetGetAtomic, req);
        
        localSetGetAtomic.run();
        
        addAllGetToFirst(localSetGetAtomic, res);
        
        return localSetGetAtomic.isSuccess();
    }
    
    private boolean doSetGetAllData(Frame frame, LocalObject object, StandardPayload res) {
        StandardPayload req = (StandardPayload)frame.getCommonFrame().getEDATA();
        
        LocalSetGetAtomic localSetGetAtomic = new LocalSetGetAtomic(object);
        
        addAllSetFromFirst(localSetGetAtomic, req);
        addAllGetFromSecond(localSetGetAtomic, req);
        
        localSetGetAtomic.run();
        
        addAllSetToFirst(localSetGetAtomic, res);
        addAllGetToSecond(localSetGetAtomic, res);
        
        return localSetGetAtomic.isSuccess();
    }
    
    private Frame createResponse(Node sender, Frame frame, LocalObject object, StandardPayload res) {
        return createResponse(sender, frame, object, res, false, null);
    }
    
    private Frame createResponse(Node sender, Frame frame, LocalObject object, StandardPayload res, boolean useGroup, Subnet subnet) {
        short tid = frame.getCommonFrame().getTID();
        CommonFrame cf = new CommonFrame();
        cf.setTID(tid);
        cf.setEDATA(res);
        
        StandardPayload req = (StandardPayload)frame.getCommonFrame().getEDATA();
        res.setDEOJ(req.getSEOJ());
        res.setSEOJ(object.getEOJ());
        
        Node peer = frame.getSender();
        if (useGroup) {
            peer = subnet.getGroupNode();
        }
        
        return new Frame(sender, peer, cf);
    }
    
    private List<LocalObject> getDestinationObject(Frame frame) {
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload)cf.getEDATA();
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
        StandardPayload res = new StandardPayload();
        if (!doSetAllData(frame, object, res)) {
            res.setESV(ESV.SetI_SNA);
            try {
                subnet.send(createResponse(subnet.getLocalNode(), frame, object, res));
            } catch (SubnetException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void processObjectSetC(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
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
    }
    private void processObjectGet(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
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
    }

    private void processObjectSetGet(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
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
    }
    private void processObjectINF_REQ(Subnet subnet, Frame frame, LocalObject object, boolean processed) {
        StandardPayload res = new StandardPayload();
        if (doGetAllData(frame, object, res, true)) {
            res.setESV(ESV.INF);
        } else {
            res.setESV(ESV.INF_SNA);
        }
        try {
            subnet.send(createResponse(subnet.getLocalNode(), frame, object, res, true, subnet));
        } catch (SubnetException e) {
            e.printStackTrace();
        }
    }
    
    private boolean processRequest(Subnet subnet, Frame frame, ESV esv, boolean processed) {
        if (processed) {
            return false;
        }
        
        List<LocalObject> objects = getDestinationObject(frame);
        if (objects.isEmpty()) {
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
                    return false;
            }
        }

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
        return processRequest(subnet, frame, ESV.SetI, processed);
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
        return processRequest(subnet, frame, ESV.SetC, processed);
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
        return processRequest(subnet, frame, ESV.Get, processed);
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
        return processRequest(subnet, frame, ESV.SetGet, processed);
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
        return processRequest(subnet, frame, ESV.INF_REQ, processed);
    }
}
