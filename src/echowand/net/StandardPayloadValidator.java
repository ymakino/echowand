package echowand.net;

import echowand.common.ESV;

/**
 * StandardPayloadの検証
 * @author Yoshiki Makino
 */
public class StandardPayloadValidator {
    /**
     * ペイロードに問題がないかを確認する
     * @param payload 確認を行うペイロード
     * @return 問題がなければtrue、そうでなければfalse
     */
    public boolean validate(StandardPayload payload) {
        return validateSEOJ(payload) &&
                validateDEOJ(payload) &&
                validateESV(payload) &&
                validateProperties(payload);
    }
    
    /**
     * 送信EOJに問題がないか確認する。
     * @param payload 確認を行うペイロード
     * @return 問題がなければtrue、そうでなければfalse
     */
    public boolean validateSEOJ(StandardPayload payload) {
        return payload.getSEOJ() != null;
    }
    
    /**
     * 宛先EOJに問題がないか確認する。
     * @param payload 確認を行うペイロード
     * @return 問題がなければtrue、そうでなければfalse
     */
    public boolean validateDEOJ(StandardPayload payload) {
        return payload.getDEOJ() != null;
    }
    
    /**
     * ペイロードのESVに問題がないか確認する。
     * @param payload 確認を行うペイロード
     * @return 問題がなければtrue、そうでなければfalse
     */
    public boolean validateESV(StandardPayload payload) {
        ESV esv = payload.getESV();
        return esv != null && esv != ESV.Invalid; 
    }
    
    private enum PropType {
        Invalid,
        Nothing,
        WithData,
        WithoutData,
        Mixture
    }

    private byte getOPC(boolean useFirst, StandardPayload payload) {
        if (useFirst) {
            return payload.getFirstOPC();
        } else {
            return payload.getSecondOPC();
        }
    }
    
    private Property getPropertyAt(int index, boolean useFirst, StandardPayload payload) {
        if (useFirst) {
            return payload.getFirstPropertyAt(index);
        } else {
            return payload.getSecondPropertyAt(index);
        }
    }
    
    private PropType nextPropType(PropType lastType, Property property) {
        PropType nextType = lastType;
        byte pdc = property.getPDC();
        switch (lastType) {
            case Nothing:
                if (pdc == 0) {
                    nextType = PropType.WithoutData;
                } else {
                    nextType = PropType.WithData;
                }
                break;
            case WithData:
                if (pdc == 0) {
                    nextType = PropType.Mixture;
                }
                break;
            case WithoutData:
                if (pdc != 0) {
                    nextType = PropType.Mixture;
                }
                break;
            case Mixture:
                nextType = PropType.Mixture;
                break;
            case Invalid:
                nextType = PropType.Invalid;
                break;
        }
        return nextType;
    }

    private PropType getPropType(boolean useFirst, StandardPayload payload) {
        PropType ret = PropType.Nothing;
        int len = getOPC(useFirst, payload);
        for (int i=0; i<len; i++) {
            Property property = getPropertyAt(i, useFirst, payload);
            
            if (property.getEPC().isInvalid()) {
                return PropType.Invalid;
            }
            
            if (property.getPDC() == 0 && !property.getEDT().isEmpty()) {
                return PropType.Invalid;
            }
            
            if (property.getPDC() != 0) {
                int l = (int)property.getPDC();
                if (property.getEDT().size() != l) {
                    return PropType.Invalid;
                }
            }
            
            ret = nextPropType(ret, property);
        }
        return ret;
    }
    
    private PropType getFirstPropType(StandardPayload payload) {
        return getPropType(true, payload);
    }
    
    private PropType getSecondPropType(StandardPayload payload) {
        return getPropType(false, payload);
    }
    
    
    /**
     * プロパティに問題がないか確認する。
     * @param payload 確認を行うペイロード
     * @return 問題がなければtrue、そうでなければfalse
     */
    public boolean validateProperties(StandardPayload payload) {
        if (payload.getESV() == null) {
            return false;
        }
        
        PropType t1 = getFirstPropType(payload);
        PropType t2 = getSecondPropType(payload);
        
        if (t1 == PropType.Invalid || t2 == PropType.Invalid) {
            return false;
        }
        
        switch (payload.getESV()) {
            case Invalid:
                return false;
            case SetI:
            case SetC:
            case Get_Res:
            case INF:
            case INFC:
                return (t1 == PropType.WithData && t2 == PropType.Nothing);
            case Get:
            case INF_REQ:
            case Set_Res:
            case INFC_Res:
                return (t1 == PropType.WithoutData && t2 == PropType.Nothing);
            case SetGet:
                return (t1 == PropType.WithData && t2 == PropType.WithoutData);
            case SetGet_Res:
                return (t1 == PropType.WithoutData && t2 == PropType.WithData);
            case SetI_SNA:
            case SetC_SNA:
            case Get_SNA:
            case INF_SNA:
            case SetGet_SNA:
                return true;
        }
        return  true; 
    }
}
