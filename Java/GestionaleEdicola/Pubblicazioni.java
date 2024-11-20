// Gestisco l'incapsulamento delle variabili generali e i metodi che sfruttano tali variabili nella seguente classe (che poi andrò a richiamare nella class dell'edicola)

import java.util.Scanner;

public class Pubblicazioni {

    private String nome;
    private int copieRicevute;
    private double prezzoCopertina;
    private int aggio; // Percentuale di guadagno
    private int copieVendute;


    // istanzio Scanner
    Scanner inputNumeri = new Scanner(System.in); // <-- per gli input numerici
    Scanner inputTesti = new Scanner(System.in); // <-- per gli input testuali (stringhe)


    // inizio incapsulamento

    // ---------- NOME ----------

    public void setNome(String nome) {
        // regola: il nome di ogni pubblicazione deve avere minimo 3 caratteri
        boolean ancora = true;


        do { 

            if (nome.length() >= 3) { // regola rispettata
                this.nome = nome;
                ancora = false;

            } else { // regola non rispettata

                System.out.println("Nome non valido. Una pubblicazione deve avere almeno 3 caratteri.");
                System.out.print("Inserisci il nome della pubblicazione (min 3 caratteri) o digita 'stop' per fermarti: ");
                nome = inputTesti.nextLine();

                if (nome.equals("stop")) {
                    break;
                }
            }

        } while (ancora);
    }
    
    
    public String getNome() {
        return nome;
    }



    // ---------- COPIE RICEVUTE ----------

    public void setCopieRicevute(int copieRicevute) {
        // regola: ho una piccola edicola, più di 50 copie a pubblicazione non è possibile vendere
        boolean ancora = true;

        do {

            if (copieRicevute > 0 && copieRicevute <= 50) { // regola rispettata
                this.copieRicevute = copieRicevute;
                ancora = false;

            } else { // regola non rispettata
                System.out.println("Troppe copie! L'edicola è piccola, non si possono ricevere più di 50 copie per pubblicazione.");
                System.out.print("Inserisci il numero di copie ricevute per pubblicazione (max 50): ");
                copieRicevute = inputNumeri.nextInt();
            }
            
        } while (ancora);
    }

    public int getCopieRicevute() {
        return copieRicevute;
    }




    // ---------- PREZZO DI COPERTINA ----------

    public void setPrezzoCopertina(double prezzoCopertina) {
        // regola: le copie non devono costare più di €10.00 e minimo €0.50
        boolean ancora = true;

        do {

            if (prezzoCopertina > 0.5 && prezzoCopertina <= 10.0) { // regola rispettata
                this.prezzoCopertina = prezzoCopertina;
                ancora = false;

            } else { // regola non rispettata
                System.out.println("Prezzo non conforme. Il costo deve essere min 0,50 euro e max 10,00 euro.");
                System.out.print("Inserisci il prezzo di copertina (min 0,50 euro e max 10,00 euro): ");
                prezzoCopertina = inputNumeri.nextDouble();
            }
            
        } while (ancora);
    }

    public double getPrezzoCopertina() {
        return prezzoCopertina;
    }



    // ---------- AGGIO (PERCENTUALE DI GUADAGNO) ----------

    public void setAggio(int aggio) {
        // regola: l'aggio deve oscillare tra il 5% ed il 20%
        boolean ancora = true;

        do {

            if (aggio >= 5 && aggio <= 20) { // regola rispettata
                this.aggio = aggio;
                ancora = false;
                
            } else { // regola non rispettata
                System.out.println("Aggio non conforme alle normative di legge. L'aggio deve essere compreso tra minimo 5% e massimo 20%");
                System.out.print("Inserire l'aggio (%) applicato (min 5%, max 20%): ");
                aggio = inputNumeri.nextInt();
            }

            
        } while (ancora);
    }

    public int getAggio() {

        return aggio;
    }




    // ---------- COPIE VENDUTE ----------

    public void setCopieVendute(int copieVendute) {
        // regola: le copie vendute per pubblicazione non devono superare le copie ricevute per pubblicazione (<= 50 come max)
        boolean ancora = true;

        do {

            if (copieVendute > 0 && copieVendute <= getCopieRicevute()) { // regola rispettata
                this.copieVendute = copieVendute;
                ancora = false;
                
            } else { // regola non rispettata
                System.out.println("Errore! Le copie vendute devono essere almeno 1 e comunque non superare le copie ricevute (" + getCopieRicevute() + ")." );
                System.out.print("Inserisci il numero delle copie vendute per pubblicazione (max " + getCopieRicevute() + "): ");
                copieVendute = inputNumeri.nextInt();
            }
            
        } while (ancora);
    }

    public int getCopieVendute() {
        return copieVendute;
    }

    
    // fine incapsulamento


    // scrivo il metodo per il calcolo della percentuale di guadagno sulle vendite dell'edicolante
    public double calcolaGuadagno() {

        return (getPrezzoCopertina() * (getAggio() / 100.0)) * getCopieVendute(); // faccio direttamente il return così evito di dichiarare un'altra variabile dove salvare il risultato dell'operazione e occupare memoria in più
    }

    // scrivo il metodo per stampare il resoconto della vendita al dettaglio di ogni pubblicazione
    public void stampaResoconto() {

        int copieRese = getCopieRicevute() - getCopieVendute(); // qui salvo le copie rimaste invendute e da restituire al venditore di zona

        System.out.println("Pubblicazione: " + getNome());
        System.out.println("Copie ricevute: " + getCopieRicevute());
        System.out.println("Copie vendute: " + getCopieVendute());
        System.out.println("Copie rese: " + copieRese);
        System.out.println();
    }


}

