# 🌐 Network Topology Simulator CLI

[![Java Version](https://img.shields.io/badge/Java-8%20%2F%2011%20%2F%2017%2B-orange.svg?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg?style=for-the-badge)](#)
[![Aesthetics](https://img.shields.io/badge/UI-Premium%20CLI%20ANSI-cyan.svg?style=for-the-badge)](#)

Sebuah program simulasi jaringan komputer interaktif berbasis Command Line Interface (CLI) menggunakan bahasa pemrograman **Java**. Proyek ini dirancang untuk mensimulasikan dan menyelesaikan masalah **Penentuan Jalur (Routing)** dan **Pelacakan Jejak Paket data (Traceability)** pada sebuah topologi jaringan yang kompleks dengan menggabungkan konsep Graph, Set, dan struktur data Stack kustom.

---

## 📌 Masalah yang Diselesaikan

Pada topologi jaringan komputer yang kompleks dan redundan, menentukan jalur terbaik serta melacak pergerakan paket data secara akurat tanpa terjebak dalam *routing loop* (perputaran rute tanpa akhir) merupakan tantangan utama.

Aplikasi ini menyimulasikan pengiriman paket data menggunakan algoritma **DFS (Depth-First Search)** dengan **Backtracking**. Setiap lompatan router terekam secara dinamis dalam **Stack Kustom**, dan pengulangan rute dicegah menggunakan pelacakan **Set** (visited).

---

## ⚙️ Desain & Arsitektur Struktur Data

Program ini dibangun menggunakan konsep Object-Oriented Programming (OOP) yang bersih (*Clean Code*) dan mengombinasikan beberapa struktur data fundamental tanpa pustaka (*library*) pihak ketiga:

1. **Graph (Adjacency List)**: 
   Memetakan hubungan kabel/koneksi antar perangkat secara timbal balik (*Undirected Graph*). Direpresentasikan dengan struktur `Map<Router, List<Router>>` menggunakan `LinkedHashMap` untuk menjaga konsistensi urutan penelusuran.
2. **Custom Stack**: 
   Struktur data Tumpukan (LIFO - *Last In First Out*) yang diimplementasikan secara **kustom** menggunakan referensi *Singly Linked List* (`Node<T>` dan `CustomStack<T>`) **tanpa menggunakan `java.util.Stack` bawaan**. Struktur ini menyimpan riwayat lompatan aktif paket secara kronologis.
3. **HashSet (Visited List)**: 
   Digunakan untuk mendata router yang telah disinggahi selama pencarian jalur aktif guna mencegah terjadinya paket berputar-putar di sirkuit koneksi yang sama (*routing loop*).

---

## 🖼️ Skema Topologi Jaringan (ASCII Art)

Berikut adalah visualisasi diagram koneksi kabel jaringan *full-duplex* (timbal balik) default yang diinisialisasi oleh program:

```text
               [Router-Lab] (192.168.1.1)
                 /                    \
                /                      \
    [Router-GedungA]                  [Router-Pusat] (10.0.0.1)
      (172.16.1.1)                     /      |      \
           |                          /       |       \
    [Router-GedungB]                 /   [Router-ISP]  \
      (172.16.2.1)                /   (200.10.10.1)  \
           \                     /           |        \
            \                   /            |         \
             [Router-Server] (10.0.2.1) <-------------┘
```

---

## ✨ Fitur Utama

- 🖥️ **Manajemen Perangkat Digital**: Menyimpan informasi Router dengan atribut Nama dan Alamat IP unik.
- 🗺️ **Detail Topologi Jaringan**: Menampilkan daftar hubungan kabel dan tabel tetangga (*Adjacency List*) dari setiap router secara terstruktur.
- ⚡ **Simulasi Routing DFS**: Menampilkan log perjalanan paket secara real-time yang mencatat peristiwa **PUSH** saat router disinggahi.
- ⏪ **Mekanisme Backtracking**: Otomatis mendeteksi jalan buntu (*dead-end*), mencatat peristiwa **POP** untuk melacak balik rute, dan mencoba rute alternatif.
- 🌐 **Pelacakan Jejak (Trace Route)**: Mencetak hasil pelacakan jalur yang datanya diambil langsung dari Stack kustom, disajikan dalam bentuk tabel rapi mirip utilitas `tracert` pada CMD Windows atau `traceroute` pada Terminal Linux.
- 🎨 **CLI Premium**: Memanfaatkan ANSI Escape Colors untuk menyajikan visualisasi log berwarna (Hijau untuk sukses, Merah untuk backtrack/buntu, Cyan untuk hop router, Kuning untuk IP).

---

## 📂 Struktur Direktori Proyek

```text
network-topologi-simulator/
├── bin/                          # Folder hasil kompilasi (.class)
├── src/
│   └── com/
│       └── simulator/
│           ├── datastructure/
│           │   ├── Node.java         # Simpul Generic Linked List
│           │   └── CustomStack.java  # Tumpukan LIFO Kustom
│           ├── model/
│           │   └── Router.java       # Representasi Perangkat Router
│           ├── network/
│           │   └── NetworkTopology.java # Graph & Logika DFS/Backtracking
│           └── Main.java             # Menu CLI & Entry Point Utama
└── README.md                     # Dokumentasi Proyek
```

---

## 🚀 Panduan Menjalankan Program

Pastikan komputer Anda sudah terinstal **Java Development Kit (JDK 8 / 11 / 17 atau versi di atasnya)**.

### 1. Kloning Repositori ini
```bash
git clone https://github.com/username-anda/network-topologi-simulator.git
cd network-topologi-simulator
```

### 2. Kompilasi Kode Sumber Java
Gunakan perintah `javac` untuk mengompilasi semua modul kode sumber ke dalam direktori hasil kompilasi (`bin`):
```bash
mkdir -p bin
javac -d bin src/com/simulator/datastructure/*.java src/com/simulator/model/*.java src/com/simulator/network/*.java src/com/simulator/Main.java
```

### 3. Eksekusi Program Simulator
Jalankan program utama yang telah dikompilasi menggunakan perintah `java`:
```bash
java -cp bin com.simulator.Main
```

### 4. Eksekusi Pengujian Otomatis Backtracking
Untuk melihat secara langsung bagaimana algoritma DFS menghadapi jalan buntu dan melakukan operasi penelusuran balik (backtracking) secara otomatis, Anda bisa mengompilasi dan menjalankan berkas pengujian scratch terkendali:
```bash
# Kompilasi berkas pengujian
javac -cp bin -d bin /home/bimadev/.gemini/antigravity/brain/c384c23c-c1ae-431c-b507-d6723c2e5156/scratch/TestBacktracking.java

# Jalankan pengujian
java -cp bin com.simulator.scratch.TestBacktracking
```

---

## 📊 Contoh Output Pengujian

### A. Contoh Log Pengiriman Paket & Hasil Trace Route (Sukses)
Berikut adalah simulasi transmisi paket dari `Router-Lab` menuju `Router-Server`:

```text
[SIMULASI] Memulai pengiriman paket data...
Dari   : Router-Lab (192.168.1.1)
Menuju : Router-Server (10.0.2.1)
======================================================================
⚡ [LOG] Mengunjungi Router-Lab         [192.168.1.1  ] -> PUSH ke Stack.
⚡ [LOG] Mengunjungi Router-GedungA     [172.16.1.1   ] -> PUSH ke Stack.
⚡ [LOG] Mengunjungi Router-GedungB     [172.16.2.1   ] -> PUSH ke Stack.
⚡ [LOG] Mengunjungi Router-Pusat       [10.0.0.1     ] -> PUSH ke Stack.
⚡ [LOG] Mengunjungi Router-Server      [10.0.2.1     ] -> PUSH ke Stack.
======================================================================
🎉 [SUKSES] Paket data berhasil mencapai tujuan!

======================================================================
                 🌐 HASIL PELACAKAN JALUR (TRACE ROUTE)               
======================================================================
 Hop     Nama Router            IP Address         Status              
----------------------------------------------------------------------
 [0  ]   Router-Lab               192.168.1.1          [Asal / Source]
 [1  ]   Router-GedungA           172.16.1.1           [Transit]  
 [2  ]   Router-GedungB           172.16.2.1           [Transit]  
 [3  ]   Router-Pusat             10.0.0.1             [Transit]  
 [4  ]   Router-Server            10.0.2.1             [Tujuan / Destination]
----------------------------------------------------------------------
Total Lompatan (Hops) : 4
Total Router Dilalui  : 5
======================================================================
```

### B. Contoh Log Pendeteksian Jalan Buntu & Backtracking
Ketika program menjelajahi simpul jalan buntu (misalnya `Router-DeadEnd`), program secara otomatis melakukan operasi **POP** keluar dari Stack untuk membersihkan rute:

```text
⚡ [LOG] Mengunjungi Router-A           [10.0.0.1     ] -> PUSH ke Stack.
⚡ [LOG] Mengunjungi Router-DeadEnd     [10.0.0.99    ] -> PUSH ke Stack.
⚠️ [BUNTU] Router-DeadEnd tidak memiliki koneksi baru ke tujuan.
⏪ [BACKTRACK] POP Router-DeadEnd dari Stack. Kembali ke Router-A.
⚡ [LOG] Mengunjungi Router-B           [10.0.0.2     ] -> PUSH ke Stack.
⚡ [LOG] Mengunjungi Router-C           [10.0.0.3     ] -> PUSH ke Stack.
```

---

## 📝 Lisensi

Proyek ini dilisensikan di bawah **MIT License** - lihat berkas [LICENSE](LICENSE) untuk detail lebih lanjut.

---

> Dibuat dengan dedikasi penuh terhadap kualitas kode dan konsep struktur data yang kokoh. 🚀
