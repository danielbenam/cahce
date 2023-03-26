package com.danielbenami.cahce.service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class CacheService<K, V> implements Map<K, V> {

    private final AtomicInteger count = new AtomicInteger(0);
    private final float loadFactor;
    private Node<K, V>[] buckets;
    private ReentrantReadWriteLock[] locks;
    private int threshold;

    public CacheService(int capacity, float loadFactor) {

        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be of power 2: " + capacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);

        locks = new ReentrantReadWriteLock[capacity];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }

        buckets = (Node<K, V>[]) new Node[capacity];
    }


    @Override
    public void put(K key, V value) {
        if (key == null || value == null) throw new IllegalArgumentException();
        int hash = hash(key);
        ReentrantReadWriteLock lock = getLockFor(hash);
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            int bucketIndex = getBucketIndex(hash);
            Node<K, V> node = buckets[bucketIndex];
            while (node != null) {
                if (isKeyEquals(key, hash, node)) {
                    node.value = value;
                    return;
                }
                node = node.next;
            }
            buckets[bucketIndex] = new Node<>(hash, key, value, node);
            count.incrementAndGet();
            if (count.intValue() >= threshold) {
                resize(buckets.length * 2);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void resize(int newCapacity) {
        Node<K, V>[] newNodesTable = new Node[newCapacity];

        locks = new ReentrantReadWriteLock[newCapacity];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }
        transfer(newNodesTable);
        buckets = newNodesTable;
        threshold = (int) (newCapacity * loadFactor);
    }


    private void transfer(Node<K, V>[] newTable) {
        int newCapacity = newTable.length;
        for (Node<K, V> node : buckets) {
            while (null != node) {
                Node<K, V> next = node.next;
                int i = node.hash % newCapacity;
                node.next = newTable[i];
                newTable[i] = node;
                node = next;
            }
        }
    }


    @Override
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException();
        int hash = hash(key);
        ReentrantReadWriteLock lock = getLockFor(hash);
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            Node<K, V> node = buckets[getBucketIndex(hash)];
            while (node != null) {
                if (isKeyEquals(key, hash, node)) {
                    return node.value;
                }
                node = node.next;
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }


    private boolean isKeyEquals(Object key, int hash, Node<K, V> node) {
        return node.hash == hash &&
                node.key == key || (node.key.equals(key));
    }

    private int getBucketIndex(int hash) {
        return Math.abs(hash) % buckets.length;
    }

    private ReentrantReadWriteLock getLockFor(int hash) {
        return locks[Math.abs(hash) % locks.length];
    }


    private static class Node<K, V> {
        final int hash;
        K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

}
