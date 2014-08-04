package com.eclipsesource.v8.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.v8.V8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class V8Tests {

    private V8 v8;

    @Before
    public void seutp() {
        v8 = V8.createV8Isolate();
    }

    @After
    public void tearDown() {
        v8.release();
    }

    @Test
    public void testV8Setup() {
        assertNotNull(v8);
    }

    @Test(expected = Error.class)
    public void testCannotAccessDisposedIsolate() {
        v8.release();
        v8.executeVoidScript("");
    }

    @Test
    public void testSingleThreadAccess() throws InterruptedException {
        final boolean[] result = new boolean[] { false };
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    v8.executeVoidScript("");
                } catch (Error e) {
                    result[0] = e.getMessage().contains("Invalid V8 thread access.");
                }
            }
        });
        t.start();
        t.join();

        assertTrue(result[0]);
    }

    @Test
    public void testSimpleIntScript() {
        int result = v8.executeIntScript("1+2;");

        assertEquals(3, result);
    }
}