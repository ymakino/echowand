package echowand.service;

import echowand.common.EPC;
import echowand.info.NodeProfileInfo;
import echowand.info.PropertyInfo;
import echowand.object.LocalObjectDateTimeDelegate;
import echowand.object.NodeProfileObjectDelegate;
import echowand.util.Constraint;
import java.util.logging.Logger;

/**
 * CoreがNodeProfileObjectを生成するための設定
 * @author ymakino
 */
public class NodeProfileObjectConfig extends LocalObjectConfig {
    private static final Logger LOGGER = Logger.getLogger(NodeProfileObjectConfig.class.getName());
    private static final String CLASS_NAME = NodeProfileObjectConfig.class.getName();
    
    private NodeProfileInfo nodeProfileInfo;
    
    private LazyConfiguration dateTimeConfiguration;
    private LazyConfiguration nodeProfileConfiguration;
    
    /**
     * NodeProfileObjectConfigを生成する。
     */
    public NodeProfileObjectConfig() {
        super(new NodeProfileInfo());
        nodeProfileInfo = (NodeProfileInfo)getObjectInfo();
        
        dateTimeConfiguration = new LazyConfiguration() {
            @Override
            public void configure(LocalObjectConfig config, Core core) {
                nodeProfileInfo.add(EPC.x97, true, false, false, 2);
                nodeProfileInfo.add(EPC.x98, true, false, false, 4);
                config.addDelegate(new LocalObjectDateTimeDelegate());
            }
        };
        
        nodeProfileConfiguration = new LazyConfiguration() {
            @Override
            public void configure(LocalObjectConfig config, Core core) {
                addDelegate(new NodeProfileObjectDelegate(core.getLocalObjectManager()));
            }
        };
        
        addLazyConfiguration(dateTimeConfiguration);
        addLazyConfiguration(nodeProfileConfiguration);
    }
    
    /**
     * LocalObjectDateTimeDelegateの利用を有効にする。
     */
    public void enableDateTimeDelegate() {
        if (!isDateTimeDelegateEnabled()) {
            addLazyConfiguration(dateTimeConfiguration);
        }
    }
    
    
    /**
     * LocalObjectDateTimeDelegateの利用を無効にする。
     */
    public void disableDateTimeDelegate() {
        removeLazyConfiguration(dateTimeConfiguration);
    }
    
    /**
     * LocalObjectDateTimeDelegateを利用するかどうかを返す。
     * @return LocalObjectDateTimeDelegateを利用する場合にはtrue、そうでなければfalse
     */
    public boolean isDateTimeDelegateEnabled() {
        return containsLazyConfiguration(dateTimeConfiguration);
    }
    
    /**
     * NodeProfileObjectDelegateの利用を有効にする。
     */
    public void enableNodeProfileDelegate() {
        if (!isNodeProfileDelegateEnabled()) {
            addLazyConfiguration(nodeProfileConfiguration);
        }
    }
    
    /**
     * NodeProfileObjectDelegateの利用を無効にする。
     */
    public void disableNodeProfileDelegate() {
        removeLazyConfiguration(nodeProfileConfiguration);
    }
    
    /**
     * NodeProfileObjectDelegateを利用するかどうかを返す。
     * @return NodeProfileDelegateを利用する場合にはtrue、そうでなければfalse
     */
    public boolean isNodeProfileDelegateEnabled() {
        return containsLazyConfiguration(nodeProfileConfiguration);
    }
    
    /**
     * このNodeProfileObjectConfigにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param size プロパティのデータサイズ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean addProperty(EPC epc, boolean gettable, boolean settable, boolean observable, int size) {
        return nodeProfileInfo.add(epc, gettable, settable, observable, size);
    }
    
    /**
     * このNodeProfileObjectConfigにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param size プロパティのデータサイズ
     * @param constraint プロパティの制約
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean addProperty(EPC epc, boolean gettable, boolean settable, boolean observable, int size, Constraint constraint) {
        return nodeProfileInfo.add(epc, gettable, settable, observable, size, constraint);
    }
    
    /**
     * このNodeProfileObjectConfigにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param data プロパティのデータ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean addProperty(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data) {
        return nodeProfileInfo.add(epc, gettable, settable, observable, data);
    }
    
    /**
     * このNodeProfileObjectConfigにプロパティを追加する。
     * @param epc 追加するプロパティのEPC
     * @param gettable Setの可否
     * @param settable Getの可否
     * @param observable 通知の有無
     * @param constraint プロパティの制約
     * @param data プロパティのデータ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean addProperty(EPC epc, boolean gettable, boolean settable, boolean observable, byte[] data, Constraint constraint) {
        return nodeProfileInfo.add(epc, gettable, settable, observable, data, constraint);
    }
    
    /**
     * このNodeProfileObjectConfigにプロパティを追加する。
     * @param prop 追加するプロパティ
     * @return 追加が成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean addProperty(PropertyInfo prop) {
        return nodeProfileInfo.add(prop);
    }
    
    /**
     * このNodeProfileObjectConfigからプロパティを削除する。
     * @param epc 削除するプロパティのEPC
     * @return 削除が成功した場合にはtrue、失敗した場合にはfalse
     */
    public boolean removeProperty(EPC epc) {
        return nodeProfileInfo.remove(epc);
    }
}
