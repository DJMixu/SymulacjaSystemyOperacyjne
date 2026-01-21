public class Proces implements Cloneable {

    int id;
    // PID (Process ID)

    int czasPrzyjscia;
    // AT (Arrival Time)
    // Moment na zegarze systemowym, w którym proces wszedł do kolejki.
    // Przed tym czasem procesor w ogóle go nie widzi.

    int burstTime;
    // Po polsku Długość fazy to tłumaczenie mnie nie przekonje
    // Całkowity czas, jaki procesor musi poświęcić, żeby wykonać to zadanie od A do Z.

    int pozostalyCzas;
    // Remaining Time
    // Licznik na początku równy dlugoscFazy.
    // Gdy dojdzie do 0 -> proces jest zakończony.

    int czasOczekiwania;
    // WT (Waiting Time)
    // Czas oczekiwania. Suma wszystkich chwil, gdy proces był gotowy do pracy,
    // ale musiał stać w kolejce, bo procesor był zajęty kimś innym.

    int czasPrzetwarzania;
    // TAT (Turnaround Time)
    // Czas życia procesu. Od momentu wejścia (czasPrzyjscia) do momentu wyjścia.

    //Konstruktor parametryczny
    public Proces(int id, int czasPrzyjscia, int burstTime) {
        this.id = id;
        this.czasPrzyjscia = czasPrzyjscia;
        this.burstTime = burstTime;
        this.pozostalyCzas = burstTime;
    }

    // Metoda do klonowania
    @Override
    public Proces clone() {
        try {
            return (Proces) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}