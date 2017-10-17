/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package org.slf4j.test_osgi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BundleTest  {

    FrameworkErrorListener fel = new FrameworkErrorListener();
    CheckingBundleListener mbl = new CheckingBundleListener();

    FelixHost felixHost = new FelixHost(fel, mbl);

    @Before
    protected void setUp() throws Exception {
        felixHost.doLaunch();
    }

    @After
    protected void tearDown() throws Exception {
        felixHost.stop();
    }

    @Test
    public void testSmoke() {
        System.out.println("===========" + new File(".").getAbsolutePath());
        mbl.dumpAll();
        // check that the bundle was installed
        assertTrue(mbl.exists("iBundle"));
        if (fel.errorList.size() != 0) {
            fel.dumpAll();
        }
        // check that no errors occured
        assertEquals(0, fel.errorList.size());
    }
}
