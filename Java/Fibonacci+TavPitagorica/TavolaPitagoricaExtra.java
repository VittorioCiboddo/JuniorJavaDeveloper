// Tavola pitagorica potenziata: chiedere da input nel terminale la dimensione della tavola pitagorica e stamparla in modo tale che esca quadrata senza che la singola riga vada a capo

import java.util.Scanner; // importo Scanner per usare l'input da terminale

public class TavolaPitagoricaExtra {

    Scanner input = new Scanner(System.in); // "collego" Scanner al terminale

    // introduco due variabili che mi servono per salvare gli input dal terminale da Scanner
    public int righe;
    public int colonne;

    // scrivo il metodo
    public void mostraTavolaExtra() {

        // stampo le istruzioni per l'utente dal terminale

        // righe
        System.out.println("Quante righe vuoi inserire nella tavola pitagorica? (Max 30)");
        righe = input.nextInt(); // "converto" l'input da Scanner (che è una String) in un tipo int

        // colonne
        System.out.println("Ed invece quante colonne? (Max 30)");
        colonne = input.nextInt(); // "converto" l'input da Scanner (che è una String) in un tipo int

        System.out.println(); // rigo vuoto per separe gli input dalla tavola stampata


        // Modifico i cicli for annidati per tenere conto dei limiti di spazio della tavola pitagorica a terminale
        // nel mio schermo, il terminale supporta max 30 colonne e 30 righe per stampare la tavola pitagorica quadrata (senza andare a capo)

        if ((righe >=1 && righe <= 30) && (colonne >= 1 && colonne <= 30)) { // imposto la condizione che la tavola pitagorica inizi come minimo da 1 e finisca come massimo a 30 (oltre, la singola riga va a capo)

            for (int i = 1; i <= righe; i++) { // ciclo for esterno per le righe (valore max dipende ora dalle righe di input)

                for (int j = 1; j <= colonne; j++) { // ciclo for interno per le colonne (valore max dipende ora dalle colonne di input)

                    System.out.printf("%4d", i * j); // .printf(format) serve per stampare i risultati di i*j in modo formattato, ovvero in uno spazio di 5 caratteri di tipo int
                }

                System.out.println();
            }

        } else { // se da input l'utente immette valori di righe e colonne fuori dai limiti...

            System.out.println("Tavola fuori portata! Righe e colonne possono essere min 1 e max 30"); // stampo il relativo messaggio di errore
        }

    }
}
