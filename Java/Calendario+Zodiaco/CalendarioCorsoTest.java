public class CalendarioCorsoTest {
    
    public static void main(String[] args) {

        CalendarioCorso mioCalendario = new CalendarioCorso();
        mioCalendario.giorno = 31;
        mioCalendario.mese = "ottobre";

        System.out.println("Stai visualizzando il " + mioCalendario.giorno + " " +  mioCalendario.mese.toUpperCase()); // stampo il giorno ed il mese scelti dall'utente

        // Chiama il metodo mostraCorso()
        mioCalendario.mostraCorso(mioCalendario.giorno, mioCalendario.mese); // il main è il caller di mostraCorso()
        
    }
}
