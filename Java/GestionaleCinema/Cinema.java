import java.util.ArrayList;
import java.util.Scanner;

public class Cinema {
    public final int CAPACITA_SALA = 300; // costante (la capacità max della sala)
    private int numSpettatori; // da incapsulare

    // istanza di Scanner 
    Scanner input = new Scanner(System.in);

    // ArrayList per memorizzare i numeri dei biglietti
    public ArrayList<Integer> biglietti = new ArrayList<>();

    // ----- inizio incapsulamento -----
    
    public void setNumSpettatori(int numSpettatori) {
        boolean ancora = false;

        do {

            if (numSpettatori > 0 && numSpettatori <= CAPACITA_SALA) { // se la regola viene rispettata
                this.numSpettatori = numSpettatori;
                ancora = true;    
            } else { // se la regola non viene rispettata
                System.out.println("Il numero di spettatori deve essere compreso tra 1 e " + CAPACITA_SALA);
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

        // stampo l'ArrayList biglietti assegnandoli elementi di tipo int che vanno da 1 al numero di spettatori passati da input
        for (int i = 1; i <= getNumSpettatori(); i++) { 

            biglietti.add(i);
        }

        // numerazione dei biglietti in modo casuale (a seconda di numSpettatori)
        mescolaBiglietti();
    }

    // Numerazioni biglietti casuale
    public void mescolaBiglietti() {
        for (int i = 0; i < biglietti.size(); i++) { // considero gli elementi dell'ArrayList biglietti 

            // voglio far sì che la numerazione dei biglietti sia casuale e non sequenziale (cioè non voglio che biglietti[0]=1, biglietti[1]=2 e così via...)
            int randomIndex = (int) (Math.random() * biglietti.size()); // uso Math.random per generare numeri casuali tra 0 e 1 (escluso) e li moltiplico per la dimensione dell'ArrayList biglietti (in questo modo, se ho stampato -esempio- 30 biglietti, avrò il sorteggio di 30 numeri in modo casuale)
            // con il casting a (int) mi assicuro di avere come output un tipo int (tronco la parte decimale di random)

            int temp = biglietti.get(i); // faccio una fotografia dell'ArrayList biglietti "originale": salvo dentro la variabile temp l'elemento di indice-i dell'Array
            biglietti.set(i, biglietti.get(randomIndex)); // con il metodo .set(index, element) impongo che nell'i-esima posizione dell'ArrayList (dove prima ci stava l'elemento salvato in temp) si salvi il numero estratto casualmente con random
            biglietti.set(randomIndex, temp); // completo il rimescolamento: impongo che l'elemento originariamente nella posizione-i (temp) si sposti nella posizione casuale data da randomIndex
        }
    }



    // scrivo il metodo per distribuire gli spettatori dentro la sala in modo equo
    public void assegnaPosti() {
        // distribuisco comunque gli spettatori nella sala in modo tale che ogni zona abbia 1/3 degli spettatori entrati
        int sinistra = getNumSpettatori() / 3;
        int centro = getNumSpettatori() / 3;
        int destra = getNumSpettatori() / 3;
    
        // Se numSpettatori non è un multiplo di 3, allora impongo una distribuzione nei posti rimanenti
        int postiRimanenti = getNumSpettatori() % 3; // uso il modulus per vedere il resto rispetto a 3 
    
        if (postiRimanenti == 1) { // se numSpettatori/3 dà resto 1, allora assegno il posto extra alla zona centrale
            centro++;
        } else if (postiRimanenti == 2) { // se numSpettatori/3 dà resto 2, allora ci sono 2 posti in più e sfrutto anche la zona di sinistra
            centro++;
            sinistra++;
        }
        
        // Stampo il riepilogo del numero degli spettatori in ciascuna zona della sala
        System.out.println("Assegnati " + sinistra + " posti a sinistra, " + centro + " posti al centro e " + destra + " posti a destra.");
        System.out.println("Totale: " + getNumSpettatori() + " spettatori.");
        
        // Assegna biglietti alla zona sinistra
        System.out.println("Zona sinistra: ");
        for (int i = 0; i < sinistra; i++) { 
            System.out.print(biglietti.get(i) + " "); // Stampa i biglietti per la zona sinistra
        }
        System.out.println();
        
        // Assegna biglietti alla zona centrale
        System.out.println("Zona centrale: ");
        for (int i = sinistra; i < sinistra + centro; i++) { // Da sinistra fino a sinistra + centro
            System.out.print(biglietti.get(i) + " "); // Stampa i biglietti per la zona centrale
        }
        System.out.println();
        
        // Assegna biglietti alla zona destra
        System.out.println("Zona destra: ");
        for (int i = sinistra + centro; i < sinistra + centro + destra; i++) { // Da sinistra + centro fino a sinistra + centro + destra
            System.out.print(biglietti.get(i) + " "); // Stampa i biglietti per la zona destra
        }
        System.out.println();
    }
    

}



