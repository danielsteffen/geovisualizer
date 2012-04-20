/*
 * MainFrame.java
 *
 * Created on 14.09.2011, 15:27:50
 */
package com.dfki.av.sudplan.ui;

import com.dfki.av.sudplan.camera.AnimatedCamera;
import com.dfki.av.sudplan.camera.SimpleCamera;
import com.dfki.av.sudplan.vis.LayerAction;
import com.dfki.av.sudplan.vis.VisualizationPanel;
import com.dfki.av.sudplan.vis.basic.VisCreateTexture;
import com.dfki.av.sudplan.vis.basic.VisPointCloud;
import com.dfki.av.sudplan.vis.core.IVisAlgorithm;
import com.dfki.av.sudplan.vis.spi.VisAlgorithmFactory;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
    /**
     * An array of WMS.
     */
    private static final String[] servers = new String[]{
        "http://serv-2118.kl.dfki.de:8888/geoserver/wms",
        "http://www.wms.nrw.de/geobasis/DOP"/*
     * ,
     * /*"http://kartor.stockholm.se/bios/wms/app/baggis/web/WMS_STHLM_ORTOFOTO_2009?"/*,
     */
    /*
     * "http://kartor.stockholm.se/bios/wms/app/baggis/web/WMS_STHLM_TATORTSKARTA_RASTER?"
     */

    };
    /**
     * The size of the {@link #wwPanel}.
     */
    private Dimension canvasSize;
    /**
     * The visualization panel wrapping the WorldWind canvas.
     */
    private VisualizationPanel wwPanel;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        this.canvasSize = new Dimension(1500, 800);
        this.wwPanel = new VisualizationPanel(canvasSize);
        this.wwPanel.setPreferredSize(canvasSize);
        this.wwPanel.getWwd().getModel().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AVKey.LAYERS)) {
                    updateLayerMenu();
                }
            }
        });
        initComponents();

        for (String server : servers) {
            addLayers(server);
        }
        updateLayerMenu();
    }

    /**
     *
     * @param server
     */
    private void addLayers(String server) {
        try {
            final URI serverURI = new URI(server.trim());

            Thread loadingThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    WMSCapabilities caps;
                    final ArrayList<LayerInfo> layerInfos = new ArrayList<LayerInfo>();
                    try {
                        caps = WMSCapabilities.retrieve(serverURI);
                        caps.parse();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return;
                    }

                    // Gather up all the named layers and make a world wind layer for each.
                    final List<WMSLayerCapabilities> namedLayerCaps = caps.getNamedLayers();
                    if (namedLayerCaps == null) {
                        log.debug("No named layers available for server: {}.", serverURI);
                        return;
                    }

                    try {
                        for (WMSLayerCapabilities lc : namedLayerCaps) {
                            Set<WMSLayerStyle> styles = lc.getStyles();
                            if (styles == null || styles.isEmpty()) {
                                LayerInfo layerInfo = LayerInfo.create(caps, lc, null);
                                layerInfos.add(layerInfo);
                            } else {
                                for (WMSLayerStyle style : styles) {
                                    LayerInfo layerInfo = LayerInfo.create(caps, lc, style);
                                    layerInfos.add(layerInfo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return;
                    }

                    // Fill the panel with the layer titles.
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            for (LayerInfo layerInfo : layerInfos) {
                                Object component = LayerInfo.createComponent(layerInfo.caps, layerInfo.params);
                                if (component instanceof Layer) {
                                    Layer layer = (Layer) component;
                                    LayerList layers = wwPanel.getWwd().getModel().getLayers();
                                    layer.setEnabled(false);
                                    if (!layers.contains(layer)) {
                                        ApplicationTemplate.insertBeforePlacenames(wwPanel.getWwd(), layer);
                                    }
                                }
                            }
                        }
                    });
                }
            });
            loadingThread.setPriority(Thread.MIN_PRIORITY);
            loadingThread.start();
        } catch (URISyntaxException ex) {
            log.error(ex.getMessage());
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

        dGoTo = new javax.swing.JDialog();
        pGoTo = new javax.swing.JPanel();
        lLatitude = new javax.swing.JLabel();
        lLongitude = new javax.swing.JLabel();
        txtLatitude = new javax.swing.JTextField();
        txtLongitude = new javax.swing.JTextField();
        btnCancelGoToDialoag = new javax.swing.JButton();
        btnGo = new javax.swing.JButton();
        jopAddServer = new javax.swing.JOptionPane();
        dChangeLayerOrder = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        pMain = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pServer = new javax.swing.JPanel();
        pVisualization = new javax.swing.JPanel();
        mbMain = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        miGotoLinz = new javax.swing.JMenuItem();
        miGotoPraque = new javax.swing.JMenuItem();
        miGoToStockhom = new javax.swing.JMenuItem();
        miGotoWuppertal = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        miFullSphere = new javax.swing.JMenuItem();
        miGoto = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        mLayer = new javax.swing.JMenu();
        mLayers = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mAddLayer = new javax.swing.JMenu();
        miAddGeoTiff = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        miAddShape = new javax.swing.JMenuItem();
        miAddShapeZip = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        miAddWMS = new javax.swing.JMenuItem();
        miRemoveAllLayer = new javax.swing.JMenuItem();
        miChangeLayerOrder = new javax.swing.JMenuItem();
        mWizard = new javax.swing.JMenu();
        miWizard = new javax.swing.JMenuItem();
        mHelp = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dfki/av/sudplan/ui/Bundle"); // NOI18N
        dGoTo.setTitle(bundle.getString("MainFrame.dGoTo.title")); // NOI18N
        dGoTo.setAlwaysOnTop(true);
        dGoTo.setMinimumSize(new java.awt.Dimension(230, 150));
        dGoTo.setResizable(false);

        pGoTo.setMaximumSize(new java.awt.Dimension(200, 100));

        lLatitude.setLabelFor(txtLatitude);
        lLatitude.setText(bundle.getString("MainFrame.lLatitude.text")); // NOI18N

        lLongitude.setLabelFor(txtLongitude);
        lLongitude.setText(bundle.getString("MainFrame.lLongitude.text")); // NOI18N

        txtLatitude.setText(bundle.getString("MainFrame.txtLatitude.text")); // NOI18N

        txtLongitude.setText(bundle.getString("MainFrame.txtLongitude.text")); // NOI18N

        javax.swing.GroupLayout pGoToLayout = new javax.swing.GroupLayout(pGoTo);
        pGoTo.setLayout(pGoToLayout);
        pGoToLayout.setHorizontalGroup(
            pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lLongitude)
                    .addComponent(lLatitude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLongitude, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                .addContainerGap())
        );
        pGoToLayout.setVerticalGroup(
            pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lLatitude)
                    .addComponent(txtLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lLongitude)
                    .addComponent(txtLongitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCancelGoToDialoag.setText(bundle.getString("MainFrame.btnCancelGoToDialoag.text")); // NOI18N
        btnCancelGoToDialoag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelGoToDialoagActionPerformed(evt);
            }
        });

        btnGo.setText(bundle.getString("MainFrame.btnGo.text")); // NOI18N
        btnGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dGoToLayout = new javax.swing.GroupLayout(dGoTo.getContentPane());
        dGoTo.getContentPane().setLayout(dGoToLayout);
        dGoToLayout.setHorizontalGroup(
            dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dGoToLayout.createSequentialGroup()
                        .addComponent(btnGo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelGoToDialoag))
                    .addComponent(pGoTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dGoToLayout.setVerticalGroup(
            dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dGoToLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pGoTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dGoToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelGoToDialoag)
                    .addComponent(btnGo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dChangeLayerOrder.setTitle(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.dChangeLayerOrder.title")); // NOI18N
        dChangeLayerOrder.setMinimumSize(new java.awt.Dimension(435, 300));
        dChangeLayerOrder.setResizable(false);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jLabel1.text")); // NOI18N

        btnUp.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnUp.text")); // NOI18N
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });

        btnDown.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.btnDown.text")); // NOI18N
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnDown, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnUp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDown))
        );

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jList1);

        javax.swing.GroupLayout dChangeLayerOrderLayout = new javax.swing.GroupLayout(dChangeLayerOrder.getContentPane());
        dChangeLayerOrder.getContentPane().setLayout(dChangeLayerOrderLayout);
        dChangeLayerOrderLayout.setHorizontalGroup(
            dChangeLayerOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dChangeLayerOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dChangeLayerOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dChangeLayerOrderLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dChangeLayerOrderLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dChangeLayerOrderLayout.setVerticalGroup(
            dChangeLayerOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dChangeLayerOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dChangeLayerOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .addGroup(dChangeLayerOrderLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        pMain.setPreferredSize(new java.awt.Dimension(1200, 1024));

        jSplitPane1.setResizeWeight(0.1);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setLastDividerLocation(1);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1024, 768));

        pServer.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pServer);

        pVisualization.setLayout(new java.awt.BorderLayout());

        pVisualization.add(wwPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(pVisualization);

        javax.swing.GroupLayout pMainLayout = new javax.swing.GroupLayout(pMain);
        pMain.setLayout(pMainLayout);
        pMainLayout.setHorizontalGroup(
            pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
        );
        pMainLayout.setVerticalGroup(
            pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );

        mFile.setText(bundle.getString("MainFrame.mFile.text")); // NOI18N

        miGotoLinz.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        miGotoLinz.setText(bundle.getString("MainFrame.miGotoLinz.text")); // NOI18N
        miGotoLinz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoLinzActionPerformed(evt);
            }
        });
        mFile.add(miGotoLinz);

        miGotoPraque.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        miGotoPraque.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miGotoPraque.text")); // NOI18N
        miGotoPraque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoPraqueActionPerformed(evt);
            }
        });
        mFile.add(miGotoPraque);

        miGoToStockhom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        miGoToStockhom.setText(bundle.getString("MainFrame.miGoToStockhom.text")); // NOI18N
        miGoToStockhom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGoToStockhomActionPerformed(evt);
            }
        });
        mFile.add(miGoToStockhom);

        miGotoWuppertal.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        miGotoWuppertal.setText(bundle.getString("MainFrame.miGotoWuppertal.text")); // NOI18N
        miGotoWuppertal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoWuppertalActionPerformed(evt);
            }
        });
        mFile.add(miGotoWuppertal);
        mFile.add(jSeparator5);

        miFullSphere.setText(bundle.getString("MainFrame.miFullSphere.text")); // NOI18N
        miFullSphere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miFullSphereActionPerformed(evt);
            }
        });
        mFile.add(miFullSphere);

        miGoto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        miGoto.setText(bundle.getString("MainFrame.miGoto.text")); // NOI18N
        miGoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoActionPerformed(evt);
            }
        });
        mFile.add(miGoto);
        mFile.add(jSeparator2);

        miExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK));
        miExit.setText(bundle.getString("MainFrame.miExit.text")); // NOI18N
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        mFile.add(miExit);

        mbMain.add(mFile);

        mLayer.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mLayer.text")); // NOI18N

        mLayers.setText(bundle.getString("MainFrame.mLayers.text")); // NOI18N
        mLayer.add(mLayers);
        mLayer.add(jSeparator1);

        mAddLayer.setText(bundle.getString("MainFrame.mAddLayer.text")); // NOI18N

        miAddGeoTiff.setText(bundle.getString("MainFrame.miAddGeoTiff.text")); // NOI18N
        miAddGeoTiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddGeoTiffActionPerformed(evt);
            }
        });
        mAddLayer.add(miAddGeoTiff);
        mAddLayer.add(jSeparator6);

        miAddShape.setText(bundle.getString("MainFrame.miAddShape.text")); // NOI18N
        miAddShape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddShapeActionPerformed(evt);
            }
        });
        mAddLayer.add(miAddShape);

        miAddShapeZip.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miAddShapeZip.text")); // NOI18N
        miAddShapeZip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddShapeZipActionPerformed(evt);
            }
        });
        mAddLayer.add(miAddShapeZip);
        mAddLayer.add(jSeparator4);

        miAddWMS.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miAddWMS.text")); // NOI18N
        miAddWMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddWMSActionPerformed(evt);
            }
        });
        mAddLayer.add(miAddWMS);

        mLayer.add(mAddLayer);

        miRemoveAllLayer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        miRemoveAllLayer.setText(bundle.getString("MainFrame.miRemoveAllLayer.text")); // NOI18N
        miRemoveAllLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveAllLayerActionPerformed(evt);
            }
        });
        mLayer.add(miRemoveAllLayer);

        miChangeLayerOrder.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miChangeLayerOrder.text")); // NOI18N
        miChangeLayerOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miChangeLayerOrderActionPerformed(evt);
            }
        });
        mLayer.add(miChangeLayerOrder);

        mbMain.add(mLayer);

        mWizard.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mWizard.text")); // NOI18N

        miWizard.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK));
        miWizard.setText(bundle.getString("MainFrame.miWizard.text")); // NOI18N
        miWizard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miWizardActionPerformed(evt);
            }
        });
        mWizard.add(miWizard);

        mbMain.add(mWizard);

        mHelp.setText(bundle.getString("MainFrame.mHelp.text")); // NOI18N

        miAbout.setText(bundle.getString("MainFrame.miAbout.text")); // NOI18N
        miAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAboutActionPerformed(evt);
            }
        });
        mHelp.add(miAbout);

        mbMain.add(mHelp);

        setJMenuBar(mbMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_miExitActionPerformed

    private void miGotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGotoActionPerformed
        dGoTo.setVisible(true);
    }//GEN-LAST:event_miGotoActionPerformed

    private void btnCancelGoToDialoagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelGoToDialoagActionPerformed
        dGoTo.setVisible(false);
    }//GEN-LAST:event_btnCancelGoToDialoagActionPerformed

    private void btnGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoActionPerformed
        try {
            double lat = Double.parseDouble(txtLatitude.getText());
            double lon = Double.parseDouble(txtLongitude.getText());
            wwPanel.setCamera(new SimpleCamera(lat, lon, 200000.0));
            dGoTo.setVisible(false);
        } catch (NumberFormatException nfe) {
            if (log.isWarnEnabled()) {
                log.warn("The content of the \"latitude\" and \"longitude\" "
                        + "component must be a double value.");
            }
        }
    }//GEN-LAST:event_btnGoActionPerformed

    private void miAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAboutActionPerformed
        JOptionPane.showMessageDialog(this, "This is the sudplan3D application."
                + "\nDFKI (c) 2011-2012",
                "About Sudplan3D",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_miAboutActionPerformed

    private void miAddGeoTiffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddGeoTiffActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("GeoTiff File ( *.tif, *.tiff)", "tif", "tiff"));

        int ret = fc.showOpenDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) {
            return;
        }

        wwPanel.addLayer(fc.getSelectedFile(), new VisCreateTexture(), null);

    }//GEN-LAST:event_miAddGeoTiffActionPerformed

    private void miAddShapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddShapeActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("ESRI Shapefile (*.shp)", "shp"));

        int ret = fc.showOpenDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) {
            return;
        }

        IVisAlgorithm algo = VisAlgorithmFactory.newInstance(VisPointCloud.class.getName());
        if (algo != null) {
            wwPanel.addLayer(fc.getSelectedFile(), algo, null);
        } else {
            log.error("VisAlgorithm {} not supported.", VisPointCloud.class.getName());
        }
    }//GEN-LAST:event_miAddShapeActionPerformed

    private void miWizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miWizardActionPerformed
        wwPanel.runVisWiz();
    }//GEN-LAST:event_miWizardActionPerformed

    private void miRemoveAllLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miRemoveAllLayerActionPerformed
        wwPanel.removeAllLayers();
    }//GEN-LAST:event_miRemoveAllLayerActionPerformed

    private void miGoToStockhomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGoToStockhomActionPerformed
        wwPanel.setCamera(new AnimatedCamera(59.328, 18.047, 20000.0));
    }//GEN-LAST:event_miGoToStockhomActionPerformed

    private void miGotoLinzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGotoLinzActionPerformed
        wwPanel.setCamera(new AnimatedCamera(48.2323, 14.3350, 20000.0));
    }//GEN-LAST:event_miGotoLinzActionPerformed

    private void miGotoWuppertalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGotoWuppertalActionPerformed
        wwPanel.setCamera(new AnimatedCamera(51.249, 7.0832, 1510.0));
    }//GEN-LAST:event_miGotoWuppertalActionPerformed

    private void miFullSphereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miFullSphereActionPerformed
        wwPanel.setCamera(new AnimatedCamera(37.0, 27.0, 19000000.0));
    }//GEN-LAST:event_miFullSphereActionPerformed

    private void miGotoPraqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miGotoPraqueActionPerformed
        wwPanel.setCamera(new AnimatedCamera(50.08781, 14.42046, 20000.0));
    }//GEN-LAST:event_miGotoPraqueActionPerformed

    private void miAddShapeZipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddShapeZipActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("ESRI Shapefile ZIP (*.zip)", "zip"));

        int ret = fc.showOpenDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) {
            return;
        }

        IVisAlgorithm algo = VisAlgorithmFactory.newInstance(VisPointCloud.class.getName());
        if (algo != null) {
            wwPanel.addLayer(fc.getSelectedFile(), algo, null);
        } else {
            log.error("VisAlgorithm {} not supported.", VisPointCloud.class.getName());
        }
    }//GEN-LAST:event_miAddShapeZipActionPerformed

    private void miAddWMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddWMSActionPerformed
        String s = JOptionPane.showInputDialog(this, "Add WMS", "Add WMS", JOptionPane.PLAIN_MESSAGE);
        addLayers(s);
    }//GEN-LAST:event_miAddWMSActionPerformed

    private void miChangeLayerOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miChangeLayerOrderActionPerformed
        updateListModel();
        dChangeLayerOrder.setVisible(true);
    }//GEN-LAST:event_miChangeLayerOrderActionPerformed

    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        int id = jList1.getSelectedIndex();
        if (id > 0) {
            LayerList layerlist = wwPanel.getWwd().getModel().getLayers();
            Layer layer = layerlist.remove(id);
            layerlist.add(id - 1, layer);
            updateListModel();
            jList1.setSelectedIndex(id - 1);
            jList1.ensureIndexIsVisible(id - 1);
        }
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        int id = jList1.getSelectedIndex();
        if (id < jList1.getModel().getSize() - 1) {
            LayerList layerlist = wwPanel.getWwd().getModel().getLayers();
            Layer layer = layerlist.remove(id);
            layerlist.add(id + 1, layer);
            updateListModel();
            jList1.setSelectedIndex(id + 1);
            jList1.ensureIndexIsVisible(id + 1);
        }
    }//GEN-LAST:event_btnDownActionPerformed

    private void updateListModel() {
        DefaultListModel listModel = new DefaultListModel();
        LayerList layerlist = wwPanel.getWwd().getModel().getLayers();
        for (Layer l : layerlist) {
            listModel.addElement(l.getName());
        }
        jList1.setModel(listModel);
    }

    private void updateLayerMenu() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mLayers.removeAll();
                for (Layer layer : wwPanel.getWwd().getModel().getLayers()) {
                    LayerAction action = new LayerAction(layer, wwPanel.getWwd(), layer.isEnabled());
                    JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(action);
                    cbmi.setSelected(action.isSelected());
                    cbmi.setName(layer.getName());
                    mLayers.add(cbmi);
                }
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;




                }
            }
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
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelGoToDialoag;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnGo;
    private javax.swing.JButton btnUp;
    private javax.swing.JDialog dChangeLayerOrder;
    private javax.swing.JDialog dGoTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JOptionPane jopAddServer;
    private javax.swing.JLabel lLatitude;
    private javax.swing.JLabel lLongitude;
    private javax.swing.JMenu mAddLayer;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenu mLayer;
    private javax.swing.JMenu mLayers;
    private javax.swing.JMenu mWizard;
    private javax.swing.JMenuBar mbMain;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miAddGeoTiff;
    private javax.swing.JMenuItem miAddShape;
    private javax.swing.JMenuItem miAddShapeZip;
    private javax.swing.JMenuItem miAddWMS;
    private javax.swing.JMenuItem miChangeLayerOrder;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miFullSphere;
    private javax.swing.JMenuItem miGoToStockhom;
    private javax.swing.JMenuItem miGoto;
    private javax.swing.JMenuItem miGotoLinz;
    private javax.swing.JMenuItem miGotoPraque;
    private javax.swing.JMenuItem miGotoWuppertal;
    private javax.swing.JMenuItem miRemoveAllLayer;
    private javax.swing.JMenuItem miWizard;
    private javax.swing.JPanel pGoTo;
    private javax.swing.JPanel pMain;
    private javax.swing.JPanel pServer;
    private javax.swing.JPanel pVisualization;
    private javax.swing.JTextField txtLatitude;
    private javax.swing.JTextField txtLongitude;
    // End of variables declaration//GEN-END:variables
}
