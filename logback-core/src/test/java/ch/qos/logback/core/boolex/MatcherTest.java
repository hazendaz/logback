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
package ch.qos.logback.core.boolex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;

public class MatcherTest {

    Context context;
    Matcher matcher;

    @Before
    public void setUp() throws Exception {
        context = new ContextBase();
        matcher = new Matcher();
        matcher.setContext(context);
        matcher.setName("testMatcher");
    }

    @After
    public void tearDown() throws Exception {
        matcher = null;
    }

    @Test
    public void testFullRegion() throws Exception {
        matcher.setRegex(".*test.*");
        matcher.start();
        assertTrue(matcher.matches("test"));
        assertTrue(matcher.matches("xxxxtest"));
        assertTrue(matcher.matches("testxxxx"));
        assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    @Test
    public void testPartRegion() throws Exception {
        matcher.setRegex("test");
        matcher.start();
        assertTrue(matcher.matches("test"));
        assertTrue(matcher.matches("xxxxtest"));
        assertTrue(matcher.matches("testxxxx"));
        assertTrue(matcher.matches("xxxxtestxxxx"));
    }

    @Test
    public void testCaseInsensitive() throws Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(false);
        matcher.start();

        assertTrue(matcher.matches("TEST"));
        assertTrue(matcher.matches("tEst"));
        assertTrue(matcher.matches("tESt"));
        assertTrue(matcher.matches("TesT"));
    }

    @Test
    public void testCaseSensitive() throws Exception {
        matcher.setRegex("test");
        matcher.setCaseSensitive(true);
        matcher.start();

        assertFalse(matcher.matches("TEST"));
        assertFalse(matcher.matches("tEst"));
        assertFalse(matcher.matches("tESt"));
        assertFalse(matcher.matches("TesT"));
    }
}
