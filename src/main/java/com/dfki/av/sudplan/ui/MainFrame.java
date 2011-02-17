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

import com.dfki.av.sudplan.control.ComponentController;
import com.dfki.av.sudplan.ui.vis.VisualisationComponent;
import com.dfki.av.sudplan.ui.vis.VisualisationComponentPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
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
import javax.swing.KeyStroke;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
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
public class MainFrame extends javax.swing.JFrame {

    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: save window Size/Position
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: save Layout
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>: either make toolbar unfloatable or make it possible to reattach
    private final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private Icon visualisationIcon;
    private Icon layerIcon;
    private Icon controlIcon;
    private View visualisationView;
    private View layerView;
    private View controlView;

    /*ToDo this should be done via interfaces in order to be able to exchange the components
     * Possible place could be the controler class
     */
    private VisualisationComponentPanel visualisationPanel = new VisualisationComponentPanel();
    private SimpleLayerControlPanel layerPanel = new SimpleLayerControlPanel();
    private SimpleControlPanel controlPanel = new SimpleControlPanel();
    private RootWindow layoutRootWindow;
    private final StringViewMap viewMap = new StringViewMap();
    private final Map<String, JMenuItem> viewMenuMap = new HashMap<String, JMenuItem>();
    ResourceBundle i18n = ResourceBundle.getBundle("com/dfki/av/sudplan/ui/Bundle");
    private ComponentController componentController;

    /** Creates new form MainFrame */
    public MainFrame() {
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
//    pack();
    }

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

        frameMainPanel = new javax.swing.JPanel();
        frameToolBar = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        cmdHome = new javax.swing.JButton();
        cmdTexture = new javax.swing.JToggleButton();
        cmdLight = new javax.swing.JToggleButton();
        frameMenueBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        visualisationMenueItem = new javax.swing.JMenuItem();
        layerMenueItem = new javax.swing.JMenuItem();
        controlMenueItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dfki/av/sudplan/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("MainFrame.title")); // NOI18N

        frameMainPanel.setLayout(new java.awt.BorderLayout());

        frameToolBar.setRollover(true);

        jPanel1.setMinimumSize(new java.awt.Dimension(20, 20));
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 20));

        cmdHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/toolbar/home.gif"))); // NOI18N
        cmdHome.setToolTipText(bundle.getString("MainFrame.cmdHome.toolTipText")); // NOI18N
        cmdHome.setBorderPainted(false);
        cmdHome.setFocusable(false);
        cmdHome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdHome.setMargin(new java.awt.Insets(2, 4, 2, 4));
        cmdHome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdHomeActionPerformed(evt);
            }
        });

        cmdTexture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/toolbar/layerIcon24.png"))); // NOI18N
        cmdTexture.setSelected(true);
        cmdTexture.setText(bundle.getString("MainFrame.cmdTexture.text")); // NOI18N
        cmdTexture.setEnabled(false);
        cmdTexture.setFocusable(false);
        cmdTexture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdTexture.setMargin(new java.awt.Insets(2, 4, 2, 4));
        cmdTexture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdTexture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdTextureActionPerformed(evt);
            }
        });

        cmdLight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/dfki/av/sudplan/ui/icon/toolbar/lightbulb24.png"))); // NOI18N
        cmdLight.setSelected(true);
        cmdLight.setText(bundle.getString("MainFrame.cmdLight.text")); // NOI18N
        cmdLight.setEnabled(false);
        cmdLight.setFocusable(false);
        cmdLight.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cmdLight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdLight.setMargin(new java.awt.Insets(2, 4, 2, 4));
        cmdLight.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdLight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdHome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdTexture)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdLight)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdHome, cmdLight, cmdTexture});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(cmdLight, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdHome, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdTexture)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdHome, cmdLight, cmdTexture});

        frameToolBar.add(jPanel1);

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
            .addComponent(frameMainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
            .addComponent(frameToolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(frameToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(frameMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void cmdHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdHomeActionPerformed
      componentController.getVisualisationComponent().gotoToHome();
}//GEN-LAST:event_cmdHomeActionPerformed

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

  private void cmdLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLightActionPerformed
      componentController.getVisualisationComponent().enableDirectedLight(cmdLight.isSelected());
  }//GEN-LAST:event_cmdLightActionPerformed

  private void cmdTextureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdTextureActionPerformed
      componentController.enableDEMTexture(cmdTexture.isSelected());
  }//GEN-LAST:event_cmdTextureActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdHome;
    private javax.swing.JToggleButton cmdLight;
    private javax.swing.JToggleButton cmdTexture;
    private javax.swing.JMenuItem controlMenueItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel frameMainPanel;
    private javax.swing.JMenuBar frameMenueBar;
    private javax.swing.JToolBar frameToolBar;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuItem layerMenueItem;
    private javax.swing.JMenuItem visualisationMenueItem;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables

    private void initLayoutFramework() {
        layoutRootWindow = DockingUtil.createRootWindow(viewMap, true);
        visualisationView = new View(i18n.getString("MainFrame.visalualisation.view.title"),
                borderIcon(visualisationIcon, 0, 3, 0, 1),
                visualisationPanel);
        viewMap.addView("visualisation", visualisationView);
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
        layerIcon = new javax.swing.ImageIcon(getClass().getResource(
                i18n.getString("MainFrame.layer.view.icon")));
        controlIcon = new javax.swing.ImageIcon(getClass().getResource(
                i18n.getString("MainFrame.control.view.icon")));
    }

    private void doDefaultLayout() {
        layoutRootWindow.setWindow(new SplitWindow(
                true,
                0.25225961f, controlView, visualisationView));
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

    public void enableDEMButtons(final boolean enabled){
        cmdLight.setEnabled(enabled);
        cmdTexture.setEnabled(enabled);
    }

    public void enableControls(final boolean enabled){
        controlPanel.enableRecoloringControls(enabled);
    }
}
