package com.simulator.datastructure;

/**
 * Representasi Node (simpul) untuk struktur data Stack kustom.
 * Setiap node menyimpan sebuah data generic T dan referensi ke node berikutnya.
 *
 * @param <T> Tipe data generic yang disimpan dalam node.
 */
public class Node<T> {
    private T data;
    private Node<T> next;

    /**
     * Konstruktor untuk membuat Node baru dengan data yang ditentukan.
     *
     * @param data Nilai data yang disimpan di dalam simpul ini.
     */
    public Node(T data) {
        this.data = data;
        this.next = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
