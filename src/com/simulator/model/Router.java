package com.simulator.model;

import java.util.Objects;

/**
 * Representasi dari sebuah objek Router digital dalam topologi jaringan.
 * Memiliki nama perangkat dan IP Address yang unik.
 */
public class Router {
    private final String name;
    private final String ipAddress;

    /**
     * Konstruktor objek Router.
     *
     * @param name      Nama perangkat (misal: Router-Lab, Router-Pusat).
     * @param ipAddress Alamat IP perangkat (misal: 192.168.1.1, 10.0.0.1).
     */
    public Router(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Membandingkan kesamaan dua router berdasarkan nama dan IP Address-nya.
     * Sangat penting ketika mencari router dalam Set/Map.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Router router = (Router) o;
        return Objects.equals(name, router.name) && 
               Objects.equals(ipAddress, router.ipAddress);
    }

    /**
     * Menghasilkan hash code berdasarkan nama dan IP Address.
     * Digunakan agar Router dapat disimpan secara optimal di HashSet dan HashMap.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, ipAddress);
    }

    @Override
    public String toString() {
        return name + " (" + ipAddress + ")";
    }
}
