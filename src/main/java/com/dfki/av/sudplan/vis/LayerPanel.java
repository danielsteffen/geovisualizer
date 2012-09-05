/*
 *  LayerPanel.java 
 *
 *  Created by DFKI AV on 15.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import com.dfki.av.sudplan.wms.ElevatedRenderableLayer;
import com.dfki.av.sudplan.wms.ElevatedRenderableSupportLayer;
import com.dfki.av.sudplan.wms.WMSControlLayer;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.text.Position;
import javax.swing.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link JPanel} holding all available {@link Layer}.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerPanel extends javax.swing.JPanel implements PropertyChangeListener, MouseListener {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LayerPanel.class);
    /**
     * The {@link WorldWindow} that has all the information about the layers.
     */
    private final WorldWindow worldWindow;

    /**
     * Creates new form LayerPanel
     */
    public LayerPanel(final WorldWindow ww) {
        this.worldWindow = ww;

        initComponents();

        LayerCheckBoxNodeRenderer renderer = new LayerCheckBoxNodeRenderer();
        jTree1.setCellRenderer(renderer);

        LayerCheckBoxNodeEditor editor = new LayerCheckBoxNodeEditor(jTree1);
        jTree1.setCellEditor(editor);

        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree1.addMouseListener(this);

        updateTreeModel();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setAutoscrolls(true);
        jTree1.setEditable(true);
        jTree1.setRootVisible(false);
        jScrollPane1.setViewportView(jTree1);

        btnUp.setText(org.openide.util.NbBundle.getMessage(LayerPanel.class, "LayerPanel.btnUp.text")); // NOI18N
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });

        btnDown.setText(org.openide.util.NbBundle.getMessage(LayerPanel.class, "LayerPanel.btnDown.text")); // NOI18N
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });

        btnDelete.setText(org.openide.util.NbBundle.getMessage(LayerPanel.class, "LayerPanel.btnDelete.text")); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDown, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText(org.openide.util.NbBundle.getMessage(LayerPanel.class, "LayerPanel.jLabel2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        TreePath treePath1 = jTree1.getSelectionPath();
        if (treePath1 == null) {
            log.debug("No layer has been selected. Not moving a layer.");
            return;
        }

        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) treePath1.getLastPathComponent();
        LayerCheckBoxNode cbn = (LayerCheckBoxNode) dmtn.getUserObject();
        String layerName = cbn.getText();
        LayerList layerList = worldWindow.getModel().getLayers();
        Layer layer = layerList.getLayerByName(layerName);

        int index = layerList.indexOf(layer);
        if (index < layerList.size() - 1) {
            layerList.remove(index);
            layerList.add(index + 1, layer);
        } else {
            log.debug("Could not move layer down. Selected layer is last layer.");
        }

        TreePath treePath2 = jTree1.getNextMatch(layerName, 0, Position.Bias.Forward);
        jTree1.setSelectionPath(treePath2);
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        TreePath tp = jTree1.getSelectionPath();
        if (tp == null) {
            log.debug("No layer has been selected. Not moving a layer.");
            return;
        }

        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();
        LayerCheckBoxNode cbn = (LayerCheckBoxNode) dmtn.getUserObject();
        String layerName = cbn.getText();
        LayerList layerList = worldWindow.getModel().getLayers();
        Layer layer = layerList.getLayerByName(layerName);

        int index = layerList.indexOf(layer);
        if (index <= 0) {
            log.debug("Could not move layer up. Selected layer is first layer.");
        } else {
            layerList.remove(index);
            layerList.add(index - 1, layer);
        }

        TreePath treePath2 = jTree1.getNextMatch(layerName, 0, Position.Bias.Forward);
        jTree1.setSelectionPath(treePath2);
    }//GEN-LAST:event_btnDownActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        TreePath tp = jTree1.getSelectionPath();
        if (tp == null) {
            log.debug("No layer has been selected.");
            return;
        }

        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();
        LayerCheckBoxNode cbn = (LayerCheckBoxNode) dmtn.getUserObject();
        String layerName = cbn.getText();
        LayerList layerList = worldWindow.getModel().getLayers();
        Layer layer = layerList.getLayerByName(layerName);

        int index;
        
        if (layer instanceof ElevatedRenderableLayer) {
            index = layerList.indexOf(((ElevatedRenderableLayer) layer).getSupportLayer());
            if (index < 0) {
                log.debug("Could not remove layer.");
            } else {
                layerList.remove(index);
            }
        }
        if (layer instanceof WMSControlLayer) {
            for (ElevatedRenderableLayer l : ((WMSControlLayer) layer).getLayers()) {
                index = layerList.indexOf(l.getSupportLayer());
                if (index < 0) {
                    log.debug("Could not remove layer.");
                } else {
                    layerList.remove(index);
                }
                index = layerList.indexOf(l);
                if (index < 0) {
                    log.debug("Could not remove layer.");
                } else {
                    layerList.remove(index);
                }
            }
        }

        index = layerList.indexOf(layer);
        if (index < 0) {
            log.debug("Could not remove layer.");
        } else {
            layerList.remove(index);
        }



    }//GEN-LAST:event_btnDeleteActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnUp;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables

    private void updateTreeModel() {
        DefaultMutableTreeNode defaultLayerNode = new DefaultMutableTreeNode("Default Layer");
        TreeModel tm = new DefaultTreeModel(defaultLayerNode);

        if (worldWindow != null) {
            LayerList layerlist = worldWindow.getModel().getLayers();
            for (int id = layerlist.size() - 1; id >= 0; id--) {
                Layer layer = layerlist.get(id);
                if (layer instanceof ElevatedRenderableSupportLayer) {
                    continue;
                }
                if (layer instanceof ElevatedRenderableLayer) {
                    ElevatedRenderableLayer l = (ElevatedRenderableLayer) layer;
                    if (l.isSlave()) {
                        continue;
                    }
                }
                LayerCheckBoxNode node = new LayerCheckBoxNode(layer.getName(), layer.isEnabled());
                DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(node);
                defaultLayerNode.add(dmt);
            }
        } else {
            log.debug("WorldWindow equals null. Could not create layer tree.");
        }
        jTree1.setModel(tm);
        jTree1.setRootVisible(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AVKey.LAYERS)) {
            updateTreeModel();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int row = jTree1.getRowForLocation(x, y);
        TreePath path = jTree1.getPathForRow(row);

        if (path != null) {

            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            LayerCheckBoxNode node = (LayerCheckBoxNode) treeNode.getUserObject();
            LayerList layerList = worldWindow.getModel().getLayers();

            Layer layer = layerList.getLayerByName(node.getText());
            if (layer != null) {
                layer.setEnabled(!layer.isEnabled());
                worldWindow.redraw();
            } else {
                log.warn("Selected node {} not in layerlist.", node.getText());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
