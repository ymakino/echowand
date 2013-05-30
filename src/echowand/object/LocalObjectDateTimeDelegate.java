package echowand.object;

import echowand.common.EPC;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * 年月日と時刻をローカル時刻に設定するDelegate
 * @author Yoshiki Makino
 */
public class LocalObjectDateTimeDelegate implements LocalObjectDelegate {
    private static final Logger logger = Logger.getLogger(LocalObjectDateTimeDelegate.class.getName());
    private static final String className = LocalObjectDateTimeDelegate.class.getName();

    /**
     * 指定されたEPCが0x97の時は時刻を、0x98の時は年月日を返す。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータを要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     */
    @Override
    public void getData(GetState result, LocalObject object, EPC epc) {
        logger.entering(className, "getData", new Object[]{result, object, epc});
        
        Calendar cal = Calendar.getInstance();
        
        switch (epc) {
            case x97:
                byte hour = (byte)cal.get(Calendar.HOUR_OF_DAY);
                byte minute = (byte)cal.get(Calendar.MINUTE);
                result.setGetData(new ObjectData(hour, minute));
                break;
            case x98:
                int year = cal.get(Calendar.YEAR);
                byte year1 = (byte)(((0x0000ff00) & year) >> 8);
                byte year2 = (byte)(0x000000ff & year);
                byte month = (byte)(cal.get(Calendar.MONTH) + 1);
                byte day = (byte)cal.get(Calendar.DAY_OF_MONTH);
                result.setGetData(new ObjectData(year1, year2, month, day));
                break;
        }
        
        logger.exiting(className, "getData");
    }
    
    /**
     * 特に処理は行わない。
     * @param result 処理状態を表すオブジェクト
     * @param epc EPCの指定
     * @param newData 設定するプロパティデータ
     * @param curData 現在のプロパティデータ
     */
    @Override
    public void setData(SetState result, LocalObject object, EPC epc, ObjectData newData, ObjectData curData) {
    }

    /**
     * 特に処理は行わない。
     * @param result 処理状態を表すオブジェクト
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param curData 新たに設定されたプロパティデータ
     * @param oldData 以前設定されていたプロパティデータ
     */
    @Override
    public void notifyDataChanged(NotifyState result, LocalObject object, EPC epc, ObjectData curData, ObjectData oldData) {
    }
}
