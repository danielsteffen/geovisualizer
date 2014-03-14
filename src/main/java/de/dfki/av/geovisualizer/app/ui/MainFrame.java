/*
 *  MainFrame.java 
 *
 *  Created by DFKI AV on 14.09.2011.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import de.dfki.av.geovisualizer.app.Configuration;
import de.dfki.av.geovisualizer.app.camera.AnimatedCamera;
import de.dfki.av.geovisualizer.app.plugins.IPlugin;
import de.dfki.av.geovisualizer.app.plugins.PluginFactory;
import de.dfki.av.geovisualizer.app.plugins.PluginListener;
import de.dfki.av.geovisualizer.app.vis.VisualizationPanel;
import de.dfki.av.geovisualizer.app.wms.EventHolder;
import de.dfki.av.geovisualizer.app.wms.LayerInfo;
import de.dfki.av.geovisualizer.app.wms.LayerInfoRetreiver;
import de.dfki.av.geovisualizer.core.IVisAlgorithm;
import de.dfki.av.geovisualizer.core.VisPointCloud;
import de.dfki.av.geovisualizer.core.spi.VisAlgorithmFactory;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwindx.applications.worldwindow.core.AppConfiguration;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class MainFrame extends javax.swing.JFrame implements PropertyChangeListener {

    /**
     * The root logger.
     */
    private static org.apache.log4j.Logger rootLog = org.apache.log4j.Logger.getRootLogger();
    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);
    /**
     * The size of the {@link #wwPanel}.
     */
    private Dimension canvasSize;
    /**
     * The visualization panel wrapping the WorldWind canvas.
     */
    private VisualizationPanel wwPanel;
    /**
     * The counter of saved views.
     */
    private int viewID = 0;
    /**
     * The {@link List} of {@IPlugin} instances.
     */
    private List<IPlugin> plugins; 
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        this.canvasSize = new Dimension(1200, 700);
        this.wwPanel = new VisualizationPanel(canvasSize);
        this.wwPanel.setPreferredSize(canvasSize);
        this.wwPanel.setMinimumSize(new Dimension(0, 0));

        initComponents();
        initActions();
        initPlugins();
        
        // Add LayerTreeComponent to the left split panel.
        JPanel layerTreeComponent = wwPanel.getLayerPanel();
        if (layerTreeComponent != null) {
            Dimension minSize = new Dimension(0, 0);
            layerTreeComponent.setMinimumSize(minSize);
            pLeftPanel.add(layerTreeComponent);
        } else {
            LOG.debug("layerTreeComponent == null");
        }
    }

    /**
     * Initialize all needed actions.
     */
    private void initActions() {
        SnapshotAction takeSnaphot = new SnapshotAction(wwPanel.getWwd());
        btnTakeScreenShot.addActionListener(takeSnaphot);
        miTakeScreenshot.addActionListener(takeSnaphot);

        VisWizAction runVisWiz = new VisWizAction(wwPanel);
        btnVizWiz.addActionListener(runVisWiz);
        miWizard.addActionListener(runVisWiz);

        ExitAction exitAction = new ExitAction(wwPanel.getWwd());
        btnExit.addActionListener(exitAction);
        miExit.addActionListener(exitAction);

        AnimatedCamera fullGlobeCamera = new AnimatedCamera(37.0, 27.0, 19000000.0);
        CameraAction fullGlobeAction = new CameraAction(wwPanel, fullGlobeCamera);
        btnGoHome.addActionListener(fullGlobeAction);
        miFullSphere.addActionListener(fullGlobeAction);

        AnimatedCamera klCamera = new AnimatedCamera(49.4447186, 7.7690169, 20000.0);
        CameraAction klCameraAction = new CameraAction(wwPanel, klCamera);
        miGotoKaiserslautern.addActionListener(klCameraAction);

        GotoAction gotoAction = new GotoAction(this, wwPanel.getWwd());
        miGoto.addActionListener(gotoAction);
        btnJumpTo.addActionListener(gotoAction);
    }
    
    /**
     * Initialize all available plug-ins of type {@link IPlugin}.‚ 
     */
    private void initPlugins(){
        List<String> pluginNames = PluginFactory.getNames();
        plugins = new ArrayList<>();
        for (Iterator<String> it = pluginNames.iterator(); it.hasNext();) {
            String string = it.next();
            IPlugin plugin = PluginFactory.newInstance(string);
            plugins.add(plugin);
        }
        
        LOG.debug("Found {} application plugins.", plugins.size());
        
        if(plugins.size() > 0){
            mPlugins.setEnabled(true);
            PluginListener listener = new PluginListener();
            for (Iterator<IPlugin> it = plugins.iterator(); it.hasNext();) {
                IPlugin plugin = it.next();
                plugin.addPropertyChangeListener(listener);
                JMenuItem miPlugin = new JMenuItem(plugin.getName());
                miPlugin.addActionListener(plugin.getAbstractAction());
                mPlugins.add(miPlugin);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dWMSHeight = new javax.swing.JDialog();
        lMaxEle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lServerURL = new javax.swing.JLabel();
        rbCbServerUrl = new javax.swing.JRadioButton();
        rbTxtServerUrl = new javax.swing.JRadioButton();
        txtServerURL = new javax.swing.JTextField();
        cbServerURL = new javax.swing.JComboBox();
        bGoWMSHeight = new javax.swing.JButton();
        pbWMS = new javax.swing.JProgressBar();
        jPanel4 = new javax.swing.JPanel();
        bCancelWMSHeight = new javax.swing.JButton();
        bAddWMSHeight = new javax.swing.JButton();
        cLayerList = new javax.swing.JComboBox();
        txtHeight = new javax.swing.JTextField();
        lHeight = new javax.swing.JLabel();
        lOpacity = new javax.swing.JLabel();
        txtOpacity = new javax.swing.JTextField();
        bgWMS = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        btnGoHome = new javax.swing.JButton();
        btnJumpTo = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnVizWiz = new javax.swing.JButton();
        btnTakeScreenShot = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnExit = new javax.swing.JButton();
        pMain = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pLeftPanel = new javax.swing.JPanel();
        pVisualization = new javax.swing.JPanel();
        mbMain = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        miOpenKMLFile = new javax.swing.JMenuItem();
        miAddGeoTiff = new javax.swing.JMenuItem();
        miAddShape = new javax.swing.JMenuItem();
        miAddShapeZip = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        mEdit = new javax.swing.JMenu();
        miRemoveAllLayer = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miTakeScreenshot = new javax.swing.JMenuItem();
        mNavi = new javax.swing.JMenu();
        miGotoKaiserslautern = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        miGoto = new javax.swing.JMenuItem();
        miFullSphere = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        miSaveView = new javax.swing.JMenuItem();
        mCustomViewPoints = new javax.swing.JMenu();
        mWMS = new javax.swing.JMenu();
        miAddWMS = new javax.swing.JMenuItem();
        miAddWMSHeight = new javax.swing.JMenuItem();
        mTools = new javax.swing.JMenu();
        miWizard = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mPlugins = new javax.swing.JMenu();
        mView = new javax.swing.JMenu();
        mToolbars = new javax.swing.JMenu();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        mWindowMode = new javax.swing.JMenu();
        miSideBySide = new javax.swing.JMenuItem();
        mFullScreen = new javax.swing.JMenuItem();
        mHelp = new javax.swing.JMenu();
        miGeoVisualizerHelp = new javax.swing.JMenuItem();
        miAbout = new javax.swing.JMenuItem();

        dWMSHeight.setTitle(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.dWMSHeight.title")); // NOI18N
        dWMSHeight.setIconImage(de.dfki.av.geovisualizer.app.Configuration.GEOVISUALIZER_ICON);
        dWMSHeight.setLocationByPlatform(true);
        dWMSHeight.setMinimumSize(new java.awt.Dimension(800, 360));
        dWMSHeight.setResizable(false);

        lMaxEle.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.lMaxEle.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jPanel1.border.title"))); // NOI18N
        jPanel1.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jPanel1.toolTipText")); // NOI18N
        jPanel1.setName(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jPanel1.name")); // NOI18N

        lServerURL.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.lServerURL.text")); // NOI18N

        bgWMS.add(rbCbServerUrl);
        rbCbServerUrl.setSelected(true);
        rbCbServerUrl.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.rbCbServerUrl.text")); // NOI18N
        rbCbServerUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbCbServerUrlActionPerformed(evt);
            }
        });

        bgWMS.add(rbTxtServerUrl);
        rbTxtServerUrl.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.rbTxtServerUrl.text")); // NOI18N
        rbTxtServerUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbTxtServerUrlActionPerformed(evt);
            }
        });

        txtServerURL.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.txtServerURL.text")); // NOI18N
        txtServerURL.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.txtServerURL.toolTipText")); // NOI18N
        txtServerURL.setEnabled(false);

        cbServerURL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "http://aniara.smhi.se/cap_SERVICE=WMS.xml", "http://geoportal.wuppertal.de:8083/deegree/wms?", "http://serv-2118.kl.dfki.de:8888/geoserver/wms?service=WMS&version=1.1.0", "http://www2.demis.nl/worldmap/wms.asp?Service=WMS&Version=1.1.0&Request=GetCapabilities", "http://www.wms.nrw.de/geobasis/DOP", "http://mapbender.wheregroup.com/cgi-bin/mapserv?map=/data/umn/osm/osm_basic.map&VERSION=1.1.1&REQUEST=GetCapabilities&SERVICE=WMS", "http://kartor.stockholm.se/bios/wms/app/baggis/web/WMS_STHLM_ORTOFOTO_2009?", "http://kartor.stockholm.se/bios/wms/app/baggis/web/WMS_STHLM_TATORTSKARTA_RASTER?", "http://85.24.165.10/cap_SERVICE=WMS.xml" }));

        bGoWMSHeight.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.bGoWMSHeight.text")); // NOI18N
        bGoWMSHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bGoWMSHeightActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(lServerURL)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(rbCbServerUrl)
                            .add(rbTxtServerUrl))
                        .add(10, 10, 10)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtServerURL)
                            .add(cbServerURL, 0, 1, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(pbWMS, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(bGoWMSHeight)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(lServerURL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbServerURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rbCbServerUrl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtServerURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rbTxtServerUrl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(bGoWMSHeight)
                    .add(pbWMS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jPanel4.border.title"))); // NOI18N

        bCancelWMSHeight.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.bCancelWMSHeight.text")); // NOI18N
        bCancelWMSHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelWMSHeightActionPerformed(evt);
            }
        });

        bAddWMSHeight.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.bAddWMSHeight.text")); // NOI18N
        bAddWMSHeight.setEnabled(false);
        bAddWMSHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddWMSHeightActionPerformed(evt);
            }
        });

        cLayerList.setEnabled(false);

        txtHeight.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.txtHeight.text")); // NOI18N

        lHeight.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.lHeight.text")); // NOI18N

        lOpacity.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.lOpacity.text")); // NOI18N

        txtOpacity.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.txtOpacity.text")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(lHeight)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtHeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(lOpacity)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtOpacity, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 194, Short.MAX_VALUE)
                        .add(bAddWMSHeight)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(bCancelWMSHeight))
                    .add(cLayerList, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(cLayerList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 20, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bCancelWMSHeight)
                    .add(bAddWMSHeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtHeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lHeight)
                    .add(lOpacity)
                    .add(txtOpacity, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout dWMSHeightLayout = new org.jdesktop.layout.GroupLayout(dWMSHeight.getContentPane());
        dWMSHeight.getContentPane().setLayout(dWMSHeightLayout);
        dWMSHeightLayout.setHorizontalGroup(
            dWMSHeightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dWMSHeightLayout.createSequentialGroup()
                .addContainerGap()
                .add(dWMSHeightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dWMSHeightLayout.createSequentialGroup()
                        .add(732, 732, 732)
                        .add(lMaxEle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 287, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(dWMSHeightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dWMSHeightLayout.setVerticalGroup(
            dWMSHeightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dWMSHeightLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(296, 296, 296)
                .add(lMaxEle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/dfki/av/geovisualizer/app/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("MainFrame.title")); // NOI18N
        setLocationByPlatform(true);

        toolbar.setRollover(true);
        toolbar.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.toolbar.toolTipText")); // NOI18N
        toolbar.setName(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.toolbar.name")); // NOI18N

        btnGoHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/24x24/apps/earth.png"))); // NOI18N
        btnGoHome.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnGoHome.text")); // NOI18N
        btnGoHome.setFocusable(false);
        btnGoHome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoHome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnGoHome);

        btnJumpTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/24x24/actions/go-jump-4.png"))); // NOI18N
        btnJumpTo.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnJumpTo.text")); // NOI18N
        btnJumpTo.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnJumpTo.toolTipText")); // NOI18N
        btnJumpTo.setFocusable(false);
        btnJumpTo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnJumpTo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnJumpTo);
        toolbar.add(jSeparator6);

        btnVizWiz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/24x24/actions/tools-wizard-3.png"))); // NOI18N
        btnVizWiz.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnVizWiz.text")); // NOI18N
        btnVizWiz.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnVizWiz.toolTipText")); // NOI18N
        btnVizWiz.setFocusable(false);
        btnVizWiz.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVizWiz.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnVizWiz);

        btnTakeScreenShot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/24x24/devices/camera-photo-5.png"))); // NOI18N
        btnTakeScreenShot.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnTakeScreenShot.text")); // NOI18N
        btnTakeScreenShot.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnTakeScreenShot.toolTipText")); // NOI18N
        btnTakeScreenShot.setFocusable(false);
        btnTakeScreenShot.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTakeScreenShot.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(btnTakeScreenShot);
        toolbar.add(jSeparator4);

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/24x24/actions/application-exit-5.png"))); // NOI18N
        btnExit.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnExit.text")); // NOI18N
        btnExit.setToolTipText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnExit.toolTipText")); // NOI18N
        btnExit.setFocusable(false);
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        toolbar.add(btnExit);

        getContentPane().add(toolbar, java.awt.BorderLayout.PAGE_START);

        pMain.setPreferredSize(new java.awt.Dimension(1280, 720));

        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1280, 720));

        pLeftPanel.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pLeftPanel);

        pVisualization.setLayout(new java.awt.BorderLayout());

        pVisualization.add(wwPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(pVisualization);

        org.jdesktop.layout.GroupLayout pMainLayout = new org.jdesktop.layout.GroupLayout(pMain);
        pMain.setLayout(pMainLayout);
        pMainLayout.setHorizontalGroup(
            pMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pMainLayout.setVerticalGroup(
            pMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(pMain, java.awt.BorderLayout.CENTER);

        mFile.setText(bundle.getString("MainFrame.mFile.text")); // NOI18N

        miOpenKMLFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        miOpenKMLFile.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miOpenKMLFile.text")); // NOI18N
        miOpenKMLFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOpenDataActionPerformed(evt);
            }
        });
        mFile.add(miOpenKMLFile);

        miAddGeoTiff.setText(bundle.getString("MainFrame.miAddGeoTiff.text")); // NOI18N
        miAddGeoTiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOpenDataActionPerformed(evt);
            }
        });
        mFile.add(miAddGeoTiff);

        miAddShape.setText(bundle.getString("MainFrame.miAddShape.text")); // NOI18N
        miAddShape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOpenDataActionPerformed(evt);
            }
        });
        mFile.add(miAddShape);

        miAddShapeZip.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miAddShapeZip.text")); // NOI18N
        miAddShapeZip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOpenDataActionPerformed(evt);
            }
        });
        mFile.add(miAddShapeZip);
        mFile.add(jSeparator2);

        miExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/16x16/actions/application-exit-5.png"))); // NOI18N
        miExit.setText(bundle.getString("MainFrame.miExit.text")); // NOI18N
        mFile.add(miExit);

        mbMain.add(mFile);

        mEdit.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mEdit.text")); // NOI18N

        miRemoveAllLayer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        miRemoveAllLayer.setText(bundle.getString("MainFrame.miRemoveAllLayer.text")); // NOI18N
        miRemoveAllLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveAllLayerActionPerformed(evt);
            }
        });
        mEdit.add(miRemoveAllLayer);
        mEdit.add(jSeparator1);

        miTakeScreenshot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/16x16/devices/camera-photo-5.png"))); // NOI18N
        miTakeScreenshot.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miTakeScreenshot.text")); // NOI18N
        mEdit.add(miTakeScreenshot);

        mbMain.add(mEdit);

        mNavi.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mNavi.text")); // NOI18N

        miGotoKaiserslautern.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        miGotoKaiserslautern.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miGotoKaiserslautern.text")); // NOI18N
        mNavi.add(miGotoKaiserslautern);
        mNavi.add(jSeparator5);

        miGoto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        miGoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/16x16/actions/go-jump-4.png"))); // NOI18N
        miGoto.setText(bundle.getString("MainFrame.miGoto.text")); // NOI18N
        mNavi.add(miGoto);

        miFullSphere.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/16x16/apps/earth.png"))); // NOI18N
        miFullSphere.setText(bundle.getString("MainFrame.miFullSphere.text")); // NOI18N
        mNavi.add(miFullSphere);
        mNavi.add(jSeparator3);

        miSaveView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        miSaveView.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miSaveView.text")); // NOI18N
        miSaveView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveViewActionPerformed(evt);
            }
        });
        mNavi.add(miSaveView);

        mCustomViewPoints.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mCustomViewPoints.text")); // NOI18N
        mNavi.add(mCustomViewPoints);

        mbMain.add(mNavi);

        mWMS.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mWMS.text")); // NOI18N

        miAddWMS.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miAddWMS.text")); // NOI18N
        miAddWMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddWMSActionPerformed(evt);
            }
        });
        mWMS.add(miAddWMS);

        miAddWMSHeight.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miAddWMSHeight.text")); // NOI18N
        miAddWMSHeight.setEnabled(false);
        miAddWMSHeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddWMSHeightActionPerformed(evt);
            }
        });
        mWMS.add(miAddWMSHeight);

        mbMain.add(mWMS);

        mTools.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mTools.text")); // NOI18N

        miWizard.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK));
        miWizard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/16x16/actions/tools-wizard-3.png"))); // NOI18N
        miWizard.setText(bundle.getString("MainFrame.miWizard.text")); // NOI18N
        mTools.add(miWizard);
        mTools.add(jSeparator7);

        mPlugins.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mPlugins.text")); // NOI18N
        mPlugins.setEnabled(false);
        mTools.add(mPlugins);

        mbMain.add(mTools);

        mView.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mView.text")); // NOI18N

        mToolbars.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mToolbars.text")); // NOI18N
        mToolbars.setEnabled(false);

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jCheckBoxMenuItem1.text")); // NOI18N
        mToolbars.add(jCheckBoxMenuItem1);

        mView.add(mToolbars);

        mWindowMode.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mWindowMode.text")); // NOI18N
        mWindowMode.setEnabled(false);

        miSideBySide.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miSideBySide.text")); // NOI18N
        miSideBySide.setEnabled(false);
        miSideBySide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSideBySideActionPerformed(evt);
            }
        });
        mWindowMode.add(miSideBySide);

        mFullScreen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        mFullScreen.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mFullScreen.text")); // NOI18N
        mFullScreen.setEnabled(false);
        mWindowMode.add(mFullScreen);

        mView.add(mWindowMode);

        mbMain.add(mView);

        mHelp.setText(bundle.getString("MainFrame.mHelp.text")); // NOI18N

        miGeoVisualizerHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        miGeoVisualizerHelp.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miGeoVisualizerHelp.text")); // NOI18N
        miGeoVisualizerHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGeoVisualizerHelpActionPerformed(evt);
            }
        });
        mHelp.add(miGeoVisualizerHelp);

        miAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/png/16x16/actions/help-about-3.png"))); // NOI18N
        miAbout.setText(bundle.getString("MainFrame.miAbout.text")); // NOI18N
        miAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAboutActionPerformed(evt);
            }
        });
        mHelp.add(miAbout);

        mbMain.add(mHelp);

        setJMenuBar(mbMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAboutActionPerformed
        ImageIcon icon = new ImageIcon(Configuration.GEOVISUALIZER_ICON);
        ResourceBundle bundle = ResourceBundle.getBundle("project");
        String version = bundle.getString("project.version");
        JOptionPane.showMessageDialog(this, "This is the GeoVisualizer application."
                + "\nDFKI (c) 2011-2014\nVersion "+version,
                "About GeoVisualizer",
                JOptionPane.INFORMATION_MESSAGE, icon);
    }//GEN-LAST:event_miAboutActionPerformed

    private void miRemoveAllLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveAllLayerActionPerformed
        wwPanel.removeAllLayers();
    }//GEN-LAST:event_miRemoveAllLayerActionPerformed

    private void miAddWMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddWMSActionPerformed
        String server = JOptionPane.showInputDialog(this, "WMS URL", "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/germany/germany.map&&VERSION=1.1.1&REQUEST=GetCapabilities&SERVICE=WMS");
        try {
            if (server == null) {
                LOG.debug("Cancled JOptionPane.");
                return;
            }
            if (server.isEmpty()) {
                String msg = "Server URL is empty";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
            final URI serverURI = new URI(server.trim());
            wwPanel.addAllWMSLayer(serverURI);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_miAddWMSActionPerformed

    private void bGoWMSHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGoWMSHeightActionPerformed
        String urlString;
        if (rbCbServerUrl.isSelected()) {
            urlString = cbServerURL.getSelectedItem().toString();
        } else {
            urlString = txtServerURL.getText();
        }
        bGoWMSHeight.setEnabled(false);
        pbWMS.setIndeterminate(true);
        pbWMS.setVisible(true);
        SwingWorker worker = new LayerInfoRetreiver(urlString, false);
        worker.addPropertyChangeListener(this);
        worker.execute();
    }//GEN-LAST:event_bGoWMSHeightActionPerformed

    private void bAddWMSHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddWMSHeightActionPerformed
        dWMSHeight.setVisible(false);
        String urlString;
        if (rbCbServerUrl.isSelected()) {
            urlString = cbServerURL.getSelectedItem().toString();
        } else {
            urlString = txtServerURL.getText();
        }
        URI uri = URI.create(urlString);
        double wmsHeight;
        double wmsOpacity;
        try {
            wmsHeight = Double.parseDouble(txtHeight.getText());
        } catch (NumberFormatException nfe) {
            LOG.warn("The content of the \"height\" component must be a double value.");
            wmsHeight = 0.0;
            txtHeight.setText("0.0");
        }
        try {
            wmsOpacity = 1.0 - (Double.parseDouble(txtOpacity.getText()) / 100.0);
        } catch (NumberFormatException nfe) {
            LOG.warn("The content of the \"opacity\" "
                    + "component must be a double value between."
                    + "0.0 and 100.0 {}", nfe);
            wmsOpacity = 0.0;
            txtOpacity.setText("0.0");
        }
        if (cLayerList.getSelectedItem() instanceof LayerInfo) {
            LayerInfo li = (LayerInfo) cLayerList.getSelectedItem();
            wwPanel.addWMSHeightLayer(uri, li.getName(), wmsHeight, wmsOpacity);
        }
    }//GEN-LAST:event_bAddWMSHeightActionPerformed

    private void bCancelWMSHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelWMSHeightActionPerformed
        resetWMSHeightDialog();
    }//GEN-LAST:event_bCancelWMSHeightActionPerformed

    private void miAddWMSHeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddWMSHeightActionPerformed
        resetWMSHeightDialog();
        dWMSHeight.setVisible(true);
        dWMSHeight.setModal(true);
    }//GEN-LAST:event_miAddWMSHeightActionPerformed

    private void rbCbServerUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCbServerUrlActionPerformed
        if (rbCbServerUrl.isSelected()) {
            cbServerURL.setEnabled(true);
            txtServerURL.setEnabled(false);
        } else {
            cbServerURL.setEnabled(false);
            txtServerURL.setEnabled(true);
        }
    }//GEN-LAST:event_rbCbServerUrlActionPerformed

    private void rbTxtServerUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTxtServerUrlActionPerformed
        if (rbTxtServerUrl.isSelected()) {
            txtServerURL.setEnabled(true);
            cbServerURL.setEnabled(false);
        } else {
            txtServerURL.setEnabled(false);
            cbServerURL.setEnabled(true);
        }
    }//GEN-LAST:event_rbTxtServerUrlActionPerformed

    private void miSideBySideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSideBySideActionPerformed
        wwPanel.startStereo(this);
    }//GEN-LAST:event_miSideBySideActionPerformed

    private void miOpenDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miOpenDataActionPerformed
        try {
            String cmd = evt.getActionCommand();
            final JFileChooser fc = new JFileChooser();

            FileFilter fileFilter;
            // Set file filter..
            if (cmd.equalsIgnoreCase(miOpenKMLFile.getActionCommand())) {
                fileFilter = new FileNameExtensionFilter("KML/KMZ File", "kml", "kmz");
            } else if (cmd.equalsIgnoreCase(miAddGeoTiff.getActionCommand())) {
                fileFilter = new FileNameExtensionFilter("GeoTiff File ( *.tif, *.tiff)", "tif", "tiff");
            } else if (cmd.equalsIgnoreCase(miAddShape.getActionCommand())) {
                fileFilter = new FileNameExtensionFilter("ESRI Shapefile (*.shp)", "shp", "SHP");
            } else if (cmd.equalsIgnoreCase(miAddShapeZip.getActionCommand())) {
                fileFilter = new FileNameExtensionFilter("ESRI Shapefile ZIP (*.zip)", "zip", "ZIP");
            } else {
                LOG.warn("No valid action command.");
                fileFilter = null;
            }
            fc.setFileFilter(fileFilter);

            // Set latest working directory...
            XMLConfiguration xmlConfig = Configuration.getXMLConfiguration();
            String path = xmlConfig.getString("geovisualizer.working.dir");
            File dir;
            if (path != null) {
                dir = new File(path);
                if (dir.exists()) {
                    fc.setCurrentDirectory(dir);
                }
            }

            // Show dialog...
            int ret = fc.showOpenDialog(this);

            // Save currently selected working directory...
            dir = fc.getCurrentDirectory();
            path = dir.getAbsolutePath();
            xmlConfig.setProperty("geovisualizer.working.dir", path);

            if (ret != JFileChooser.APPROVE_OPTION) {
                return;
            }

            if (cmd.equalsIgnoreCase(miOpenKMLFile.getActionCommand())) {
                wwPanel.addKMLLayer(fc.getSelectedFile());
            } else if (cmd.equalsIgnoreCase(miAddGeoTiff.getActionCommand())) {
                wwPanel.addGeoTiffLayer(fc.getSelectedFile());
            } else if (cmd.equalsIgnoreCase(miAddShape.getActionCommand())
                    || cmd.equalsIgnoreCase(miAddShapeZip.getActionCommand())) {
                IVisAlgorithm algo = VisAlgorithmFactory.newInstance(VisPointCloud.class.getName());
                if (algo != null) {
                    wwPanel.addLayer(fc.getSelectedFile(), algo, null);
                } else {
                    LOG.error("VisAlgorithm {} not supported.", VisPointCloud.class.getName());
                    JOptionPane.showMessageDialog(this, "Algorithm not supported.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                LOG.warn("No valid action command.");
            }
        } catch (Exception ex) {
            LOG.error(ex.toString());
            JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_miOpenDataActionPerformed

    private void miSaveViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveViewActionPerformed

        String defaultName = "View " + viewID;
        String name = JOptionPane.showInputDialog(this, "Enter a name for the view", defaultName);
        if (name == null) {
            LOG.debug("Cancled JOptionPane.");
            return;
        }
        if (name.isEmpty()) {
            String msg = "Server URL is empty";
            LOG.error(msg);
            name = defaultName;
        }

        WorldWindowGLCanvas worldWindow = wwPanel.getWwd();
        View view = worldWindow.getView();
        String xml = view.getRestorableState();
        LOG.info(xml);

        final Position eyePosition = view.getEyePosition();
        final Position centerPosition = view.getGlobe().computePositionFromPoint(view.getCenterPoint());
        LOG.debug("Saving view: eye({}), center({})", eyePosition.toString(), centerPosition.toString());

        Action changeView = new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorldWindowGLCanvas canvas = wwPanel.getWwd();
                View view = canvas.getView();
                view.setOrientation(eyePosition, centerPosition);
                canvas.redraw();
            }
        };
        if (0 <= viewID && viewID <= 9) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke("ctrl " + viewID);
            if(keyStroke != null){
                changeView.putValue(Action.ACCELERATOR_KEY, keyStroke);
            } else {
                LOG.warn("keyStroke == null");
            }
        } else {
            LOG.debug("Short cut keys already exhausted.");
        }
        mCustomViewPoints.add(changeView);
        viewID++;
    }//GEN-LAST:event_miSaveViewActionPerformed

    private void miGeoVisualizerHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGeoVisualizerHelpActionPerformed
        try {
            Desktop desktop = Desktop.getDesktop();
            URI uri = URI.create("http://www.geovisualizer.de");
            desktop.browse(uri);
        } catch (IOException ex) {
            String msg = ex.toString();
            LOG.error(msg);
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_miGeoVisualizerHelpActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(EventHolder.LAYERINFO_RETREIVAL_COMPLETE)) {
            pbWMS.setIndeterminate(false);
            pbWMS.setVisible(false);
            cLayerList.removeAllItems();
            if (evt.getNewValue() instanceof List<?>) {
                for (LayerInfo layerInfo : (List<LayerInfo>) evt.getNewValue()) {
                    cLayerList.addItem(layerInfo);
                }
            } else if (evt.getNewValue() instanceof LayerInfo) {
                cLayerList.addItem((LayerInfo) evt.getNewValue());
            } else {
                LOG.error("Wrong event value (not instanceof LayerInfo or List<LayerInfo>)");
            }
            cLayerList.setEnabled(true);
            bAddWMSHeight.setEnabled(true);
            bGoWMSHeight.setEnabled(true);
        }

        if (evt.getPropertyName().equals(EventHolder.LAYERINFO_RETREIVAL_FAILED)) {
            resetWMSHeightDialog();
            JOptionPane.showMessageDialog(dWMSHeight,
                    "Could not retreive WMS data from server.",
                    "WMS-Server Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Reset the {@link #dWMSHeight} dialog.
     */
    private void resetWMSHeightDialog() {
        dWMSHeight.setVisible(false);
        txtServerURL.setText("http://serv-2118.kl.dfki.de:8888/geoserver/wms?service=WMS&version=1.1.0");
        txtServerURL.setEnabled(true);
        txtHeight.setText("1500.0");
        bGoWMSHeight.setEnabled(true);
        bAddWMSHeight.setEnabled(false);
        cLayerList.setEnabled(false);
        txtServerURL.setEnabled(false);
        cbServerURL.setEnabled(true);
        pbWMS.setVisible(false);
        pbWMS.setIndeterminate(false);
        rbCbServerUrl.setSelected(true);
    }

    /**
     * Init logging to logs/GeoVisualizer.log.
     */
    private static void initLogging() {
        try {
            String pattern = "%5p %d{ISO8601} %c{1} - %m%n";
            PatternLayout layout = new PatternLayout(pattern);
            XMLConfiguration xmlConfig = Configuration.getXMLConfiguration();
            String path = xmlConfig.getString("geovisualizer.user.dir");
            String logFile = path + "/logs/GeoVisualizer.log";
            RollingFileAppender appender = new RollingFileAppender(layout, logFile, false);
            appender.setMaxBackupIndex(2);
            appender.setMaxFileSize("1MB");
            rootLog.addAppender(appender);
            rootLog.setLevel(Level.ALL);
        } catch (Exception ex) {
            LOG.error(ex.toString());
        }
    }

    /**
     * Print some system information.
     */
    private static void printSystemSettings() {
        String javaClassPath = System.getProperty("java.class.path");
        LOG.debug("java.class.path: {}", javaClassPath);
        String javaExtDirs = System.getProperty("java.ext.dirs");
        LOG.debug("java.ext.dirs: {}", javaExtDirs);
        String classpath = System.getenv("CLASSPATH");
        LOG.debug("CLASSPATH: {}", classpath);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        initLogging();
        printSystemSettings();

        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mf = new MainFrame();
                mf.setIconImage(Configuration.GEOVISUALIZER_ICON);
                mf.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAddWMSHeight;
    private javax.swing.JButton bCancelWMSHeight;
    private javax.swing.JButton bGoWMSHeight;
    private javax.swing.ButtonGroup bgWMS;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnGoHome;
    private javax.swing.JButton btnJumpTo;
    private javax.swing.JButton btnTakeScreenShot;
    private javax.swing.JButton btnVizWiz;
    private javax.swing.JComboBox cLayerList;
    private javax.swing.JComboBox cbServerURL;
    private javax.swing.JDialog dWMSHeight;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lHeight;
    private javax.swing.JLabel lMaxEle;
    private javax.swing.JLabel lOpacity;
    private javax.swing.JLabel lServerURL;
    private javax.swing.JMenu mCustomViewPoints;
    private javax.swing.JMenu mEdit;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenuItem mFullScreen;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenu mNavi;
    private javax.swing.JMenu mPlugins;
    private javax.swing.JMenu mToolbars;
    private javax.swing.JMenu mTools;
    private javax.swing.JMenu mView;
    private javax.swing.JMenu mWMS;
    private javax.swing.JMenu mWindowMode;
    private javax.swing.JMenuBar mbMain;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miAddGeoTiff;
    private javax.swing.JMenuItem miAddShape;
    private javax.swing.JMenuItem miAddShapeZip;
    private javax.swing.JMenuItem miAddWMS;
    private javax.swing.JMenuItem miAddWMSHeight;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miFullSphere;
    private javax.swing.JMenuItem miGeoVisualizerHelp;
    private javax.swing.JMenuItem miGoto;
    private javax.swing.JMenuItem miGotoKaiserslautern;
    private javax.swing.JMenuItem miOpenKMLFile;
    private javax.swing.JMenuItem miRemoveAllLayer;
    private javax.swing.JMenuItem miSaveView;
    private javax.swing.JMenuItem miSideBySide;
    private javax.swing.JMenuItem miTakeScreenshot;
    private javax.swing.JMenuItem miWizard;
    private javax.swing.JPanel pLeftPanel;
    private javax.swing.JPanel pMain;
    private javax.swing.JPanel pVisualization;
    private javax.swing.JProgressBar pbWMS;
    private javax.swing.JRadioButton rbCbServerUrl;
    private javax.swing.JRadioButton rbTxtServerUrl;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JTextField txtHeight;
    private javax.swing.JTextField txtOpacity;
    private javax.swing.JTextField txtServerURL;
    // End of variables declaration//GEN-END:variables
}
