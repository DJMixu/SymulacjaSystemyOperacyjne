import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final String PLIK_DANYCH_PROCESORA = "dane_cpu.txt";
    private static final String PLIK_DANYCH_PAMIECI = "dane_pamiec.txt";
    private static final String PLIK_WYNIKOWY = "wyniki_eksperymentow.txt";
    private final static double PRAWDOPODOBIENSTWO_LOKALNOSCI = 0.88;
    private final static int ZAKRES_STRON_LOKALNOSCI = 5;
    // Parametry dla Zadania 1
    private final static int LICZBA_PROCESOW = 10;       // Ile procesów wygenerować
    private final static int KWANT_CZASU = 4;        // Kwant czasu dla Round Robin
    private final static int MAX_CZAS_PRZYJSCIA = 50;    // Zakres losowania czasu przyjścia
    private final static int MAX_BURST_TIME = 20;    // Zakres losowania długości procesu
    private final static int ROZMIAR_KOLA = 6;       // Wielkość ROund RObin
    // Parametry dla Zadania 2
    private final static int DLUGOSC_CIAGU = 50;       // Ile odwołań do pamięci wygenerować
    private final static int ZAKRES_STRON = 60;       // Numery stron od 1 do 20
    private final static int ILOSC_RAMEK = 6;        // Ile ramek ma pamięć fizyczna (spróbuj zmienić na 3 lub 6)
    private static boolean ODCZYT_Z_PLIKU = true;

    public static void main(String[] args) {
        ManagerLogow logger = new ManagerLogow(PLIK_WYNIKOWY);

        // SYmulacja dla procesora
        logger.log("=== ZADANIE 1: Symulacja CPU ===");
        logger.log("Parametry: Ilość=" + LICZBA_PROCESOW + ", Kwant=" + KWANT_CZASU);
        //Deklaracja danych
        List<Proces> daneTestoweCPU = new ArrayList<>();
        List<Proces> listaSJF = new ArrayList<>();
        List<Proces> listaRR = new ArrayList<>();
        int[] odwolywania = new int[DLUGOSC_CIAGU];

        //Generowanie danych
        if (ODCZYT_Z_PLIKU) {
            daneTestoweCPU.addAll(odczytajDaneTestoweCPU(PLIK_DANYCH_PROCESORA));
            odwolywania = odczytajDaneTestowePamiec(PLIK_DANYCH_PAMIECI);
        }
        if (!ODCZYT_Z_PLIKU) {
            Random rand = new Random();
            for (int i = 0; i < LICZBA_PROCESOW; i++) {
                daneTestoweCPU.add(new Proces(i, rand.nextInt(MAX_CZAS_PRZYJSCIA), rand.nextInt(MAX_BURST_TIME) + 1));
            }
            //Zapis danych do pliku
            zapiszDaneTestoweCPU(daneTestoweCPU, PLIK_DANYCH_PROCESORA);

            //algorytm losowania uzględniający lokalność odwołań
            for (int i = 0; i < DLUGOSC_CIAGU; i++) {
                if (rand.nextDouble() < PRAWDOPODOBIENSTWO_LOKALNOSCI) {
                    if (ZAKRES_STRON_LOKALNOSCI > 0 && ZAKRES_STRON_LOKALNOSCI <= ZAKRES_STRON) {
                        odwolywania[i] = rand.nextInt(ZAKRES_STRON_LOKALNOSCI) + 1;
                    }
                } else {
                    odwolywania[i] = rand.nextInt(ZAKRES_STRON) + 1;
                }
            }
            //Czysto losowy
                /*
                for(int i=0; i<DLUGOSC_CIAGU; i++) {
                    odwolywania[i] = rand.nextInt(ZAKRES_STRON) + 1;
                }*/
            //Zapis danych do pliku
            zapiszDaneTestowePamiec(odwolywania, PLIK_DANYCH_PAMIECI);
        }

        //Kopiowanie danych dla algorytmów
        for (Proces p : daneTestoweCPU) {
            listaSJF.add(p.clone());
            listaRR.add(p.clone());
        }

        //Uruchomienie algorytmów
        AlgorytmyProcesora.symulacjaSJF(listaSJF, logger);
        AlgorytmyProcesora.symulacjaRR(listaRR, KWANT_CZASU, ROZMIAR_KOLA, logger);

        // Symulacja pamięci
        logger.log("\n=== ZADANIE 2: Zastępowanie Stron ===");
        logger.log("Parametry: Długość=" + DLUGOSC_CIAGU + ", Strony 1-" + ZAKRES_STRON + ", Ramki=" + ILOSC_RAMEK);


        //Uruchomienie algorytmów
        AlgorytmyPamieci.symulacjaLRU(odwolywania, ILOSC_RAMEK, logger);
        AlgorytmyPamieci.symulacjaFIFO(odwolywania, ILOSC_RAMEK, logger);


        // Zamykamy zapis do pliku
        logger.zamknij();
        System.out.println("\nZakończono. Wyniki w pliku '" + PLIK_WYNIKOWY + "'.");
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

    public static List<Proces> odczytajDaneTestoweCPU(String nazwaPliku) {
        List<Proces> procesy = new ArrayList<>();
        File file = new File(nazwaPliku);

        if (!file.exists() || file.isDirectory()) {
            System.err.println("BŁĄD: Plik '" + nazwaPliku + "' nie został znaleziony!");
            ODCZYT_Z_PLIKU = false;
            return new ArrayList<>();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(nazwaPliku))) {
            String linia = br.readLine();

            while ((linia = br.readLine()) != null) {
                if (linia.trim().isEmpty()) continue;

                String[] dane = linia.split(";");
                if (dane.length == 3) {
                    int id = Integer.parseInt(dane[0]);
                    int czasPrzyjscia = Integer.parseInt(dane[1]);
                    int burstTime = Integer.parseInt(dane[2]);

                    procesy.add(new Proces(id, czasPrzyjscia, burstTime));
                }
            }
        } catch (IOException | NumberFormatException e) {
            ODCZYT_Z_PLIKU = false;
            e.printStackTrace();
            return new ArrayList<>();
        }

        return procesy;
    }

    public static int[] odczytajDaneTestowePamiec(String nazwaPliku) {
        List<Integer> listaTymczasowa = new ArrayList<>();
        File file = new File(nazwaPliku);

        if (!file.exists() || file.isDirectory()) {
            System.err.println("BŁĄD: Plik '" + nazwaPliku + "' nie został znaleziony!");
            ODCZYT_Z_PLIKU = false;
            return new int[DLUGOSC_CIAGU];
        }

        try (Scanner scanner = new Scanner(new File(nazwaPliku))) {
            if (scanner.hasNextLine()) {
                String naglowek = scanner.nextLine();
            }

            while (scanner.hasNextInt()) {
                listaTymczasowa.add(scanner.nextInt());
            }
        } catch (FileNotFoundException e) {
            ODCZYT_Z_PLIKU = false;
            e.printStackTrace();
            return new int[DLUGOSC_CIAGU];
        }

        int[] strony = new int[listaTymczasowa.size()];
        for (int i = 0; i < listaTymczasowa.size(); i++) {
            strony[i] = listaTymczasowa.get(i);
        }
        return strony;
    }

}