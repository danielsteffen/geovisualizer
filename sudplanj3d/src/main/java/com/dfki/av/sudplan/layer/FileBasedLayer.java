/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.layer;

import java.io.File;
import java.net.URL;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public abstract class FileBasedLayer extends AbstractLayer{
    protected  File file;

    public FileBasedLayer(final URL url) throws LayerIntialisationException{
        this(url.getFile());
    }    

    public FileBasedLayer(final File file) throws LayerIntialisationException{
        this.file = file;
        setName(getFilePrefix(file.getName()));
        initialiseLayerFromFile();
    }

    public FileBasedLayer(final String file) throws LayerIntialisationException{
        this(new File(file));        
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        final File oldValue = this.file;
        this.file = file;
        changeSupport.firePropertyChange("file", oldValue, file);
    }

    abstract protected void initialiseLayerFromFile() throws LayerIntialisationException;

    private String getFilePrefix(String fileName){
        if(fileName != null & fileName.contains(".")){
            return fileName.substring(0,fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }
}
