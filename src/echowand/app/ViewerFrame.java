package echowand.app;

import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.InstanceListRequestExecutor;
import echowand.object.RemoteObject;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JTextField;
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
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object[] objects = objectList.getSelectedValues();
                if (objects.length == 1) {
                    if (objectTable.getModel() != objectTableModel) {
                        objectTable.setModel(objectTableModel);
                        viewerMain.fixObjectTableColumnWidth(objectTable);
                        multipleObjectTableModel.setCachedObjects(new LinkedList<CachedRemoteObject>());
                    }
                    CachedRemoteObject cachedObject = new CachedRemoteObject((RemoteObject)objects[0]);
                    objectTableModel.setCachedObject(cachedObject);
                } else {
                    if (objectTable.getModel() != multipleObjectTableModel) {
                        objectTable.setModel(multipleObjectTableModel);
                        objectTableModel.setCachedObject(null);
                    }
                    LinkedList<CachedRemoteObject> cachedObjects = new LinkedList<CachedRemoteObject>();
                    for (int i=0; i<objects.length; i++) {
                        cachedObjects.add(new CachedRemoteObject((RemoteObject)objects[i]));
                    }
                    multipleObjectTableModel.setCachedObjects(cachedObjects);
                }
            }
        });
        
        KeyListener objectTableKeyListener = new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent ke) {
                int row = objectTable.getSelectedRow();
                if (objectTable.editCellAt(row, 5)) {
                    JTextField editingField = (JTextField) objectTable.getEditorComponent();
                    editingField.requestFocus();
                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if (objectTable.isEditing()) {
                    return;
                }
                
                int keyCode = ke.getKeyCode();
                if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE) {
                    int row = objectTable.getSelectedRow();
                    objectTable.editCellAt(row, 5);
                    objectTable.getEditorComponent().requestFocus();
                }
            }
        };
        objectTable.addKeyListener(objectTableKeyListener);
        
        updateNodeListModel();
        
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
            InstanceListRequestExecutor updater = viewerMain.createInstanceListRequestExecutor();

            try {
                updater.executeAndJoin();
            } catch (SubnetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (valid) {
                nodeListModel.updateNodes();
            }
            
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    nodeList.setSelectedIndex(0);
                }
            });
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
        reloadButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        reloadMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ECHONET Object Viewer");

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
        jScrollPane3.setViewportView(objectTable);

        jSplitPane1.setRightComponent(jScrollPane3);

        reloadButton.setText("Reload");
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        reloadMenuItem.setText("Reload");
        reloadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(reloadMenuItem);

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(reloadButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reloadButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void reloadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadMenuItemActionPerformed
        updateNodeListModel();
    }//GEN-LAST:event_reloadMenuItemActionPerformed

    private void mouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseClicked
        if (evt.getComponent() == objectList) {
            if (evt.getClickCount() == 2) {
                for (Object o : objectList.getSelectedValues()) {
                    viewerMain.openObjectTableFrame((RemoteObject)o);
                }
            }
        }
    }//GEN-LAST:event_mouseClicked

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        AbstractObjectTableModel tableModel = (AbstractObjectTableModel)objectTable.getModel();
        tableModel.refreshCache();
    }//GEN-LAST:event_reloadButtonActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JList nodeList;
    private javax.swing.JList objectList;
    private javax.swing.JTable objectTable;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JButton reloadButton;
    private javax.swing.JMenuItem reloadMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables
}
