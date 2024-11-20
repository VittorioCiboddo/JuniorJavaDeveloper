public class Fibonacci {
    
    // definisco le due variabili iniziali della serie di Fibonacci
    public int a = 0;
    public int b = 1;


    // scrivo il metodo
    public void mostraSequenza() {

        // prima del ciclo, stampo con .print() (così evito di andare a capo) il dominio del problema
        System.out.print("Serie di Fibonacci fino a 100: ");

        // costruisco l'effettiva serie con while
        while (a <= 100) { // fintantochè a <= 100...

            System.out.print(a + " "); // tengo traccia dei numeri calcolati

            b = a + b; // Sfrutto la variabile b e la aggiorno con la somma di a e b precedente (in questo modo b risulta essere il numero successivo nella serie dato dalla somma dei due numeri precedenti)

            a = b - a; // faccio un passaggio simile del rigo 17 ma per la variabile a: in questo modo, a si aggiorna al valore precedente di b e "avanza di un posto nella serie"

            /* SPIEGAZIONE PIU' ESTESA
                Dati a=0 e b=1 come numeri iniziali della sequenza di Fibonacci e ricordando che la stessa sequenza è composta da numeri frutto della somma dei due precedenti:
                    -rigo 17: sommo i due numeri a disposizione (a + b) ed il risultato lo salvo nella stessa variable b (in questo modo b si aggiorna al nuovo valore e diventa il nuovo numero della sequenza)
                    -rigo 18: il b precedente è come se fosse avanzato di un posto in avanti nella sequenza (se all'inizio è b=1 2° numero, dopo la somma risulterà b=1 ma 3° numero); per continuare con la sequenza devo aggiornare in modo simile anche a (se all'inizio a=0 1° numero, per continuare la sequenza ora che b=1 è diventato 3° numero, devo far avanzare a come 2° numero e per farlo sottraggo b 3° numero ad a 1° numero; salvo il risultato sempre nella variabile a che, aggiornandosi, diventa a=1 2° numero)
            */
        }
    }
}
