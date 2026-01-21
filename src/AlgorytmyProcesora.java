import java.util.*;

public class AlgorytmyProcesora {

    //SJF
    public static void symulacjaSJF(List<Proces> listaProcesow, ManagerLogow logger) {
        int czasObecny = 0;
        int ukonczone = 0;
        int n = listaProcesow.size();
        boolean[] wykonany = new boolean[n];

        // Zmienne do statystyk
        double sumaCzasuOczekiwania = 0;
        double sumaCzasuPrzetwarzania = 0;

        //Walidacja danych
        if (listaProcesow == null || listaProcesow. isEmpty()) {
            logger.log("ERROR: Brak procesów do symulacji!");
            return;
        }

        logger.log("--- Wyniki SJF ---");
        logger.logTylkoPlik(String.format("%-5s %-15s %-15s", "ID", "Czas Oczek.", "Czas Przetw."));

        while (ukonczone < n) {
            int indeksNajkrotszego = -1;
            //Ustawiamy minDługość na jak najwięszką wartość aby 1 kandydat na pewno spełnił
            int minDlugosc = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Proces p = listaProcesow.get(i);
                if (!wykonany[i] && p.czasPrzyjscia <= czasObecny && p.burstTime < minDlugosc) {
                    minDlugosc = p.burstTime;
                    indeksNajkrotszego = i;
                }
            }

            if (indeksNajkrotszego != -1) {
                Proces p = listaProcesow.get(indeksNajkrotszego);
                czasObecny += p.burstTime;
                p.czasPrzetwarzania = czasObecny - p.czasPrzyjscia;
                p.czasOczekiwania = p.czasPrzetwarzania - p.burstTime;

                // Sumowanie wyników
                sumaCzasuOczekiwania += p.czasOczekiwania;
                sumaCzasuPrzetwarzania += p.czasPrzetwarzania;

                wykonany[indeksNajkrotszego] = true;
                ukonczone++;

                logger.logTylkoPlik(String.format("P%-4d %-15d %-15d", p.id, p.czasOczekiwania, p.czasPrzetwarzania));
            } else {
                czasObecny++;
            }
        }

        logger.log("------------------------------------------------");
        logger.log("Średni czas oczekiwania: " + String.format("%.2f", sumaCzasuOczekiwania / n));
        logger.log("Średni czas przetwarzania: " + String.format("%.2f", sumaCzasuPrzetwarzania / n));
        logger.log("Sumaryczny czas przetwarzania: " + sumaCzasuPrzetwarzania);
        logger.log("\n");
    }

    // Round Robin
    public static void symulacjaRR(List<Proces> listaProcesow, int kwantCzasu, int limitMiejscWKole, ManagerLogow logger) {
        Queue<Proces> poczekalnia = new LinkedList<>();
        Queue<Proces> aktywneKolo = new LinkedList<>();

        //Walidacja danych
        if (listaProcesow == null || listaProcesow. isEmpty()) {
            logger.log("ERROR:  Brak procesów!");
            return;
        }
        if (kwantCzasu <= 0) {
            logger.log("ERROR: Kwant czasu musi być > 0!");
            return;
        }
        if (limitMiejscWKole <= 0) {
            logger.log("ERROR: Limit miejsc musi być > 0!");
            return;
        }

        int czasObecny = 0;
        // Sortujemy po przyjściu, żeby wiedzieć kto kiedy wchodzi
        listaProcesow.sort(Comparator.comparingInt(p -> p.czasPrzyjscia));

        int i = 0;
        int n = listaProcesow.size();
        int ukonczone = 0;

        // Zmienne do statystyk
        double sumaCzasuOczekiwania = 0;
        double sumaCzasuPrzetwarzania = 0;
        int maxCzasOczekiwania = 0;

        logger.log("--- Wyniki Round Robin (Q=" + kwantCzasu + ", Miejsca=" + limitMiejscWKole + ") ---");
        logger.logTylkoPlik(String.format("%-5s %-15s %-15s", "ID", "Czas Oczek.", "Czas Przetw."));

        while (ukonczone < n) {
            //Dodajemy nowe procesy do poczekalni
            while (i < n && listaProcesow.get(i).czasPrzyjscia <= czasObecny) {
                poczekalnia.add(listaProcesow.get(i));
                i++;
            }

            //Przenosimy z poczekalni do koła (jeśli są miejsca)
            while (aktywneKolo.size() < limitMiejscWKole && !poczekalnia.isEmpty()) {
                aktywneKolo.add(poczekalnia.poll());
            }

            //Obsługa bezczynności CPU
            if (aktywneKolo.isEmpty()) {
                czasObecny++;
                continue;
            }

            //Przetwarzanie procesu
            Proces p = aktywneKolo.poll();

            int czasWykonania = Math.min(kwantCzasu, p.pozostalyCzas);
            p.pozostalyCzas -= czasWykonania;
            czasObecny += czasWykonania;

            //Sprawdzamy czy w trakcie pracy przyszły nowe procesy
            while (i < n && listaProcesow.get(i).czasPrzyjscia <= czasObecny) {
                poczekalnia.add(listaProcesow.get(i));
                i++;
            }

            if (p.pozostalyCzas > 0) {
                aktywneKolo.add(p); // Wraca do koła
            } else {
                // Proces zakończony
                ukonczone++;
                p.czasPrzetwarzania = czasObecny - p.czasPrzyjscia;
                p.czasOczekiwania = p.czasPrzetwarzania - p.burstTime;

                // Sumowanie wyników
                sumaCzasuOczekiwania += p.czasOczekiwania;
                sumaCzasuPrzetwarzania += p.czasPrzetwarzania;

                if (p.czasOczekiwania > maxCzasOczekiwania) maxCzasOczekiwania = p.czasOczekiwania;
                //Zapisz procesu do pliku
                logger.logTylkoPlik(String.format("P%-4d %-15d %-15d", p.id, p.czasOczekiwania, p.czasPrzetwarzania));
            }
        }
        //Zapis do logów
        logger.log("------------------------------------------------");
        logger.log("Średni czas oczekiwania: " + String.format("%.2f", sumaCzasuOczekiwania / n));
        logger.log("Średni czas przetwarzania: " + String.format("%.2f", sumaCzasuPrzetwarzania / n));
        logger.log("Sumaryczny czas przetwarzania: " + sumaCzasuPrzetwarzania);
        logger.log("Maksymalny czas oczekiwania: " + maxCzasOczekiwania);
        logger.log("\n");
    }
}