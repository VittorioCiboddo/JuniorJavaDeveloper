// Versione potenziata di Edicola.java
// Creazione di un menù del software di gestione degli articoli di una edicola, utilizzabile dall'edicolante 

import java.util.ArrayList;
import java.util.Scanner;

public class EdicolaExtra {

    // Creo l'ArrayList per le pubblicazioni
    ArrayList<Pubblicazioni> pubblicazioni = new ArrayList<>();

    // Istanze di Scanner necessarie
    Scanner inputNumeri = new Scanner(System.in); // Per gli input numerici
    Scanner inputTesti = new Scanner(System.in); // Per gli input testuali (stringhe)

    // Metodo principale per il menù
    public void mostraMenu() {

        while (true) { // uso il booleano true per entrare nel ciclo

            // stampo le voci del menù dell'edicola
            System.out.println("---------- EDICOLA IL GAZZETTINO ----------");
            System.out.println("1. Aggiungi pubblicazione");
            System.out.println("2. Modifica pubblicazione");
            System.out.println("3. Rimuovi pubblicazione");
            System.out.println("4. Vedi pubblicazioni");
            System.out.println("5. Chiusura cassa");
            System.out.println("6. Esci");
            System.out.print("Scegli un'opzione: ");
            
            int scelta = inputNumeri.nextInt(); // qui salvo gli input da terminale dell'edicolante

            // programmo con switch() ogni voce del menù sopra con il relativo metodo
            switch (scelta) {
                case 1:
                    aggiungiPubblicazione();
                    break;
                case 2:
                    modificaPubblicazione();
                    break;
                case 3:
                    rimuoviPubblicazione();
                    break;
                case 4:
                    dettaglioPubblicazioni();
                    break;
                case 5:
                    chiusuraCassa();
                    break;
                case 6:
                    System.out.println("Uscita dal programma.");
                    return; // Esco dal ciclo e dal programma
                default:
                    System.out.println("Opzione non valida. Riprovare.");
                    break;
            }
        }
    }


    // ---------- IMPLEMENTO OGNI METODO DELLO switch() ----------

    // aggiorno il "database" (= ArrayList in Pubblicazioni) con le relative info di dettaglio
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

        System.out.println();
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

        System.out.println();
    }



    // Metodo per modificare una pubblicazione
    public void modificaPubblicazione() {

        // controllo preventivo: verifico se l'ArrayList delle pubblicazioni è vuoto
        if (pubblicazioni.isEmpty()) { 

            System.out.println("Nessuna pubblicazione disponibile per la modifica.");
            return; // ritorno al menù dell'edicola
        }  
        
       
            
        // se l'ArrayList NON è vuoto...
        while (true) { // faccio un ciclo while (boolean true per entrarci) per permettere all'utente di rimanere dentro alla lista delle pubblicazioni registrate fintantochè non decide esplicitamente di tornare indietro (vedi rigo 256)

            mostraPubblicazioni(); // stampo l'elenco delle pubblicazioni registrate
            
            // stampo l'istruzione per l'utente
            System.out.print("Inserisci l'indice (da 1 a " + pubblicazioni.size() + ") della pubblicazione da modificare (0 per uscire): ");
            int indice = inputNumeri.nextInt();

            // Verifico se l'utente vuole uscire dalla voce corrente
            if (indice == 0) {

                System.out.println("Sto tornando indietro...");
                return; 
            }

            // controllo che l'indice immesso a terminale rispetti le regole (= sia compreso tra gli indici stampati prima)
            if (indice < 0 || indice > pubblicazioni.size()) {

                System.out.println("Indice non valido. Riprova.");
                continue; // ritorno all'inizio del ciclo e richiedo l'indice
            }

            // se l'indice è coerente, eseguo la modifica dei vari campi
            Pubblicazioni pubblicazione = pubblicazioni.get(indice - 1); // -1 per allineare gli indici salvati con gli indici dell'ArrayList
            System.out.println("Stai modificando: " + pubblicazione.getNome()); // stampo la scelta dell'utente

            // stampo l'istruzione per l'utente
            System.out.print("Nuovo nome (o lascia vuoto per mantenere attuale): ");

            String nuovoNome = inputTesti.nextLine(); // salvo l'input dell'utente (qualunque esso sia: un testo o un invio -campo vuoto-)
            if (!nuovoNome.isEmpty()) { // se l'utente NON ha lasciato il campo vuoto...

                pubblicazione.setNome(nuovoNome); //... aggiorno quella specifica pubblicazione con il nuovo nome
            }


            // con lo stesso principio, modifico anche tutti gli altri campi (ricordo anche all'utente i valori attualmente presenti)
            System.out.print("Nuovo numero di copie ricevute (attualmente: " + pubblicazione.getCopieRicevute() + "): ");
            int nuoveCopieRicevute = inputNumeri.nextInt();
            pubblicazione.setCopieRicevute(nuoveCopieRicevute);

            System.out.print("Nuovo prezzo di copertina (attualmente: " + pubblicazione.getPrezzoCopertina() + "): ");
            double nuovoPrezzo = inputNumeri.nextDouble();
            pubblicazione.setPrezzoCopertina(nuovoPrezzo);

            System.out.print("Nuovo aggio (%) (attualmente: " + pubblicazione.getAggio() + "): ");
            int nuovoAggio = inputNumeri.nextInt();
            pubblicazione.setAggio(nuovoAggio);

            System.out.print("Nuovo numero di copie vendute (attualmente: " + pubblicazione.getCopieVendute() + "): ");
            int nuoveCopieVendute = inputNumeri.nextInt();
            pubblicazione.setCopieVendute(nuoveCopieVendute);

            // stampo l'output finale
            System.out.println("Pubblicazione modificata con successo.");

            System.out.println();
        }
        
    }


    // Metodo per rimuovere una pubblicazione -stesso principio di modificaPubblicazione()-
    public void rimuoviPubblicazione() {

        // controllo preventivo: verifico se l'ArrayList delle pubblicazioni è vuoto
        if (pubblicazioni.isEmpty()) {

            System.out.println("Nessuna pubblicazione disponibile per la rimozione.");
            return;
        } 
        

        // se l'ArrayList NON è vuoto...
        while (true) {

            mostraPubblicazioni(); // stampo l'elenco delle pubblicazioni registrate


            System.out.print("Inserisci l'indice (da 1 a " + pubblicazioni.size() + ") della pubblicazione da rimuovere (0 per uscire): ");
            int indice = inputNumeri.nextInt();

            // Verifico se l'utente vuole uscire dalla voce corrente
            if (indice == 0) {

                System.out.println("Sto tornando indietro...");
                return; 
            }

            if (indice < 0 || indice >= pubblicazioni.size()) {
                System.out.println("Indice non valido. Riprova.");
                continue;
            }

            // se l'indice è coerente...
            Pubblicazioni pubblicazione = pubblicazioni.get(indice - 1); // -1 per allineare gli indici salvati con gli indici dell'ArrayList
            pubblicazioni.remove(indice - 1); // rimuovo l'indice corrispondente (-1 come sopra)
            System.out.println("Sto rimuovendo " + pubblicazione.getNome() + "..."); // stampo la scelta dell'utente
            System.out.println("Pubblicazione rimossa con successo.");

            System.out.println();
        } 
  
        
    }


    // per comodità, scrivo metodo per stampare a terminale gli elementi indicizzati dell'ArrayList 
    public void mostraPubblicazioni() {

        System.out.println("---------- PUBBLICAZIONI REGISTRATE ----------");

            for (int i = 0; i < pubblicazioni.size(); i++) {

                Pubblicazioni pubblicazione = pubblicazioni.get(i);
                System.out.println((i +1) + ": " + pubblicazione.getNome()); // (i + 1) così impongo che l'indice parta da 1 e non da 0
            }
    }

    public void dettaglioPubblicazioni() {

        // controllo preventivo: verifico se l'ArrayList delle pubblicazioni è vuoto
        if (pubblicazioni.isEmpty()) {

            System.out.println();
            System.out.println("---------- PUBBLICAZIONI REGISTRATE ----------");
            System.out.println("Nessuna pubblicazione registrata.");
            System.out.println();
            return;
        } 
        
        // se l'ArrayList NON è vuoto...
        while (true) { // faccio un ciclo while (boolean true per entrarci) per permettere all'utente di rimanere dentro alla lista delle pubblicazioni registrate fintantochè non decide esplicitamente di tornare indietro (vedi rigo 256)
            
            mostraPubblicazioni(); // stampo l'indice delle pubblicazioni registrate
          
           // stampo l'istruzione per l'utente
            System.out.print("Scegli un indice della pubblicazione per vedere i dettagli (0 per uscire): ");
            int scelta = inputNumeri.nextInt(); // salvo l'input

            // Verifico se l'utente vuole uscire dalla voce corrente
            if (scelta == 0) {

                System.out.println("Sto tornando indietro...");
                return; 
            }

            // Verifico se l'indice scelto è coerente con l'indicizzazione delle pubblicazioni salvate
            if (scelta < 0 || scelta > pubblicazioni.size()) {

                System.out.println("Scelta non valida. Per favore, riprova.");
                continue; // ritorno all'inizio del ciclo while e richiedo l'indice
            }

            // Stampo il dettaglio della pubblicazione selezionata sfruttando il metodo stampaResoconto() nella classe Pubblicazioni
            // faccio una nuova istanza della classe Pubblicazione per richiamare il metodo corretto
            Pubblicazioni pubblicazioneDettaglio = pubblicazioni.get(scelta - 1); // -1 perchè negli ArrayList gli indici partono da 0 (quindi è tutto traslato di una unità)

            pubblicazioneDettaglio.stampaResoconto(); // richiamo finalmente il metodo corretto

            System.out.println();

        }    

        
    }

       
}

