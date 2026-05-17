package com.simulator.datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.EmptyStackException;

/**
 * Struktur data Stack (Tumpukan) kustom yang diimplementasikan dengan Singly Linked List.
 * Stack bekerja dengan prinsip LIFO (Last-In-First-Out).
 * Digunakan untuk merekam jejak perjalanan paket data (Hop History) dan mendukung backtracking.
 *
 * @param <T> Tipe data generic elemen di dalam stack.
 */
public class CustomStack<T> {
    private Node<T> top;
    private int size;

    /**
     * Inisialisasi Stack kosong.
     */
    public CustomStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Memasukkan (push) data baru ke posisi teratas stack.
     * Operasi ini memakan waktu konstan O(1).
     *
     * @param data Elemen yang ingin ditambahkan.
     */
    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.setNext(top);
        top = newNode;
        size++;
    }

    /**
     * Mengeluarkan (pop) dan mengembalikan data dari posisi teratas stack.
     * Operasi ini memakan waktu konstan O(1).
     *
     * @return Data teratas yang dikeluarkan.
     * @throws EmptyStackException Jika stack dalam keadaan kosong.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.getData();
        top = top.getNext();
        size--;
        return data;
    }

    /**
     * Melihat (peek) data teratas stack tanpa mengeluarkannya.
     * Operasi ini memakan waktu konstan O(1).
     *
     * @return Data teratas saat ini.
     * @throws EmptyStackException Jika stack dalam keadaan kosong.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.getData();
    }

    /**
     * Memeriksa apakah stack kosong.
     *
     * @return true jika stack tidak memiliki elemen, false jika sebaliknya.
     */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Mendapatkan jumlah elemen di dalam stack saat ini.
     *
     * @return Jumlah elemen.
     */
    public int size() {
        return size;
    }

    /**
     * Mengonversi isi stack menjadi list bertipe java.util.List dengan urutan dari bawah ke atas.
     * Hal ini sangat penting untuk menampilkan jejak rute (Trace Route) secara kronologis
     * (dari Router sumber ke Router tujuan).
     *
     * @return List elemen yang terurut dari bawah ke atas (insertion order).
     */
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = top;
        
        // Telusuri dari top ke bottom (LIFO order)
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        
        // Balikkan list agar menjadi urutan dari bottom ke top (FIFO / insertion order)
        Collections.reverse(list);
        return list;
    }
}
