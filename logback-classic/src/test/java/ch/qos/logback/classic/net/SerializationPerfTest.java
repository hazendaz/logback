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
package ch.qos.logback.classic.net;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.net.testObjectBuilders.Builder;
import ch.qos.logback.classic.net.testObjectBuilders.MinimalSerBuilder;
import ch.qos.logback.classic.net.testObjectBuilders.TrivialLoggingEventVOBuilder;

public class SerializationPerfTest {

    ObjectOutputStream oos;

    int loopNumber = 10000;
    int resetFrequency = 100;
    int pauseFrequency = 10;
    long pauseLengthInMillis = 20;

    /**
     * <p>
     * Run the test with a MockSocketServer or with a NOPOutputStream
     * 
     * <p>
     * Run with external mock can be done using the ExternalMockSocketServer. It
     * needs to be launched from a separate JVM. The ExternalMockSocketServer does
     * not consume the events but passes through the available bytes that it is
     * receiving.
     * 
     * <p>
     * For example, with 4 test methods, you can launch the
     * ExternalMockSocketServer this way:
     * </p>
     * <p>
     * <code>java ch.qos.logback.classic.net.ExternalMockSocketServer 4</code>
     * </p>
     */
    boolean runWithExternalMockServer = true;

    /**
     * Last results:
     * Data sent mesured in kilobytes.
     * Avg time mesured in microsecs.
     * 
     * NOPOutputStream: 
     *   |                |  Runs | Avg time | Data sent |
     *   | MinimalObj Ext | 10000 |          |           |
     *   | MinimalObj Ser | 10000 |          |           |
     *   | LoggEvent Ext  | 10000 |          |           |
     *   | LoggEvent Ser  | 10000 |          |           |
     * 
     * External MockServer with 45 letters-long message: on localhost
     * (always the same message)
     *       |                |  Runs | Avg time | Data sent |
     *   | MinimalObj Ext | 10000 |      -   |       -   |
     *   | MinimalObj Ser | 10000 |     74   |     248   |
     *   | LoggEvent Ext  | 10000 |      -   |       -   |
     *   | LoggEvent Ser  | 10000 |    156   |     835   |
     *       pauseFrequency = 10 and pauseLengthInMillis = 20
     *
     * External MockServer with 45 letters-long message: on localhost
     * (different message each time)
     *       |                |  Runs | Avg time | Data sent |
     *   | MinimalObj Ext | 10000 |          |           |
     *   | MinimalObj Ser | 10000 |     73   |    1139   |
     *   | LoggEvent Ext  | 10000 |          |           |
     *   | LoggEvent Ser  | 10000 |    162   |    1752   |
     *       pauseFrequency = 10 and pauseLengthInMillis = 20
     *
     * External MockServer with 45 letters-long message: on PIXIE
     * (always the same message)
     *       |                |  Runs | Avg time | Data sent |
     *   | MinimalObj Ext | 10000 |      -   |       -   |
     *   | MinimalObj Ser | 10000 |     29   |     248   |
     *   | LoggEvent Ext  | 10000 |      -   |       -   |
     *   | LoggEvent Ser  | 10000 |     42   |     835   |
     *       pauseFrequency = 10 and pauseLengthInMillis = 20
     *
     * External MockServer with 45 letters-long message: on PIXIE
     * (different message each time)
     *       |                |  Runs | Avg time | Data sent |
     *   | MinimalObj Ext | 10000 |          |           |
     *   | MinimalObj Ser | 10000 |     27   |    1139   |
     *   | LoggEvent Ext  | 10000 |          |           |
     *   | LoggEvent Ser  | 10000 |     44   |    1752   |
     *       pauseFrequency = 10 and pauseLengthInMillis = 20
     *
     */

    @Before
    public void setUp() throws Exception {
        if (runWithExternalMockServer) {
            oos = new ObjectOutputStream(new Socket("localhost", ExternalMockSocketServer.PORT).getOutputStream());
        } else {
            oos = new ObjectOutputStream(new NOPOutputStream());
        }
    }

    @After
    public void tearDown() throws Exception {
        oos.close();
        oos = null;
    }

    @Test
    public void runPerfTest(Builder<?> builder, String label) throws Exception {
        // long time1 = System.nanoTime();

        // Object builtObject = builder.build(1);

        // first run for just in time compiler
        int resetCounter = 0;
        int pauseCounter = 0;
        for (int i = 0; i < loopNumber; i++) {
            try {
                oos.writeObject(builder.build(i));
                oos.flush();
                if (++resetCounter >= resetFrequency) {
                    oos.reset();
                    resetCounter = 0;
                }
                if (++pauseCounter >= pauseFrequency) {
                    Thread.sleep(pauseLengthInMillis);
                    pauseCounter = 0;
                }

            } catch (IOException ex) {
                fail(ex.getMessage());
            }
        }

        // second run
        Long t1;
        Long t2;
        Long total = 0L;
        resetCounter = 0;
        pauseCounter = 0;
        // System.out.println("Beginning mesured run");
        for (int i = 0; i < loopNumber; i++) {
            try {
                t1 = System.nanoTime();
                oos.writeObject(builder.build(i));
                oos.flush();
                t2 = System.nanoTime();
                total += (t2 - t1);
                if (++resetCounter >= resetFrequency) {
                    oos.reset();
                    resetCounter = 0;
                }
                if (++pauseCounter >= pauseFrequency) {
                    Thread.sleep(pauseLengthInMillis);
                    pauseCounter = 0;
                }
            } catch (IOException ex) {
                fail(ex.getMessage());
            }
        }
        total /= 1000;
        System.out.println(label + " : average time = " + total / loopNumber + " microsecs after " + loopNumber + " writes.");
    }

    @Test
    public void testWithMinimalSerialization() throws Exception {
        Builder<?> builder = new MinimalSerBuilder();
        runPerfTest(builder, "Minimal object serialization");
    }

    @Test
    public void testWithSerialization() throws Exception {
        Builder<?> builder = new TrivialLoggingEventVOBuilder();
        runPerfTest(builder, "LoggingEventVO object serialization");
    }

}
