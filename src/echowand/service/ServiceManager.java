package echowand.service;

import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.logic.MainLoop;
import echowand.logic.RequestDispatcher;
import echowand.logic.TooManyObjectsException;
import echowand.logic.TransactionManager;
import echowand.net.Inet4Subnet;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import echowand.object.AnnounceRequestProcessor;
import echowand.object.LocalObject;
import echowand.object.LocalObjectDateTimeDelegate;
import echowand.object.LocalObjectManager;
import echowand.object.LocalObjectNotifyDelegate;
import echowand.object.NodeProfileObjectDelegate;
import echowand.object.RemoteObjectManager;
import echowand.object.SetGetRequestProcessor;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class ServiceManager {
    private static final Logger LOGGER = Logger.getLogger(ServiceManager.class.getName());
    private static final String CLASS_NAME = ServiceManager.class.getName();
    
    private Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private LocalObjectManager localManager;
    private LocalObject nodeProfileObject;
    private RequestDispatcher requestDispatcher;
    private MainLoop mainLoop;
    private SetGetRequestProcessor setGetRequestProcessor;
    private AnnounceRequestProcessor announceRequestProcessor;
    private ResultObserveProcessor observeServiceProcessor;
    
    private LinkedList<LocalObjectConfig> localObjectConfigs;
    private LinkedList<LocalObjectUpdater> localObjectUpdaters;
    
    private Thread mainThread;
    
    private boolean inService = false;
    
    private Service service = null;
    
    /**
     * Inet4Subnetを利用するServiceを作成する。
     * initializeメソッドを呼び出すまでは、特に処理を行なわない。
     * @throws SubnetException Inet4Subnetの生成に失敗した場合
     */
    public ServiceManager() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "ServiceManager");
        
        this.subnet = Inet4Subnet.startSubnet();
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "ServiceManager");
    }
    
    /**
     * 指定されたSubnetを利用するServiceを作成する。
     * initializeメソッドを呼び出すまでは、特に処理を行なわない。
     * @param subnet 構築するServiceが利用するsubnet
     */
    public ServiceManager(Subnet subnet) {
        LOGGER.entering(CLASS_NAME, "ServiceManager", subnet);
        
        this.subnet = subnet;
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "ServiceManager");
    }
    
    public boolean addLocalObjectConfig(LocalObjectConfig config) {
        LOGGER.entering(CLASS_NAME, "addLocalObjectConfig", config);
        
        boolean result = localObjectConfigs.add(config);
        
        LOGGER.exiting(CLASS_NAME, "addLocalObjectConfig", result);
        return result;
    }
    
    public boolean removeLocalObjectConfig(LocalObjectConfig config) {
        LOGGER.entering(CLASS_NAME, "removeLocalObjectConfig", config);
        
        boolean result = localObjectConfigs.remove(config);
        
        LOGGER.exiting(CLASS_NAME, "removeLocalObjectConfig", result);
        return result;
    }
    
    /**
     * 利用しているSubnetを返す
     * @return 利用しているSubnet
     */
    public Subnet getSubnet() {
        return subnet;
    }
    
    /**
     * 利用中のTransactionManagerを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のTransactionManager
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
    
    /**
     * 利用中のRemoteObjectManagerを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のRemoteObjectManager
     */
    public RemoteObjectManager getRemoteObjectManager() {
        return remoteManager;
    }
    
    /**
     * 利用中のLocalObjectManagerを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のLocalObjectManager
     */
    public  LocalObjectManager getLocalObjectManager() {
        return localManager;
    }
    
    /**
     * ノードプロファイルオブジェクトとして利用中のLocalObjectを返す。
     * initializeメソッドを呼び出すまではnullを返す。
     * @return ノードプロファイルオブジェクトを表すLocalObject
     */
    public LocalObject getNodeProfileObject() {
        return nodeProfileObject;
    }
    
    /**
     * 利用中のRequestDispatcherを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のRequestDispatcher
     */
    public RequestDispatcher getRequestDispatcher() {
        return requestDispatcher;
    }
    
    /**
     * 利用中のMainLoopを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のMainLoop
     */
    public MainLoop getMainLoop() {
        return mainLoop;
    }
    
    /**
     * 利用中のSetGetRequestProcessorを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のSetGetRequestProcessor
     */
    public SetGetRequestProcessor getSetGetRequestProcessor() {
        return setGetRequestProcessor;
    }
    
    /**
     * 利用中のAnnounceRequestProcessorを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のAnnounceRequestProcessor
     */
    public AnnounceRequestProcessor getAnnounceRequestProcessor() {
        return announceRequestProcessor;
    }
    
    public ResultObserveProcessor getObserveServiceProsessor() {
        return observeServiceProcessor;
    }

    private NodeProfileInfo createNodeProfileInfo() {
        LOGGER.entering(CLASS_NAME, "createNodeProfileInfo");
        
        NodeProfileInfo nodeProfileInfo = new NodeProfileInfo();
        nodeProfileInfo.add(EPC.x97, true, false, false, 2);
        nodeProfileInfo.add(EPC.x98, true, false, false, 4);
        
        LOGGER.exiting(CLASS_NAME, "createNodeProfileInfo", nodeProfileInfo);
        return nodeProfileInfo;
    }

    private LocalObject createNodeProfileObject(Subnet subnet, LocalObjectManager manager, TransactionManager transactionManager) {
        LOGGER.entering(CLASS_NAME, "createNodeProfileObject", new Object[]{subnet, manager, transactionManager});
        
        LocalObject nodeProfile = new LocalObject(createNodeProfileInfo());
        nodeProfile.addDelegate(new LocalObjectDateTimeDelegate());
        nodeProfile.addDelegate(new NodeProfileObjectDelegate(manager));
        nodeProfile.addDelegate(new LocalObjectNotifyDelegate(subnet, transactionManager));
        
        LOGGER.exiting(CLASS_NAME, "createNodeProfileObject", nodeProfile);
        return nodeProfile;
    }
    
    private TransactionManager createTransactionManager(Subnet subnet) {
        LOGGER.entering(CLASS_NAME, "createTransactionManager", new Object[]{subnet});
        
        TransactionManager transactionManager = new TransactionManager(subnet);
        
        LOGGER.exiting(CLASS_NAME, "createTransactionManager", transactionManager);
        return transactionManager;
    }
    
    private RemoteObjectManager createRemoteObjectManager() {
        LOGGER.entering(CLASS_NAME, "createRemoteObjectManager");
        
        RemoteObjectManager remoteManager = new RemoteObjectManager();
        
        LOGGER.exiting(CLASS_NAME, "createRemoteObjectManager", remoteManager);
        return remoteManager;
    }
    
    private LocalObjectManager createLocalObjectManager() {
        LOGGER.entering(CLASS_NAME, "createLocalObjectManager");
        
        LocalObjectManager localManager = new LocalObjectManager();
        
        LOGGER.exiting(CLASS_NAME, "createLocalObjectManager", localManager);
        return localManager;
    }
    
    private RequestDispatcher createRequestDispatcher() {
        LOGGER.entering(CLASS_NAME, "createRequestDispatcher");
        
        RequestDispatcher requestDispatcher = new RequestDispatcher();
        
        LOGGER.exiting(CLASS_NAME, "createRequestDispatcher", requestDispatcher);
        return requestDispatcher;
    }
    
    private SetGetRequestProcessor createSetGetRequestProcessor(LocalObjectManager localManager) {
        LOGGER.entering(CLASS_NAME, "createSetGetRequestProcessor", new Object[]{localManager});
        
        SetGetRequestProcessor setGetRequestProcessor = new SetGetRequestProcessor(localManager);
        
        LOGGER.exiting(CLASS_NAME, "createSetGetRequestProcessor", setGetRequestProcessor);
        return setGetRequestProcessor;
    }

    private AnnounceRequestProcessor createAnnounceRequestProcessor(LocalObjectManager localManager, RemoteObjectManager remoteManager) {
        LOGGER.entering(CLASS_NAME, "createAnnounceRequestProcessor", new Object[]{localManager, remoteManager});
        
        AnnounceRequestProcessor announceRequestProcessor = new AnnounceRequestProcessor(localManager, remoteManager);
        
        LOGGER.exiting(CLASS_NAME, "createAnnounceRequestProcessor", announceRequestProcessor);
        return announceRequestProcessor;
    }

    private ResultObserveProcessor createObserveServiceProcessor() {
        LOGGER.entering(CLASS_NAME, "createObserveServiceProcessor");
        
        ResultObserveProcessor observeServiceProcessor = new ResultObserveProcessor();
        
        LOGGER.exiting(CLASS_NAME, "createObserveServiceProcessor", observeServiceProcessor);
        return observeServiceProcessor;
    }
    
    private MainLoop createMainLoop(Subnet subnet, RequestDispatcher requestDispatcher, TransactionManager transactionManager) {
        LOGGER.entering(CLASS_NAME, "createMainLoop", new Object[]{subnet, requestDispatcher, transactionManager});
        
        MainLoop mainLoop = new MainLoop();
        mainLoop.setSubnet(subnet);
        mainLoop.addListener(requestDispatcher);
        mainLoop.addListener(transactionManager);
        
        LOGGER.exiting(CLASS_NAME, "createMainLoop", mainLoop);
        return mainLoop;
    }
    
    /**
     * 利用中のMainLoopを実行するためのThreadを返す。initializeメソッドを呼び出すまではnullを返す。
     * このThreadのstartを呼び出すことで、echowandのサービスを開始する。
     * @return MainLoopを実行するためのThread
     */
    public Thread getMainThread() {
        return mainThread;
    }
    
    /**
     * EchowandServiceManagerが初期化済みであるか返す。
     * @return 初期化済みであればtrue、初期化済みでなければfalse
     */
    public boolean isInService() {
        return inService;
    }

    /**
     * ServiceManagerを初期化する。
     * @return 初期化が成功すればtrue、すでに初期化済みであればfalse
     * @throws HarmonyException 初期化中に例外が発生した場合
     */
    private void initialize() throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "initialize");
        
        transactionManager = createTransactionManager(subnet);
        remoteManager = createRemoteObjectManager();
        localManager = createLocalObjectManager();
        nodeProfileObject = createNodeProfileObject(subnet, localManager, transactionManager);

        setGetRequestProcessor = createSetGetRequestProcessor(localManager);
        announceRequestProcessor = createAnnounceRequestProcessor(localManager, remoteManager);
        observeServiceProcessor = createObserveServiceProcessor();

        requestDispatcher = createRequestDispatcher();
        requestDispatcher.addRequestProcessor(setGetRequestProcessor);
        requestDispatcher.addRequestProcessor(announceRequestProcessor);
        requestDispatcher.addRequestProcessor(observeServiceProcessor);
        
        for (LocalObjectConfig config: localObjectConfigs) {
            LocalObjectCreator creator = new LocalObjectCreator(config);
            LocalObjectCreatorResult result = creator.create(this);
            localObjectUpdaters.add(result.updater);
        }

        LOGGER.exiting(CLASS_NAME, "initialize");
    }
    
    private void startThreads() throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "startThreads");
        
        mainLoop = createMainLoop(subnet, requestDispatcher, transactionManager);

        localManager.add(nodeProfileObject);
        
        for (LocalObjectUpdater updater: localObjectUpdaters) {
            if (updater != null) {
                new Thread(updater).start();
            }
        }
        
        new Thread(mainLoop).start();
        
        LOGGER.exiting(CLASS_NAME, "startThreads");
    }
    
    public boolean startService() throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "startService");

        boolean result = true;
        
        if (inService) {
            result = false;
        }
        
        if (result) {
            initialize();
            startThreads();
            inService = true;
        }
        
        LOGGER.exiting(CLASS_NAME, "startService", result);
        return result;
    }
    
    public Service getService() {
        if (service == null) {
            service = new Service(this);
        }
        
        return service;
    }
}
