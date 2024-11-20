import java.util.ArrayList;
import java.util.Scanner;

public class CinemaExtra {

    private int capacitaSala; // da incapsulare
    private int numSpettatori; // da incapsulare
    public int bigliettiRimanenti; // qui tengo traccia dei biglietti rimasti durante la vendita multipla

    // istanza di Scanner 
    Scanner input = new Scanner(System.in);

    // gestisco i biglietti con un unico ArrayList
    public ArrayList<Integer> bigliettiAcquistatiList = new
    ArrayList<>();

    // ----- inizio incapsulamento -----

    public void setCapacitaSala(int capacitaSala) {
        boolean ancora = true;

        do {

            if (capacitaSala > 0) {
                this.capacitaSala = capacitaSala;
                this.bigliettiRimanenti = capacitaSala; // inizializzo anche i biglietti rimanenti
                ancora = false;

            } else {
                System.out.println("La sala cinematografica deve avere minimo 1 posto di capacità.");
                System.out.print("Inserisci la capacità della sala (min 1): ");
                capacitaSala = input.nextInt();
            }

        } while (ancora);
    }

    public int getCapacitaSala() {
        return capacitaSala;
    }

    public void setNumSpettatori(int numSpettatori) {
        boolean ancora = false;

        do {

            if (numSpettatori > 0 && numSpettatori <= getCapacitaSala()) { // se la regola viene rispettata
                this.numSpettatori = numSpettatori;
                ancora = true;

            } else { // se la regola non viene rispettata
                System.out.println("Il numero di spettatori deve essere compreso tra 1 e " + getCapacitaSala());
                System.out.print("Inserisci il numero di spettatori: ");
                numSpettatori = input.nextInt();
            }
        } while (!ancora);

        // Genero i numeri dei biglietti una volta che numSpettatori è stato impostato
        generaBiglietti(numSpettatori);        
    }

    public int getNumSpettatori() {
        return numSpettatori;
    }

    // ----- fine incapsulamento -----

    // Metodo per "stampare" i numeri dei biglietti
    public void generaBiglietti(int numSpettatori) {
        bigliettiAcquistatiList.clear(); // "svuoto" l'ArrayList dei biglietti acquistati in modo tale che, ogni volta che viene richiamato il metodo generaBiglietti(), si evitano i duplicati di biglietti già emessi precedentemente

        for (int i = 1; i <= numSpettatori; i++) { 
            bigliettiAcquistatiList.add(i);
        }
    }

    // scrivo il metodo per acquistare biglietti multipli
    public void acquistaBigliettiMultipli() {
        int bigliettiAcquistati = 0;

        while (bigliettiAcquistati < numSpettatori) {
            System.out.print("Inserisci il numero di biglietti da acquistare (o 'stop' per terminare): ");
            String inputUtente = input.next();

            if (inputUtente.equalsIgnoreCase("stop")) {

                break; // Esco dal ciclo se l'utente scrive 'stop'
            }

            // converto l'inputUtente in un tipo int e lo salvo nella variabile numBigliettiRichiesti
            int numBigliettiRichiesti = Integer.parseInt(inputUtente);

            // controllo che le quantità in gioco siano tutte coerenti
            if (numBigliettiRichiesti <= bigliettiRimanenti && (bigliettiAcquistati + numBigliettiRichiesti) <= numSpettatori) {

                acquistaBiglietti(numBigliettiRichiesti); // richiamo il metodo per rendere i biglietti univoci
                bigliettiAcquistati += numBigliettiRichiesti; // aggiorno i biglietti acquistati
                
            } else {
                System.out.println("Numero di biglietti non disponibile o maggiore del numero massimo di spettatori.");
            }

            // stampo quanti biglietti sono rimasti acquistabili dall'utente
            System.out.println("Numero di biglietti ancora acquistabili: " + (numSpettatori - bigliettiAcquistati));
        }

        // Chiamo il metodo assegnaPosti() solo dopo che sono stati acquistati i biglietti
        assegnaPosti(bigliettiAcquistati);
    }

    public void acquistaBiglietti(int numBigliettiRichiesti) {

        System.out.println("Acquistato " + numBigliettiRichiesti + " biglietti.");

        for (int i = 0; i < numBigliettiRichiesti; i++) {

            int biglietto = bigliettiAcquistatiList.get(i); // prendo il biglietto dall'ArrayList
            System.out.println("Biglietto numero: " + biglietto); // stampo il biglietto
            bigliettiAcquistatiList.remove(bigliettiAcquistatiList.get(i)); // rimuovo il biglietto acquistato dall'ArrayList (così non si contano doppioni)
        }

        bigliettiRimanenti -= numBigliettiRichiesti; // aggiorno il numero dei biglietti ancora disponibili alla vendita
    }

    // scrivo il metodo per assegnare e distribuire in modo equo i posti dentro la sala
    public void assegnaPosti(int bigliettiAcquistati) {

        int sinistra = bigliettiAcquistati / 3;
        int centro = bigliettiAcquistati / 3;
        int destra = bigliettiAcquistati / 3;

        int postiRimanenti = bigliettiAcquistati % 3; // uso il modulus per tenere conto di eventuali posti extra oltre i multipli di 3

        if (postiRimanenti == 1) { // se rimane un posto extra...
            centro++; // ...lo assegno alla zona centrale

        } else if (postiRimanenti == 2) { // se rimangono 2 posti extra....
            centro++; // ...ne assegno 1 alla zona centrale
            sinistra++; // ...e 1 alla zona di sinistra

        }


        // stampo il riepilogo conclusivo

        int postiVuoti = capacitaSala - bigliettiAcquistati; // Calcolo i posti rimasti vuoti
        System.out.println("Assegnati " + sinistra + " posti a sinistra, " + centro + " posti al centro e " + destra + " posti a destra.");
        System.out.println("Totale: " + bigliettiAcquistati + " spettatori.");
        System.out.println("Posti totali rimasti vuoti: " + postiVuoti);

        // Stampo i biglietti per la zona sinistra 
        System.out.println("Zona sinistra: ");
        for (int i = 0; i < sinistra && i < bigliettiAcquistati; i++) { 
            System.out.print((i + 1) + " "); 
        }
        System.out.println();
        
        // Stampo i biglietti per la zona centrale
        System.out.println("Zona centrale: ");
        for (int i = sinistra; i < sinistra + centro && i < bigliettiAcquistati; i++) { 
            System.out.print((i + 1) + " "); 
        }
        System.out.println();
        
        // Stampo i biglietti per la zona destra
        System.out.println("Zona destra: ");
        for (int i = sinistra + centro; i < bigliettiAcquistati; i++) { 
            System.out.print((i + 1) + " "); 
        }
        System.out.println();
    }
}
