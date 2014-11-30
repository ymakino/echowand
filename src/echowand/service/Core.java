package echowand.service;

import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.logic.Listener;
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
public class Core {
    private static final Logger LOGGER = Logger.getLogger(Core.class.getName());
    private static final String CLASS_NAME = Core.class.getName();
    
    private Subnet subnet;
    private TransactionManager transactionManager;
    private RemoteObjectManager remoteManager;
    private LocalObjectManager localManager;
    private LocalObject nodeProfileObject;
    private RequestDispatcher requestDispatcher;
    private MainLoop mainLoop;
    private SetGetRequestProcessor setGetRequestProcessor;
    private AnnounceRequestProcessor announceRequestProcessor;
    private ObserveResultProcessor observeResultProcessor;
    private CaptureResultObserver captureResultObserver;
    
    private LinkedList<LocalObjectConfig> localObjectConfigs;
    private LinkedList<LocalObjectUpdater> localObjectUpdaters;
    
    private boolean initialized = false;
    private boolean inService = false;
    private boolean captureEnabled = false;
    
    /**
     * Inet4Subnetを利用するCoreを作成する。
     * startServiceメソッドを呼び出すまでは、特に処理を行なわない。
     * @throws SubnetException Inet4Subnetの生成に失敗した場合
     */
    public Core() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Core");
        
        this.subnet = new CaptureSubnet(Inet4Subnet.startSubnet());
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "Core");
    }
    
    /**
     * 指定されたSubnetを利用するCoreを作成する。
     * startServiceメソッドを呼び出すまでは、特に処理を行なわない。
     * @param subnet 構築するCoreが利用するsubnet
     */
    public Core(Subnet subnet) {
        LOGGER.entering(CLASS_NAME, "Core", subnet);
        
        this.subnet = subnet;
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "Core");
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
    
    public ObserveResultProcessor getObserveResultProsessor() {
        return observeResultProcessor;
    }
    
    public CaptureResultObserver getCaptureResultObserver() {
        return captureResultObserver;
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

    private ObserveResultProcessor createObserveResultProcessor() {
        LOGGER.entering(CLASS_NAME, "createObserveResultProcessor");
        
        ObserveResultProcessor observeResultProcessor = new ObserveResultProcessor();
        
        LOGGER.exiting(CLASS_NAME, "createObserveResultProcessor", observeResultProcessor);
        return observeResultProcessor;
    }
    
    private CaptureResultObserver createCaptureResultObserver() {
        LOGGER.entering(CLASS_NAME, "createCaptureResultObserver");
        
        CaptureResultObserver captureResultObserver = new CaptureResultObserver();
        
        LOGGER.exiting(CLASS_NAME, "createCaptureResultObserver", captureResultObserver);
        return captureResultObserver;
    }
    
    private MainLoop createMainLoop(Subnet subnet, Listener... listeners) {
        LOGGER.entering(CLASS_NAME, "createMainLoop", new Object[]{subnet, listeners});
        
        MainLoop mainLoop = new MainLoop();
        mainLoop.setSubnet(subnet);
        for (Listener listener: listeners) {
            mainLoop.addListener(listener);
        }
        
        LOGGER.exiting(CLASS_NAME, "createMainLoop", mainLoop);
        return mainLoop;
    }
    
    /**
     * Coreが初期化済みであるか返す。
     * @return 初期化済みであればtrue、初期化済みでなければfalse
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Coreが実行中であるか返す。
     * @return 実行中であればtrue、実行中でなければfalse
     */
    public boolean isInService() {
        return inService;
    }
    
    public boolean isCaptureEnabled() {
        return captureEnabled;
    }
    
    private void createLocalObjects() throws TooManyObjectsException {
        for (LocalObjectConfig config : localObjectConfigs) {
            LocalObjectCreator creator = new LocalObjectCreator(config);
            LocalObjectCreatorResult creatorResult = creator.create(this);
            localObjectUpdaters.add(creatorResult.updater);
        }
    }

    /**
     * Coreを初期化する。
     * @return 初期化が成功すればtrue、すでに初期化済みであればfalse
     * @throws HarmonyException 初期化中に例外が発生した場合
     */
    public synchronized boolean initialize() throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "initialize");
        
        if (initialized) {
            LOGGER.exiting(CLASS_NAME, "initialize", false);
            return false;
        }

        transactionManager = createTransactionManager(subnet);
        remoteManager = createRemoteObjectManager();
        localManager = createLocalObjectManager();
        nodeProfileObject = createNodeProfileObject(subnet, localManager, transactionManager);

        setGetRequestProcessor = createSetGetRequestProcessor(localManager);
        announceRequestProcessor = createAnnounceRequestProcessor(localManager, remoteManager);
        observeResultProcessor = createObserveResultProcessor();

        requestDispatcher = createRequestDispatcher();
        requestDispatcher.addRequestProcessor(setGetRequestProcessor);
        requestDispatcher.addRequestProcessor(announceRequestProcessor);
        requestDispatcher.addRequestProcessor(observeResultProcessor);

        captureResultObserver = createCaptureResultObserver();

        if (subnet instanceof CaptureSubnet) {
            ((CaptureSubnet) subnet).addObserver(captureResultObserver);
            captureEnabled = true;
        }

        localManager.add(nodeProfileObject);

        createLocalObjects();

        initialized = true;

        LOGGER.exiting(CLASS_NAME, "initialize", true);
        return true;
    }
    
    private void startUpdateThreads() {
        for (LocalObjectUpdater updater : localObjectUpdaters) {
            if (updater != null) {
                new Thread(updater).start();
            }
        }
    }
    
    private void startMainLoopThread() {
        mainLoop = createMainLoop(subnet, requestDispatcher, transactionManager);
        new Thread(mainLoop).start();
    }
    
    public synchronized boolean startThreads() {
        LOGGER.entering(CLASS_NAME, "startThreads");
        
        if (inService) {
            LOGGER.exiting(CLASS_NAME, "startThreads", false);
            return false;
        }

        if (!isInitialized()) {
            LOGGER.exiting(CLASS_NAME, "startThreads", false);
            return false;
        }
        
        startUpdateThreads();
        
        startMainLoopThread();

        inService = true;
        
        LOGGER.exiting(CLASS_NAME, "startThreads", true);
        return true;
    }
    
    /**
     * Coreを実行する。
     * @return 実行が成功すればtrue、すでに実行済みであればfalse
     * @throws TooManyObjectsException ローカルオブジェクトの数が多すぎる場合
     */
    public synchronized boolean startService() throws TooManyObjectsException {
        LOGGER.entering(CLASS_NAME, "startService");
        
        if (inService) {
            LOGGER.exiting(CLASS_NAME, "startService", false);
            return false;
        }

        if (!isInitialized()) {
            boolean result = initialize();
            if (!result) {
                LOGGER.exiting(CLASS_NAME, "startService", false);
            }
        }
        
        startThreads();

        LOGGER.exiting(CLASS_NAME, "startService", true);
        return true;
    }
}
