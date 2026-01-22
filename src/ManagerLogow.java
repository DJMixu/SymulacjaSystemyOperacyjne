import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ManagerLogow {
    private PrintWriter writer;
    private boolean dzialaZapis = false;

    public ManagerLogow(String nazwaPliku) {
        try {
            FileWriter fw = new FileWriter(nazwaPliku, false);
            writer = new PrintWriter(fw, true);
            dzialaZapis = true;
        } catch (IOException e) {
            System.err.println("BŁĄD: Nie można utworzyć pliku logów! " + e.getMessage());
            dzialaZapis = false;
        }
    }


    // Wypisuje tekst na konsolę ORAZ do pliku.
    public void log(String tekst) {
        System.out.println(tekst);
        if (dzialaZapis && writer != null) {
            writer.println(tekst);
        }
    }

    // Wypisuje tekst TYLKO do pliku
    public void logTylkoPlik(String tekst) {
        if (dzialaZapis && writer != null) {
            writer.println(tekst);
        }
    }

    //Wypisuje pustą linię.
    public void nowaLinia() {
        System.out.println();
        if (dzialaZapis && writer != null) {
            writer.println();
        }
    }

    //zamyka
    public void zamknij() {
        if (writer != null) {
            writer.close();
        }
    }
}