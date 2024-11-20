import java.util.Scanner;


public class CinemaExtraTest {
    
    public static void main(String[] args) {

        // istanzio Scanner e la classe CinemaExtra
        CinemaExtra myMultisala = new CinemaExtra();
        Scanner input = new Scanner(System.in);

        // chiedo all'utente quanto è grande la sala
        System.out.print("Inserisci la capacità della sala: ");
        myMultisala.setCapacitaSala(input.nextInt());

        // chiedo all'utente quanti spettatori sono entrati
        System.out.print("Inserisci il numero di spettatori: ");
        myMultisala.setNumSpettatori(input.nextInt());

        // richiamo il metodo necessario
        myMultisala.acquistaBigliettiMultipli();

        // chiudo le istanze di Scanner e libero risorse
        input.close();
        System.exit(0);
    }
}
