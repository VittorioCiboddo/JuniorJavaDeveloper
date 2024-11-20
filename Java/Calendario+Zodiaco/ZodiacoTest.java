public class ZodiacoTest {
    
    public static void main(String[] args) {
        Zodiaco mioZodiaco = new Zodiaco();
        mioZodiaco.giorno = 9;
        mioZodiaco.mese = "marzo".toLowerCase();

        System.out.println("Stai visualizzando il " + mioZodiaco.giorno + " " +  mioZodiaco.mese.toUpperCase()); // stampo il giorno ed il mese scelti dall'utente
        mioZodiaco.mostraZodiaco(mioZodiaco.giorno, mioZodiaco.mese); // richiamo il metodo per visualizzare i segni zodiacali -mostraZodiaco() è il worker del main()-
    }
}
