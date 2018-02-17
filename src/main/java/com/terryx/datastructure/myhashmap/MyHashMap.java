package com.terryx.datastructure.myhashmap;

import java.util.*;

/**
 * @author taoranxue on 2/17/18 2:14 PM.
 */
public class MyHashMap<K, V> implements Map<K, V> {
    class Node<K, V> {
        // hash and key can't be change
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry e = (Entry) o;
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }
    }

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;


    /**
     * The table, initialized on first use, and resized as
     * necessary. When allocated, length is always a power of two.
     * (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)
     */
    Node<K, V>[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     *
     * @serial
     */
    int threshold;


    public MyHashMap() {
    }

    @Override
    public V get(Object key) {
        int hash = hash(key);
        Node<K, V> e = getNode(hash, key);
        return e == null ? null : e.value;
    }

    /**
     * Implements Map.get and related methods
     *
     * @param hash hash for key
     * @param key  the key
     * @return the node, or null if none
     */
    Node<K, V> getNode(int hash, Object key) {
        int n = table.length;
        if (table != null && n > 0 && table[hash & (n - 1)] != null) {
            Node<K, V> e = table[hash & (n - 1)];
            do {
                if (e.hash == hash && (e.key == key || (key != null && key.equals(e.key)))) {
                    return e;
                }
            } while ((e = e.next) != null);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        if (table == null || table.length == 0) {
            table = resize();
        }
        int n = table.length, index = hash & (n - 1);
        if (table[index] == null) {
            table[index] = new Node<>(hash, key, value, null);
        } else {
            Node<K, V> p = table[index], e = null;
            // hash code & key to identify the element
            do {
                if (p.hash == hash && (p.key == key || (key != null && key.equals(p.key)))) {
                    e = p;
                    break;
                }
                if (p.next == null) {
                    e = new Node<>(hash, key, value, null);
                    break;
                }
            } while ((p = p.next) != null);


            if (e != null) {
                // existing mapped value;
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        if (++size > threshold)
            resize();
        return null;
    }

    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     *
     * @return the table
     */
    Node<K, V>[] resize() {
        int oldCap = (table == null) ? 0 : table.length, oldThr = threshold;
        int newCap, newThr;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return table;
            }
            newCap = oldCap << 1;
            newThr = oldThr << 1; // double threshold
        } else {
            // first to use
            // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        threshold = newThr;
        @SuppressWarnings("unchecked")
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCap];
        if (table != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K, V> e = table[j];
                if (e != null) {
                    table[j] = null;
                    if (e.next == null) {
                        newTable[e.hash & (newCap - 1)] = e;
                    } else {
                        // split hash linked list
                        Node<K, V> loHead = null, loTail = null, hiHead = null, hiTail = null, next = null;
                        do {
                            next = e.next;
                            // if have high bit?
                            if ((e.hash & oldCap) == 0) {
                                //stay on old list
                                if (loHead == null) {
                                    loHead = e;
                                } else {
                                    loTail.next = e;
                                }
                                loTail = e;
                            } else {
                                // move to new list
                                if (hiHead == null) {
                                    hiHead = e;
                                } else {
                                    hiTail.next = e;
                                }
                                hiTail = e;
                            }
                        } while ((e = e.next) != null);

                        if (loHead != null) {
                            loTail.next = null;
                            newTable[j] = loHead;
                        }

                        if (hiHead != null) {
                            hiTail.next = null;
                            newTable[j + newCap] = hiHead;
                        }
                    }
                }
            }
        }
        table = newTable;
        return newTable;
    }

    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     */
    static int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return (key == null) ? 0 : h ^ (h >>> 16);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (table != null && size > 0) {
            for (int i = 0; i < table.length; ++i) {
                Node<K, V> e = table[i];
                if (e != null) {
                    do {
                        if (e.value == value || (value != null && value.equals(e.value))) {
                            return true;
                        }
                    } while ((e = e.next) != null);
                }
            }
        }
        return false;
    }


    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

}
