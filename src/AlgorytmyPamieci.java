import java.util.*;

public class AlgorytmyPamieci {

    //FIFO (First In First Out)
    public static void symulacjaFIFO(int[] strony, int iloscRamek, ManagerLogow logger) {
        Queue<Integer> kolejkaRamek = new LinkedList<>();
        Set<Integer> stronyWRamkach = new HashSet<>();
        int bledyStron = 0;

        logger.log("\n--- Wyniki symulacji FIFO (Ramki: " + iloscRamek + ") ---");
        logger.logTylkoPlik(String.format("%-6s %-10s %-15s %-20s", "Krok", "Strona", "Status", "Zawartość Ramek"));
        logger.log("-------------------------------------------------------------");

        for (int i = 0; i < strony.length; i++) {
            int strona = strony[i];
            String status;

            if (stronyWRamkach.contains(strona)) {
                status = "HIT";
            } else {
                status = "MISS";
                bledyStron++;

                if (kolejkaRamek.size() >= iloscRamek) {
                    int usunieta = kolejkaRamek.poll();
                    stronyWRamkach.remove(usunieta);
                }
                kolejkaRamek.add(strona);
                stronyWRamkach.add(strona);
            }

            // Wizualizacja zawartości ramek
            String ramkiStr = kolejkaRamek.toString();
            logger.logTylkoPlik(String.format("%-6d %-10d %-15s %-20s", (i + 1), strona, status, ramkiStr));
        }

        logger.logTylkoPlik("-------------------------------------------------------------");
        logger.log("Suma błędów stron (Page Faults): " + bledyStron);
        logger.log("Trafienia (Hits): " + (strony.length - bledyStron));
    }

    //LRU (Least Recently Used)
    public static void symulacjaLRU(int[] strony, int iloscRamek, ManagerLogow logger) {
        // Używamy Listy:
        // Indeks 0 = Najdawniej używana (do usunięcia)
        // Ostatni indeks = Ostatnio używana (najświeższa)
        List<Integer> ramki = new ArrayList<>();
        int bledyStron = 0;

        logger.log("\n--- Wyniki symulacji LRU (Ramki: " + iloscRamek + ") ---");
        logger.logTylkoPlik(String.format("%-6s %-10s %-15s %-20s", "Krok", "Strona", "Status", "Zawartość Ramek"));
        logger.log("-------------------------------------------------------------");

        for (int i = 0; i < strony.length; i++) {
            int strona = strony[i];
            String status;

            if (ramki.contains(strona)) {
                status = "HIT";
                // Aktualizacja LRU: usuwamy z obecnej pozycji i dajemy na koniec, czyli najdalej do usunięcia
                ramki.remove((Integer) strona);
                ramki.add(strona);
            } else {
                status = "MISS";
                bledyStron++;

                if (ramki.size() >= iloscRamek) {
                    ramki.removeFirst(); //Usuwamy najdawniej używaną
                }
                ramki.add(strona); //Nowa ramka idzie na koniec
            }

            String ramkiStr = ramki.toString();
            logger.logTylkoPlik(String.format("%-6d %-10d %-15s %-20s", (i + 1), strona, status, ramkiStr));
        }

        logger.logTylkoPlik("-------------------------------------------------------------");
        logger.log("Suma błędów stron (Page Faults): " + bledyStron);
        logger.log("Trafienia (Hits): " + (strony.length - bledyStron));
    }
}