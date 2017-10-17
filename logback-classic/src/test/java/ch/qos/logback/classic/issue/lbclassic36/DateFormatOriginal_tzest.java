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
package ch.qos.logback.classic.issue.lbclassic36;

import org.junit.After;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.DateTime;

public class DateFormatOriginal_tzest  {
    public static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
    static final long NANOS_IN_ONE_SEC = 1000 * 1000 * 1000L;

    @Test
    public void testSynchronized() throws Exception {
        SynchronizedDateFormatter formatter = new SynchronizedDateFormatter();
        int threads = 10;
        int iterations = 10000;
        Thread[] formatThreads = new Thread[threads];
        Date date = new Date();

        for (int i = 0; i < threads; i++) {
            formatThreads[i] = new DateFormatThread(formatter, date, iterations);
        }
        long start = System.nanoTime();
        for (Thread thread : formatThreads) {
            thread.start();
        }
        for (Thread thread : formatThreads) {
            thread.join();
        }
        long end = System.nanoTime();
        double actual = ((double) (end - start)) / NANOS_IN_ONE_SEC;
        System.out.printf("Synchronized DateFormat: %,.4f seconds\n", actual);

    }

    @Test
    public void testUnSynchronized() throws Exception {
        UnsynchronizedDateFormatter formatter = new UnsynchronizedDateFormatter();
        int threads = 10;
        int iterations = 10000;
        Thread[] formatThreads = new Thread[threads];
        Date date = new Date();

        for (int i = 0; i < threads; i++) {
            formatThreads[i] = new DateFormatThread(formatter, date, iterations);
        }
        long start = System.nanoTime();
        for (Thread thread : formatThreads) {
            thread.start();
        }
        for (Thread thread : formatThreads) {
            thread.join();
        }
        long end = System.nanoTime();
        double actual = ((double) (end - start)) / NANOS_IN_ONE_SEC;
        System.out.printf("Unsynchronized DateFormat: %,.4f seconds\n", actual);

    }

    @Test
    public void testThreadLocal() throws Exception {
        ThreadLocalDateFormatter formatter = new ThreadLocalDateFormatter();
        int threads = 10;
        int iterations = 10000;
        Thread[] formatThreads = new Thread[threads];
        Date date = new Date();

        for (int i = 0; i < threads; i++) {
            formatThreads[i] = new DateFormatThread(formatter, date, iterations);
        }
        long start = System.nanoTime();
        for (Thread thread : formatThreads) {
            thread.start();
        }
        for (Thread thread : formatThreads) {
            thread.join();
        }
        long end = System.nanoTime();
        double actual = ((double) (end - start)) / NANOS_IN_ONE_SEC;
        System.out.printf("ThreadLocal DateFormat: %,.4f seconds\n", actual);

    }

    public interface Formatter {
        String format(Date date);
    }

    public static class SynchronizedDateFormatter implements Formatter {
        SimpleDateFormat simpleFormat = new SimpleDateFormat(ISO8601_PATTERN);

        public synchronized String format(Date date) {
            return simpleFormat.format(date);
        }
    }

    public static class UnsynchronizedDateFormatter implements Formatter {
        public synchronized String format(Date date) {
            return new SimpleDateFormat(ISO8601_PATTERN).format(date);
        }
    }

    public static class ThreadLocalDateFormatter implements Formatter {
        ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
            protected synchronized SimpleDateFormat initialValue() {
                return new SimpleDateFormat(ISO8601_PATTERN);
            }
        };

        public String format(Date date) {
            return formatter.get().format(date);
        }
    }

    public static class DateFormatThread extends Thread {
        Formatter formatter;
        Date date;
        long iterCount;

        public DateFormatThread(Formatter f, Date date, long iterations) {
            this.formatter = f;
            this.date = date;
            this.iterCount = iterations;
        }

        public void run() {
            for (int i = 0; i < iterCount; i++) {
                formatter.format(this.date);
            }
        }
    }

}
