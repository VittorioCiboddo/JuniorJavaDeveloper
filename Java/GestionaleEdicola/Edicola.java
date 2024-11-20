// Richiamo qui la class Pubblicazioni e faccio tutti i controlli necessari sfruttando l'incapsulamento.
// Implemento il metodo finale per stampare il resoconto totale dell'attività commerciale

import java.util.ArrayList;
import java.util.Scanner;

public class Edicola {

    // Creo l'ArrayList pubblicazioni inizializzato nella class Pubblicazioni
    ArrayList<Pubblicazioni> pubblicazioni = new ArrayList<>();

    // Istanze di Scanner necessarie
    Scanner inputNumeri = new Scanner(System.in); // <--- per gli input numerici
    Scanner inputTesti = new Scanner(System.in); // <--- per gli input testuali (stringhe)


    // scrivo il metodo per aggiungere le pubblicazioni all'ArrayList secondo dei determinati controlli
    public void aggiungiPubblicazione() {

        while (true) { // uso il boolean true per entrare nel ciclo

            // Stampo ciò che si sta chiedendo all'utente 
            System.out.print("Inserisci il nome della pubblicazione (min 3 caratteri) o digita 'stop' per fermarti: ");
            String nomePubblicazione = inputTesti.nextLine();

            // Introduco la condizione per fermare l'aggiunta di elementi nell'ArrayList
            if (nomePubblicazione.equalsIgnoreCase("stop")) {
                break; // Esco dal ciclo se l'input è 'stop'
            }

            // Rispetto l'incapsulamento della String nome
            if (nomePubblicazione.length() >= 3) {

                // Istanzio la class Pubblicazioni (che contiene l'ArrayList)
                Pubblicazioni pubblicazione = new Pubblicazioni();
                pubblicazione.setNome(nomePubblicazione); // aggiorno il setter di nome con nomePubblicazione

                // Di seguito: stampo a schermo tutta la serie di istruzioni per l'utente
                System.out.print("Inserisci il numero di copie ricevute (max 50): ");
                pubblicazione.setCopieRicevute(inputNumeri.nextInt());

                System.out.print("Inserisci il prezzo di copertina (min 0,50 euro e max 10,00 euro): ");
                pubblicazione.setPrezzoCopertina(inputNumeri.nextDouble());

                System.out.print("Inserire l'aggio (%) applicato (min 5%, max 20%): ");
                pubblicazione.setAggio(inputNumeri.nextInt());

                System.out.print("Inserisci il numero di copie vendute: ");
                pubblicazione.setCopieVendute(inputNumeri.nextInt());

                // aggiungo gli input all'istanza ove è presente l'ArrayList
                pubblicazioni.add(pubblicazione);

            } else { // se la regola dell'incapsulamento del nome non è rispettata...

                System.out.println("Nome non valido. Deve avere almeno 3 caratteri.");
            }
        }
    }

    // scrivo il metodo per stampare il resoconto finale
    public void chiusuraCassa() {

        double guadagnoTotale = 0.0; // inizializzo la variabile necessaria

        System.out.println("---------- CHIUSURA CASSA ----------");
        
        // uso il ciclo for avanzato per prendere ogni elemento dell'ArrayList e stamparne gli attributi (con i relativi metodi)
        for (Pubblicazioni pubblicazioni : pubblicazioni) {

            pubblicazioni.stampaResoconto();
            guadagnoTotale += pubblicazioni.calcolaGuadagno(); // aggiorno il guadagno totale
        }

        System.out.printf("Guadagno netto complessivo: %.2f euro", guadagnoTotale); // stampo il risultato
    }
}