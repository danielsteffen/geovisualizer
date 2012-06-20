/*
 *  LayerCheckBoxNodeRenderer.java 
 *
 *  Created by DFKI AV on 20.06.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.vis;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renderer for the checkboxes of the leaf nodes of the tree.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class LayerCheckBoxNodeRenderer implements TreeCellRenderer {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(LayerCheckBoxNodeRenderer.class);
    /**
     * The {@link JCheckBox} used for rendering the checkbox of leaf nodes.
     */
    private JCheckBox leafRenderer;
    /**
     * The {@link DefaultTreeCellRenderer} user for rendering non-leaf nodes.
     */
    private DefaultTreeCellRenderer nonLeafRenderer;

    /**
     * The consturctor.
     */
    public LayerCheckBoxNodeRenderer() {
        this.leafRenderer = new JCheckBox();
        this.nonLeafRenderer = new DefaultTreeCellRenderer();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {

        Component component;

        if (leaf) {

            String sValue = tree.convertValueToText(value, selected, expanded,
                    leaf, row, hasFocus);
            leafRenderer.setText(sValue);
            leafRenderer.setSelected(false);

            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
                Object userObject = treeNode.getUserObject();
                if (userObject instanceof LayerCheckBoxNode) {
                    LayerCheckBoxNode node = (LayerCheckBoxNode) userObject;
                    leafRenderer.setText(node.getText());
                    leafRenderer.setSelected(node.isSelected());
                } else {
                    log.debug("User Object of type {}.", userObject.getClass());
                }
            } else {
            }
            
            leafRenderer.setEnabled(tree.isEnabled());

            component = leafRenderer;
        } else {
            component = nonLeafRenderer.getTreeCellRendererComponent(tree, value,
                    leaf, expanded, leaf, row, hasFocus);
        }

        return component;
    }

    /**
     * Returns the renderer for the leaf nodes of a tree.
     *
     * @return the leafRenderer to return.
     */
    public JCheckBox getLeafRenderer() {
        return leafRenderer;
    }
}
