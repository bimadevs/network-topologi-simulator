package com.simulator.test;

import com.simulator.model.Router;
import com.simulator.network.NetworkTopology;
import com.simulator.datastructure.CustomStack;

import java.util.List;

/**
 * Rangkaian pengujian otomatis untuk algoritma routing DFS dan BFS
 * pada Network Topology Simulator.
 *
 * Menggunakan metode main() mandiri tanpa library eksternal (JUnit).
 * Setiap metode pengujian mengembalikan boolean true/false dan
 * mencetak hasil PASS/FAIL dengan pewarnaan ANSI.
 */
public class RouteTest {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    private static int passed = 0;
    private static int total  = 0;

    public static void main(String[] args) {
        System.out.println(BOLD + CYAN + "==============================================" + RESET);
        System.out.println(BOLD + "   PENGUJIAN ROUTING DFS & BFS");
        System.out.println(BOLD + CYAN + "==============================================" + RESET);

        NetworkTopology topology = buildDefaultTopology();

        // Test 1: DFS jalur normal Lab -> Server
        test("DFS: Lab -> Server menemukan jalur", () -> {
            Router src = topology.findRouterByName("Router-Lab");
            Router dst = topology.findRouterByName("Router-Server");
            CustomStack<Router> path = topology.findRoute(src, dst);
            return !path.isEmpty();
        });

        // Test 2: BFS jalur normal Lab -> Server
        test("BFS: Lab -> Server menemukan jalur", () -> {
            Router src = topology.findRouterByName("Router-Lab");
            Router dst = topology.findRouterByName("Router-Server");
            CustomStack<Router> path = topology.findRouteBFS(src, dst);
            return !path.isEmpty();
        });

        // Test 3: BFS lebih pendek dari DFS untuk Lab -> Server
        // DFS akan menemukan Lab -> GedungA -> GedungB -> Pusat -> Server (4 hops)
        // BFS akan menemukan Lab -> Pusat -> Server (2 hops)
        test("BFS: jalur lebih pendek dari DFS (Lab -> Server)", () -> {
            Router src = topology.findRouterByName("Router-Lab");
            Router dst = topology.findRouterByName("Router-Server");

            CustomStack<Router> dfsPath = topology.findRoute(src, dst);
            CustomStack<Router> bfsPath = topology.findRouteBFS(src, dst);

            int dfsHops = dfsPath.toList().size() - 1;
            int bfsHops = bfsPath.toList().size() - 1;

            System.out.println("   DFS hops: " + dfsHops + ", BFS hops: " + bfsHops);
            return bfsHops < dfsHops;
        });

        // Test 4: Router tidak ditemukan
        test("DFS: router tidak ditemukan (null source)", () -> {
            try {
                topology.findRoute(null, topology.findRouterByName("Router-Lab"));
                return false; // harusnya throw NullPointerException
            } catch (NullPointerException e) {
                return true; // expected
            }
        });

        // Test 5: Tidak ada jalur (router terisolasi)
        test("DFS: router terisolasi -> jalur kosong", () -> {
            NetworkTopology isolatedTopo = new NetworkTopology();
            Router r1 = new Router("Router-A", "10.0.0.1");
            Router r2 = new Router("Router-B", "10.0.0.2");
            isolatedTopo.addRouter(r1);
            isolatedTopo.addRouter(r2);
            // r1 dan r2 tidak terhubung
            CustomStack<Router> path = isolatedTopo.findRoute(r1, r2);
            return path.isEmpty();
        });

        // Test 7: BFS router terisolasi -> jalur kosong
        test("BFS: router terisolasi -> jalur kosong", () -> {
            NetworkTopology isolatedTopo = new NetworkTopology();
            Router r1 = new Router("Router-A", "10.0.0.1");
            Router r2 = new Router("Router-B", "10.0.0.2");
            isolatedTopo.addRouter(r1);
            isolatedTopo.addRouter(r2);
            CustomStack<Router> path = isolatedTopo.findRouteBFS(r1, r2);
            return path.isEmpty();
        });

        // Test 8: DFS Server -> Lab (arah terbalik, graph undirected)
        test("DFS: Server -> Lab jalur ditemukan", () -> {
            Router src = topology.findRouterByName("Router-Server");
            Router dst = topology.findRouterByName("Router-Lab");
            CustomStack<Router> path = topology.findRoute(src, dst);
            return !path.isEmpty();
        });

        // Test 9: BFS Server -> Lab (arah terbalik, graph undirected)
        test("BFS: Server -> Lab jalur ditemukan", () -> {
            Router src = topology.findRouterByName("Router-Server");
            Router dst = topology.findRouterByName("Router-Lab");
            CustomStack<Router> path = topology.findRouteBFS(src, dst);
            return !path.isEmpty();
        });

        // Test 10: Router ditemukan berdasarkan IP
        test("Router: cari berdasarkan IP", () -> {
            Router r = topology.findRouterByIp("10.0.0.1");
            return r != null && r.getName().equals("Router-Pusat");
        });

        // Test 11: Router tidak ditemukan berdasarkan IP
        test("Router: IP tidak dikenal -> null", () -> {
            Router r = topology.findRouterByIp("999.999.999.999");
            return r == null;
        });

        // Test 12: Nama router tidak ditemukan
        test("Router: nama tidak dikenal -> null", () -> {
            Router r = topology.findRouterByName("Router-TidakAda");
            return r == null;
        });

        // Ringkasan
        System.out.println("\n" + BOLD + "==============================================" + RESET);
        System.out.println(BOLD + "            RINGKASAN PENGUJIAN");
        System.out.println(BOLD + "==============================================" + RESET);
        System.out.println("Total : " + total);
        System.out.println("Lulus : " + BOLD + GREEN + passed + RESET);
        System.out.println("Gagal : " + BOLD + RED + (total - passed) + RESET);
        System.out.println(BOLD + "==============================================" + RESET);

        if (passed == total) {
            System.out.println(GREEN + BOLD + "🌟 SEMUA PENGUJIAN BERHASIL!" + RESET);
        } else {
            System.out.println(RED + BOLD + "❌ ADA PENGUJIAN YANG GAGAL!" + RESET);
            System.exit(1);
        }
    }

    /**
     * Membangun topologi default yang identik dengan inisialisasi di Main.java.
     */
    private static NetworkTopology buildDefaultTopology() {
        NetworkTopology topology = new NetworkTopology();

        Router rLab     = new Router("Router-Lab",     "192.168.1.1");
        Router rGedungA = new Router("Router-GedungA", "172.16.1.1");
        Router rGedungB = new Router("Router-GedungB", "172.16.2.1");
        Router rPusat   = new Router("Router-Pusat",   "10.0.0.1");
        Router rServer  = new Router("Router-Server",  "10.0.2.1");
        Router rIsp     = new Router("Router-ISP",     "200.10.10.1");

        topology.addRouter(rLab);
        topology.addRouter(rGedungA);
        topology.addRouter(rGedungB);
        topology.addRouter(rPusat);
        topology.addRouter(rServer);
        topology.addRouter(rIsp);

        topology.connectRouters(rLab, rGedungA);
        topology.connectRouters(rLab, rPusat);
        topology.connectRouters(rGedungA, rGedungB);
        topology.connectRouters(rGedungB, rPusat);
        topology.connectRouters(rGedungB, rServer);
        topology.connectRouters(rPusat, rServer);
        topology.connectRouters(rPusat, rIsp);
        topology.connectRouters(rIsp, rServer);

        return topology;
    }

    /**
     * Menjalankan satu kasus pengujian dan menampilkan hasil PASS/FAIL.
     *
     * @param nama        Nama deskriptif pengujian.
     * @param assertion   Logika pengujian yang mengembalikan boolean.
     */
    private static void test(String nama, Assertion assertion) {
        total++;
        System.out.print("\n[" + total + "] " + nama + " ... ");

        try {
            boolean result = assertion.run();
            if (result) {
                passed++;
                System.out.println(BOLD + GREEN + "PASS" + RESET);
            } else {
                System.out.println(BOLD + RED + "FAIL" + RESET);
            }
        } catch (Exception e) {
            System.out.println(BOLD + RED + "ERROR (" + e.getClass().getSimpleName()
                    + ": " + e.getMessage() + ")" + RESET);
        }
    }

    /**
     * Interface fungsional untuk lambda assertion.
     */
    @FunctionalInterface
    interface Assertion {
        boolean run();
    }
}
