package echowand.service;

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
import echowand.object.LocalObjectManager;
import echowand.object.RemoteObjectManager;
import echowand.object.SetGetRequestProcessor;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serviceを利用するために必要なechowandクラス群を管理
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
    
    private TimestampManager timestampManager;
    private TimestampObserver timestampObserver;
    private CaptureResultObserver captureResultObserver;
    
    private NodeProfileObjectConfig nodeProfileObjectConfig;
    private LinkedList<LocalObjectConfig> localObjectConfigs;
    private LinkedList<LocalObjectUpdater> localObjectUpdaters;
    
    private boolean initialized = false;
    private boolean inService = false;
    private boolean captureEnabled = false;
    
    private Thread mainLoopThread;
    
    private boolean managedSubnet;
    
    /**
     * Inet4Subnetを利用するCoreを作成する。
     * startServiceメソッドを呼び出すまでは、特に処理を行なわない。
     * @throws SubnetException Inet4Subnetの生成に失敗した場合
     */
    public Core() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "Core");
        
        subnet = new CaptureSubnet(new Inet4Subnet());
        managedSubnet = true;
        nodeProfileObjectConfig = new NodeProfileObjectConfig();
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "Core");
    }
    
    /**
     * 指定されたSubnetを利用するCoreを作成する。
     * startServiceメソッドを呼び出すまでは、特に処理を行なわない。
     * sunbetはこのCoreで管理され、startServiceとstopServiceが自動的に呼び出される。
     * @param subnet 構築するCoreが利用するsubnet
     */
    public Core(Subnet subnet) {
        LOGGER.entering(CLASS_NAME, "Core", subnet);
        
        this.subnet = subnet;
        managedSubnet = true;
        nodeProfileObjectConfig = new NodeProfileObjectConfig();
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "Core");
    }
    
    /**
     * 指定されたSubnetを利用するCoreを作成する。
     * startServiceメソッドを呼び出すまでは、特に処理を行なわない。
     * このCoreがSubnetの管理を行う場合にはmanagedSubnetにtrueを指定する。
     * @param subnet 構築するCoreが利用するSubnet
     * @param managedSubnet 指定されたSubnetを管理する場合にはtrue、そうでなければfalse
     */
    public Core(Subnet subnet, boolean managedSubnet) {
        LOGGER.entering(CLASS_NAME, "Core", new Object[]{subnet, managedSubnet});
        
        this.subnet = subnet;
        this.managedSubnet = managedSubnet;
        
        nodeProfileObjectConfig = new NodeProfileObjectConfig();
        localObjectConfigs = new LinkedList<LocalObjectConfig>();
        localObjectUpdaters = new LinkedList<LocalObjectUpdater>();
        
        LOGGER.exiting(CLASS_NAME, "Core");
    }
    
    /**
     * ノードプロファイルオブジェクト作成に利用するNodeProfileObjectConfigを返す。
     * @return ノードプロファイルオブジェクト作成に利用するNodeProfileObjectConfig
     */
    public NodeProfileObjectConfig getNodeProfileObjectConfig() {
        return nodeProfileObjectConfig;
    }
    
    /**
     * 指定されたLocalObjectConfigを追加する。
     * @param config 追加するLocalObjectConfig
     * @return 追加に成功したらtrue、そうでなければfalse
     */
    public boolean addLocalObjectConfig(LocalObjectConfig config) {
        LOGGER.entering(CLASS_NAME, "addLocalObjectConfig", config);
        
        boolean result = localObjectConfigs.add(config);
        
        LOGGER.exiting(CLASS_NAME, "addLocalObjectConfig", result);
        return result;
    }
    
    /**
     * 指定されたLocalObjectConfigを抹消する。
     * @param config 抹消するLocalObjectConfig
     * @return 抹消に成功したらtrue、そうでなければfalse
     */
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
    
    /**
     * 利用中のObserveResultProcessorを返す。initializeメソッドを呼び出すまではnullを返す。
     * @return 利用中のObserveResultProcessor
     */
    public ObserveResultProcessor getObserveResultProsessor() {
        return observeResultProcessor;
    }
    
    public TimestampManager getTimestampManager() {
        return timestampManager;
    }
    
    public TimestampObserver getTimestampObserver() {
        return timestampObserver;
    }
    
    public CaptureResultObserver getCaptureResultObserver() {
        return captureResultObserver;
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
    
    private TimestampObserver createTimestampObserver() {
        LOGGER.entering(CLASS_NAME, "createTimestampObserver");
        
        TimestampObserver timestampObserver = new TimestampObserver(timestampManager);
        
        LOGGER.exiting(CLASS_NAME, "createTimestampObserver", timestampObserver);
        return timestampObserver;
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
            if (creatorResult.updater != null) {
                localObjectUpdaters.add(creatorResult.updater);
            }
        }
    }
    
    private CaptureSubnet getCaptureSubnet() {
        if (subnet instanceof ExtendedSubnet) {
            return ((ExtendedSubnet)subnet).getSubnet(CaptureSubnet.class);
        } else {
            return null;
        }
    }

    /**
     * Coreを初期化する。
     * @return 初期化が成功すればtrue、すでに初期化済みであればfalse
     */
    public synchronized boolean initialize() {
        LOGGER.entering(CLASS_NAME, "initialize");
        
        if (initialized) {
            LOGGER.exiting(CLASS_NAME, "initialize", false);
            return false;
        }
        
        transactionManager = createTransactionManager(subnet);
        remoteManager = createRemoteObjectManager();
        localManager = createLocalObjectManager();

        setGetRequestProcessor = createSetGetRequestProcessor(localManager);
        announceRequestProcessor = createAnnounceRequestProcessor(localManager, remoteManager);
        observeResultProcessor = createObserveResultProcessor();

        requestDispatcher = createRequestDispatcher();
        requestDispatcher.addRequestProcessor(setGetRequestProcessor);
        requestDispatcher.addRequestProcessor(announceRequestProcessor);
        requestDispatcher.addRequestProcessor(observeResultProcessor);

        timestampManager = new TimestampManager();
        timestampObserver = createTimestampObserver();
        captureResultObserver = createCaptureResultObserver();

        CaptureSubnet captureSubnet = getCaptureSubnet();
        if (captureSubnet != null) {
            captureSubnet.addObserver(timestampObserver);
            captureSubnet.addObserver(captureResultObserver);
            captureEnabled = true;
        }
        
        try {
            LocalObjectCreatorResult creatorResult = new LocalObjectCreator(nodeProfileObjectConfig).create(this);
            nodeProfileObject = creatorResult.object;
            if (creatorResult.updater != null) {
                localObjectUpdaters.add(creatorResult.updater);
            }
        } catch (TooManyObjectsException ex) {
            LOGGER.logp(Level.WARNING, CLASS_NAME, "initialize", "cannot create NodeProfileObject", ex);
        }

        initialized = true;
        
        LOGGER.exiting(CLASS_NAME, "initialize", true);
        return true;
    }
    
    private void startUpdateThreads() {
        LOGGER.entering(CLASS_NAME, "startUpdateThreads");
        
        for (LocalObjectUpdater updater : localObjectUpdaters) {
            updater.start();
        }
        
        LOGGER.exiting(CLASS_NAME, "startUpdateThreads");
    }
    
    
    private void stopUpdateThreads() {
        LOGGER.entering(CLASS_NAME, "stopUpdateThreads");
        
        for (LocalObjectUpdater updater : localObjectUpdaters) {
            updater.terminate();
        }
        
        LOGGER.exiting(CLASS_NAME, "stopUpdateThreads");
    }
    
    private void startMainLoopThread() {
        LOGGER.entering(CLASS_NAME, "startMainLoopThread");
        
        mainLoop = createMainLoop(subnet, requestDispatcher, transactionManager);
        mainLoopThread = new Thread(mainLoop);
        mainLoopThread.start();
        
        LOGGER.exiting(CLASS_NAME, "startMainLoopThread");
    }
    
    private void stopMainLoopThread() {
        LOGGER.entering(CLASS_NAME, "stopMainLoopThread");
        
        mainLoopThread.interrupt();
        
        LOGGER.exiting(CLASS_NAME, "stopMainLoopThread");
    }
    
    private boolean startThreads() {
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
     * 初期化されていない場合には初期化を先に行う。
     * addLocalObjectConfigで登録されたローカルオブジェクトの生成と登録を行い、実行に必要なスレッドを開始する。
     * @return 実行が成功すればtrue、すでに実行済みであればfalse
     * @throws TooManyObjectsException ローカルオブジェクトの数が多すぎる場合
     * @throws SubnetException 実行に失敗した場合
     */
    public synchronized boolean startService() throws TooManyObjectsException, SubnetException {
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

        createLocalObjects();
        
        if (managedSubnet) {
            if (!subnet.startService()) {
                LOGGER.logp(Level.WARNING, CLASS_NAME, "startService", "subnet has already started");
            }
        }
        
        startThreads();

        LOGGER.exiting(CLASS_NAME, "startService", true);
        return true;
    }
    
    /**
     * Coreを停止する。
     * @return 停止に成功した場合にはtrue、すでに停止していた場合にはfalse
     * @throws SubnetException 停止に失敗した場合
     */
    public synchronized boolean stopService() throws SubnetException {
        LOGGER.entering(CLASS_NAME, "stopService");
        
        if (!inService) {
            LOGGER.exiting(CLASS_NAME, "stopService", false);
            return false;
        }
        
        stopUpdateThreads();
        stopMainLoopThread();
        
        if (managedSubnet) {
            if (!subnet.stopService()) {
                LOGGER.logp(Level.WARNING, CLASS_NAME, "startService", "has already stopped");
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "stopService", true);
        return true;
    }
}
