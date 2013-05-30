package echowand.object;

import echowand.common.EPC;

/**
 * LocalObjectの代理処理を実行するLocalObjectDelegateのサブクラスを表す。
 * このクラスを利用することにより、必要なメソッドの実装のみを行うことでLocalObjectDelegateのサブクラスの生成が可能となる。
 * @author Yoshiki Makino
 */
public class LocalObjectDefaultDelegate implements LocalObjectDelegate {
    
    /**
     * 指定されたEPCのプロパティデータをresultに設定して返す。
     * 特に処理を行わないダミーメソッドである。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータが要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     */
    @Override
    public void getData(GetState result, LocalObject object, EPC epc){}
    
    /**
     * 指定されたEPCのデータを指定されたデータの内容の変更をresultに設定して返す。
     * 特に処理を行わないダミーメソッドである。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータの変更を要求されているオブジェクト
     * @param epc 変更するプロパティデータのEPC
     * @param newData 新たに設定されるプロパティデータ
     * @param curData 現在のプロパティデータ
     */
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData){}
    
    /**
     * 指定されたEPCのプロパティが指定されたデータで更新された際の処理を行う。
     * 特に処理を行わないダミーメソッドである。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param curData 新たに設定されたプロパティデータ
     * @param oldData 以前設定されていたプロパティデータ
     */
    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData){}
}
