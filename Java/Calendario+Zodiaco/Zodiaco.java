public class Zodiaco {
    
    // variabili 
    public String mese;
    public int giorno;



    // scrivo il metodo per stampare i segni zodiacali
    public void mostraZodiaco(int day, String month) {

        // Istanzio la classe CheckMeseGiorno 
        CheckMeseGiorno controllo = new CheckMeseGiorno();
        controllo.checkValori(day, month); // in questo caso, mostraZodiaco() è il caller di checkValori()

        if ((giorno >= 21 && giorno <= 31) && mese.equals("marzo") || ((giorno >= 1 && giorno <= 19) && mese.equals("aprile")) ) {
            System.out.println("Il tuo segno zodiacale è Ariete"); //Ariete

        } else if (((giorno >= 20 && giorno <= 30) && mese.equals("aprile")) || ((giorno >= 1 && giorno <= 20) && mese.equals("maggio"))) {
            System.out.println("Il tuo segno zodiacale é Toro"); // Toro

        } else if (((giorno >= 21 && giorno <= 31) && mese.equals("maggio")) || ((giorno >= 1 && giorno <= 20) && mese.equals("giugno"))) {
            System.out.println("Il tuo segno zodiacale è Gemelli"); // Gemelli

        } else if (((giorno >= 21 && giorno <= 30) && mese.equals("giugno")) || ((giorno >= 1 && giorno <= 22) && mese.equals("luglio"))) {
            System.out.println("Il tuo segno zodiacale é Cancro"); // Cancro

        } else if (((giorno >= 23 && giorno <= 31) && mese.equals("luglio")) || ((giorno >= 1 && giorno <= 23) && mese.equals("agosto"))) {
            System.out.println("Il tuo segno zodiacale é Leone"); // Leone

        } else if (((giorno >= 24 && giorno <= 31) && mese.equals("agosto")) || ((giorno >= 1 && giorno <= 22) && mese.equals("settembre"))) {
            System.out.println("Il tuo segno zodiacale é Vergine"); // Vergine

        } else if (((giorno >= 23 && giorno <= 30) && mese.equals("settembre")) || ((giorno >= 1 && giorno <= 22) && mese.equals("ottobre"))) {
            System.out.println("Il tuo segno zodiacale é Bilancia"); // Bilancia

        } else if (((giorno >= 23 && giorno <= 31) && mese.equals("ottobre")) || ((giorno >= 1 && giorno <= 21) && mese.equals("novembre"))) {
            System.out.println("Il tuo segno zodiacale é Scorpione"); // Scorpione

        } else if (((giorno >= 22 && giorno <= 30) && mese.equals("novembre")) || ((giorno >= 1 && giorno <= 21) && mese.equals("dicembre"))) {
            System.out.println("Il tuo segno zodiacale é Sagittario"); // Sagittario

        } else if (((giorno >= 22 && giorno <= 31) && mese.equals("dicembre")) || ((giorno >= 1 && giorno <= 19) && mese.equals("gennaio"))) {
            System.out.println("Il tuo segno zodiacale é Capricorno"); // Capricorno

        } else if (((giorno >= 20 && giorno <= 31) && mese.equals("gennaio")) || ((giorno >= 1 && giorno <= 19) && mese.equals("febbraio"))) {
            System.out.println("Il tuo segno zodiacale é Acquario"); // Acquario

        } else if (((giorno >= 20 && giorno <= 28) && mese.equals("febbraio")) || ((giorno >= 1 && giorno <= 20) && mese.equals("marzo"))) {
            System.out.println("Il tuo segno zodiacale é Pesci"); // Pesci
        }
    }

}
