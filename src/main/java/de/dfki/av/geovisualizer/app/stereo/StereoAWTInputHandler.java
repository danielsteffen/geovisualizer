/*
 *  StereoAWTInputHandler.java 
 *
 *  Created by DFKI AV on 01.08.2012.
 *  Copyright (c) 2011-2012 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package de.dfki.av.geovisualizer.app.stereo;

/*
 * Copyright (C) 2011 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration. All
 * Rights Reserved.
 */
import gov.nasa.worldwind.awt.AWTInputHandler;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the AWTInputHandler and does the exact same behavior with
 * 2 added features: catching null pointers exception when switching from and to
 * the stereo mode, and accessing the selectListeners assigned so as to add them
 * to the stereo mode providing the user the ability of using any controls from
 * stereo mode.
 *
 * @author tarek
 */
public class StereoAWTInputHandler extends AWTInputHandler {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(StereoAWTInputHandler.class);
    /**
     * {@link ArrayList} of {@code gov.nasa.worldwind.event.SelectListener}.
     */
    private ArrayList<SelectListener> selectListeners;

    /**
     * Constructs a new instance of the StereoAWTInputHandler which is the same
     * as the default AWTInputHandler and overrides an attribute initialization
     * which is hoverTime to be the same but with catching the thrown
     * exceptions. Initializes the selectListener list to an empty array list.
     */
    public StereoAWTInputHandler() {
        super();
        selectListeners = new ArrayList<>();
        hoverTimer = new javax.swing.Timer(600, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (StereoAWTInputHandler.this.pickMatches(
                            StereoAWTInputHandler.this.hoverObjects)) {
                        StereoAWTInputHandler.this.isHovering = true;
                        StereoAWTInputHandler.this.callSelectListeners(
                                new SelectEvent(StereoAWTInputHandler.this.wwd,
                                SelectEvent.HOVER, mousePoint,
                                StereoAWTInputHandler.this.hoverObjects));
                        StereoAWTInputHandler.this.hoverTimer.stop();
                    }
                } catch (NullPointerException e) {
                    log.error(e.toString());
                }
            }
        });
    }

    @Override
    public void addSelectListener(SelectListener listener) {
        this.eventListeners.add(SelectListener.class, listener);
        this.selectListeners.add(listener);
    }

    @Override
    public void removeSelectListener(SelectListener listener) {
        this.eventListeners.remove(SelectListener.class, listener);
        this.selectListeners.remove(listener);
    }

    /**
     * Accesses the list of all assigned
     * {@code gov.nasa.worldwind.event.SelectListener}.
     *
     * @return List of assigned {@code gov.nasa.worldwind.event.SelectListener}.
     */
    public ArrayList<SelectListener> getSelectListeners() {
        return selectListeners;
    }
}
