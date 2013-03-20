/*
 *  LayerCheckBoxNodeEditor.java 
 *
 *  Created by DFKI AV on 20.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerCheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

    /**
     * The used {@link LayerCheckBoxNodeRenderer}.
     */
    private LayerCheckBoxNodeRenderer renderer;
    /**
     * The {@link JTree} used.
     */
    private JTree jTree;
    /*
     * set to null.??? 
     */
    ChangeEvent changeEvent = null;

    /**
     * The consturctor.
     */
    public LayerCheckBoxNodeEditor(JTree tree) {
        this.jTree = tree;
        this.renderer = new LayerCheckBoxNodeRenderer();
    }

    @Override
    public Object getCellEditorValue() {
        JCheckBox checkBox = renderer.getLeafRenderer();
        LayerCheckBoxNode checkBoxNode = new LayerCheckBoxNode(checkBox.getText(), checkBox.isSelected());
        return checkBoxNode;
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        boolean value = false;
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            int x = mouseEvent.getX();
            int y = mouseEvent.getY();
            TreePath path = jTree.getPathForLocation(x, y);
            if (path != null) {
                Object node = path.getLastPathComponent();
                if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    Object userObject = treeNode.getUserObject();
                    value = (treeNode.isLeaf() && (userObject instanceof LayerCheckBoxNode));
                }
            }
        }
        return value;
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
        Component editor = renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, leaf);

        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (stopCellEditing()) {
                    fireEditingStopped();
                }
            }
        };
        if (editor instanceof JCheckBox) {
            ((JCheckBox) editor).addItemListener(itemListener);
        }
        return editor;
    }
}
