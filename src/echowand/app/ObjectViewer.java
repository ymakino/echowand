package echowand.app;

import echowand.common.EPC;
import echowand.info.BaseObjectInfo;
import echowand.info.HumiditySensorInfo;
import echowand.info.NodeProfileInfo;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.*;
import echowand.net.InetSubnet;
import echowand.net.Subnet;
import echowand.object.*;
import echowand.util.Pair;
import java.util.LinkedList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

class IntervalDataUpdater extends Thread {
    private LocalObject object;
    private EPC epc;
    private LinkedList<Pair<ObjectData, Integer>> updateInfo;
    
    public IntervalDataUpdater(LocalObject object, EPC epc) {
        this.object = object;
        this.epc = epc;
        updateInfo = new LinkedList<Pair<ObjectData, Integer>>();
    }
    
    public void addData(ObjectData objectData, int sleepMSecs) {
        updateInfo.add(new Pair<ObjectData, Integer>(objectData, sleepMSecs));
    }
    
    @Override
    public void run() {
        for (;;) {
            try {
                for (Pair<ObjectData, Integer> currentInfo : updateInfo) {
                    object.forceSetData(epc, currentInfo.first);
                    Thread.sleep(currentInfo.second);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectViewer implements Runnable {

    private InetSubnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private LocalObjectManager localManager;
    private LocalObject nodeProfile;
    private RequestDispatcher requestDispatcher;
    private MainLoop loop;
    
    public NodeListModel createNodeListModel() {
        return new NodeListModel(remoteManager);
    }
    
    public ObjectListModel createObjectListModel() {
        return new ObjectListModel(remoteManager);
    }
    
    public InstanceListRequestExecutor createInstanceListRequestExecutor() {
        return new InstanceListRequestExecutor(subnet, transactionManager, remoteManager);
    }

    private NodeProfileInfo createNodeProfileInfo() {
        NodeProfileInfo nodeProfileInfo = new NodeProfileInfo();
        nodeProfileInfo.add(EPC.x97, true, false, false, 2);
        nodeProfileInfo.add(EPC.x98, true, false, false, 4);
        return nodeProfileInfo;
    }

    private LocalObject createNodeProfileObject(Subnet subnet, LocalObjectManager manager, TransactionManager transactionManager) {
        LocalObject nodeProfileObject = new LocalObject(createNodeProfileInfo());
        nodeProfileObject.addDelegate(new LocalObjectDateTimeDelegate());
        nodeProfileObject.addDelegate(new NodeProfileObjectDelegate(manager));
        nodeProfileObject.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        return nodeProfileObject;
    }

    private void initialize() {
        subnet = new InetSubnet();
        
        if (!subnet.isEnabled()) {
            BindErrorFrame frame = new BindErrorFrame(null, true);
            frame.setVisible(true);
        }
        
        transactionManager = new TransactionManager(subnet);
        remoteManager = new RemoteObjectManager();
        localManager = new LocalObjectManager();
        nodeProfile = createNodeProfileObject(subnet, localManager, transactionManager);
        requestDispatcher = new RequestDispatcher();

        requestDispatcher.addRequestProcessor(new SetGetRequestProcessor(localManager));
        requestDispatcher.addRequestProcessor(new AnnounceRequestProcessor(localManager, remoteManager));

        loop = new MainLoop();
        loop.setSubnet(subnet);
        loop.addListener(requestDispatcher);
        loop.addListener(transactionManager);

        try {
            localManager.add(nodeProfile);
        } catch (TooManyObjectsException e) {
            e.printStackTrace();
        }
    }
    
    public void openViewerFrame() {
        ViewerFrame viewerFrame = new ViewerFrame(this);
        viewerFrame.setVisible(true);
    }

    public void openObjectTableFrame(RemoteObject remoteObject) {
        ObjectTableModel model = new ObjectTableModel();
        ObjectTableFrame frame = new ObjectTableFrame(this, model);
        model.setCachedObject(new CachedRemoteObject(remoteObject));
        frame.setTitle(remoteObject.getNode() + " - " + remoteObject.getEOJ());
        frame.setVisible(true);
    }
    
    public void fixObjectTableColumnWidth(JTable objectTable) {
        TableColumnModel colModel = objectTable.getColumnModel();
        for (int i=0; i<5; i++) {
            TableColumn col = colModel.getColumn(i);
            col.setResizable(false);
            if (i == 0) {
                col.setMinWidth(50);
                col.setMaxWidth(50);
            } else {
                col.setMinWidth(40);
                col.setMaxWidth(40);
            }
        }
    }

    public void fixMultipleObjectTableColumnWidth(JTable objectTable) {
        TableColumnModel colModel = objectTable.getColumnModel();
        TableColumn col = colModel.getColumn(0);
        col.setResizable(false);
        col.setMinWidth(50);
        col.setMaxWidth(50);
    }
    
    public void setObjectTableRenderer(JTable objectTable) {
        objectTable.setDefaultRenderer(EPC.class, new ObjectTableEPCCellRenderer());
        objectTable.setDefaultRenderer(Boolean.class, new ObjectTableBooleanCellRenderer());
    }
    
    public void setObjectTableEditor(JTable objectTable) {
        objectTable.setDefaultEditor(ObjectData.class, new ObjectTableObjectDataCellEditor());
    }

    @Override
    public void run() {
        initialize();

        BaseObjectInfo temperatureSensorInfo = new TemperatureSensorInfo();
        temperatureSensorInfo.add(EPC.x97, true, false, false, 2);
        temperatureSensorInfo.add(EPC.x98, true, false, false, 4);
        LocalObject temperatureSensor = new LocalObject(temperatureSensorInfo);
        temperatureSensor.addDelegate(new LocalObjectDateTimeDelegate());
        temperatureSensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));

        try {
            localManager.add(temperatureSensor);
        } catch (TooManyObjectsException ex) {
            Logger.getLogger(ViewerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        IntervalDataUpdater updater1 = new IntervalDataUpdater(temperatureSensor, EPC.x80);
        updater1.addData(new ObjectData((byte) 0x32), 1000);
        updater1.addData(new ObjectData((byte) 0x31), 1000);
        updater1.start();
        
        
        BaseObjectInfo humiditySensorInfo = new HumiditySensorInfo();
        humiditySensorInfo.add(EPC.x97, true, false, false, 2);
        humiditySensorInfo.add(EPC.x98, true, false, false, 4);
        LocalObject humiditySensor = new LocalObject(humiditySensorInfo);
        humiditySensor.addDelegate(new LocalObjectDateTimeDelegate());
        humiditySensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        
        try {
            localManager.add(humiditySensor);
        } catch (TooManyObjectsException ex) {
            Logger.getLogger(ViewerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        IntervalDataUpdater updater2 = new IntervalDataUpdater(nodeProfile, EPC.xD5);
        updater2.addData(new ObjectData(new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x11, (byte) 01}), 1000);
        updater2.addData(new ObjectData(new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x11, (byte) 0x01, (byte) 0x00, (byte) 0x11, (byte) 0x02}), 1000);
        updater2.start();

        Thread loopThread = new Thread(loop);
        loopThread.setDaemon(true);
        loopThread.start();

        openViewerFrame();
    }
    
    private static ConsoleHandler handler = null;
    public static void changeLogLevelAll(String name) {
        if (handler == null) {
            handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
        }
        Logger.getLogger(name).setLevel(Level.ALL);
        Logger.getLogger(name).addHandler(handler);
    }

    public static void main(String[] args) {
        boolean trace = false;
        
        handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);

        if (trace) {
            changeLogLevelAll(Transaction.class.getName());
            changeLogLevelAll(AnnounceTransactionConfig.class.getName());
            changeLogLevelAll(MainLoop.class.getName());
            changeLogLevelAll(RequestDispatcher.class.getName());
            changeLogLevelAll(SetGetTransactionConfig.class.getName());
            changeLogLevelAll(TransactionConfig.class.getName());
            changeLogLevelAll(TransactionManager.class.getName());
            changeLogLevelAll(AnnounceRequestProcessor.class.getName());
            changeLogLevelAll(InstanceListRequestExecutor.class.getName());
            changeLogLevelAll(LocalObject.class.getName());
            changeLogLevelAll(LocalObjectDateTimeDelegate.class.getName());
            changeLogLevelAll(LocalObjectManager.class.getName());
            changeLogLevelAll(LocalObjectNotifyDelegate.class.getName());
            changeLogLevelAll(LocalObjectRandomDelegate.class.getName());
            changeLogLevelAll(LocalSetGetAtomic.class.getName());
            changeLogLevelAll(NodeProfileObjectDelegate.class.getName());
            changeLogLevelAll(NodeProfileObjectListener.class.getName());
            changeLogLevelAll(RemoteObject.class.getName());
            changeLogLevelAll(RemoteObjectManager.class.getName());
            changeLogLevelAll(SetGetRequestProcessor.class.getName());
            changeLogLevelAll(BaseObjectInfo.class.getName());
        }

        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new ObjectViewer());
    }
}
