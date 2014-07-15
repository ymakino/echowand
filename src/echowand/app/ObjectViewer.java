package echowand.app;

import echowand.common.EPC;
import echowand.info.DeviceObjectInfo;
import echowand.info.HumiditySensorInfo;
import echowand.info.NodeProfileInfo;
import echowand.info.PropertyConstraintHumidity;
import echowand.info.PropertyConstraintTemperature;
import echowand.info.TemperatureSensorInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Inet4Subnet;
import echowand.net.Inet6Subnet;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.net.TCPConnection;
import echowand.net.TCPAcceptor;
import echowand.net.UDPNetwork;
import echowand.object.*;
import echowand.util.LoggerConfig;
import echowand.util.Pair;
import java.awt.Dimension;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.LinkedList;
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

    private Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private LocalObjectManager localManager;
    private LocalObject nodeProfile;
    private RequestDispatcher requestDispatcher;
    private MainLoop loop;
    
    private boolean dummyDeviceEnabled = false;
    
    private int width;
    private int height;
    
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
    
    private static void quitWithErrorFrame(SubnetException ex) {
        ErrorFrame frame = new ErrorFrame(null, true);
        if (ex.getCause() == null) {
            frame.setMessage(ex.getMessage());
        } else {
            frame.setMessage(ex.getCause().getMessage());
        }
        frame.setVisible(true);
    }
    
    public void enableDummyDevice() {
        dummyDeviceEnabled = true;
    }

    private void initialize() {
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
        } catch (TooManyObjectsException ex) {
            Logger.getLogger(ObjectViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ObjectViewer(Subnet subnet) {
        this(subnet, 0, 0);
    }
    
    public ObjectViewer(Subnet subnet, int width, int height) {
        this.subnet = subnet;
        this.width = width;
        this.height = height;
    }
    
    public void openViewerFrame() {
        ViewerFrame viewerFrame = new ViewerFrame(this);
        viewerFrame.setVisible(true);
        Dimension d = viewerFrame.getSize();
        
        if (width == 0) {
            width = d.width;
        }
        
        if (height == 0) {
            height = d.height;
        }
        
        viewerFrame.setSize(width, height);
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
    
    private void createDummyDevices() {
        DeviceObjectInfo temperatureSensorInfo = new TemperatureSensorInfo();
        temperatureSensorInfo.add(EPC.x97, true, false, false, 2);
        temperatureSensorInfo.add(EPC.x98, true, false, false, 4);
        temperatureSensorInfo.add(EPC.xE0, true, false, true, 2, new PropertyConstraintTemperature());
        LocalObject temperatureSensor = new LocalObject(temperatureSensorInfo);
        temperatureSensor.addDelegate(new LocalObjectDateTimeDelegate());
        temperatureSensor.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));

        try {
            localManager.add(temperatureSensor);
        } catch (TooManyObjectsException ex) {
            Logger.getLogger(ViewerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        IntervalDataUpdater updater1 = new IntervalDataUpdater(temperatureSensor, EPC.x80);
        updater1.addData(new ObjectData((byte) 0x30), 1000);
        updater1.addData(new ObjectData((byte) 0x31), 1000);
        updater1.start();
        
        
        DeviceObjectInfo humiditySensorInfo = new HumiditySensorInfo();
        humiditySensorInfo.add(EPC.x97, true, false, false, 2);
        humiditySensorInfo.add(EPC.x98, true, false, false, 4);
        humiditySensorInfo.add(EPC.xE0, true, false, false, 1, new PropertyConstraintHumidity());
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
    }

    @Override
    public void run() {
        initialize();
        
        if (dummyDeviceEnabled) {
            createDummyDevices();
        }
        
        Thread loopThread = new Thread(loop);
        loopThread.setDaemon(true);
        loopThread.start();
        
        openViewerFrame();
    }
    
    private static void usage() {
        System.out.println("Usage: ObjectViewer [--help] [-i interface] [-w width] [-h height]");
    }

    public static void main(String[] args) {
        final boolean trace = true;
        
        if (trace) {
            /*
            LoggerConfig.changeLogLevelAll(Transaction.class.getName());
            LoggerConfig.changeLogLevelAll(AnnounceTransactionConfig.class.getName());
            LoggerConfig.changeLogLevelAll(MainLoop.class.getName());
            LoggerConfig.changeLogLevelAll(RequestDispatcher.class.getName());
            LoggerConfig.changeLogLevelAll(SetGetTransactionConfig.class.getName());
            LoggerConfig.changeLogLevelAll(TransactionConfig.class.getName());
            LoggerConfig.changeLogLevelAll(TransactionManager.class.getName());
            LoggerConfig.changeLogLevelAll(AnnounceRequestProcessor.class.getName());
            LoggerConfig.changeLogLevelAll(InstanceListRequestExecutor.class.getName());
            LoggerConfig.changeLogLevelAll(LocalObject.class.getName());
            LoggerConfig.changeLogLevelAll(LocalObjectDateTimeDelegate.class.getName());
            LoggerConfig.changeLogLevelAll(LocalObjectManager.class.getName());
            LoggerConfig.changeLogLevelAll(LocalObjectNotifyDelegate.class.getName());
            LoggerConfig.changeLogLevelAll(LocalObjectRandomDelegate.class.getName());
            LoggerConfig.changeLogLevelAll(LocalSetGetAtomic.class.getName());
            LoggerConfig.changeLogLevelAll(NodeProfileObjectDelegate.class.getName());
            LoggerConfig.changeLogLevelAll(NodeProfileObjectListener.class.getName());
            LoggerConfig.changeLogLevelAll(RemoteObject.class.getName());
            LoggerConfig.changeLogLevelAll(RemoteObjectManager.class.getName());
            LoggerConfig.changeLogLevelAll(SetGetRequestProcessor.class.getName());
            LoggerConfig.changeLogLevelAll(DeviceObjectInfo.class.getName());
            */
            
            //LoggerConfig.changeLogLevelAll(InetSubnet.class.getName());
            //LoggerConfig.changeLogLevelAll(UDPNetwork.class.getName());
            //LoggerConfig.changeLogLevelAll(TCPAcceptor.class.getName());
            //LoggerConfig.changeLogLevelAll(InetSubnetTCPAcceptorThread.class.getName());
            //LoggerConfig.changeLogLevelAll(InetSubnetTCPReceiverThread.class.getName());
            //LoggerConfig.changeLogLevelAll(TCPConnection.class.getName());
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
        
        int n=0;
        String interfaceName = null;
        int width = 0;
        int height = 0;
        
        while (args.length > n) {
            if (args[n].equals("--help")) {
                usage();
                System.exit(0);
            } else if (args[n].equals("-i")) {
                if (args.length <= n+1) {
                    usage();
                    System.exit(1);
                }
                interfaceName = args[n+1];
                n += 2;
            } else if (args[n].equals("-w")) {
                if (args.length <= n+1) {
                    usage();
                    System.exit(1);
                }
                width = Integer.parseInt(args[n+1]);
                n += 2;
            } else if (args[n].equals("-h")) {
                if (args.length <= n+1) {
                    usage();
                    System.exit(1);
                }
                height = Integer.parseInt(args[n+1]);
                n += 2;
            }
        }
        
        try {
            Inet4Subnet subnet;
            if (interfaceName == null) {
                subnet = new Inet4Subnet();
            } else {
                NetworkInterface nif = NetworkInterface.getByName(interfaceName);
                subnet = new Inet4Subnet(nif);
            }
            subnet.startService();
            ObjectViewer viewer = new ObjectViewer(subnet, width, height);
            java.awt.EventQueue.invokeLater(viewer);
        } catch (SubnetException ex) {
            quitWithErrorFrame(ex);
        } catch (SocketException ex) {
            Logger.getLogger(ObjectViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
