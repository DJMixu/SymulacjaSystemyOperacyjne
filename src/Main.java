import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Random rand = new Random();
        ManagerLogow logger = new ManagerLogow("wyniki_eksperymentow.txt");

        // --- Parametry dla Zadania 1 (Procesor) ---
        int LICZBA_PROCESOW = 30;       // Ile procesów wygenerować
        int KWANT_CZASU     = 4;        // Kwant czasu dla Round Robin
        int MAX_CZAS_PRZYJSCIA = 50;    // Zakres losowania czasu przyjścia
        int MAX_DLUGOSC_FAZY   = 30;    // Zakres losowania długości procesu
        int ROZMIAR_KOLA    = 6;       // Wielkość ROund RObin
        // --- Parametry dla Zadania 2 (Pamięć) ---
        int DLUGOSC_CIAGU   = 50;       // Ile odwołań do pamięci wygenerować
        int ZAKRES_STRON    = 60;       // Numery stron od 1 do 20
        int ILOSC_RAMEK     = 6;        // Ile ramek ma pamięć fizyczna (spróbuj zmienić na 3 lub 6)


        // SYmulacja dla procesora
        logger.log("=== ZADANIE 1: Symulacja CPU ===");
        logger.log("Parametry: Ilość=" + LICZBA_PROCESOW + ", Kwant=" + KWANT_CZASU);

        //Generowanie danych
        List<Proces> daneTestoweCPU = new ArrayList<>();
        for (int i = 0; i < LICZBA_PROCESOW; i++) {
            daneTestoweCPU.add(new Proces(i, rand.nextInt(MAX_CZAS_PRZYJSCIA), rand.nextInt(MAX_DLUGOSC_FAZY) + 1));
        }

        //Zapis danych do pliku
        zapiszDaneTestoweCPU(daneTestoweCPU, "dane_cpu.txt");

        //Kopiowanie danych dla algorytmów
        List<Proces> listaSJF = new ArrayList<>();
        List<Proces> listaRR = new ArrayList<>();
        for (Proces p : daneTestoweCPU) {
            listaSJF.add(p.clone());
            listaRR.add(p.clone());
        }

        //Uruchomienie algorytmów
        AlgorytmyProcesora.symulacjaSJF(listaSJF, logger);
        AlgorytmyProcesora.symulacjaRR(listaRR, KWANT_CZASU,ROZMIAR_KOLA, logger);

        // Symulacja pamięci
        logger.log("\n=== ZADANIE 2: Zastępowanie Stron ===");
        logger.log("Parametry: Długość=" + DLUGOSC_CIAGU + ", Strony 1-" + ZAKRES_STRON + ", Ramki=" + ILOSC_RAMEK);

        //Generowanie danych
        int[] odwolywania = new int[DLUGOSC_CIAGU];
        //algorytm losowania uzględniający lokalność odwołań
        for(int i=0; i<DLUGOSC_CIAGU; i++) {
            if (rand.nextDouble() < 0.8) {
                // 80% szans na odwołanie do stron 1-5
                odwolywania[i] = rand.nextInt(5) + 1;
            } else {
                // 20%
                odwolywania[i] = rand.nextInt(ZAKRES_STRON) + 1;
            }
        }
        //Czysto losowy
        /*
        for(int i=0; i<DLUGOSC_CIAGU; i++) {
            odwolywania[i] = rand.nextInt(ZAKRES_STRON) + 1;
        }*/
        //Zapis danych do pliku
        zapiszDaneTestowePamiec(odwolywania, "dane_pamiec.txt");

        //Uruchomienie algorytmów
        AlgorytmyPamieci.symulacjaLRU(odwolywania, ILOSC_RAMEK, logger);
        AlgorytmyPamieci.symulacjaFIFO(odwolywania, ILOSC_RAMEK, logger);


        // Zamykamy zapis do pliku
        logger.zamknij();
        System.out.println("\nZakończono. Wyniki w pliku 'wyniki_eksperymentow.txt'.");
    }

    private static void zapiszDaneTestoweCPU(List<Proces> procesy, String nazwaPliku) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nazwaPliku))) {
            pw.println("ID;CzasPrzyjscia;burstTime");
            for (Proces p : procesy) {
                pw.println(p.id + ";" + p.czasPrzyjscia + ";" + p.burstTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zapiszDaneTestowePamiec(int[] strony, String nazwaPliku) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nazwaPliku))) {
            pw.println("CiagOdwołań:");
            for (int s : strony) {
                pw.print(s + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}