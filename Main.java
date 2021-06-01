import java.util.*;

public class Main {
	
	public static void main(String[] args)
	{
		Othello game = new Othello();
		Scanner input = new Scanner(System.in);
		int answer = 0;
		while(answer > 2 || answer < 1)
		{
			System.out.println("How many players for Othello?");
			answer = input.nextInt();
			if(answer > 2)
			{
				System.out.println("You must play with 1 or 2 players.");
				System.out.println();
			}
		}
		game.play(answer == 2);
	}
}