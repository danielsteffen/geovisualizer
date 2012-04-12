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
import com.dfki.av.sudplan.vis.wiz.VisWizIterator;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwindx.examples.WMSLayersPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static final String[] servers = new String[]{
        "http://serv-2118.kl.dfki.de:8888/geoserver/wms",
        "http://www.wms.nrw.de/geobasis/DOP"
    };
    private Dimension canvasSize;
    private VisualizationPanel wwPanel;
    private int previousTabIndex;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        this.canvasSize = new Dimension(1200, 800);
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
        initWMSPanel();
        updateLayerMenu();
    }

    /**
     * Initialize the WMS server panel.
     */
    private void initWMSPanel() {
        jTabbedPane1.setTitleAt(0, "+");
        for (int i = 0; i < servers.length; i++) {
            // i+1 to place all server tabs to the right of the Add Server tab
            addTab(i + 1, servers[i]);
        }
        // Display the first server pane by default.
        jTabbedPane1.setSelectedIndex(this.jTabbedPane1.getTabCount() > 0 ? 1 : 0);
        previousTabIndex = this.jTabbedPane1.getSelectedIndex();
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {

            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });
    }

    /**
     * Add a server to the tabbed dialog at position
     * <code>position</code>.
     *
     * @param position
     * @param server
     * @return
     */
    private WMSLayersPanel addTab(int position, String server) {

        try {
            WMSLayersPanel layersPanel = new WMSLayersPanel(this.wwPanel.getWwd(), server, new Dimension(100, 100));
            this.jTabbedPane1.add(layersPanel, BorderLayout.CENTER);
            String title = layersPanel.getServerDisplayString();
            this.jTabbedPane1.setTitleAt(position, title != null && title.length() > 0 ? title : server);

            // Add a listener to notice wms layer selections and tell the layer panel to reflect the new state.
            layersPanel.addPropertyChangeListener("LayersPanelUpdated", new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
//                    this.getLayerPanel().update(wwPanel.getWwd());
//                    log.debug("property change event from layers panel");
                }
            });

            return layersPanel;
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(null, "Server URL is invalid", "Invalid Server URL",
                    JOptionPane.ERROR_MESSAGE);
            jTabbedPane1.setSelectedIndex(previousTabIndex);
            return null;
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
        pMain = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pServer = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        pVisualization = new javax.swing.JPanel();
        mbMain = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        miFullSphere = new javax.swing.JMenuItem();
        mGoto = new javax.swing.JMenu();
        miGoToStockhom = new javax.swing.JMenuItem();
        miGotoLinz = new javax.swing.JMenuItem();
        miGotoWuppertal = new javax.swing.JMenuItem();
        miGotoPraque = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
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
        miRemoveAllLayer = new javax.swing.JMenuItem();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        pMain.setPreferredSize(new java.awt.Dimension(1200, 1024));

        jSplitPane1.setResizeWeight(0.1);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setLastDividerLocation(1);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1024, 768));

        pServer.setPreferredSize(new java.awt.Dimension(150, 603));
        pServer.setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(200, 603));

        jPanel1.setPreferredSize(new java.awt.Dimension(33, 50));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        pServer.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(pServer);

        pVisualization.setPreferredSize(new java.awt.Dimension(100, 100));
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

        miFullSphere.setText(bundle.getString("MainFrame.miFullSphere.text")); // NOI18N
        miFullSphere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miFullSphereActionPerformed(evt);
            }
        });
        mFile.add(miFullSphere);

        mGoto.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.mGoto.text")); // NOI18N

        miGoToStockhom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        miGoToStockhom.setText(bundle.getString("MainFrame.miGoToStockhom.text")); // NOI18N
        miGoToStockhom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGoToStockhomActionPerformed(evt);
            }
        });
        mGoto.add(miGoToStockhom);

        miGotoLinz.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        miGotoLinz.setText(bundle.getString("MainFrame.miGotoLinz.text")); // NOI18N
        miGotoLinz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoLinzActionPerformed(evt);
            }
        });
        mGoto.add(miGotoLinz);

        miGotoWuppertal.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        miGotoWuppertal.setText(bundle.getString("MainFrame.miGotoWuppertal.text")); // NOI18N
        miGotoWuppertal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoWuppertalActionPerformed(evt);
            }
        });
        mGoto.add(miGotoWuppertal);

        miGotoPraque.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        miGotoPraque.setText(org.openide.util.NbBundle.getMessage(MainFrame.class, "MainFrame.miGotoPraque.text")); // NOI18N
        miGotoPraque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoPraqueActionPerformed(evt);
            }
        });
        mGoto.add(miGotoPraque);
        mGoto.add(jSeparator3);

        miGoto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        miGoto.setText(bundle.getString("MainFrame.miGoto.text")); // NOI18N
        miGoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miGotoActionPerformed(evt);
            }
        });
        mGoto.add(miGoto);

        mFile.add(mGoto);
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

        mLayer.add(mAddLayer);

        miRemoveAllLayer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        miRemoveAllLayer.setText(bundle.getString("MainFrame.miRemoveAllLayer.text")); // NOI18N
        miRemoveAllLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveAllLayerActionPerformed(evt);
            }
        });
        mLayer.add(miRemoveAllLayer);

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
                "About Sudplan",
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
        if(algo != null){
            wwPanel.addLayer(fc.getSelectedFile(), algo, null);
        } else {
            log.error("VisAlgorithm {} not supported.", VisPointCloud.class.getName());
        }
    }//GEN-LAST:event_miAddShapeActionPerformed

    private void miWizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miWizardActionPerformed
        // Check all vis algos and collect them somehow.
        WizardDescriptor.Iterator iterator = new VisWizIterator();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator);
        // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
        // {1} will be replaced by WizardDescriptor.Iterator.name()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
        wizardDescriptor.setTitle("VisWiz");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            Object data = wizardDescriptor.getProperty("SelectedDataSource");
            IVisAlgorithm visAlgo = (IVisAlgorithm) wizardDescriptor.getProperty("SelectedVisualization");
            String[] dataAttributes = (String[]) wizardDescriptor.getProperty("SelectedDataAttributes");
            wwPanel.addLayer(data, visAlgo, dataAttributes);
        }
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
        if(algo != null){
            wwPanel.addLayer(fc.getSelectedFile(), algo, null);
        } else {
            log.error("VisAlgorithm {} not supported.", VisPointCloud.class.getName());
        }        
    }//GEN-LAST:event_miAddShapeZipActionPerformed

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
    private javax.swing.JButton btnGo;
    private javax.swing.JDialog dGoTo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JOptionPane jopAddServer;
    private javax.swing.JLabel lLatitude;
    private javax.swing.JLabel lLongitude;
    private javax.swing.JMenu mAddLayer;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenu mGoto;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenu mLayer;
    private javax.swing.JMenu mLayers;
    private javax.swing.JMenu mWizard;
    private javax.swing.JMenuBar mbMain;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miAddGeoTiff;
    private javax.swing.JMenuItem miAddShape;
    private javax.swing.JMenuItem miAddShapeZip;
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

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {
        if (jTabbedPane1.getSelectedIndex() != 0) {
            previousTabIndex = jTabbedPane1.getSelectedIndex();
            return;
        }

        String server = JOptionPane.showInputDialog("Enter wms server URL");
        if (server == null || server.length() < 1) {
            jTabbedPane1.setSelectedIndex(previousTabIndex);
            return;
        }

        // Respond by adding a new WMSLayerPanel to the tabbed pane.
        if (addTab(jTabbedPane1.getTabCount(), server.trim()) != null) {
            jTabbedPane1.setSelectedIndex(jTabbedPane1.getTabCount() - 1);
        }
    }
}
