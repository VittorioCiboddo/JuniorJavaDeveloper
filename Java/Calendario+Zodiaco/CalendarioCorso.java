public class CalendarioCorso {
    
    // variabili 
    public String mese;
    public int giorno;
    


    // scrivo il metodo generale per stampare il calendario
    public void mostraCorso(int day, String month) {
        
        // Istanzio la classe CheckMeseGiorno 
        CheckMeseGiorno controllo = new CheckMeseGiorno();
        controllo.checkValori(day, month); // in questo caso, mostraCorso() è il caller di checkValori()

        
        // controllo le combinazioni di giorno + mese in base al messaggio da stampare
        if ((giorno >= 11 && giorno <= 29) && mese.equals("luglio")) {
            System.out.println("Corso: Web"); // Web
        

        } else if (giorno == 30 && mese.equals("luglio")) {
            System.out.println("Corso: DASA"); // DASA
            

        } else if ((giorno == 31 && mese.equals("luglio")) || (((giorno >=1 && giorno <= 4) || (giorno >= 26 && giorno <= 31)) && mese.equals("agosto") || ((giorno >= 1 && giorno <= 19) && mese.equals("settembre")) )) {
            System.out.println("Corso: Java"); // Java
            

        } else if (((giorno >= 20 && giorno <= 30) && mese.equals("settembre")) || ((giorno >= 1 && giorno <= 3) && mese.equals("ottobre")) ) {
            System.out.println("Corso: Database"); // Database
            

        } else if ((giorno >= 4 && giorno <= 7) && mese.equals("ottobre")) {
            System.out.println("Corso: JDBC"); // JDBC
            
             
        } else if ((giorno >= 8 && giorno <= 20) && mese.equals("ottobre")) {
            System.out.println("Corso: Spring"); // Spring
            
            
        } else if (giorno == 21 && mese.equals("ottobre")) {
            System.out.println("Corso: GitHub"); // GitHub
            

        } else if (((giorno >= 22 && giorno <= 31) && mese.equals("ottobre")) || ((giorno >= 2 && giorno <= 4) && mese.equals("novembre"))) {
            System.out.println("Corso: Project Work"); // Project Work
            

        } else if (((giorno >= 5 && giorno <= 25) && mese.equals("agosto")) || (giorno == 1 && mese.equals("novembre"))) {
            System.out.println("Festivo"); // Festivi
            

        } else if ((giorno >= 1 && giorno <= 10) & mese.equals("luglio")) {
            System.out.println("Corso non ancora avviato"); // Corso non ancora avviato
            

        } else if (((giorno >= 5 && giorno <= 30) && mese.equals("novembre")) || mese.equals("dicembre")) {
            System.out.println("Corso terminato"); // Corso terminato


        } else { 
            System.out.println("Corso non presente"); // Corso non presente
        }
    }


}
