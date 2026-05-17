package com.simulator;

import com.simulator.model.Router;
import com.simulator.network.NetworkTopology;
import com.simulator.datastructure.CustomStack;

import java.util.List;
import java.util.Scanner;

/**
 * Kelas utama (Runner) untuk aplikasi Network Topology Simulator.
 * Menyediakan antarmuka Command Line Interface (CLI) premium dengan
 * warna ANSI untuk pengalaman simulasi interaktif.
 */
public class Main {
    private static NetworkTopology topology;
    private static final Scanner scanner = new Scanner(System.in);

    // Konstanta Warna ANSI untuk CLI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BG_BLUE = "\u001B[44m";

    public static void main(String[] args) {
        initializeDefaultTopology();
        displayWelcomeBanner();

        boolean running = true;
        while (running) {
            displayMenu();
            System.out.print(BOLD + "Pilih opsi menu (1-6): " + RESET);
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    viewAllRouters();
                    break;
                case "2":
                    topology.printTopologyDetails();
                    break;
                case "3":
                    runPacketSimulation();
                    break;
                case "4":
                    displayTopologyDiagram();
                    break;
                case "5":
                    resetTopology();
                    break;
                case "6":
                    displayGoodbyeMessage();
                    running = false;
                    break;
                default:
                    System.out.println(BOLD + RED + "⚠️ Opsi tidak valid! Silakan masukkan angka dari 1 hingga 6." + RESET);
                    pressAnyKeyToContinue();
            }
        }
        scanner.close();
    }

    /**
     * Menginisialisasi topologi jaringan default sesuai rencana implementasi.
     */
    private static void initializeDefaultTopology() {
        topology = new NetworkTopology();

        // 1. Inisialisasi Router
        Router rLab = new Router("Router-Lab", "192.168.1.1");
        Router rGedungA = new Router("Router-GedungA", "172.16.1.1");
        Router rGedungB = new Router("Router-GedungB", "172.16.2.1");
        Router rPusat = new Router("Router-Pusat", "10.0.0.1");
        Router rServer = new Router("Router-Server", "10.0.2.1");
        Router rIsp = new Router("Router-ISP", "200.10.10.1");

        // Daftarkan Router ke Topology
        topology.addRouter(rLab);
        topology.addRouter(rGedungA);
        topology.addRouter(rGedungB);
        topology.addRouter(rPusat);
        topology.addRouter(rServer);
        topology.addRouter(rIsp);

        // 2. Hubungkan Router (Undirected Graph)
        topology.connectRouters(rLab, rGedungA);
        topology.connectRouters(rLab, rPusat);
        topology.connectRouters(rGedungA, rGedungB);
        topology.connectRouters(rGedungB, rPusat);
        topology.connectRouters(rGedungB, rServer);
        topology.connectRouters(rPusat, rServer);
        topology.connectRouters(rPusat, rIsp);
        topology.connectRouters(rIsp, rServer);
    }

    /**
     * Menampilkan banner pembuka aplikasi yang memukau.
     */
    private static void displayWelcomeBanner() {
        System.out.println(BOLD + BLUE + "======================================================================");
        System.out.println("   _   _  _____ _____  _    _  _____  _____  _   __ ");
        System.out.println("  | \\ | ||  ___|_   _|| |  | ||  _  || ___ \\| | / / ");
        System.out.println("  |  \\| || |__   | |  | |  | || | | || |_/ /| |/ /  ");
        System.out.println("  | . ` ||  __|  | |  | |/\\| || | | ||    / |    \\  ");
        System.out.println("  | |\\  || |___  | |  \\  /\\  /\\ \\_/ /| |\\ \\ | |\\  \\ ");
        System.out.println("  \\_| \\_/\\____/  \\_/   \\/  \\/  \\___/ \\_| \\_|\\_| \\_/ ");
        System.out.println("                                                    ");
        System.out.println("             ⚡ NETWORK TOPOLOGY SIMULATOR CLI ⚡" + RESET);
        System.out.println(BOLD + CYAN + "     Solusi Routing & Traceability dengan Graph, Set, & Custom Stack" + RESET);
        System.out.println(BOLD + BLUE + "======================================================================" + RESET);
        System.out.println("Selamat datang di simulator jaringan cerdas. Program ini mensimulasikan");
        System.out.println("perjalanan paket data, mendeteksi siklus (routing loop), dan menelusuri");
        System.out.println("jalur terpendek/alternatif secara otomatis dengan visualisasi detail.");
        System.out.println("----------------------------------------------------------------------");
    }

    /**
     * Menampilkan menu utama program.
     */
    private static void displayMenu() {
        System.out.println("\n" + BOLD + WHITE + BG_BLUE + "  📌 MENU UTAMA SIMULATOR  " + RESET);
        System.out.println("1. 🖥️  Lihat Daftar Router");
        System.out.println("2. 🗺️  Lihat Detail Hubungan Kabel (Topologi)");
        System.out.println("3. ⚡  Simulasikan Pengiriman Paket (Trace Route)");
        System.out.println("4. 🖼️  Tampilkan Diagram Skema Topologi (ASCII)");
        System.out.println("5. 🔄  Reset / Inisialisasi Ulang Topologi");
        System.out.println("6. 🚪  Keluar dari Program");
        System.out.println("----------------------------------------------------------------------");
    }

    /**
     * Menampilkan daftar seluruh router dalam tabel format premium.
     */
    private static void viewAllRouters() {
        System.out.println("\n" + BOLD + "======================================================================");
        System.out.println("                   🖥️  DAFTAR PERANGKAT ROUTER DIGITAL");
        System.out.println("======================================================================" + RESET);
        System.out.printf(BOLD + " %-5s %-25s %-20s %-15s\n" + RESET, "No", "Nama Router", "Alamat IP", "Koneksi Aktif");
        System.out.println("----------------------------------------------------------------------");

        int index = 1;
        for (Router r : topology.getRouters()) {
            int connectionsCount = topology.getAdjacencyList().get(r).size();
            System.out.printf(" [%d]   %-25s %-20s %-15s\n", 
                    index++, 
                    CYAN + r.getName() + RESET, 
                    YELLOW + r.getIpAddress() + RESET, 
                    connectionsCount + " router");
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Total Router terdaftar: " + BOLD + topology.getRouters().size() + RESET);
        System.out.println("======================================================================");
        pressAnyKeyToContinue();
    }

    /**
     * Memandu pengguna dalam menyimulasikan pengiriman paket data dan mencetak traceroute.
     */
    private static void runPacketSimulation() {
        System.out.println("\n" + BOLD + "======================================================================");
        System.out.println("                ⚡ SIMULASI PENGIRIMAN PAKET DATA (DFS)");
        System.out.println("======================================================================" + RESET);
        
        System.out.print("Masukkan Nama atau IP Router ASAL  : ");
        String srcInput = scanner.nextLine().trim();
        Router source = findRouterByInput(srcInput);

        if (source == null) {
            System.out.println(BOLD + RED + "❌ Router ASAL tidak ditemukan! Periksa kembali nama/IP yang diinput." + RESET);
            pressAnyKeyToContinue();
            return;
        }

        System.out.print("Masukkan Nama atau IP Router TUJUAN: ");
        String destInput = scanner.nextLine().trim();
        Router dest = findRouterByInput(destInput);

        if (dest == null) {
            System.out.println(BOLD + RED + "❌ Router TUJUAN tidak ditemukan! Periksa kembali nama/IP yang diinput." + RESET);
            pressAnyKeyToContinue();
            return;
        }

        if (source.equals(dest)) {
            System.out.println(BOLD + YELLOW + "ℹ️ Router asal dan tujuan sama. Paket langsung sampai tanpa transmisi!" + RESET);
            pressAnyKeyToContinue();
            return;
        }

        // Jalankan pencarian rute DFS (mencetak log perjalanan secara real-time)
        CustomStack<Router> pathStack = topology.findRoute(source, dest);

        // Jika rute ditemukan (Stack tidak kosong)
        if (!pathStack.isEmpty()) {
            displayTraceRouteResult(pathStack);
        }
        pressAnyKeyToContinue();
    }

    /**
     * Memproses data Stack Kustom untuk mencetak tabel pelacakan rute (traceroute style).
     */
    private static void displayTraceRouteResult(CustomStack<Router> pathStack) {
        // Ambil elemen stack secara terurut dari bawah ke atas (insertion order)
        List<Router> routeList = pathStack.toList();

        System.out.println("\n" + BOLD + "======================================================================");
        System.out.println("                 🌐 HASIL PELACAKAN JALUR (TRACE ROUTE)               ");
        System.out.println("======================================================================" + RESET);
        System.out.printf(BOLD + " %-5s   %-20s   %-16s   %-20s\n" + RESET, "Hop", "Nama Router", "IP Address", "Status");
        System.out.println("----------------------------------------------------------------------");

        int hopsCount = routeList.size() - 1;
        for (int i = 0; i < routeList.size(); i++) {
            Router r = routeList.get(i);
            String status;
            String coloredName = CYAN + r.getName() + RESET;
            
            if (i == 0) {
                status = GREEN + "[Asal / Source]" + RESET;
            } else if (i == hopsCount) {
                status = GREEN + "[Tujuan / Destination]" + RESET;
            } else {
                status = YELLOW + "[Transit]" + RESET;
            }

            System.out.printf(" [%-3d]   %-31s   %-27s   %-20s\n", 
                    i, 
                    coloredName, 
                    YELLOW + r.getIpAddress() + RESET, 
                    status);
        }
        
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Total Lompatan (Hops) : " + BOLD + hopsCount + RESET);
        System.out.println("Total Router Dilalui  : " + BOLD + routeList.size() + RESET);
        System.out.println("======================================================================\n");
    }

    /**
     * Membantu memetakan nama/IP input dari user menjadi objek Router.
     */
    private static Router findRouterByInput(String input) {
        Router r = topology.findRouterByName(input);
        if (r == null) {
            r = topology.findRouterByIp(input);
        }
        return r;
    }

    /**
     * Menampilkan diagram skema topologi jaringan menggunakan ASCII Art berwarna.
     */
    private static void displayTopologyDiagram() {
        System.out.println("\n" + BOLD + "======================================================================");
        System.out.println("                 🖼️  DIAGRAM SKEMA TOPOLOGI JARINGAN (ASCII)");
        System.out.println("======================================================================" + RESET);
        System.out.println("");
        System.out.println("                " + CYAN + "[Router-Lab] (192.168.1.1)" + RESET);
        System.out.println("                  /                    \\");
        System.out.println("                 /                      \\");
        System.out.println("     " + CYAN + "[Router-GedungA]" + RESET + "                  " + CYAN + "[Router-Pusat] (10.0.0.1)" + RESET);
        System.out.println("       (172.16.1.1)                     /      |      \\");
        System.out.println("            |                          /       |       \\");
        System.out.println("     " + CYAN + "[Router-GedungB]" + RESET + "                  /   " + CYAN + "[Router-ISP]" + RESET + "    \\");
        System.out.println("       (172.16.2.1)                 /   (200.10.10.1)  \\");
        System.out.println("            \\                      /           |        \\");
        System.out.println("             \\                    /            |         \\");
        System.out.println("              " + CYAN + "[Router-Server] (10.0.2.1) <-------------┘" + RESET);
        System.out.println("");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Keterangan:");
        System.out.println("- Garis mewakili kabel jaringan bertipe duplex penuh (Undirected/Timbal-Balik).");
        System.out.println("- Router-Lab dan Router-Server memiliki beberapa rute redundan.");
        System.out.println("- Algoritma DFS akan mencari jalur secara mendalam ke tetangga pertama,");
        System.out.println("  dan melakukan " + RED + "Backtracking" + RESET + " jika menghadapi jalan buntu (dead-end).");
        System.out.println("======================================================================");
        pressAnyKeyToContinue();
    }

    /**
     * Melakukan reset topologi ke status awal default.
     */
    private static void resetTopology() {
        initializeDefaultTopology();
        System.out.println("\n" + BOLD + GREEN + "🔄 Topologi jaringan berhasil direset ke pengaturan default!" + RESET);
        pressAnyKeyToContinue();
    }

    /**
     * Menampilkan pesan perpisahan yang indah ketika program keluar.
     */
    private static void displayGoodbyeMessage() {
        System.out.println("\n" + BOLD + BLUE + "======================================================================");
        System.out.println("           Terima kasih telah menggunakan Network Simulator!");
        System.out.println("           Dibuat dengan ❤️ oleh Java Developer Profesional");
        System.out.println("======================================================================" + RESET);
    }

    /**
     * Membekukan layar terminal sementara hingga pengguna menekan Enter.
     */
    private static void pressAnyKeyToContinue() {
        System.out.print("\nTekan [ENTER] untuk melanjutkan...");
        scanner.nextLine();
    }
}
