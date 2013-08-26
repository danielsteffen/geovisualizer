/*
 * SnapshotAction.java
 *
 * Created by DFKI AV on 23.11.2012.
 * Copyright (c) 2011-2013 DFKI GmbH, Kaiserslautern. All rights reserved.
 * Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.ui;

import com.jogamp.opengl.util.awt.Screenshot;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copied from {@code gov.nasa.worldwindx.examples.util.ScreenShotAction}.
 * Changed name of initial snapshot and visibility of object members.
 *
 * @author Daniel Steffen <daniel.steffen at dfki.de>
 */
public class SnapshotAction extends AbstractAction implements RenderingListener {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(SnapshotAction.class);
    /**
     * The {@link WorldWindow}.
     */
    private WorldWindow wwd;
    /**
     * The {@link File} to save the snapshot.
     */
    private File snapshotFile;
    /**
     * The {@link JFileChooser}.
     */
    private JFileChooser fileChooser;

    /**
     *
     * @param wwd
     */
    public SnapshotAction(WorldWindow wwd) {
        super("Snap Shot");
        this.wwd = wwd;
        this.fileChooser = new JFileChooser();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Component frame = wwd instanceof Component ? ((Component) wwd).getParent() : null;
        snapshotFile = this.chooseFile(frame);
    }

    /**
     *
     * @param parentFrame
     * @return {@link File}
     */
    private File chooseFile(Component parentFrame) {
        File outFile = null;

        try {
            while (true) {
                fileChooser.setDialogTitle("Save Screen Shot");
                File file = new File(composeSuggestedName());
                fileChooser.setSelectedFile(file);

                int status = fileChooser.showSaveDialog(parentFrame);
                if (status != JFileChooser.APPROVE_OPTION) {
                    return null;
                }

                outFile = fileChooser.getSelectedFile();
                if (outFile == null) {// Shouldn't happen, but include a reaction just in case
                    JOptionPane.showMessageDialog(parentFrame, "Please select a location for the image file.",
                            "No Location Selected", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                if (!outFile.getPath().endsWith(".png")) {
                    outFile = new File(outFile.getPath() + ".png");
                }

                if (outFile.exists()) {
                    status = JOptionPane.showConfirmDialog(parentFrame,
                            "Replace existing file\n" + outFile.getName() + "?",
                            "Overwrite Existing File?", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (status == JOptionPane.NO_OPTION) {
                        continue;
                    }
                    if (status != JOptionPane.YES_OPTION) {
                        return null;
                    }
                }
                break;
            }
        } catch (Exception e) {
            LOG.error(e.toString());
        }

        this.wwd.removeRenderingListener(this); // ensure not to add a duplicate
        this.wwd.addRenderingListener(this);

        return outFile;
    }

    @Override
    public void stageChanged(RenderingEvent event) {
        if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP) && this.snapshotFile != null) {
            try {
                GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                int[] viewport = new int[4];
                glad.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                Screenshot.writeToFile(snapshotFile, viewport[2] + 10, viewport[3], false);
                glad.getGL().glViewport(0, 0, glad.getWidth(), glad.getHeight());
                LOG.debug("Image saved to file {}", snapshotFile.getPath());
            } catch (IOException e) {
                LOG.error(e.toString());
            } finally {
                this.snapshotFile = null;
                this.wwd.removeRenderingListener(this);
            }
        }
    }

    /**
     *
     *
     * @return Composed suggested name as {@link String}
     */
    private String composeSuggestedName() {
        String baseName = "GeoVisualizer-SnapShot";
        String suffix = ".png";
        File currentDirectory = this.fileChooser.getCurrentDirectory();
        File candidate = new File(currentDirectory.getPath() + File.separatorChar + baseName + suffix);
        for (int i = 1; candidate.exists(); i++) {
            String sequence = String.format("%03d", i);
            candidate = new File(currentDirectory.getPath() + File.separatorChar + baseName + sequence + suffix);
        }

        return candidate.getPath();
    }
}
