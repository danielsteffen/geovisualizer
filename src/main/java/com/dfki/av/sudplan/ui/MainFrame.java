/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on 25.01.2011, 11:54:16
 */
package com.dfki.av.sudplan.ui;

import com.dfki.av.sudplan.conf.InitialisationException;
import com.dfki.av.sudplan.control.ComponentBroker;
import com.dfki.av.sudplan.control.ComponentController;
import com.dfki.av.sudplan.layer.Layer;
import com.dfki.av.sudplan.layer.LayerManager;
import com.dfki.av.sudplan.layer.LayerSelectionEvent;
import com.dfki.av.sudplan.layer.LayerSelectionListener;
import com.dfki.av.sudplan.ui.vis.VisualisationComponent;
import com.dfki.av.sudplan.ui.vis.VisualisationComponentPanel;
import com.dfki.av.sudplan.util.AdvancedBoundingBox;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import net.infonode.docking.DockingWindow;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DeveloperUtil;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.StringViewMap;
import net.infonode.gui.componentpainter.AlphaGradientComponentPainter;
import net.infonode.util.Direction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 */
public class MainFrame extends javax.swing.JFrame implements LayerSelectionListener {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: save window Size/Position
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: save Layout
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: either make toolbar unfloatable or make it possible to reattach
    private final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private Icon visualisationIcon;
    private Icon visualisation2dIcon;
    private Icon layerIcon;
    private Icon controlIcon;
    private View visualisationView;
    private View visualisation2DView;
    private View layerView;
    private View controlView;

    /*ToDo this should be done via interfaces in order to be able to exchange the components
     * Possible place could be the controler class
     */
    private VisualisationComponentPanel visualisationPanel = new VisualisationComponentPanel();
    private JPanel visualisation2dPanel = visualisationPanel.getPanel2d();
    private SimpleControlPanel controlPanel = new SimpleControlPanel();
    private RootWindow layoutRootWindow;
    private final StringViewMap viewMap = new StringViewMap();
    private final Map<String, JMenuItem> viewMenuMap = new HashMap<String, JMenuItem>();
    ResourceBundle i18n = ResourceBundle.getBundle("com/dfki/av/sudplan/ui/Bundle");
    private ComponentController componentController;
    private SimpleLayerPanel layerPanel;
    private ArrayList<Layer> currentSelectedLayer;
    private final LayerManager layerManager;
//    private boolean initialised = false;

    /** Creates new form MainFrame */
    public MainFrame() throws InitialisationException {
        try {
//            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Info: "+info.getName());
//                }
//ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:Nimbus look & feel is buggy removeNodefrom()parent results in Nullpointer exception
//                // known from http://netbeans.org/bugzilla/show_bug.cgi?id=132550 not in JDK bug database
//                                if ("Nimbus".equals(info.getName())) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("Found Nimbus Look & Feel.");
//                    }
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }               
//                }
//            }
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:make this failsave if the laf is not available --> use default.
            javax.swing.UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:place all this in controller ? --> no because other people will do this by them selfs
            componentController = new ComponentController();
            layerManager = componentController.getLayerManager();
            layerPanel = new SimpleLayerPanel(layerManager);
            layerPanel.addLayerSelectionListener(this);
            componentController.addDropTarger(layerPanel);
            logger.debug("{} Constructor() call", MainFrame.class.toString());
            initComponents();
            loadIcons();
            initLayoutFramework();
            //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: save user Layout
            doDefaultLayout();
            initDeveloperShortcut();
            //configure JFrame
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1024, 768);
            componentController.getLayerManager().addLayerListener(layerPanel);
//            ComponentBroker.getInstance().setController(componentController);
            componentController.setMainFrame(this);
            componentController.setVisualisationComponent(getVisualisationComponent());
        } catch (Exception ex) {
            final String message = "Error during Controller initialisation.";
            if (logger.isErrorEnabled()) {
                logger.error(message, ex);
            }
            throw new InitialisationException(message, ex);
        }
//    pack();
    }

//    public void initialise() throws InitialisationException {
//        if (!initialised) {
//        } else {
//            throw new InitialisationException("The main application is already inistialised.");
//        }
//    }
    public VisualisationComponent getVisualisationComponent() {
        return visualisationPanel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainToolbarPanel = new javax.swing.JPanel();
        postionControlPanel = new javax.swing.JPanel();
        layerControlPanel = new javax.swing.JPanel();
        frameMainPanel = new javax.swing.JPanel();
        frameToolBar = new javax.swing.JToolBar();
        gotoHomeButton = new javax.swing.JButton();
        toggleTextureButton = new javax.swing.JToggleButton();
        toggleLightButton = new javax.swing.JToggleButton();
        first_seperator = new javax.swing.JToolBar.Separator();
        toggleCombinedButton = new javax.swing.JToggleButton();
        togglePanButton = new javax.swing.JToggleButton();
        toggleRotateButton = new javax.swing.JToggleButton();
        toggleZoomButton = new javax.swing.JToggleButton();
        second_seperator = new javax.swing.JToolBar.Separator();
        deleteLayerButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        resetCameraDirection = new javax.swing.JButton();
        printBoundingBox = new javax.swing.JButton();
        gotoPoint = new javax.swing.JButton();
        gotoBoundingBox = new javax.swing.JButton();
        setCameraDirection = new javax.swing.JButton();
        frameMenueBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        visualisationMenueItem = new javax.swing.JMenuItem();
        layerMenueItem = new javax.swing.JMenuItem();
        controlMenueItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();

        mainToolbarPanel.setMinimumSize(new java.awt.Dimension(300, 40));
        mainToolbarPanel.setPreferredSize(new java.awt.Dimension(300, 40));

        javax.swing.GroupLayout postionControlPanelLayout = new javax.swing.GroupLayout(postionControlPanel);
        postionControlPanel.setLayout(postionControlPanelLayout);
        postionControlPanelLayout.setHorizontalGroup(
            postionControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 123, Short.MAX_VALUE)
        );
        postionControlPanelLayout.setVerticalGroup(
            postionControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layerControlPanelLayout = new javax.swing.GroupLayout(layerControlPanel);
        layerControlPanel.setLayout(layerControlPanelLayout);
        layerControlPanelLayout.setHorizontalGroup(
            layerControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        layerControlPanelLayout.setVerticalGroup(
            layerControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainToolbarPanelLayout = new javax.swing.GroupLayout(mainToolbarPanel);
        mainToolbarPanel.setLayout(mainToolbarPanelLayout);
        mainToolbarPanelLayout.setHorizontalGroup(
            mainToolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainToolbarPanelLayout.createSequentialGroup()
                .addComponent(postionControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132)
                .addComponent(layerControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(151, Short.MAX_VALUE))
        );
        mainToolbarPanelLayout.setVerticalGroup(
            mainToolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainToolbarPanelLayout.createSequentialGroup()
                .addComponent(postionControlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(mainToolbarPanelLayout.createSequentialGroup()
                .addComponent(layerControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dfki/av/sudplan/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        frameMainPanel.setLayout(new java.awt.BorderLayout());

        frameToolBar.setRollover(true);

        gotoHomeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/toolbar/home.gif"))); // NOI18N
        gotoHomeButton.setToolTipText(bundle.getString("MainFrame.gotoHomeButton.toolTipText")); // NOI18N
        gotoHomeButton.setBorderPainted(false);
        gotoHomeButton.setFocusable(false);
        gotoHomeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gotoHomeButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        gotoHomeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gotoHomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoHomeButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(gotoHomeButton);

        toggleTextureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/layer/layerIcon24.png"))); // NOI18N
        toggleTextureButton.setSelected(true);
        toggleTextureButton.setText(bundle.getString("MainFrame.toggleTextureButton.text")); // NOI18N
        toggleTextureButton.setToolTipText(bundle.getString("MainFrame.toggleTextureButton.toolTipText")); // NOI18N
        toggleTextureButton.setEnabled(false);
        toggleTextureButton.setFocusable(false);
        toggleTextureButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleTextureButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        toggleTextureButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleTextureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleTextureButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(toggleTextureButton);

        toggleLightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/toolbar/lightbulb24.png"))); // NOI18N
        toggleLightButton.setSelected(true);
        toggleLightButton.setText(bundle.getString("MainFrame.toggleLightButton.text")); // NOI18N
        toggleLightButton.setEnabled(false);
        toggleLightButton.setFocusable(false);
        toggleLightButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        toggleLightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleLightButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        toggleLightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleLightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleLightButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(toggleLightButton);

        first_seperator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        frameToolBar.add(first_seperator);

        toggleCombinedButton.setText(bundle.getString("MainFrame.toggleCombinedButton.text")); // NOI18N
        toggleCombinedButton.setToolTipText(bundle.getString("MainFrame.toggleCombinedButton.toolTipText")); // NOI18N
        toggleCombinedButton.setFocusable(false);
        toggleCombinedButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleCombinedButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        toggleCombinedButton.setMaximumSize(new java.awt.Dimension(37, 33));
        toggleCombinedButton.setMinimumSize(new java.awt.Dimension(37, 33));
        toggleCombinedButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleCombinedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleCombinedButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(toggleCombinedButton);

        togglePanButton.setText(bundle.getString("MainFrame.togglePanButton.text")); // NOI18N
        togglePanButton.setToolTipText(bundle.getString("MainFrame.togglePanButton.toolTipText")); // NOI18N
        togglePanButton.setFocusable(false);
        togglePanButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        togglePanButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        togglePanButton.setMaximumSize(new java.awt.Dimension(37, 33));
        togglePanButton.setMinimumSize(new java.awt.Dimension(37, 33));
        togglePanButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        togglePanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePanButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(togglePanButton);

        toggleRotateButton.setText(bundle.getString("MainFrame.toggleRotateButton.text")); // NOI18N
        toggleRotateButton.setToolTipText(bundle.getString("MainFrame.toggleRotateButton.toolTipText")); // NOI18N
        toggleRotateButton.setFocusable(false);
        toggleRotateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleRotateButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        toggleRotateButton.setMaximumSize(new java.awt.Dimension(37, 33));
        toggleRotateButton.setMinimumSize(new java.awt.Dimension(37, 33));
        toggleRotateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleRotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleRotateButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(toggleRotateButton);

        toggleZoomButton.setText(bundle.getString("MainFrame.toggleZoomButton.text")); // NOI18N
        toggleZoomButton.setToolTipText(bundle.getString("MainFrame.toggleZoomButton.toolTipText")); // NOI18N
        toggleZoomButton.setFocusable(false);
        toggleZoomButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleZoomButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        toggleZoomButton.setMaximumSize(new java.awt.Dimension(37, 33));
        toggleZoomButton.setMinimumSize(new java.awt.Dimension(37, 33));
        toggleZoomButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleZoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleZoomButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(toggleZoomButton);

        second_seperator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        frameToolBar.add(second_seperator);

        deleteLayerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/toolbar/deleteLayer24.png"))); // NOI18N
        deleteLayerButton.setToolTipText(bundle.getString("MainFrame.deleteLayerButton.toolTipText")); // NOI18N
        deleteLayerButton.setBorderPainted(false);
        deleteLayerButton.setEnabled(false);
        deleteLayerButton.setFocusable(false);
        deleteLayerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteLayerButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        deleteLayerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteLayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLayerButtonActionPerformed(evt);
            }
        });
        frameToolBar.add(deleteLayerButton);
        frameToolBar.add(jSeparator1);

        resetCameraDirection.setText(bundle.getString("MainFrame.resetCameraDirection.text")); // NOI18N
        resetCameraDirection.setToolTipText(bundle.getString("MainFrame.resetCameraDirection.toolTipText")); // NOI18N
        resetCameraDirection.setBorderPainted(false);
        resetCameraDirection.setFocusable(false);
        resetCameraDirection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetCameraDirection.setMargin(new java.awt.Insets(2, 4, 2, 4));
        resetCameraDirection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetCameraDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCameraDirectionActionPerformed(evt);
            }
        });
        frameToolBar.add(resetCameraDirection);

        printBoundingBox.setText(bundle.getString("MainFrame.printBoundingBox.text")); // NOI18N
        printBoundingBox.setToolTipText(bundle.getString("MainFrame.printBoundingBox.toolTipText")); // NOI18N
        printBoundingBox.setBorderPainted(false);
        printBoundingBox.setFocusable(false);
        printBoundingBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        printBoundingBox.setMargin(new java.awt.Insets(2, 4, 2, 4));
        printBoundingBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        printBoundingBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBoundingBoxActionPerformed(evt);
            }
        });
        frameToolBar.add(printBoundingBox);

        gotoPoint.setText(bundle.getString("MainFrame.gotoPoint.text")); // NOI18N
        gotoPoint.setToolTipText(bundle.getString("MainFrame.gotoPoint.toolTipText")); // NOI18N
        gotoPoint.setBorderPainted(false);
        gotoPoint.setFocusable(false);
        gotoPoint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gotoPoint.setMargin(new java.awt.Insets(2, 4, 2, 4));
        gotoPoint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gotoPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoPointActionPerformed(evt);
            }
        });
        frameToolBar.add(gotoPoint);

        gotoBoundingBox.setText(bundle.getString("MainFrame.gotoBoundingBox.text")); // NOI18N
        gotoBoundingBox.setToolTipText(bundle.getString("MainFrame.gotoBoundingBox.toolTipText")); // NOI18N
        gotoBoundingBox.setBorderPainted(false);
        gotoBoundingBox.setFocusable(false);
        gotoBoundingBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gotoBoundingBox.setMargin(new java.awt.Insets(2, 4, 2, 4));
        gotoBoundingBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gotoBoundingBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoBoundingBoxActionPerformed(evt);
            }
        });
        frameToolBar.add(gotoBoundingBox);

        setCameraDirection.setText(bundle.getString("MainFrame.setCameraDirection.text")); // NOI18N
        setCameraDirection.setToolTipText(bundle.getString("MainFrame.setCameraDirection.toolTipText")); // NOI18N
        setCameraDirection.setBorderPainted(false);
        setCameraDirection.setFocusable(false);
        setCameraDirection.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setCameraDirection.setMargin(new java.awt.Insets(2, 4, 2, 4));
        setCameraDirection.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        setCameraDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setCameraDirectionActionPerformed(evt);
            }
        });
        frameToolBar.add(setCameraDirection);

        fileMenu.setText(bundle.getString("MainFrame.fileMenu.text")); // NOI18N

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText(bundle.getString("MainFrame.exitMenuItem.text")); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        frameMenueBar.add(fileMenu);

        editMenu.setText(bundle.getString("MainFrame.editMenu.text")); // NOI18N
        frameMenueBar.add(editMenu);

        windowMenu.setText(bundle.getString("MainFrame.windowMenu.text")); // NOI18N

        visualisationMenueItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        visualisationMenueItem.setText(bundle.getString("MainFrame.visualisationMenueItem.text")); // NOI18N
        visualisationMenueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualisationMenueItemActionPerformed(evt);
            }
        });
        windowMenu.add(visualisationMenueItem);

        layerMenueItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        layerMenueItem.setText(bundle.getString("MainFrame.layerMenueItem.text")); // NOI18N
        layerMenueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerMenueItemActionPerformed(evt);
            }
        });
        windowMenu.add(layerMenueItem);

        controlMenueItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        controlMenueItem.setText(bundle.getString("MainFrame.controlMenueItem.text")); // NOI18N
        controlMenueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controlMenueItemActionPerformed(evt);
            }
        });
        windowMenu.add(controlMenueItem);

        frameMenueBar.add(windowMenu);

        helpMenu.setText(bundle.getString("MainFrame.helpMenu.text")); // NOI18N
        frameMenueBar.add(helpMenu);

        setJMenuBar(frameMenueBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameMainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
            .addComponent(frameToolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(frameMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void gotoHomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoHomeButtonActionPerformed
      componentController.getVisualisationComponent().gotoToHome();
}//GEN-LAST:event_gotoHomeButtonActionPerformed

  private void visualisationMenueItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualisationMenueItemActionPerformed
      showOrHideView(visualisationView);
  }//GEN-LAST:event_visualisationMenueItemActionPerformed

  private void layerMenueItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerMenueItemActionPerformed
      showOrHideView(layerView);
  }//GEN-LAST:event_layerMenueItemActionPerformed

  private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
      System.exit(0);
  }//GEN-LAST:event_exitMenuItemActionPerformed

  private void controlMenueItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_controlMenueItemActionPerformed
      showOrHideView(controlView);
  }//GEN-LAST:event_controlMenueItemActionPerformed

  private void toggleLightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleLightButtonActionPerformed
      componentController.getVisualisationComponent().enableDirectedLight(toggleLightButton.isSelected());
  }//GEN-LAST:event_toggleLightButtonActionPerformed

  private void toggleTextureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleTextureButtonActionPerformed
      componentController.enableDEMTexture(toggleTextureButton.isSelected());
  }//GEN-LAST:event_toggleTextureButtonActionPerformed

  private void deleteLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLayerButtonActionPerformed
      layerManager.removeLayers(currentSelectedLayer);
  }//GEN-LAST:event_deleteLayerButtonActionPerformed

  private void toggleZoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleZoomButtonActionPerformed
      disableAllInteractionModeButtons();
      toggleZoomButton.setSelected(true);
      componentController.enableModeZoom();
  }//GEN-LAST:event_toggleZoomButtonActionPerformed

  private void toggleCombinedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleCombinedButtonActionPerformed
      disableAllInteractionModeButtons();
      toggleCombinedButton.setSelected(true);
      componentController.enableModeCombined();
  }//GEN-LAST:event_toggleCombinedButtonActionPerformed

  private void togglePanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togglePanButtonActionPerformed
      disableAllInteractionModeButtons();
      togglePanButton.setSelected(true);
      componentController.enableModePan();

  }//GEN-LAST:event_togglePanButtonActionPerformed

  private void toggleRotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleRotateButtonActionPerformed
      disableAllInteractionModeButtons();
      toggleRotateButton.setSelected(true);
      componentController.enableModeRotate();
  }//GEN-LAST:event_toggleRotateButtonActionPerformed

  private void resetCameraDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCameraDirectionActionPerformed
      visualisationPanel.get3dCamera().setCameraToInitalViewingDirection();
  }//GEN-LAST:event_resetCameraDirectionActionPerformed

  private void printBoundingBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBoundingBoxActionPerformed
      if (logger.isDebugEnabled()) {
          logger.debug("ViewBoundingBox: " + visualisationPanel.get3dCamera().getViewBoundingBox());
          visualisationPanel.get3dCamera().calculateBoundingBoxes();
          logger.debug("Recalculated ViewBoundingBox: " + visualisationPanel.get3dCamera().getViewBoundingBox());
      }
  }//GEN-LAST:event_printBoundingBoxActionPerformed

  private void gotoPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoPointActionPerformed
      final Point3d cameraPosition = visualisationPanel.get3dCamera().getCameraPosition();
      String initialValue = "x=0.0,y=0.0,z=0.0";
      if (cameraPosition != null) {
          initialValue = "x=" + Math.floor(cameraPosition.x * 100) / 100 + ",y=" + Math.floor(cameraPosition.y * 100) / 100 + ",z=" + Math.floor(cameraPosition.z * 100) / 100;
      }
      final String input = (String) JOptionPane.showInputDialog(this, "Please enter a point.", "Goto point...", JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
      try {
          if (input != null) {
              String[] xyz = input.split(",");
              Point3d newCameraPosition = new Point3d(Double.parseDouble(xyz[0].split("=")[1]), Double.parseDouble(xyz[1].split("=")[1]), Double.parseDouble(xyz[2].split("=")[1]));
              if (logger.isDebugEnabled()) {
                  logger.debug("Parsed point: " + newCameraPosition);
                  visualisationPanel.get3dCamera().setCameraPosition(newCameraPosition);
              }
          }
      } catch (Exception ex) {
          try {
              if (input != null) {
                  String[] xyz = input.split(", ");
                  Point3d newCameraPosition = new Point3d(Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2]));
                  if (logger.isDebugEnabled()) {
                      logger.debug("Parsed point: " + newCameraPosition);
                      visualisationPanel.get3dCamera().setCameraPosition(newCameraPosition);
                  }
              }
          } catch (Exception ex2) {
              if (logger.isDebugEnabled()) {
                  logger.debug("Error while parsing Point, goto not possible: ", ex2);
              }
          }
      }
  }//GEN-LAST:event_gotoPointActionPerformed

  private void gotoBoundingBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoBoundingBoxActionPerformed
      ComponentBroker.getInstance().getController().getVisualisationComponent().getGeographicCamera().gotoBoundingBox(
              new AdvancedBoundingBox(
              new Point3d(15.358974386418915, 58.039748350545125, 0.0),
              new Point3d(20.072223733698287, 61.47788349763985, 0.0)));
  }//GEN-LAST:event_gotoBoundingBoxActionPerformed

  private void setCameraDirectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setCameraDirectionActionPerformed
      final Vector3d cameraDirection = visualisationPanel.get3dCamera().getCameraDirection();
      String initialValue = "x=0.0,y=0.0,z=0.0";
      if (cameraDirection != null) {
          initialValue = "x=" + Math.floor(cameraDirection.x * 100) / 100 + ",y=" + Math.floor(cameraDirection.y * 100) / 100 + ",z=" + Math.floor(cameraDirection.z * 100) / 100;
      }
      final String input = (String) JOptionPane.showInputDialog(this, "Please enter a view direction.", "Change view point...", JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
      try {
          if (input != null) {
              String[] xyz = input.split(",");
              Vector3d newCameraDirection = new Vector3d(Double.parseDouble(xyz[0].split("=")[1]), Double.parseDouble(xyz[1].split("=")[1]), Double.parseDouble(xyz[2].split("=")[1]));
              if (logger.isDebugEnabled()) {
                  logger.debug("Parsed vector: " + newCameraDirection);
                  ComponentBroker.getInstance().getController().
                          getVisualisationComponent().getGeographicCamera().setCameraDirection(newCameraDirection);
              }
          }
      } catch (Exception ex) {
          try {
              if (input != null) {
                  String[] xyz = input.split(", ");
                  Vector3d newCameraDirection = new Vector3d(Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2]));
                  if (logger.isDebugEnabled()) {
                      logger.debug("Parsed point: " + newCameraDirection);
                      ComponentBroker.getInstance().getController().
                              getVisualisationComponent().getGeographicCamera().setCameraDirection(newCameraDirection);
                  }
              }
          } catch (Exception ex2) {
              if (logger.isDebugEnabled()) {
                  logger.debug("Error while parsing Point, goto not possible: ", ex2);
              }
          }
      }
  }//GEN-LAST:event_setCameraDirectionActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem controlMenueItem;
    private javax.swing.JButton deleteLayerButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToolBar.Separator first_seperator;
    private javax.swing.JPanel frameMainPanel;
    private javax.swing.JMenuBar frameMenueBar;
    private javax.swing.JToolBar frameToolBar;
    private javax.swing.JButton gotoBoundingBox;
    private javax.swing.JButton gotoHomeButton;
    private javax.swing.JButton gotoPoint;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPanel layerControlPanel;
    private javax.swing.JMenuItem layerMenueItem;
    private javax.swing.JPanel mainToolbarPanel;
    private javax.swing.JPanel postionControlPanel;
    private javax.swing.JButton printBoundingBox;
    private javax.swing.JButton resetCameraDirection;
    private javax.swing.JToolBar.Separator second_seperator;
    private javax.swing.JButton setCameraDirection;
    private javax.swing.JToggleButton toggleCombinedButton;
    private javax.swing.JToggleButton toggleLightButton;
    private javax.swing.JToggleButton togglePanButton;
    private javax.swing.JToggleButton toggleRotateButton;
    private javax.swing.JToggleButton toggleTextureButton;
    private javax.swing.JToggleButton toggleZoomButton;
    private javax.swing.JMenuItem visualisationMenueItem;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables

    private void initLayoutFramework() {
        layoutRootWindow = DockingUtil.createRootWindow(viewMap, true);
        visualisationView = new View(i18n.getString("MainFrame.visalualisation.view.title"),
                borderIcon(visualisationIcon, 0, 3, 0, 1),
                visualisationPanel);
        viewMap.addView("visualisation", visualisationView);
        visualisation2DView = new View(i18n.getString("MainFrame.visalualisation2d.view.title"),
                borderIcon(visualisation2dIcon, 0, 3, 0, 1),
                visualisation2dPanel);
        viewMap.addView("visualisation", visualisation2DView);
        layerView = new View(i18n.getString("MainFrame.layer.view.title"),
                borderIcon(layerIcon, 0, 3, 0, 1),
                layerPanel);
        viewMap.addView("layer", layerView);
        controlView = new View(i18n.getString("MainFrame.control.view.title"),
                borderIcon(controlIcon, 0, 3, 0, 1),
                controlPanel);
        viewMap.addView("layer", layerView);
        layoutRootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
        final DockingWindowsTheme theme = new ShapedGradientDockingTheme();
        layoutRootWindow.getRootWindowProperties().addSuperObject(
                theme.getRootWindowProperties());
        final RootWindowProperties titleBarStyleProperties = PropertiesUtil.createTitleBarStyleRootWindowProperties();
        layoutRootWindow.getRootWindowProperties().addSuperObject(
                titleBarStyleProperties);
        layoutRootWindow.getRootWindowProperties().getDockingWindowProperties().setUndockEnabled(true);
        layoutRootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
        layoutRootWindow.getWindowBar(Direction.RIGHT).setEnabled(true);
        final AlphaGradientComponentPainter x = new AlphaGradientComponentPainter(
                java.awt.SystemColor.inactiveCaptionText,
                java.awt.SystemColor.activeCaptionText,
                java.awt.SystemColor.activeCaptionText,
                java.awt.SystemColor.inactiveCaptionText);
        visualisationView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        visualisation2DView.getViewProperties().getViewTitleBarProperties().getNormalProperties().getCloseButtonProperties().setVisible(true);
        layoutRootWindow.getRootWindowProperties().getDragRectangleShapedPanelProperties().setComponentPainter(x);
        //????
        //            viewMap.addView("activeLayers", vLayers);
        //            viewMenuMap.put("activeLayers", mniLayer);
//    layerView.close();
        frameMainPanel.add(layoutRootWindow, BorderLayout.CENTER);
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: copied from Static2DTools;
    public static Icon borderIcon(final Icon icon, final int left, final int right, final int top, final int bottom) {
        final BufferedImage bi = new BufferedImage(icon.getIconWidth() + left + right,
                icon.getIconHeight()
                + top
                + bottom,
                BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bi.getGraphics(), left, top);
        return new ImageIcon(bi);
    }

    private void loadIcons() {
        visualisationIcon = new javax.swing.ImageIcon(getClass().getResource(
                i18n.getString("MainFrame.visualisation.view.icon")));
        visualisation2dIcon = new javax.swing.ImageIcon(getClass().getResource(
                i18n.getString("MainFrame.visualisation2d.view.icon")));
        layerIcon = new javax.swing.ImageIcon(getClass().getResource(
                i18n.getString("MainFrame.layer.view.icon")));
        controlIcon = new javax.swing.ImageIcon(getClass().getResource(
                i18n.getString("MainFrame.control.view.icon")));
    }

    private void doDefaultLayout() {
        layoutRootWindow.setWindow(new SplitWindow(
                true,
                0.25225961f, new TabWindow(new DockingWindow[]{layerView, controlView}), new TabWindow(new DockingWindow[]{visualisationView, visualisation2DView})));
        layerView.restoreFocus();
        visualisationView.restoreFocus();
    }

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: works only the first time + should be in a developer mode
    private void initDeveloperShortcut() {
        final KeyStroke configLayoutKeyStroke = KeyStroke.getKeyStroke(
                'L',
                InputEvent.CTRL_MASK
                + InputEvent.SHIFT_MASK);
        final Action configAction = new AbstractAction() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        DeveloperUtil.createWindowLayoutFrame("layout", layoutRootWindow);
                    }
                });
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(configLayoutKeyStroke,
                "config.layout");                                    // NOI18N
        getRootPane().getActionMap().put("config.layout", configAction);
    }

    public ComponentController getController() {
        return componentController;
    }

    public void setController(ComponentController compController) {
        this.componentController = compController;
    }

    private void showOrHideView(final View v) {
        ///irgendwas besser als Closable ??
        // Problem wenn floating --> close -> open  (muss zweimal open)
        if (v.isClosable()) {
            v.close();
        } else {
            v.restore();
        }
    }

    public void enableDEMButtons(final boolean enabled) {
        toggleLightButton.setEnabled(enabled);
        toggleTextureButton.setEnabled(enabled);
    }

    public void enableControls(final boolean enabled) {
        controlPanel.enableRecoloringControls(enabled);
    }

    @Override
    public void layerSelectionChanged(LayerSelectionEvent layerSelectionEvent) {
        currentSelectedLayer = layerSelectionEvent.getSelectedLayer();
        if (layerSelectionEvent.isLayersSelected() && currentSelectedLayer.size() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("layer selection: ");
            }
            deleteLayerButton.setEnabled(true);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("layer deselection: ");
            }
            deleteLayerButton.setEnabled(false);
        }
    }

    private void disableAllInteractionModeButtons() {
        toggleCombinedButton.setSelected(false);
        toggleZoomButton.setSelected(false);
        toggleRotateButton.setSelected(false);
        togglePanButton.setSelected(false);
    }
}
