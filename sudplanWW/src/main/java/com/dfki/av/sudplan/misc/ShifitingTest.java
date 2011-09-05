/*
 *  Created by DFKI AV on 05/09/2012.
 *  Copyright (c) 2011 DFKI GmbH, Kaiserslautern. All rights reserved.
 *  Use is subject to license terms.
 */
package com.dfki.av.sudplan.misc;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Puhl <sebastian.puhl@dfki.de>
 * @version 1.0
 * @since 1.6
 */
public class ShifitingTest {
//    private final static Logger logger = LoggerFactory.getLogger(ShifitingTest.class);
    protected static final double LEVEL_A = 0x1 << 25;
    protected static final double LEVEL_B = 0x1 << 24;
    protected static final double LEVEL_C = 0x1 << 23;
    protected static final double LEVEL_D = 0x1 << 22;
    protected static final double LEVEL_E = 0x1 << 21;
    protected static final double LEVEL_F = 0x1 << 20;
    protected static final double LEVEL_G = 0x1 << 19;
    protected static final double LEVEL_H = 0x1 << 18;
    protected static final double LEVEL_I = 0x1 << 17;
    protected static final double LEVEL_J = 0x1 << 16;
    protected static final double LEVEL_K = 0x1 << 15;
    protected static final double LEVEL_L = 0x1 << 14;
    protected static final double LEVEL_M = 0x1 << 13;
    protected static final double LEVEL_N = 0x1 << 12;
    protected static final double LEVEL_O = 0x1 << 11;
    protected static final double LEVEL_P = 0x1 << 10;

    public static void main(String[] args) {
        System.out.println("Level_A: "+Double.toHexString(LEVEL_A));
        System.out.println("Level_B: "+Double.toHexString(LEVEL_B));
        System.out.println("Level_C: "+Double.toHexString(LEVEL_C));
    }
}
