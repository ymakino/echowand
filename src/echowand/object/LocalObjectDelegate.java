package echowand.object;

import echowand.common.EPC;
import java.util.LinkedList;

/**
 * LocalObjectの代理処理を実行するクラスを作成するためのインタフェースを表す。
 * @author Yoshiki Makino
 */
public interface LocalObjectDelegate {
    /**
     * Delegateの各メソッドの処理状態のスーパークラスを表す
     */
    static class State {
        private boolean done = false;
        private boolean fail = false;
        private LinkedList<String> messages = new LinkedList<String>();
        
        /**
         * 処理が完全に終了したことを設定する。
         * 処理が完全に終了した場合には、残りのDelegateの処理は行われない。
         */
        public void setDone() {
            done = true;
        }
        
        /**
         * 処理が完全に終了したかどうかを返す。
         * @return 処理が完全に終了している場合にはtrue、そうでなければfalse
         */
        public boolean isDone() {
            return done;
        }
        
        /**
         * 処理が失敗したことを設定する。
         * 処理が完全に終了するまで、残りのDelegateの処理は行われる。
         * 残りのDelegateの処理を行わないようにするためには、setDoneメソッドを呼び出す必要がある。
         */
        public void setFail() {
            fail = true;
        }
        
        /**
         * 処理が失敗したかどうかを返す。
         * @return 処理が失敗した場合にはtrue、そうでなければfalse
         */
        public boolean isFail() {
            return fail;
        }
        
        /**
         * 処理結果に関するメッセージを追加する。
         * 処理の失敗理由等を設定するために利用し、通常は利用する必要はない。
         * @param message 処理結果に関するメッセージ
         */
        public void addMessage(String message) {
            messages.add(message);
        }
        
        /**
         * 処理結果に関するメッセージの数を返す。
         * @return 処理結果に関するメッセージの数
         */
        public int countMessages() {
            return messages.size();
        }
        
        /**
         * 指定された順番に保存された処理結果に関するメッセージを返す。
         * @param index 処理結果に関するメッセージの順番の指定
         * @return 処理結果に関するメッセージ
         */
        public String getMessage(int index) {
            return messages.get(index);
        }
    }
    
    /**
     * DelegateのgetDataメソッドの処理状態を表す
     */
    static class GetState extends State {
        private ObjectData data;
        
        /**
         * DelegateのgetDataメソッドの処理状態を表すオブジェクトを生成する。
         * @param data 結果の値の初期値
         */
        public GetState(ObjectData data) {
            this.data = data;
        }
        
        /**
         * Getにより返される値を設定する
         * @param data Getにより返される値
         */
        public void setGetData(ObjectData data) {
            this.data = data;
        }
        
        /**
         * Getにより返される値を返す
         * @return Getにより返される値
         */
        public ObjectData getGetData() {
            return data;
        }
    }
    
    /**
     * DelegateのsetDataメソッドの処理状態を表す
     */
    static class SetState extends State {
        private ObjectData newData;
        private ObjectData curData;
        
        /**
         * DelegateのsetDataメソッドの処理状態を表すオブジェクトを生成する。
         * @param newData 新たに設定される値の初期値
         * @param curData 現在設定されている値の初期値
         */
        public SetState(ObjectData newData, ObjectData curData) {
            this.newData = newData;
            this.curData = curData;
        }
        
        /**
         * 新たに設定される値と現在設定されている値を更新する。
         * @param newData 新たに設定される値
         * @param curData 現在設定されている値
         */
        public void setSetData(ObjectData newData, ObjectData curData) {
            this.newData = newData;
            this.curData = curData;
        }
        
        /**
         * 新たに設定される値を更新する。
         * @param data 新たに設定される値
         */
        public void setNewData(ObjectData data) {
            newData = data;
        }
        
        /**
         * 新たに設定される値を返す。
         * @return 新たに設定される値
         */
        public ObjectData getNewData() {
            return newData;
        }
        
        /**
         * 現在設定されている値を更新する。
         * @param data 現在設定されている値
         */
        public void setCurrentData(ObjectData data) {
            curData = data;
        }
        
        /**
         * 現在設定されている値を返す。
         * @return 現在設定されている値
         */
        public ObjectData getCurrentData() {
            return curData;
        }

        
        /**
         * 値に変更があったかどうかを返す。
         * @return 値に変更があった場合にはtrue、そうでなければfalse
         */
        public boolean isDataChanged() {
            if (curData == null) {
                return newData != null;
            } else {
                return !curData.equals(newData);
            }
        }
    }

    /**
     * DelegateのnotifyDataChangedメソッドの処理状態を表す
     */
    static class NotifyState extends State {
    }
    
    /**
     * 指定されたEPCのプロパティデータをresultに設定して返す。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータが要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     */
    void getData(GetState result, LocalObject object, EPC epc);
    
    /**
     * 指定されたEPCのデータを指定されたデータの内容の変更をresultに設定して返す。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータの変更を要求されているオブジェクト
     * @param epc 変更するプロパティデータのEPC
     * @param newData 新たに設定されるプロパティデータ
     * @param curData 現在のプロパティデータ
     */
    void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData);
    
    /**
     * 指定されたEPCのプロパティが指定されたデータで更新された際の処理を行う。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param curData 新たに設定されたプロパティデータ
     * @param oldData 以前設定されていたプロパティデータ
     */
    void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData);
}
