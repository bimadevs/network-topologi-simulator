package com.simulator.network;

import com.simulator.model.Router;
import com.simulator.datastructure.CustomStack;

import java.util.*;

/**
 * Mengelola representasi Graph (Adjacency List) dari Topologi Jaringan.
 * Menyediakan logika pencarian jalur menggunakan DFS (Depth First Search)
 * yang memanfaatkan Custom Stack untuk traceability dan HashSet untuk pencegahan loop.
 */
public class NetworkTopology {
    // Adjacency List: Memetakan objek Router ke daftar Router tetangganya yang terhubung
    private final Map<Router, List<Router>> adjacencyList;

    // ANSI Escape Codes untuk mempercantik output konsol (CLI Premium)
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    /**
     * Inisialisasi topologi jaringan baru dengan graph kosong.
     */
    public NetworkTopology() {
        this.adjacencyList = new LinkedHashMap<>(); // LinkedHashMap agar urutan router konsisten
    }

    /**
     * Menambahkan Router baru ke dalam topologi jaringan.
     *
     * @param router Objek Router yang ingin ditambahkan.
     */
    public void addRouter(Router router) {
        if (!adjacencyList.containsKey(router)) {
            adjacencyList.put(router, new ArrayList<>());
        }
    }

    /**
     * Menghubungkan dua router secara timbal-balik (Undirected Graph).
     *
     * @param r1 Router pertama.
     * @param r2 Router kedua.
     */
    public void connectRouters(Router r1, Router r2) {
        // Pastikan kedua router sudah terdaftar dalam graph
        addRouter(r1);
        addRouter(r2);

        // Tambahkan hubungan timbal balik jika belum pernah terhubung sebelumnya
        if (!adjacencyList.get(r1).contains(r2)) {
            adjacencyList.get(r1).add(r2);
        }
        if (!adjacencyList.get(r2).contains(r1)) {
            adjacencyList.get(r2).add(r1);
        }
    }

    /**
     * Mendapatkan daftar semua router yang terdaftar dalam topologi.
     *
     * @return Set berisi semua Router.
     */
    public Set<Router> getRouters() {
        return adjacencyList.keySet();
    }

    /**
     * Mendapatkan adjacency list (koneksi antar router) untuk keperluan rendering.
     *
     * @return Map berisi relasi graph.
     */
    public Map<Router, List<Router>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Mencari objek Router berdasarkan namanya (case-insensitive).
     *
     * @param name Nama router yang dicari.
     * @return Objek Router jika ditemukan, null jika sebaliknya.
     */
    public Router findRouterByName(String name) {
        for (Router r : adjacencyList.keySet()) {
            if (r.getName().equalsIgnoreCase(name.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Mencari objek Router berdasarkan IP Address-nya.
     *
     * @param ip Alamat IP yang dicari.
     * @return Objek Router jika ditemukan, null jika sebaliknya.
     */
    public Router findRouterByIp(String ip) {
        for (Router r : adjacencyList.keySet()) {
            if (r.getIpAddress().equals(ip.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Memulai proses pencarian jalur (Routing) dari Router sumber ke tujuan.
     * Metode ini menginisialisasi Custom Stack dan Set untuk mendeteksi loop,
     * kemudian memanggil helper rekursif DFS.
     *
     * @param source      Router asal pengiriman paket.
     * @param destination Router tujuan akhir.
     * @return CustomStack berisi urutan router yang dilalui (rute sukses), 
     *         atau stack kosong jika tidak ada jalur.
     */
    public CustomStack<Router> findRoute(Router source, Router destination) {
        CustomStack<Router> pathStack = new CustomStack<>();
        Set<Router> visited = new HashSet<>();

        System.out.println(BOLD + PURPLE + "\n[SIMULASI] Memulai pengiriman paket data..." + RESET);
        System.out.println("Dari   : " + YELLOW + source.getName() + " (" + source.getIpAddress() + ")" + RESET);
        System.out.println("Menuju : " + YELLOW + destination.getName() + " (" + destination.getIpAddress() + ")" + RESET);
        System.out.println("======================================================================");

        boolean success = dfsHelper(source, destination, visited, pathStack);

        System.out.println("======================================================================");
        if (success) {
            System.out.println(BOLD + GREEN + "🎉 [SUKSES] Paket data berhasil mencapai tujuan!" + RESET);
            return pathStack;
        } else {
            System.out.println(BOLD + RED + "❌ [GAGAL] Paket tidak dapat mencapai tujuan. Jalur terputus!" + RESET);
            return new CustomStack<>(); // Return stack kosong
        }
    }

    /**
     * Mencari jalur terpendek menggunakan algoritma BFS (Breadth-First Search).
     * BFS menjamin jalur dengan jumlah lompatan (hops) paling sedikit pada
     * graf tidak berbobot (unweighted graph). Menggunakan Queue untuk antrian
     * penjelajahan dan Map untuk melacak router sebelumnya (parent) guna
     * merekonstruksi jalur terpendek.
     *
     * @param source      Router asal pengiriman paket.
     * @param destination Router tujuan akhir.
     * @return CustomStack berisi urutan router yang dilalui (rute terpendek),
     *         atau stack kosong jika tidak ada jalur.
     */
    public CustomStack<Router> findRouteBFS(Router source, Router destination) {
        CustomStack<Router> pathStack = new CustomStack<>();
        Set<Router> visited = new HashSet<>();
        Queue<Router> queue = new LinkedList<>();
        Map<Router, Router> parent = new HashMap<>();

        System.out.println(BOLD + PURPLE + "\n[SIMULASI BFS] Memulai pencarian jalur terpendek..." + RESET);
        System.out.println("Dari   : " + YELLOW + source.getName() + " (" + source.getIpAddress() + ")" + RESET);
        System.out.println("Menuju : " + YELLOW + destination.getName() + " (" + destination.getIpAddress() + ")" + RESET);
        System.out.println("======================================================================");

        // Inisialisasi: masukkan source ke antrian
        queue.add(source);
        visited.add(source);
        parent.put(source, null);
        System.out.printf("📥 " + CYAN + "[ANTRIAN] Enqueue %-18s [%-13s] -> Masuk Antrian.\n" + RESET,
                source.getName(), source.getIpAddress());

        boolean found = false;

        // Loop BFS: proses antrian sampai habis atau tujuan ditemukan
        while (!queue.isEmpty()) {
            Router current = queue.poll();
            System.out.printf("📤 " + CYAN + "[PROSES] Dequeue  %-18s [%-13s] -> Diproses.\n" + RESET,
                    current.getName(), current.getIpAddress());

            if (current.equals(destination)) {
                found = true;
                break;
            }

            List<Router> neighbors = adjacencyList.get(current);
            if (neighbors != null) {
                for (Router neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parent.put(neighbor, current);
                        queue.add(neighbor);
                        System.out.printf("📥 " + CYAN + "[ANTRIAN] Enqueue %-18s [%-13s] -> Masuk Antrian.\n" + RESET,
                                neighbor.getName(), neighbor.getIpAddress());
                    }
                }
            }
        }

        System.out.println("======================================================================");

        if (found) {
            // Rekonstruksi jalur terpendek dari tujuan ke sumber menggunakan parent map
            List<Router> path = new ArrayList<>();
            Router step = destination;
            while (step != null) {
                path.add(step);
                step = parent.get(step);
            }
            Collections.reverse(path); // Balik agar urutan dari source ke destination

            // Push setiap router ke CustomStack
            for (Router r : path) {
                pathStack.push(r);
            }

            System.out.println(BOLD + GREEN + "🎉 [SUKSES] BFS menemukan jalur terpendek! (" + (path.size() - 1) + " hops)" + RESET);
            return pathStack;
        } else {
            System.out.println(BOLD + RED + "❌ [GAGAL] BFS: Tidak ada jalur dari "
                    + source.getName() + " ke " + destination.getName() + "." + RESET);
            return new CustomStack<>();
        }
    }

    /**
     * Fungsi pembantu rekursif DFS (Depth-First Search) dengan logika backtracking.
     *
     * @param current     Router yang sedang dikunjungi saat ini.
     * @param dest        Router tujuan akhir.
     * @param visited     Set untuk melacak router yang sudah dikunjungi (mencegah loop).
     * @param pathStack   Stack kustom untuk merekam jalur perjalanan aktif.
     * @return true jika tujuan telah dicapai, false jika sebaliknya.
     */
    private boolean dfsHelper(Router current, Router dest, Set<Router> visited, CustomStack<Router> pathStack) {
        // 1. PUSH router saat ini ke Stack & tandai sebagai dikunjungi
        pathStack.push(current);
        visited.add(current);

        // LOG: Menampilkan langkah penjelajahan di konsol
        System.out.printf("⚡ " + CYAN + "[LOG] Mengunjungi %-18s [%-13s] -> PUSH ke Stack.\n" + RESET, 
                current.getName(), current.getIpAddress());

        // 2. Base Case: Jika router saat ini adalah tujuan akhir
        if (current.equals(dest)) {
            return true;
        }

        // 3. Rekursif: Jelajahi semua tetangga yang belum dikunjungi
        List<Router> neighbors = adjacencyList.get(current);
        if (neighbors != null) {
            for (Router neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    // Panggil rekursif untuk tetangga tersebut
                    if (dfsHelper(neighbor, dest, visited, pathStack)) {
                        return true; // Jika jalur ditemukan, hentikan pencarian & teruskan status sukses
                    }
                }
            }
        }

        // 4. Backtracking: Jika tidak ada tetangga yang mengarah ke tujuan (dead end)
        System.out.printf("⚠️ " + RED + "[BUNTU] %s tidak memiliki koneksi baru ke tujuan.\n" + RESET, current.getName());
        
        // POP router ini dari Stack
        Router popped = pathStack.pop();
        
        // Tentukan router sebelumnya untuk log pelacakan balik (jika ada)
        String nextLog = "Selesai";
        if (!pathStack.isEmpty()) {
            nextLog = "Kembali ke " + pathStack.peek().getName();
        }
        
        System.out.printf("⏪ " + RED + "[BACKTRACK] POP %s dari Stack. %s.\n" + RESET, popped.getName(), nextLog);
        
        // Catatan: Router tetap berada di Set 'visited' untuk mencegah pencarian ulang di simpul yang sama, 
        // karena simpul ini telah terbukti tidak memiliki jalur ke tujuan.
        
        return false;
    }

    /**
     * Menampilkan daftar hubungan koneksi antar router dalam bentuk Adjacency List yang rapi.
     */
    public void printTopologyDetails() {
        System.out.println("\n" + BOLD + "======================================================================");
        System.out.println("                     🗺️  DAFTAR HUBUNGAN KABEL (TOPOLOGI)");
        System.out.println("======================================================================" + RESET);
        
        for (Map.Entry<Router, List<Router>> entry : adjacencyList.entrySet()) {
            Router router = entry.getKey();
            List<Router> connections = entry.getValue();
            
            System.out.printf(BOLD + "Router " + YELLOW + "%-15s" + RESET + " (" + router.getIpAddress() + ") terhubung ke:\n", router.getName());
            if (connections.isEmpty()) {
                System.out.println("   └─ " + RED + "(Tidak ada koneksi/Kabel terputus)" + RESET);
            } else {
                for (int i = 0; i < connections.size(); i++) {
                    Router conn = connections.get(i);
                    String prefix = (i == connections.size() - 1) ? "   └── " : "   ├── ";
                    System.out.println(prefix + CYAN + conn.getName() + RESET + " (" + conn.getIpAddress() + ")");
                }
            }
            System.out.println("----------------------------------------------------------------------");
        }
    }
}
