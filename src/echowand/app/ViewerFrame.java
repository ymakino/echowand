package echowand.app;

import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.InstanceListRequestExecutor;
import echowand.object.RemoteObject;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Yoshiki Makino
 */
public class ViewerFrame extends javax.swing.JFrame {

    private ObjectViewer viewerMain;
    private NodeListModel nodeListModel;
    private ObjectListModel objectListModel;

    /**
     * Creates new form ViewerFrame
     */
    public ViewerFrame(final ObjectViewer viewerMain) {
        this.viewerMain = viewerMain;

        initComponents();

        nodeListModel = viewerMain.createNodeListModel();
        nodeList.setModel(nodeListModel);
        
        objectListModel = viewerMain.createObjectListModel();
        objectList.setModel(objectListModel);
        
        final ObjectTableModel objectTableModel = new ObjectTableModel();
        final MultipleObjectTableModel multipleObjectTableModel = new MultipleObjectTableModel();
        
        objectTable.setModel(objectTableModel);
        
        final HashMap<Component, AdjacentComponents> adjacents;
        adjacents = new HashMap<Component, AdjacentComponents>();
        adjacents.put(nodeList, new AdjacentComponents(null, objectList));
        adjacents.put(objectList, new AdjacentComponents(nodeList, objectTable));
        adjacents.put(objectTable, new AdjacentComponents(objectList, null));

        adjacents.get(objectTable).setRequestLeftFocus(new AdjacentComponentsRequestFocus() {
            @Override
            public void requestFocus(Component component) {
                if (objectTable.getSelectedColumn() <= 0) {
                    objectList.requestFocus();
                }
            }
        });
        
        nodeList.addKeyListener(new AdjacentComponentsKeyListener(adjacents.get(nodeList)));
        objectList.addKeyListener(new AdjacentComponentsKeyListener(adjacents.get(objectList)));
        objectTable.addKeyListener(new AdjacentComponentsKeyListener(adjacents.get(objectTable)));

        nodeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object[] objects = nodeList.getSelectedValues();
                LinkedList<Node> nodes = new LinkedList<Node>();
                for (int i=0; i<objects.length; i++) {
                    nodes.add((Node)objects[i]);
                }
                objectListModel.selectNodes(nodes);
                objectList.updateUI();
            }
        });
        
        objectList.addListSelectionListener(new ListSelectionListener() {
            
            private void updateModelWith(Object object) {
                if (objectTable.getModel() != objectTableModel) {
                    objectTable.setModel(objectTableModel);
                    viewerMain.fixObjectTableColumnWidth(objectTable);
                    multipleObjectTableModel.setCachedObjects(new LinkedList<CachedRemoteObject>());
                }
                
                CachedRemoteObject cachedObject = null;
                if (object != null) {
                    cachedObject = new CachedRemoteObject((RemoteObject)object);
                }
                
                objectTableModel.setCachedObject(cachedObject);
            }

            private void updateModelWithMultiple(Object[] objects) {
                LinkedList<CachedRemoteObject> cachedObjects = new LinkedList<CachedRemoteObject>();
                for (int i = 0; i < objects.length; i++) {
                    cachedObjects.add(new CachedRemoteObject((RemoteObject) objects[i]));
                }
                multipleObjectTableModel.setCachedObjects(cachedObjects);
                
                if (objectTable.getModel() != multipleObjectTableModel) {
                    objectTable.setModel(multipleObjectTableModel);
                    objectTableModel.setCachedObject(null);
                }
                
                viewerMain.fixMultipleObjectTableColumnWidth(objectTable);
            }
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object[] objects = objectList.getSelectedValues();
                switch (objects.length) {
                    case 0:
                        updateModelWith(null);
                        break;
                    case 1:
                        updateModelWith(objects[0]);
                        break;
                    default:
                        updateModelWithMultiple(objects);
                        break;
                }
            }
        });
        
        viewerMain.fixObjectTableColumnWidth(objectTable);
        
        viewerMain.setObjectTableRenderer(objectTable);
        viewerMain.setObjectTableEditor(objectTable);
    }
    
    class UpdateNodeListModelThread extends Thread {

        private boolean valid = true;

        public void invalidate() {
            this.valid = false;
        }

        @Override
        public void run() {
            while (valid) {
                InstanceListRequestExecutor updater = viewerMain.createInstanceListRequestExecutor();

                try {
                    updater.execute();
                    updater.join();
                } catch (SubnetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                javax.swing.SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        nodeListModel.updateNodes();
                        objectListModel.updateObjects();
                    }
                });

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ViewerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private UpdateNodeListModelThread updateNodeListModelThread;
    
    private void updateNodeListModel() {
        if (updateNodeListModelThread != null) {
            updateNodeListModelThread.invalidate();
        }
        
        updateNodeListModelThread = new UpdateNodeListModelThread();
        updateNodeListModelThread.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        nodeList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        objectList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        objectTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ECHONET Object Viewer");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel3.setMinimumSize(new java.awt.Dimension(46, 100));
        jPanel3.setLayout(new java.awt.GridLayout(1, 3));

        nodeList.setCellRenderer(new echowand.app.NodeListCellRenderer());
        nodeList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nodeListFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(nodeList);

        jPanel3.add(jScrollPane1);

        objectList.setCellRenderer(new echowand.app.ObjectListCellRenderer());
        objectList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ViewerFrame.this.mouseClicked(evt);
            }
        });
        objectList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                objectListFocusGained(evt);
            }
        });
        jScrollPane2.setViewportView(objectList);

        jPanel3.add(jScrollPane2);

        jSplitPane1.setTopComponent(jPanel3);

        objectTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        objectTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                objectTableFocusGained(evt);
            }
        });
        objectTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                objectTableKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(objectTable);

        jSplitPane1.setRightComponent(jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (evt.getComponent() == objectList) {
            if (evt.getClickCount() == 2) {
                for (Object o : objectList.getSelectedValues()) {
                    viewerMain.openObjectTableFrame((RemoteObject)o);
                }
            }
        }
    }//GEN-LAST:event_mouseClicked

    private void nodeListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nodeListFocusGained
        if (nodeList.getSelectedIndex() == -1) {
            nodeList.setSelectedIndex(0);
        }
    }//GEN-LAST:event_nodeListFocusGained

    private void objectListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_objectListFocusGained
        if (objectList.getSelectedIndex() == -1) {
            objectList.setSelectedIndex(0);
        }
    }//GEN-LAST:event_objectListFocusGained

    private void objectTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_objectTableFocusGained
        if (objectTable.getSelectedRow() == -1) {
            objectTable.changeSelection(0, 0, true, false);
        }
    }//GEN-LAST:event_objectTableFocusGained

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        if (updateNodeListModelThread == null) {
            updateNodeListModelThread = new UpdateNodeListModelThread();
            updateNodeListModelThread.start();
        }
    }//GEN-LAST:event_formWindowActivated

    private void objectTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_objectTableKeyPressed
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_R) {
            AbstractObjectTableModel tableModel = (AbstractObjectTableModel) objectTable.getModel();
            tableModel.refreshCache();
        }
    }//GEN-LAST:event_objectTableKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JList nodeList;
    private javax.swing.JList objectList;
    private javax.swing.JTable objectTable;
    // End of variables declaration//GEN-END:variables
}
