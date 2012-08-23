//
//package com.dfki.av.sudplan.stereo;
//
//import com.dfki.av.sudplan.ui.MainFrame;
//import gov.nasa.worldwind.WorldWindow;
//
///**
// * This class mainly adds the stereo feature to the SUDPLAN main frame as a
// * JMenu item in the file menu and creates a SideBySideStereSetup object on
// * selection and starts it.
// *
// * @author tarek
// */
//public class StereoSupportMain {
//
//    /**
//     * Menu item of the stereo feature.
//     */
//    private javax.swing.JMenuItem stereo;
//    /**
//     * Main frame instance.
//     */
//    private MainFrame mf;
//
//    /**
//     * Constructs a new instance of the StereoSupportMain class. Initializes the
//     * main frame to a new MainFrame instance, accesses its file menu, adds the
//     * stereo feature with its actionListeners.
//     */
//    public StereoSupportMain() {
//        mf = new MainFrame();
//        stereo = new javax.swing.JMenuItem();
//        stereo.setText("Stereo");
//        stereo.addActionListener(new java.awt.event.ActionListener() {
//
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                stereoActionPerformed(evt);
//            }
//        });
//        mf.getmFile().add(stereo);
//    }
//
//    /**
//     * Called when the jMenuItem is selected and it creates a new instance of
//     * the SideBySideStereoSetup, responsible for setting all requirements
//     * needed for the side by side stereo mode, and starts it.
//     *
//     * @param evt Fired event on selection the JMenuItem.
//     */
//    private void stereoActionPerformed(java.awt.event.ActionEvent evt) {
//        WorldWindow worldWindow = mf.getWwPanel().getWwd();
//        SideBySideStereoSetup stereoSetup = new SideBySideStereoSetup(mf, worldWindow);
//        stereoSetup.start();
//    }
//
//    public static void main(String[] args) {
//
//        /*
//         * Set the Nimbus look and feel
//         */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//
//
//
//
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /*
//         * Create and display the form
//         */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                StereoSupportMain stereoSupportMain = new StereoSupportMain();
//                stereoSupportMain.mf.setVisible(true);
//            }
//        });
//
//    }
//}
