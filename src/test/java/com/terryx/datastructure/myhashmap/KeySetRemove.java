package com.terryx.datastructure.myhashmap;


/**
 * @test
 * @bug 4286765
 * @summary HashMap and TreeMap entrySet().remove(k) spuriously returned
 * false if the Map previously mapped k to null.
 */

import java.util.*;

public class KeySetRemove {
    public static void main(String args[]) throws Exception {
        Map m[] = {new HashMap(), new TreeMap()};
        for (int i = 0; i < m.length; i++) {
            m[i].put("bananas", null);
            if (!m[i].keySet().remove("bananas"))
                throw new Exception("Yes, we have no bananas: " + i);
        }
    }
}
