// Versione avanzata dell'esercizio nel file Cellulare.java
// In questa versione è stata aggiunta la rubrica (nomi e numeri telefonici), potenziata la voce del contatore chiamate (in modo tale da vedere anche i nomi chiamati o i numeri se non salvati in rubrica) e aggiunto anche il gioco della slot machine con i relativi menù/comandi


import java.util.Scanner;
import java.util.ArrayList;


public class CellulareExtra {

    Scanner input = new Scanner(System.in); // "collego" Scanner al terminale 

    // scrivo le tre variabili globali
    double creditoDisponibile; // double perchè devo tenere conto anche dei centesimi
    int chiamateEffettuate; // chiaramente le chiamate effettuate sono numeri interi
    double tariffa; // in teoria la ricarica si può fare anche con i centesimi (ad es. tramite home banking)


    // richiamo la classe Contatti e creo un ArrayList per sfruttarne gli elementi
    ArrayList<Contatti> contattiRubrica = new ArrayList<>();
    
    // preparo un altro ArrayList per registrare le chiamate effettuate
    ArrayList<String> registroChiamate = new ArrayList<>();

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
            System.out.println("5. Registro chiamate");
            System.out.println("6. Azzera chiamate");
            System.out.println("7. Rubrica"); // aggiunta della rubrica
            System.out.println("8. Slot machine"); // aggiunta della slot machine
            System.out.println("9. Spegni");

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
                case 5: // registro chiamate
                    visualizzaChiamateEffettuate(); // metodo relativo
                    break;
                case 6: // azzera chiamate
                    azzerareChiamateEffettuate(); // metodo relativo
                    break;
                case 7: // rubrica
                    gestisciRubrica(); // metodo relativo
                    break;
                case 8: // slot machine
                    giocaSlotMachine(); // metodo relativo
                    break;
                case 9: // spegni
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
        
        // faccio dei controlli prima di eventualmente fare delle chiamate

        if (creditoDisponibile == 0) { // se il credito a disposizione è nullo...
            System.out.println("SMS: il tuo credito è esaurito. Per chiamare è necessario effettuare una ricarica");

        } else { // 2° livello di controllo: piano tariffario

            if (tariffa == 0) { // se la tariffa non è stata scelta... (non si possono fare chiamate gratis)
                System.out.println("SMS: nessun piano tariffario scelto. Per chiamare scegliere una tariffa da applicare");

            } else { // se economicamente il telefono ha le risorse per effettuare le chiamate...

                // pongo l'utente davanti ad una scelta
                System.out.println("Per chiamare un contatto salvato in rubrica digita 1; per chiamare un numero di cellulare generico digita 2");
                int scelta = input.nextInt(); // qui salvo la scelta dell'utente
                input.nextLine(); // gestisco il tasto invio

                // inizializzo due variabili necessarie a gestire il metodo (inizialmente do loro valore null perchè mi agevola i controlli)
                String numeroCellulare = null;
                String nomeContatto = null;

                if (scelta == 1) { // l'utente ha scelto di chiamare un contatto salvato in rubrica
                    System.out.println("Scegli un contatto dalla rubrica (inserisci l'indice): ");

                    visualizzaRubrica(); // stampo la rubrica 

                    int indice = input.nextInt();
                    input.nextLine(); // gestisco il tasto invio

                    // controllo che l'indice immesso da terminale sia coerente
                    if (indice < 1 || indice > contattiRubrica.size()) {

                        System.out.println("Indice non valido. Riprova.");
                        return;
                    }

                    // recupero il contatto con l'indice corrispondente dall'ArrayList di Contatti
                    Contatti contatto = contattiRubrica.get(indice - 1); // -1 per "aggiustarmi" con gli indici dell'ArrayList
                    numeroCellulare = contatto.numero;
                    nomeContatto = contatto.nome;

                } else if (scelta == 2) { // l'utente ha scelto di chiamare un numero di cellulare generico non salvato in rubrica

                    System.out.println("Inserisci il numero di cellulare da chiamare: ");
                    numeroCellulare = input.nextLine();
                    
                } else { // per tutti gli altri casi (forse non necessario, ma per sicurezza...)

                    System.out.println("Scelta non valida.");
                    return;
                }

                // dichiaro un'ulteriore variabile per gestire il contatto o il numero di cellulare chiamati
                String nomeDaChiamare;

                if (nomeContatto != null) { // se l'utente ha scelto di chiamare un contatto della rubrica, dunque nomeContatto è una stringa non vuota...

                    nomeDaChiamare = nomeContatto; // inizializzo nomeDaChiamare con nomeContatto

                } else { // viceversa, se si chiama un numero di cellulare generico...

                    nomeDaChiamare = numeroCellulare; // inizializzo con numeroCellulare

                }
                

                // messaggio di chiamata
                System.out.println("Sto chiamando " + nomeDaChiamare + " ..."); // in questo modo l'utente vede se sta chiamando un contatto salvato oppure il numero immesso con una unica variabile
 
                int durata;
                System.out.print("Inserisci durata desiderata della chiamata in minuti: ");
                durata = input.nextInt();

                if (creditoDisponibile >= (tariffa / 100) * durata) { // controllo che ci sia sufficiente credito per effettuare la chiamata
                    System.out.println("...");
                    System.out.println("...");
                    creditoDisponibile -= (tariffa / 100) * durata; // aggiorno il credito
                    chiamateEffettuate++; // conto la chiamata effettuata

                    if (nomeContatto != null) {
                        registroChiamate.add(nomeContatto); // aggiungo il contatto chiamato al registro delle chiamate

                    } else {
                        registroChiamate.add(numeroCellulare); // altrimenti salvo il numero chiamato nel registro delle chiamate

                    }


                    System.out.println("SMS: Chiamata effettuata con successo!");

                } else { // se il credito non è sufficiente/finisce...
                    System.out.println("SMS: Credito insufficiente. Chiamata interrotta.");
                    creditoDisponibile = 0;
                    chiamateEffettuate++; // conto comunque la chiamata anche se interrotta

                    if (nomeContatto != null) {
                        registroChiamate.add(nomeContatto); // aggiungo comunque il contatto chiamato al registro delle chiamate
                        

                    } else {
                        registroChiamate.add(numeroCellulare); // altrimenti salvo comunque il numero chiamato nel registro delle chiamate

                    }
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

        if (registroChiamate.isEmpty()) {
            System.out.println("Nessuna chiamata registrata");
        } else {
            System.out.println("Dettagli delle chiamate:");
            for (String chiamata : registroChiamate) { // uso il ciclo for avanzato per stampare ogni elemento presente nell'ArrayList di registroChiamate che chiamo chiamata
                System.out.println(chiamata);
            }
        }
    }

    // 6) azzera chiamate
    public void azzerareChiamateEffettuate() {
        chiamateEffettuate = 0; // pulisco il registro chiamate
        registroChiamate.clear(); // ripulisco anche i dettagli delle chiamate effettuate (.clear() serve a svuotare una lista -un ArrayList in questo caso- di tutti i suoi elementi)
        System.out.println("SMS: Chiamate effettuate azzerate con successo!");
    }
    

    // 7) RUBRICA
    public void gestisciRubrica() {
        int sceltaRubrica; // memoria su cui salverò le scelte dell'utente da terminale (input) per il sottomenù della rubrica

        while (true) { // come per menu() scelgo di nuovo il boolean true come condizione di entrata nel ciclo while

            // costruisco il sotto menù per la rubrica
            System.out.println("-------- MENU'  RUBRICA --------");
            System.out.println("1. Aggiungi contatto");
            System.out.println("2. Rimuovi contatto");
            System.out.println("3. Visualizza contatti");
            System.out.println("4. Torna al menù principale");
            System.out.println(); // rigo vuoto per leggibilità da terminale

            System.out.print("Scegli un'opzione: "); // stampo le istruzioni per l'utente
            sceltaRubrica = input.nextInt();
            input.nextLine(); // 

            switch (sceltaRubrica) {
                case 1: // salvo un nuovo contatto in rubrica
                    aggiungiContatto();
                    break;
                case 2: // elimino un contatto dalla rubrica
                    rimuoviContatto();
                    break;
                case 3: // mostro la rubrica
                    visualizzaRubrica();
                    break;
                case 4: // se si vuole tornare al menù principale (= uscire dall'ambiente rubrica)
                    return;
                default: // in tutti gli altri casi...
                    System.out.println("Scelta non valida. Riprova!");
            }
        }

    }

    // -------- IMPLEMENTO TUTTI I METODI DEL SOTTO MENU' DELLA RUBRICA -------

    // 1) salvare nuovi contatti in rubrica
    public void aggiungiContatto() {
        System.out.print("Inserisci nome contatto: "); // stampo l'istruzione per l'utente (primo input)
        String nomeIn = input.nextLine(); // memoria dove salvo il primo input dell'utente 
        System.out.print("Inserisci numero di telefono: "); // stampo l'istruzione per l'utente (secondo input)
        String numeroTelefono = input.nextLine(); // memoria dove salvo il secondo input dell'utente

        contattiRubrica.add(new Contatti(nomeIn, numeroTelefono)); // aggiungo il contatto (nome + numero di telefono) all'ArrayList dei Contatti

        System.out.println("Contatto aggiunto con successo!"); // stampo il messaggio di avvenuta aggiunta in rubrica
    }

    // 2) rimuovere contatti dalla rubrica
    public void rimuoviContatto() {

        visualizzaRubrica(); // stampo la rubrica con i contatti salvati

        System.out.print("Inserisci l'indice del contatto da rimuovere: "); // stampo l'istruzione per l'utente
        int indice = input.nextInt();

        if (indice < 1 || indice > contattiRubrica.size()) {
            System.out.println("Indice non valido. Riprova.");
            return;
        }

        Contatti contattoRimosso = contattiRubrica.remove(indice - 1);
        System.out.println("Il contatto " + contattoRimosso.nome + " è stato rimosso con successo!");

    }

    // 3) stampare a terminale la rubrica (magari per effettuare delle chiamate)
    public void visualizzaRubrica() {
        System.out.println("----- RUBRICA ------");

        // controllo preventivo che la rubrica abbia qualche contatto salvato
        if (contattiRubrica.isEmpty()) { // .isEmpty() permette di avere come output un boolean per controllare che una lista (o un ArrayList in questo caso) abbia zero elementi oppure no

            System.out.println("Nessun contatto disponibile");

        } else { // se la rubrica non è vuota...
            for (int i = 0; i < contattiRubrica.size(); i++) {
                Contatti contatto = contattiRubrica.get(i);
                System.out.println((i + 1) + ". " + contatto.nome + " (" + contatto.numero + ") "); //... stampo i nomi ed il numero dei contatti -(i + 1) per stampare i contatti salvati a partire da 1 e non da 0-
            }
        }
    }

    // --- GIOCO SLOT MACHINE ---
    public void giocaSlotMachine() {

        int sceltaSlot; // memoria dove salvare la scelta dell'utente da terminale (input)

        // costruisco il mini menù della slot machine
        while (true) {
            System.out.println("-------- SLOT MACHINE --------");
            System.out.println("1. Gioca");
            System.out.println("2. Esci");
            System.out.print("Scegli un'opzione: ");
            sceltaSlot = input.nextInt();

            // "collego" ogni voce del mini menù ai relativi metodi
            switch (sceltaSlot) {
                case 1:
                    gioca();
                    break;
                case 2:
                    mostraStatisticheSlot();
                    return;
                default:
                    System.out.println("Scelta non valida. Riprova!");
            }
        }
    }

    // dichiaro le due variabili che mi servono in gioca() (uso come contatori)
    int partiteGiocate = 0;
    int vittorie = 0;

    // implemento il metodo del case 1 (rigo 309)
    public void gioca() {

        boolean continua = true; // variabile booleana per continuare a giocare alla slot
        
        while (continua) {

            partiteGiocate++; // ogni volta che si richiama il metodo gioca() la considero una nuova partita e dunque incremento il contatore 

            // vedo la slot machine come gioco di 3 numeri (invece di 3 figure); estraggo casualmente tre numeri tra 1 e 5 e ne faccio il casting a int (così tronco la parte decimale)
            int num1 = (int) (Math.random() * 5) + 1;
            int num2 = (int) (Math.random() * 5) + 1;
            int num3 = (int) (Math.random() * 5) + 1;

            // stampo a schermo il risultato dell'estrazione
            System.out.println("Hai estratto: " + num1 + " " + num2 + " " + num3);

            // Controlla se i numeri sono uguali
            if (num1 == num2 && num2 == num3) {

                System.out.println("Hai vinto!"); // VITTORIA
                vittorie++; // incremento il contatore delle vittorie

            } else {
                System.out.println("Hai perso!"); // SCONFITTA
            }

            continua = false; // condizione di uscita dal ciclo while
        
        }
        
    }

    // metodo per il case 2 (vedi rigo 312)
    public void mostraStatisticheSlot() {

        if (partiteGiocate > 0) { // se vi è uno storico non nullo di partite...
            System.out.println("Hai vinto " + vittorie + " volte su " + partiteGiocate + " partite giocate."); // stampo un riepilogo delle statistiche
        } else { // altrimenti...
            System.out.println("Non hai ancora giocato a nessuna partita"); // messagio di errore
        }
        
    }
}

