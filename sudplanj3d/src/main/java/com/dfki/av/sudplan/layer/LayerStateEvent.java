/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import java.io.File;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class LayerStateEvent {
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:why does such a event has a file make it generic layerObject or split class
    private long timestamp;
    private File file;
    private Exception ex;

    public LayerStateEvent(long timestamp, File file, Exception ex) {
        this.timestamp = timestamp;
        this.file = file;
        this.ex = ex;
    }

    public Exception getEx() {
        return ex;
    }

    public File getFile() {
        return file;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
