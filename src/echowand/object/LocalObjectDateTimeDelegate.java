package echowand.object;

import echowand.common.EPC;
import java.util.Calendar;

/**
 * 年月日と時刻をローカル時刻に設定するDelegate
 * @author Yoshiki Makino
 */
public class LocalObjectDateTimeDelegate implements LocalObjectDelegate {

    /**
     * 指定されたEPCが0x97の時は時刻を、0x98の時は年月日を返す。
     * それ以外の場合には処理を行わずにnullを返す。
     * @param object プロパティデータを要求されているオブジェクト
     * @param epc 要求プロパティデータのEPC
     * @return 時刻か年月日を表すデータ、それ以外の場合はnull
     */
    @Override
    public ObjectData getData(LocalObject object, EPC epc) {
        Calendar cal = Calendar.getInstance();
        switch (epc) {
            case x97:
                byte hour = (byte)cal.get(Calendar.HOUR_OF_DAY);
                byte minute = (byte)cal.get(Calendar.MINUTE);
                return new ObjectData(hour, minute);
            case x98:
                int year = cal.get(Calendar.YEAR);
                byte year1 = (byte)(((0x0000ff00) & year) >> 8);
                byte year2 = (byte)(0x000000ff & year);
                byte month = (byte)(cal.get(Calendar.MONTH) + 1);
                byte day = (byte)cal.get(Calendar.DAY_OF_MONTH);
                return new ObjectData(year1, year2, month, day);
            default:
                return null;
        }
    }
    
    /**
     * 何も処理せずにfalseを返す。
     * @param epc EPCの指定
     * @param data セットするデータの指定
     * @return 常にfalse
     */
    @Override
    public boolean setData(LocalObject object, EPC epc, ObjectData data) {
        return false;
    }

    /**
     * 何も処理を行わない。
     * @param object プロパティデータの変更通知を行っているオブジェクト
     * @param epc プロパティデータに変更のあったEPC
     * @param data 新しいプロパティデータ
     */
    @Override
    public void notifyDataChanged(LocalObject object, EPC epc, ObjectData data) {
    }
}
