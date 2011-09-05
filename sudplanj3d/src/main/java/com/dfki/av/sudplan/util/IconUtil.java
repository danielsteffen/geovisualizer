/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dfki.av.sudplan.util;

import java.awt.Image;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class IconUtil {
    private final static Logger logger = LoggerFactory.getLogger(IconUtil.class);

    public static final ImageIcon DELETE_LAYER_24 = new javax.swing.ImageIcon(IconUtil.class.getResource("/com/dfki/av/sudplan/ui/icon/toolbar/deleteLayer24.png"));
    //ToDo Sebastian Puhl <sebastian.puhl@dfki.de>:create the icon sizes by hand and place them in the folder ? 
    public static final ImageIcon DELETE_LAYER_12 = getHalfSizeIcon(DELETE_LAYER_24);

    public static ImageIcon getHalfSizeIcon(ImageIcon iconToScale){
        if(iconToScale != null){
           return new ImageIcon(iconToScale.getImage().getScaledInstance((int)(iconToScale.getIconWidth()*0.5),(int)(iconToScale.getIconHeight()*0.5),Image.SCALE_SMOOTH));
        }
        return null;
    }
}
