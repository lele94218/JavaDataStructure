package com.terryx.datastructure.myhashmap;


/**
 * @author taoranxue on 2/17/18 6:18 PM.
 */

/*
 * @test
 * @bug 6306829
 * @summary Verify assertions in get() javadocs
 * @author Martin Buchholz
 */

import java.util.*;

public class Get {

    private static void realMain(String[] args) throws Throwable {
        testMap(new MyHashMap<Character, Boolean>());
    }

    private static void put(Map<Character, Boolean> m,
                            Character key, Boolean value,
                            Boolean oldValue) throws Throwable {
        if (oldValue != null) {
            check(m.containsValue(oldValue));
        /* commented since not implement */
//            check(m.values().contains(oldValue));
        }
        equal(m.put(key, value), oldValue);
        equal(m.get(key), value);
        check(!m.isEmpty());
        check(m.containsValue(value));
        check(m.containsKey(key));

        /* commented since not implement */

//        check(m.keySet().contains(key));
//        check(m.values().contains(value));
    }

    private static void testMap(Map<Character, Boolean> m) throws Throwable {
        // We verify following assertions in get(Object) method javadocs
        boolean permitsNullKeys = true;
        boolean permitsNullValues = true;
        boolean usesIdentity = false;

        System.out.println(m.getClass());
        put(m, 'A', true, null);
        put(m, 'A', false, true);       // Guaranteed identical by JLS
        put(m, 'B', true, null);
        put(m, new Character('A'), false, usesIdentity ? null : false);
        if (permitsNullKeys) {
            try {
                put(m, null, true, null);
                put(m, null, false, true);
            } catch (Throwable t) {
                unexpected(t);
            }
        } else {
            try {
                m.get(null);
                fail();
            } catch (NullPointerException e) {
            } catch (Throwable t) {
                unexpected(t);
            }

            try {
                m.put(null, true);
                fail();
            } catch (NullPointerException e) {
            } catch (Throwable t) {
                unexpected(t);
            }
        }
        if (permitsNullValues) {
            try {
                put(m, 'C', null, null);
                put(m, 'C', true, null);
                put(m, 'C', null, true);
            } catch (Throwable t) {
                unexpected(t);
            }
        } else {
            try {
                m.put('A', null);
                fail();
            } catch (NullPointerException e) {
            } catch (Throwable t) {
                unexpected(t);
            }

            try {
                m.put('C', null);
                fail();
            } catch (NullPointerException e) {
            } catch (Throwable t) {
                unexpected(t);
            }
        }
    }

    //--------------------- Infrastructure ---------------------------
    static volatile int passed = 0, failed = 0;

    static void pass() {
        passed++;
    }

    static void fail() throws Throwable {
        failed++;
        Thread.dumpStack();
        throw new Exception("failed...");
    }

    static void fail(String msg) throws Throwable {
        System.out.println(msg);
        fail();
    }

    static void unexpected(Throwable t) {
        failed++;
        t.printStackTrace();
    }

    static void check(boolean cond) throws Throwable {
        if (cond) pass();
        else fail();
    }

    static void equal(Object x, Object y) throws Throwable {
        if (x == null ? y == null : x.equals(y)) pass();
        else {
            System.out.println(x + " not equal to " + y);
            fail();
        }
    }

    public static void main(String[] args) throws Throwable {
        try {
            realMain(args);
        } catch (Throwable t) {
            unexpected(t);
        }

        System.out.printf("%nPassed = %d, failed = %d%n%n", passed, failed);
        if (failed > 0) throw new Exception("Some tests failed");
    }
}
