public class TavolaPitagorica {
    

    // scrivo il metodo
    public void mostraTavola() {

        // mi servono i cicli for annidati

        for (int i = 1; i <= 10; i++) { // ciclo for esterno per le righe (numeri da 1 a 10 inclusi)

            for (int j = 1; j <= 10; j++) { // ciclo for interno per le colonne (numeri da 1 a 10 inclusi)
                System.out.printf("%4d", i * j); // .printf(format) serve per stampare i risultati di i*j in modo formattato, ovvero in uno spazio di 5 caratteri di tipo int
            }

            System.out.println(); 
        }

    }
}
