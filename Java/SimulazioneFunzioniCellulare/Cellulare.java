// Simulazione del menù e di alcune funzionalità di un telefono cellulare


import java.util.Scanner; // importo la class Scanner


public class Cellulare {

    Scanner input = new Scanner(System.in); // "collego" Scanner al terminale 

    // scrivo le tre variabili globali
    double creditoDisponibile; // double perchè devo tenere conto anche dei centesimi
    int chiamateEffettuate; // chiaramente le chiamate effettuate sono numeri interi
    double tariffa; // in teoria la ricarica si può fare anche con i centesimi (ad es. tramite home banking)


    // scrivo il menù di interazione con l'utente a terminale
    public void menu() {

        int scelta; // qui andrò a salvare la scelta dell'utente (l'input) da terminale -è di tipo int perchè il menù avrà numeri interi a collegare le varie voci

        // costruisco il menù
        while (true) { // scelgo il boolean true per entrare dentro il ciclo while

            // stampo a schermo il menù del telefono cellulare con le varie opzioni
            System.out.println("-------- MENU' --------");
            System.out.println("1. Ricarica");
            System.out.println("2. Imposta tariffa");
            System.out.println("3. Chiama");
            System.out.println("4. Credito");
            System.out.println("5. Contatore chiamate");
            System.out.println("6. Azzera chiamate");
            System.out.println("7. Spegni");

            System.out.println(); // rigo vuoto per leggibilità del terminale
            System.out.print("Scegli una voce dal menù: "); // stampo ciò che si vuole chiedere all'utente

            scelta = input.nextInt(); // "collego e traduco" l'input da terminale (Scanner) nella memoria con il tipo primitivo dichiarato (int per scelta)


            switch (scelta) { // faccio uno switch() su scelta per programmare tutte le azioni delle varie opzioni
                case 1: // ricarica
                    ricaricaCredito(); // metodo relativo
                    break;
                case 2: // tariffa
                    impostaTariffa(); // metodo relativo
                    break;
                case 3: // chiamata
                    effettuaChiamata(); // metodo relativo
                    break;
                case 4: // credito
                    visualizzaCreditoDisponibile(); // metodo relativo
                    break;
                case 5: // contatore chiamate
                    visualizzaChiamateEffettuate(); // metodo relativo
                    break;
                case 6: // azzera chiamate
                    azzerareChiamateEffettuate(); // metodo relativo
                    break;
                case 7: // spegni
                    System.out.println("Il cellulare si sta spegnendo: buonanotte!"); // stampo messaggio di chiusura
                    return;
                default: // in tutti gli altri casi (l'utente immette un numero non corrispondente a nessuna voce)
                    System.out.println("Scelta non valida. Riprova!"); // stampo messaggio di errore
                    break;
            }
        }
        
    }

    // --------- IMPLEMENTO TUTTI I METODI INSERITI NELLO SWITCH() ---------

    // 1) ricarica
    public void ricaricaCredito() {

        double importo; // memoria dell'input dell'utente per questo metodo
        System.out.print("Inserisci importo da ricaricare (usa la virgola per i centesimi): "); // stampo le istruzioni per l'utente
        importo = input.nextDouble(); // "collego e traduco" l'input da terminale (Scanner) nella memoria con il tipo primitivo


        // controllo l'input importo
        if (importo > 0) { // se l'utente ha inserito una cifra positiva e non nulla...
            creditoDisponibile += importo; // ...aggiungo la cifra al credito pre-esistente (quest'ultimo parte da 0 se è la prima ricarica)
            System.out.println("SMS: ricarica effettuata con successo! Nuovo credito disponibile: " + creditoDisponibile + " euro"); // stampo il messaggio di avvenuta ricarica con il credito aggiornato

        } else { // controllo se l'utente ha immesso una cifra negativa
            System.out.println("SMS: la ricarica non è stata effettuata, importo non valido. Inserisci un importo positivo.");
        }
    }
    

    // 2) tariffa
    public void impostaTariffa() {

        System.out.print("Inserisci tariffa in centesimi/minuto: ");
        tariffa = input.nextDouble(); // ricordarsi che tariffa è di tipo double, già dichiarate tra le variabili globali (vedi rigo 14)


        if (tariffa > 0) { // come per la ricarica, controllo che la tariffa immessa dall'utente sia valida
            System.out.println("SMS: Tariffa impostata con successo!");

        } else { // altrimenti, stampo messaggio di errore
            System.out.println("SMS: tariffa non impostata, valore non valido. Inserisci un tariffa positiva.");
        }
    }

    // 3) chiamata

    public void effettuaChiamata() {

        if (creditoDisponibile == 0) { // controllo preventivo sul credito disponibile; se è nullo...

            System.out.println("SMS: il tuo credito è esaurito. Per chiamare è necessario effettuare una ricarica"); // messaggio di errore

        } else { // se credito è > 0, controllo il piano tariffario
            
            if (tariffa == 0) { // controllo preventivo sul piano tariffario applicato (non si possono effettuare chiamate gratis)

                System.out.println("SMS: nessun piano tariffario scelto. Per chiamare scegliere una tariffa da applicare"); // messaggio di errore

            } else { // se anche il piano tariffario è presente...

                int durata;
                System.out.print("Inserisci durata desiderata della chiamata in minuti: ");
                durata = input.nextInt();
                input.nextLine(); // registro il tasto invio dopo aver salvato l'input di durata desiderata della chiamata; così, entrando nell'if, l'utente può scrivere il numero di cellulare da chiamare

                if (creditoDisponibile >= (tariffa / 100) * durata) { // controllo se vi è sufficiente credito per effettuare la chiamata a seconda del piano tariffario scelto alla voce precedente ( tariffa/100 perchè è in centesimi/minuto)

                    String numeroCellulare; // uso la class String per non perdere nessuna informazione (compresi prefissi del tipo +39)
                    System.out.print("Inserisci numero da comporre: ");
                    numeroCellulare =  input.nextLine();

                    System.out.println("Sto chiamando il numero " + numeroCellulare + "...");
                    System.out.println("...");
                    System.out.println("...");

                    creditoDisponibile -= (tariffa / 100) * durata; // scalo dal credito il costo della chiamata effettuata e aggiorno il saldo di conseguenza
                    chiamateEffettuate++; // aggiorno il numero delle chiamate effettuate
                    System.out.println("SMS: Chiamata effettuata con successo!"); // stampo il messaggio della chiamata avvenuta correttamente

                } else { // se il credito non è sufficiente...
                    System.out.println("SMS: Credito insufficiente. Chiamata interrotta."); // messaggio di errore
                    creditoDisponibile = 0; // azzero il credito disponibile
                    chiamateEffettuate++; // tengo comunque conto della chiamata effettuata, anche se stata interrotta
                }
            }

        }

        
    }

    // 4) credito
    public void visualizzaCreditoDisponibile() {
        System.out.println("Credito disponibile: " + creditoDisponibile + " euro"); // se si vuole sapere il credito disponibile
    }

    // 5) contatore chiamate
    public void visualizzaChiamateEffettuate() {
        System.out.println("Chiamate effettuate: " + chiamateEffettuate); // è una sorta di registro chiamate, dove si stampa il numero delle chiamate effettuate (interrotte e non)
    }

    // 6) azzera chiamate
    public void azzerareChiamateEffettuate() {
        chiamateEffettuate = 0; // pulisco il registro chiamate
        System.out.println("SMS: Chiamate effettuate azzerate con successo!");
    }
    

}
