package echowand.app;

import echowand.object.RemoteObject;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Yoshiki Makino
 */
public class ObjectListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        RemoteObject object = (RemoteObject)value;
        setText(object.getEOJ().toString());
        
        return this;
    }
    
}