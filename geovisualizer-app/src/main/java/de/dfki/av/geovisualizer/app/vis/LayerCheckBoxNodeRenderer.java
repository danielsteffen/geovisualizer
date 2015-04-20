/*
 * LayerCheckBoxNodeRenderer.java
 *
 * Created by DFKI AV on 20.06.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.vis;

import java.awt.Color;
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
 */
public class LayerCheckBoxNodeRenderer implements TreeCellRenderer {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LayerCheckBoxNodeRenderer.class);
    /**
     * The {@link JCheckBox} used for rendering the checkbox of leaf nodes.
     */
    private JCheckBox leafRenderer;
    /**
     * The {@link DefaultTreeCellRenderer} user for rendering non-leaf nodes.
     */
    private DefaultTreeCellRenderer nonLeafRenderer;

    /**
     * The constructor.
     */
    public LayerCheckBoxNodeRenderer() {
        this.leafRenderer = new JCheckBox();
        Color c = new Color(255, 255, 255, 0);
        this.leafRenderer.setBackground(c);
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
                    LOG.debug("User Object of type {}.", userObject.getClass());
                }
            } else {
                LOG.debug("value == null or not instance of DefaultMutableTreeNode");
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
