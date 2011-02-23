/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SimpleLayerControl.java
 *
 * Created on 25.01.2011, 12:36:20
 */
package com.dfki.av.sudplan.ui;

import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.layer.ElevationLayer;
import com.dfki.av.sudplan.layer.Layer;
import com.dfki.av.sudplan.layer.LayerManager;
import com.dfki.av.sudplan.layer.LayerSelectionEvent;
import com.dfki.av.sudplan.layer.LayerSelectionListener;
import com.dfki.av.sudplan.layer.LayerStateEvent;
import com.dfki.av.sudplan.layer.ShapeLayer;
import com.dfki.av.sudplan.util.IconUtil;
import com.dfki.av.sudplan.layer.LayerListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 */
public class SimpleLayerPanel extends javax.swing.JPanel implements
        LayerListener,
        TreeModelListener,
        TreeSelectionListener {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Workaround use Checkboxes from look & feel or render them.
    public static final ImageIcon CHECKBOX_CHECKED_ICON = new javax.swing.ImageIcon(ElevationLayer.class.getResource("/com/dfki/av/sudplan/ui/icon/workaround/checkboxChecked.png"));
    public static final ImageIcon CHECKBOX_NOT_CHECKED_ICON = new javax.swing.ImageIcon(ElevationLayer.class.getResource("/com/dfki/av/sudplan/ui/icon/workaround/checkBoxNotChecked.png"));
    private final Logger logger = LoggerFactory.getLogger(SimpleLayerPanel.class);
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Layer");
    private final DefaultMutableTreeNode elevationNode = new DefaultMutableTreeNode("Elevation");
    private final DefaultMutableTreeNode shapeNode = new DefaultMutableTreeNode("Shapefile");
    private final DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode("Other");
    private final DefaultTreeModel layerTreeModel = new DefaultTreeModel(rootNode);
    private final LayerManager layerManager;
    private final ArrayList<LayerSelectionListener> layerSelectionListener = new ArrayList<LayerSelectionListener>();
    private final HashMap<Layer, LayerNode> layerToNodeMap = new HashMap<Layer, LayerNode>();
    private final JPopupMenu layerPopup = new JPopupMenu();
    private final PopupListener popupListener = new PopupListener(layerPopup);

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: World Wind has almost one to one the same implementation
    // copied it ref it (LayerTree,LayerNode,LayerTreeNode,WMSPanel,LayerManagerPanel)
    /** Creates new form SimpleLayerControl */
    public SimpleLayerPanel(LayerManager layerManager) {
        this.layerManager = layerManager;
        logger.debug("{} Constructor() call", SimpleLayerPanel.class.toString());
        initComponents();
        layerTree.setModel(layerTreeModel);
        layerTree.expandPath(new TreePath(rootNode.getPath()));
        layerTree.setCellRenderer(new LayerTreeCellRenderer());
        layerTree.setCellEditor(new LayerTreeCellEditor(layerTree, (LayerTreeCellRenderer) layerTree.getCellRenderer()));
        layerTree.setEditable(true);
        layerTree.getSelectionModel().addTreeSelectionListener(this);
        layerTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        layerTreeModel.addTreeModelListener(this);
        layerTree.addMouseListener(popupListener);
        //this is necessary that the contextmenu will be shown on awt components --> http://java.sun.com/products/jfc/tsc/articles/mixing/        
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:problems with shadow of popup and repaint of awt
        layerPopup.setLightWeightPopupEnabled(false);
        createPopupMenuItems();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        layerTreeScrollPane = new javax.swing.JScrollPane();
        layerTree = new javax.swing.JTree();

        layerTreeScrollPane.setBorder(null);

        layerTree.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        layerTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        layerTreeScrollPane.setViewportView(layerTree);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 304, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(layerTreeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 453, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(layerTreeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree layerTree;
    private javax.swing.JScrollPane layerTreeScrollPane;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void layerAdded(final Layer addedLayer) {
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:redundandcode --> extract nodes
        if (addedLayer != null) {
            final LayerNode newLayerNode = new LayerNode(addedLayer);
            if (addedLayer instanceof ElevationLayer) {
                if (elevationNode.getChildCount() == 0) {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:postioning of Layers DEM always deeper than objects 
                    layerTreeModel.insertNodeInto(elevationNode, rootNode, rootNode.getChildCount());
                    layerTree.expandPath(new TreePath(elevationNode.getPath()));
                }
                layerTreeModel.insertNodeInto(
                        newLayerNode,
                        elevationNode,
                        elevationNode.getChildCount());
                if (elevationNode.getChildCount() != 0) {
                    layerTree.expandPath(new TreePath(elevationNode.getPath()));
                }
            } else if (addedLayer instanceof ShapeLayer) {
                if (shapeNode.getChildCount() == 0) {
                    layerTreeModel.insertNodeInto(shapeNode, rootNode, rootNode.getChildCount());
                }
                layerTreeModel.insertNodeInto(
                        newLayerNode,
                        shapeNode,
                        shapeNode.getChildCount());
                if (shapeNode.getChildCount() != 0) {
                    layerTree.expandPath(new TreePath(shapeNode.getPath()));
                }
            } else {
                otherNode.add(new DefaultMutableTreeNode(addedLayer));
                if (otherNode.getChildCount() == 0) {
                    layerTreeModel.insertNodeInto(otherNode, rootNode, 1);
                }
                layerTreeModel.insertNodeInto(
                        newLayerNode,
                        otherNode,
                        otherNode.getChildCount());
                if (otherNode.getChildCount() != 0) {
                    layerTree.expandPath(new TreePath(otherNode));
                }
            }
            layerToNodeMap.put(addedLayer, newLayerNode);
        }
    }

    @Override
    public void layerNotAdded(final LayerStateEvent event) {
    }

    @Override
    public void layerRemoved(final Layer removedLayer) {

        if (logger.isDebugEnabled()) {
            logger.debug("layer removed. Removing Treenode...");
        }
        if (removedLayer != null) {
            final LayerNode layerNodeToRemove = layerToNodeMap.get(removedLayer);
            if (layerNodeToRemove != null) {
                TreeNode parent = layerNodeToRemove.getParent();
                if (logger.isDebugEnabled()) {
                    logger.debug("parent: " + parent);
                }
                if (parent != null) {
                    layerTreeModel.removeNodeFromParent(layerNodeToRemove);
                    if (logger.isDebugEnabled()) {
                        logger.debug("removed Node: " + layerNodeToRemove);
                    }
                    layerToNodeMap.remove(removedLayer);
                    if (removedLayer instanceof ShapeLayer && shapeNode.getChildCount() == 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Shape node is empty, will also be removed");
                        }
                        layerTreeModel.removeNodeFromParent(shapeNode);
                    } else if (removedLayer instanceof ElevationLayer && elevationNode.getChildCount() == 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Elevation node is empty, will also be removed");
                        }
                        layerTreeModel.removeNodeFromParent(elevationNode);
                    }
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: other node
                }
            }
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:make more generic
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: also this should be used to create the regular Menue
    private void createPopupMenuItems() {
        JMenuItem gotoBoundingBoxMenuItem = new JMenuItem("Zoom to extend");
        gotoBoundingBoxMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (popupListener.getLastSelectedLayer() != null && popupListener.getLastSelectedLayer().getBoundingBox() != null) {
                    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:better to set the bounding box of the viewing Component then propertyChange Visualisation is changed.Viewing Component does not exist at the moment.
                    ComponentBroker.getInstance().getController().getVisualisationComponent().gotoBoundingBox(popupListener.getLastSelectedLayer().getBoundingBox());
                }
            }
        });
        gotoBoundingBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        layerPopup.add(gotoBoundingBoxMenuItem);

        JMenuItem setLayerVisibleMenuItem = new JMenuItem("Change Visibility");
        setLayerVisibleMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (popupListener.getLastSelectedLayer() != null) {
                    popupListener.getLastSelectedLayer().setVisible(!popupListener.getLastSelectedLayer().isVisible());
                }
            }
        });
        setLayerVisibleMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        layerPopup.add(setLayerVisibleMenuItem);

        JMenuItem removeLayerMenuItem = new JMenuItem("Remove Layer");
        removeLayerMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (popupListener.getLastSelectedLayer() != null) {
                    layerManager.removeLayer(popupListener.getLastSelectedLayer());
                }
            }
        });
        removeLayerMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        removeLayerMenuItem.setIcon(IconUtil.DELETE_LAYER_24);
        layerPopup.add(removeLayerMenuItem);
    }

    public class LayerNode extends DefaultMutableTreeNode {

        Layer layer;

        public LayerNode(final Layer layer, boolean allowsChildren) {
            super(layer, allowsChildren);
            this.layer = layer;
        }

        public LayerNode(final Layer layer) {
            this(layer, true);
        }

        public LayerNode() {
            this(null, true);
        }

        public TreePath getTreePath() {
            return new TreePath(getPath());
        }

        public Layer getLayer() {
            return layer;
        }

        public void setLayer(Layer layer) {
            this.layer = layer;
        }

//        @Override
//        public Layer getUserObject() {
//            return (Layer) super.getUserObject();
//        }      
        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:better to have layer directly than to cast every time;
        @Override
        public String toString() {
            if (getLayer() != null) {
                return getLayer().getName();
            } else {
                return null;
            }
        }
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        final TreeNode parentNode = (TreeNode) e.getTreePath().getLastPathComponent();
        if ((parentNode.equals(rootNode) && rootNode.getChildCount() > 0)
                || (parentNode.equals(elevationNode) && elevationNode.getChildCount() > 0)
                || (parentNode.equals(shapeNode) && shapeNode.getChildCount() > 0)
                || (parentNode.equals(shapeNode) && shapeNode.getChildCount() > 0)) {
            layerTree.expandPath(e.getTreePath());
        }
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
    }

    public class LayerTreeCellRenderer extends DefaultTreeCellRenderer {

//       Icon checkedIcon = (Icon) UIManager.get("CheckBox.icon");
//       private Icon uncheckedIcon =  UIManager.getLookAndFeel().getDisabledSelectedIcon(new JCheckBox(), checkedIcon);
        public LayerTreeCellRenderer() {
            super();
            setBackgroundNonSelectionColor(layerTree.getBackground());
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            final JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (rootNode.equals(value)) {
                label.setIcon(ComponentBroker.getInstance().LAYER_ICON);
            } else if (shapeNode.equals(value)) {
                label.setIcon(ShapeLayer.SHAPE_ICON_12);
            } else if (elevationNode.equals(value)) {
                label.setIcon(ElevationLayer.ELEVATION_ICON_12);
            } else if (leaf && value instanceof LayerNode) {
                final Layer layer = ((LayerNode) value).getLayer();
                if (layer.isVisible()) {
                    label.setIcon(CHECKBOX_CHECKED_ICON);
                } else {
                    label.setIcon(CHECKBOX_NOT_CHECKED_ICON);
                }
            }
            return label;
        }
    }

    public class LayerTreeCellEditor extends DefaultTreeCellEditor //implements MouseListener
    {

        LayerNode currentEditedLayerNode = null;

        public LayerTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
            super(tree, renderer, editor);
        }

        public LayerTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
//            renderer.addMouseListener(this);
        }

        //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:modified suncode
        @Override
        public boolean isCellEditable(EventObject event) {
            if (logger.isDebugEnabled()) {
                logger.debug("isEditable");
            }
//            final boolean isEditable = super.isCellEditable(event);

//            if (!isEditable) {
//                return false;
//            }
            Object value = null;
            if (event != null) {
                if (event.getSource() instanceof JTree) {
                    if (event instanceof MouseEvent) {
                        final MouseEvent mouseEvent = ((MouseEvent) event);
                        if (mouseEvent.getClickCount() < 2) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("clickcount not right");
                            }
                            return false;
                        }
                        TreePath path = tree.getPathForLocation(
                                mouseEvent.getX(),
                                mouseEvent.getY());
                        if (path != null) {
                            lastRow = tree.getRowForPath(path);
                            value = path.getLastPathComponent();
                        }
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("super is editable");
            }
            if (value != null && value instanceof LayerNode) {
                if (logger.isDebugEnabled()) {
                    logger.debug("layernode");
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            Component editor = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
            if (logger.isDebugEnabled()) {
                logger.debug("editor: " + editor.getClass());
            }
            if (leaf && value instanceof LayerNode) {
                currentEditedLayerNode = (LayerNode) value;
                currentEditedLayerNode.getLayer().setVisible(!currentEditedLayerNode.getLayer().isVisible());
                final Layer layer = currentEditedLayerNode.getLayer();
                if (layer.isVisible()) {
                    renderer.setIcon(CHECKBOX_CHECKED_ICON);
                } else {
                    renderer.setIcon(CHECKBOX_NOT_CHECKED_ICON);
                }
            }
            return renderer;
        }
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("mouseClicked");
//            }
//            if(e.getClickCount() > 1){
//                currentEditedLayerNode.getLayer().setVisible(!currentEditedLayerNode.getLayer().isVisible());
//            }
//        }
//
//        @Override
//        public void mouseEntered(MouseEvent e) {
//        }
//
//        @Override
//        public void mouseExited(MouseEvent e) {
//        }
//
//        @Override
//        public void mousePressed(MouseEvent e) {
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent e) {
//        }
    }

    public void addLayerSelectionListener(final LayerSelectionListener listenerToAdd) {
        if (listenerToAdd != null) {
            layerSelectionListener.add(listenerToAdd);
        }
    }

    public void removeLayerSelectionListener(final LayerSelectionListener listenerToRemove) {
        if (listenerToRemove != null) {
            layerSelectionListener.remove(listenerToRemove);
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: implement special handling of group nodes example some childs are selected and the containing node.
    @Override
    public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
        // selected
        final ArrayList<Layer> selectedLayers = new ArrayList<Layer>();
        final ArrayList<Layer> deselectedLayers = new ArrayList<Layer>();
        final LayerSelectionEvent selectionEvent = new LayerSelectionEvent(this);
        selectionEvent.setLayersSelected(true);
        final LayerSelectionEvent unselectionEvent = new LayerSelectionEvent(this);
        unselectionEvent.setLayersSelected(false);
        selectionEvent.setSelectedLayer(selectedLayers);
        unselectionEvent.setSelectedLayer(deselectedLayers);
        if (treeSelectionEvent.getPaths() != null) {
            TreePath[] paths = treeSelectionEvent.getPaths();
            for (int i = 0; i < paths.length; i++) {
//                if (logger.isDebugEnabled()) {
//                    logger.debug("component: "+paths[i].getLastPathComponent());
//                }
                final Object currentSelectedObject = paths[i].getLastPathComponent();
                if (currentSelectedObject instanceof LayerNode) {
                    if (treeSelectionEvent.isAddedPath(i)) {
                        selectedLayers.add(((LayerNode) currentSelectedObject).getLayer());
                    } else {
                        deselectedLayers.add(((LayerNode) currentSelectedObject).getLayer());
                    }
                }
            }
        }
        for (LayerSelectionListener currentListener : layerSelectionListener) {
            if (deselectedLayers.size() > 0) {
                currentListener.layerSelectionChanged(unselectionEvent);
            }
            if (selectedLayers.size() > 0) {
                currentListener.layerSelectionChanged(selectionEvent);
            }
        }
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:sun tutorial code
    protected class PopupListener extends MouseAdapter {

        private JPopupMenu popup;
        private Layer lastSelectedLayer = null;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(final MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(final MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                final int mouseX = e.getX();
                final int mouseY = e.getY();
                final TreePath closestPath = layerTree.getPathForLocation(mouseX, mouseY);
                if (logger.isDebugEnabled()) {
                    logger.debug("closestPath: " + closestPath + " class: " + closestPath.getLastPathComponent().getClass() + "comp: " + closestPath.getLastPathComponent());
                }
                if (closestPath != null && closestPath.getLastPathComponent() instanceof LayerNode) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("popup: " + e.getComponent() + mouseX + mouseY);
                    }
                    lastSelectedLayer = ((LayerNode) closestPath.getLastPathComponent()).getLayer();
                    if (logger.isDebugEnabled()) {
                        logger.debug("lastSelectedLayer: " + lastSelectedLayer + " popup: " + popup);
                    }
                    popup.show(e.getComponent(), mouseX, mouseY);
                    layerTree.repaint();
                }
            }
        }

        public Layer getLastSelectedLayer() {
            return lastSelectedLayer;
        }
    }

    public void addPopupItem(final JMenuItem newPopupItem) {
        if (newPopupItem != null) {
            layerPopup.add(newPopupItem);
        }
    }

    public void removePopupItem(final JMenuItem popupItemToRemove) {
        if (popupItemToRemove != null) {
            layerPopup.remove(popupItemToRemove);
        }
    }
}