public class CheckMeseGiorno {

    public int giorno;
    public String mese;
    
    public void checkValori(int giorno, String mese) {

        if (giorno <= 0 || giorno > 31) { // controllo i valori numerici di giorno
            System.out.println("Giorno non valido!");
            return;

        } else if (mese.equals("aprile") || mese.equals("giugno") || mese.equals("settembre") || mese.equals("novembre")) { // controllo i mesi < 31 giorni
                if (giorno > 30) {
                    System.out.println("Attenzione! " + mese + " ha 30 giorni al massimo!");
                }

        } else if (mese.equals("febbraio")) { // controllo Febbraio
                if (giorno > 28) {
                    System.out.println("Attenzione! " + mese + " ha 28 giorni al massimo");
                }

        } else if (mese.equals("gennaio") || mese.equals("marzo") || mese.equals("maggio") || mese.equals("luglio") || mese.equals("agosto") || mese.equals("ottobre") || mese.equals("dicembre")) {
            if (giorno > 31) {
                System.out.println("Attenzione! " + mese + " ha 31 giorni al massimo!");
            }

        } else { // se l'utente scrive un mese inesistente/errore di battitura
            System.out.println("Mese inesistente");
        }
        
    }
}
