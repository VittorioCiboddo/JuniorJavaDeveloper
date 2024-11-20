import java.util.Scanner;

public class CinemaTest {
    
    public static void main(String[] args) {
        Cinema mySala = new Cinema();
        Scanner input = new Scanner(System.in);

        System.out.print("Inserisci il numero di spettatori: ");

        mySala.setNumSpettatori(input.nextInt());

        mySala.assegnaPosti();

        input.close();
        System.exit(0);

    }
}
