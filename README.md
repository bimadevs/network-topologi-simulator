# Network Topology Simulator CLI

**Tugas:** Proyek Struktur Data — 1 orang 1 proyek sederhana
**Mata Kuliah:** Struktur Data
**Bahasa:** Java (100% Standard Library, tanpa pustaka eksternal)

---

## Identitas

Nama    : Bima
Kelas   : TJKT
Proyek  : Network Topology Simulator CLI

---

## 1. Latar Belakang — Masalah yang Ingin Diselesaikan

Dalam jaringan komputer, ketika sebuah paket data dikirim dari satu perangkat ke perangkat lain, paket tersebut harus melewati serangkaian router. Tantangannya adalah:

1. **Menemukan jalur** yang tersedia dari router asal ke router tujuan.
2. **Mencegah routing loop** — yaitu paket berputar-putar di antara router yang sama tanpa pernah sampai ke tujuan.
3. **Mencatat jejak perjalanan** paket (trace route) agar bisa dilihat router mana saja yang dilalui.
4. **Menentukan jalur terpendek** — karena dalam jaringan redundan, ada banyak jalur alternatif.

Proyek ini menyimulasikan permasalahan tersebut dalam sebuah program CLI berbasis Java. Program menggunakan tiga struktur data utama yang saling bekerja sama untuk menyelesaikan masalah routing di atas.

---

## 2. Struktur Data yang Digunakan

### 2.1 Graph (Adjacency List) — Memetakan Koneksi Antar Router

**File:** `NetworkTopology.java`

**Konsep:** Setiap router adalah sebuah titik (vertex) dan kabel yang menghubungkan dua router adalah garis (edge). Graph digunakan untuk merepresentasikan hubungan ini.

**Implementasi:**

```
Map<Router, List<Router>>
```

- `Router` adalah vertex.
- `List<Router>` adalah daftar router lain yang terhubung langsung (neighbors/retangga).
- `LinkedHashMap` digunakan agar urutan router dan tetangganya konsisten setiap kali program dijalankan. Ini penting karena algoritma DFS sangat bergantung pada urutan eksplorasi tetangga.

**Kenapa Graph?** Masalah routing pada dasarnya adalah masalah pencarian jalur pada graph. Dengan adjacency list, kita bisa dengan cepat mengetahui tetangga mana saja yang terhubung ke suatu router, lalu menelusurinya satu per satu.

### 2.2 Custom Stack (Singly Linked List) — Merekam Jejak Perjalanan Paket

**File:** `Node.java` dan `CustomStack.java`

**Konsep:** Stack adalah struktur data LIFO (Last In, First Out). Ketika paket data melompat dari satu router ke router lain, nama router tersebut "ditumpuk" (push) ke dalam stack. Jika menemui jalan buntu, router terakhir dikeluarkan (pop) dan algoritma mencoba jalur alternatif. Ini disebut **backtracking**.

**Implementasi:**

```java
public class CustomStack<T> {
    private Node<T> top;   // Simpul teratas
    private int size;      // Jumlah elemen di stack
}
```

Setiap `Node<T>` menyimpan data (tipe generic) dan referensi ke node di bawahnya (singly linked list).

**Kenapa Stack?** Karena algoritma DFS (Depth-First Search) bekerja dengan prinsip "masuk ke satu cabang, telusuri sampai ujung, kalau buntu balik ke percabangan terakhir". Stack adalah struktur data alami untuk mekanisme backtracking ini — router yang terakhir dikunjungi adalah yang pertama harus dikeluarkan saat backtrack.

**Kenapa dibuat manual, bukan pakai `java.util.Stack`?** Tujuannya untuk mendemonstrasikan pemahaman tentang bagaimana stack bekerja di level implementasi: bagaimana node saling terhubung, bagaimana push dan pop bekerja dalam O(1), dan bagaimana singly linked list digunakan sebagai underlying structure.

### 2.3 HashSet (Visited Set) — Mencegah Routing Loop

**Konsep:** Set adalah struktur data yang hanya menyimpan elemen unik (tidak ada duplikat). Setiap kali router dikunjungi, ia dicatat dalam `visited` set. Jika algoritma mencoba mengunjungi router yang sudah pernah dikunjungi, kunjungan tersebut dilewati.

**Implementasi:** `HashSet<Router>` dengan metode `equals()` dan `hashCode()` yang dioverride di kelas `Router` berdasarkan nama dan IP address.

**Kenapa HashSet?** Operasi `contains()` pada HashSet memiliki kompleksitas O(1) rata-rata. Ini penting karena dalam pencarian routing, kita perlu mengecek "apakah router ini sudah pernah dikunjungi?" berkali-kali. HashSet jauh lebih cepat dibandingkan List untuk operasi pengecekan keanggotaan.

---

## 3. Algoritma

### 3.1 DFS (Depth-First Search) dengan Backtracking

DFS menjelajahi graph dengan cara: mulai dari router asal, kunjungi tetangga pertama, lalu tetangga dari tetangga tersebut, dan seterusnya sampai kedalaman maksimum. Jika mencapai jalan buntu (dead-end), algoritma melakukan backtrack — yaitu mengembalikan langkah ke router sebelumnya dan mencoba tetangga lain yang belum dikunjungi.

Ilustrasi pencarian rute dari Router-Lab ke Router-Server:

```
Router-Lab → Router-GedungA → Router-GedungB → Router-Pusat → Router-Server
```

DFS tidak menjamin jalur terpendek, tetapi menjamin akan menemukan jalur jika ada. Hasilnya bisa berbeda tergantung urutan koneksi router.

### 3.2 BFS (Breadth-First Search) — Jalur Terpendek

BFS menjelajahi graph secara melebar: mulai dari router asal, kunjungi semua tetangga terdekat (jarak 1), lalu semua tetangga dari tetangga tersebut (jarak 2), dan seterusnya. BFS menggunakan Queue (antrian) sebagai struktur data utamanya — router yang pertama ditemukan adalah yang pertama diproses (FIFO).

Ilustrasi pencarian rute terpendek dari Router-Lab ke Router-Server:

```
Router-Lab → Router-Pusat → Router-Server
```

Hanya butuh 2 lompatan, dibandingkan DFS yang butuh 4 lompatan.

BFS menjamin jalur **terpendek** (paling sedikit hop) pada graph yang tidak memiliki bobot (unweighted graph) — persis seperti jaringan yang disimulasikan di program ini.

**Mengapa BFS lebih pendek?** Karena BFS memeriksa semua kemungkinan pada setiap level sebelum turun ke level berikutnya. Ketika tujuan ditemukan, itu pasti melalui jalur terpendek karena belum ada level yang lebih dalam yang diperiksa.

### Perbandingan DFS vs BFS

| Aspek | DFS | BFS |
|-------|-----|-----|
| Struktur data | Stack (LIFO) | Queue (FIFO) |
| Cara kerja | Dalam dulu, melebar kemudian | Melebar dulu, dalam kemudian |
| Jaminan jalur terpendek | Tidak | Ya |
| Penggunaan memori | Bergantung kedalaman graph | Bergantung lebar graph |
| Implementasi | Rekursif dengan backtracking | Iteratif dengan parent tracking |

---

## 4. Struktur Proyek

```
network-topologi-simulator/
├── bin/                                        # Hasil kompilasi (.class)
├── src/com/simulator/
│   ├── Main.java                               # Entry point + menu CLI + algorithm selector
│   ├── model/
│   │   └── Router.java                         # Model data perangkat router
│   ├── datastructure/
│   │   ├── Node.java                           # Simpul generic untuk singly linked list
│   │   └── CustomStack.java                    # Implementasi Stack LIFO kustom
│   ├── network/
│   │   └── NetworkTopology.java                # Graph + algoritma DFS dan BFS
│   └── test/
│       └── RouteTest.java                      # Pengujian otomatis (tanpa JUnit)
└── README.md                                   # Dokumentasi proyek
```

---

## 5. Alur Program

1. Program menginisialisasi topologi jaringan dengan 6 router yang saling terhubung (undirected graph).
2. Pengguna memilih router asal dan router tujuan melalui menu CLI.
3. Pengguna memilih algoritma routing: DFS, BFS, atau perbandingan keduanya.
4. Algoritma bekerja, mencetak log perjalanan paket secara real-time:
   - **DFS:** Mencetak `PUSH` setiap kali mengunjungi router dan `POP` saat backtrack.
   - **BFS:** Mencetak `Enqueue` saat router masuk antrian dan `Dequeue` saat diproses.
5. Hasil trace route ditampilkan dalam tabel lengkap dengan hop, nama router, IP, dan status.
6. Jika mode perbandingan dipilih, ditampilkan tabel perbandingan hops dan algoritma pemenang.

---

## 6. Cara Menjalankan

### Prasyarat

- Java Development Kit (JDK) versi 8 atau lebih baru.
- Terminal yang mendukung ANSI color codes (command prompt modern, terminal Linux/macOS).

### Kompilasi

```bash
javac -d bin src/com/simulator/datastructure/*.java \
             src/com/simulator/model/*.java \
             src/com/simulator/network/*.java \
             src/com/simulator/Main.java \
             src/com/simulator/test/RouteTest.java
```

### Menjalankan Simulator

```bash
java -cp bin com.simulator.Main
```

### Menjalankan Pengujian

```bash
java -cp bin com.simulator.test.RouteTest
```

---

## 7. Hasil Pengujian

Program ini dilengkapi dengan 11 skenario pengujian otomatis yang mencakup:

| No | Skenario | Status |
|----|----------|--------|
| 1 | DFS Lab → Server dapat menemukan jalur | PASS |
| 2 | BFS Lab → Server dapat menemukan jalur | PASS |
| 3 | BFS menghasilkan jalur lebih pendek dari DFS | PASS |
| 4 | Source router null menghasilkan exception | PASS |
| 5 | Router terisolasi — DFS mengembalikan stack kosong | PASS |
| 6 | Router terisolasi — BFS mengembalikan stack kosong | PASS |
| 7 | DFS Server → Lab (arah terbalik) tetap menemukan jalur | PASS |
| 8 | BFS Server → Lab (arah terbalik) tetap menemukan jalur | PASS |
| 9 | Pencarian router berdasarkan IP berhasil | PASS |
| 10 | IP yang tidak dikenal mengembalikan null | PASS |
| 11 | Nama router yang tidak dikenal mengembalikan null | PASS |

Semua pengujian lulus (11/11 PASS).

---

## 8. Hal yang Dipelajari

Dari proyek ini, saya mempelajari beberapa hal:

1. **Graph (Adjacency List):** Memahami bagaimana merepresentasikan hubungan antar objek dalam bentuk vertex dan edge. LinkedHashMap berguna untuk menjaga urutan iterasi yang konsisten.

2. **Stack (Linked List):** Implementasi stack dari nol menggunakan singly linked list mengajarkan bagaimana node saling terhubung melalui referensi, bagaimana operasi push/pop bekerja dalam O(1), dan mengapa stack cocok untuk backtracking.

3. **Set (HashSet):** Memahami pentingnya operasi contains yang cepat (O(1)) untuk mencegah duplikasi dan loop. Juga belajar tentang pentingnya mengoverride `equals()` dan `hashCode()` agar objek kustom bisa digunakan di HashSet.

4. **DFS vs BFS:** Kedua algoritma traversal graph ini punya karakteristik berbeda. DFS menggunakan stack dan cocok untuk eksplorasi mendalam dengan backtracking. BFS menggunakan queue dan menjamin jalur terpendek. Keduanya punya kompleksitas waktu O(V + E) tetapi hasilnya bisa berbeda tergantung struktur graph.

5. **Testing tanpa framework:** Membuat unit test sederhana dengan method `main()` dan lambda interface membantu memvalidasi bahwa setiap fungsi berjalan sesuai harapan.

---

## 9. Kesimpulan

Proyek ini menggunakan tiga struktur data (Graph adjacency list, Custom Stack, dan HashSet) untuk menyelesaikan masalah routing dan trace route pada simulasi jaringan komputer. Setiap struktur data memiliki peran spesifik:

- **Graph** memetakan hubungan antar router.
- **Stack** merekam jejak perjalanan paket dan mendukung backtracking.
- **HashSet** mencegah routing loop.

Dengan menggabungkan ketiganya, program mampu mencari jalur, mendeteksi jalan buntu, melakukan backtracking, dan menampilkan hasil trace route. BFS sebagai tambahan menyediakan solusi jalur terpendek yang menjadi pembanding terhadap DFS.
