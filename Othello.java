import java.util.*;

public class Othello
{
	public static int[][] board = new int[8][8];
	
	public Othello()
	{
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[i].length; j++)
			{
				board[i][j] = 0;
			}
		}
		board[3][3] = 1;
		board[3][4] = -1;
		board[4][3] = -1;
		board[4][4] = 1;
	}

	/*
	 * Flips the piece on the board at the given row/column
	 */
	public void flip(int row, int col, int[][] theBoard)
	{
		theBoard[row][col] *= -1;
	}
	
	/*
	 * Prints out the board
	 */
	public void printBoard()
	{
		System.out.println("   1  2  3  4   5  6  7  8");
		for(int i=0; i<board.length; i++)
		{
			System.out.print((char)(i + 65) + " ");
			for(int j=0; j<board[i].length; j++)
			{
				if(board[i][j] == 1)
				{
					System.out.print("🔵 ");
				}
				else if(board[i][j] == -1)
				{
					System.out.print("🔴 ");
				}
				else
				{
					System.out.print("⚪ ");
				}
			}
			System.out.println();
		}
	}
	
	/*
	 * Gets each position that would flip if a piece were
	 * placed on the board at the given row/column
	 * @param r, c The row and column of the piece that would be placed
	 * @param color The color of the piece that would be placed
	 */
	public List<int[]> getFlips(int r, int c, int[][] theBoard, String color)
	{
		List<int[]> spacesToFlip = new ArrayList<int[]>();
		int colorToNumber;
		if(color.equals("black"))
		{
			colorToNumber = 1;
		}
		else
		{
			colorToNumber = -1;

		}
		int row;
		int col;
		for(int x= -1; x <= 1; x++)
		{
			for(int y = -1; y <= 1; y++)
			{
				if(x == 0 && y == 0)
				{
					continue;
				}
				row = r;
				col = c;
				//Create new ArrayList
				List<int[]> directionalFlips = new ArrayList<int[]>();
				
				try
				{
					while(theBoard[row + x][col + y] == colorToNumber * -1)
					{
						row += x;
						col += y;
						directionalFlips.add(new int[] {row, col});
					}
					//If we didn't hit an edge
					if(theBoard[row + x][col + y] == colorToNumber)
					{
						for(int[] i : directionalFlips)
						{
							spacesToFlip.add(i);
						}
					}
				}
				catch(Exception IndexOutOfBoundsError)
				{
					continue;
				}
			}
		}
		return spacesToFlip;
	}
	
	/*
	 * Places a piece on the board
	 * @param color The color of the piece to be placed
	 */
	public void placePiece(int row, int col, int[][] theBoard, String color)
	{
		if(color.equals("black"))
		{
			theBoard[row][col] = 1;
		}
		else
		{
			theBoard[row][col] = -1;
		}
		for(int[] i: getFlips(row, col, theBoard, color))
		{
			flip(i[0], i[1], theBoard);
		}
	}
	
	/*
	 * Finds the best place to go by maximizing gain
	 * Extra points for edges and corners
	 * @param color The color of the player
	 */
	public int[] findBestSpot(int[][] theBoard, String color)
	{
		int maxScore = 0;
		int[] bestSpot = new int[2];
		int score;
		for(int i = 0; i < theBoard.length; i++)
		{
			for(int j = 0; j < theBoard[i].length; j++)
			{
				if(theBoard[i][j] != 0)
				{
					continue;
				}
				score = getFlips(i, j, theBoard, color).size();
				if((i % 7 == 0 || j % 7 == 0) && score > 0)
				{
					score += 1;
					if(i % 7 == 0 && j % 7 == 0)
					{
						score += 5;
					}
				}
				if(score > maxScore)
				{
					maxScore = score;
					bestSpot[0] = i;
					bestSpot[1] = j;
				}
			}
		}
		return bestSpot;
	}
	
	/*
	 * For bot only: 
	 * Finds best spot by maximizing gain and minimizing loss
	 */
	public int[] smartFindBestSpot()
	{
		int maxScore = -32;
		int[] bestSpot = new int[2];
		int score;
		int[][] nextBoard;
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[i].length; j++)
			{
				if(board[i][j] != 0)
				{
					continue;
				}
				score = getFlips(i, j, board, "white").size();
				if((i % 7 == 0 || j % 7 == 0) && score > 0)
				{
					score += 1;
					if(i % 7 == 0 && j % 7 == 0)
					{
						score += 5;
					}
				}
				else if(score == 0)
				{
					continue;
				}
				nextBoard = new int[8][8];
				for(int b=0; b<nextBoard.length; b++)
				{
					for(int c=0; c<nextBoard[i].length; c++)
					{
						nextBoard[b][c]=board[b][c];
					}
				}
					//subtract what our opponent would score next turn * 2 
					placePiece(i, j, nextBoard, "white");
					score -= getFlips((findBestSpot(nextBoard, "black"))[0], (findBestSpot(nextBoard, "black"))[1], nextBoard, "black").size() * 2;
						if(((findBestSpot(nextBoard, "black"))[0] % 7 == 0 || findBestSpot(nextBoard, "black")[1] % 7 == 0))
						{
							score -= 2;
							if((findBestSpot(nextBoard, "black"))[0] % 7 == 0 && findBestSpot(nextBoard, "black")[1] % 7 == 0)
							{
								score -= 10;
							}
						}
					if(score > maxScore)
					{
						maxScore = score;
						bestSpot[0] = i;
						bestSpot[1] = j;
					}
				
			}
		}
		return bestSpot;
	}
	
	/*
	 * Returns true if there are empty spots left on the board
	 */
	public boolean spotsLeft(String color)
	{
		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[i].length; j++)
			{
				if(board[i][j] == 0 && getFlips(i, j, board, color).size() > 0)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void endGame()
	{
		int blueScore = 0;
		int redScore = 0;
		for(int[] i: board)
		{
			for(int j: i)
			{
				if(j == 1)
				{
					blueScore++;
				}
				else if(j == -1)
				{
					redScore++;
				}
			}
		}
		System.out.println("\nGAME OVER");
		if(blueScore > redScore)
		{
			System.out.println("BLUE WINS!");
			System.out.println("with a score of " + blueScore);
			System.out.println("red scored " + redScore);
		}
		else if(redScore > blueScore)
		{
			System.out.println("RED WINS!");
			System.out.println("with a score of " + redScore);
			System.out.println("blue scored " + blueScore);
		}
			System.exit(0);
	}
	
	/*
	 * Simulates a game of Othello
	 * @param isTwoPlayer Whether opponent is a human or a bot
	 */
	public void play(boolean isTwoPlayer)
	{
		Scanner input = new Scanner(System.in);
		boolean validPosition;
		int row;
		int col;
		while(spotsLeft("white") || spotsLeft("black"))
		{
			if(spotsLeft("white"))
			{
				if(isTwoPlayer)
				{
					try
					{
					    Thread.sleep(1000);
					}
					catch(InterruptedException ex)
					{
					    Thread.currentThread().interrupt();
					}
					printBoard();
					System.out.println("\nPlayer one (red)");
					validPosition = false;
					while(!validPosition)
					{
						System.out.println("Row: ");
						while(true)
						{
							try
							{
								row = input.next().toLowerCase().charAt(0) - 97;
								if(row < 0)
								{
									row += 48;
								}
							}
							catch(InputMismatchException e)
							{
								System.out.println("Invalid input for row. Enter row:");
								input.next();
								continue;
							}
							break;
						}
						System.out.println();
						System.out.println("Column: ");
						while(true)
						{
							try
							{
								col = input.nextInt();
							}
							catch(InputMismatchException e)
							{
								System.out.println("Invalid input for column. Enter column:");
								input.next();
								continue;
							}
							break;
						}
						col--;
						if(getFlips(row, col, board, "white").size() > 0 && !(board[row][col] == 0))
						{
							placePiece(row, col, board, "white");
							validPosition = true;
							continue;
						}
						System.out.println("Not a valid position! (Note: you must flip at least one piece)");
					}
				}
				else
				{
					printBoard();
					System.out.println("\nOpponent's turn:\n");
					placePiece(smartFindBestSpot()[0], smartFindBestSpot()[1], board, "white");
				}
			}
			else
			{
				if(isTwoPlayer)
					{
						System.out.println("Player 1 has no moves available!");
					}
				else
					{
						System.out.println("Opponent has no moves available!");
					}
			}
			if(!spotsLeft("black"))
			{
				if(isTwoPlayer)
				{
					System.out.println("Player 1 has no moves available!");
				}
				else
				{
					System.out.println("You have no moves available!");
				}
				continue;
			}
			try
			{
			    Thread.sleep(1000);
			}
			catch(InterruptedException ex)
			{
			    Thread.currentThread().interrupt();
			}
			printBoard();
			if(isTwoPlayer)
			{
				System.out.println("\nPlayer two (blue)");
			}
			else
			{
				System.out.println("\nYour turn");
			}
			validPosition = false;
			while(!validPosition)
			{
				System.out.println("Row: ");
				while(true)
				{
					try
					{
						row = input.next().toLowerCase().charAt(0) - 97;
						if(row < 0)
						{
							row += 48;
						}
					}
					catch(InputMismatchException e)
					{
						System.out.println("Invalid input for row. Enter row:");
						input.next();
						continue;
					}
					break;
				}
				
				System.out.println();
				System.out.println("Column: ");
				while(true)
				{
					try
					{
						col = input.nextInt();
						break;
					}
					catch(InputMismatchException e)
					{
						System.out.println("Invalid input for column. Enter column:");
						input.next();
						continue;
					}
				}
				col--;
				if(getFlips(row, col, board, "black").size() > 0 && board[row][col] == 0)
				{
					placePiece(row, col, board, "black");
					validPosition = true;
					continue;
				}
				System.out.println("Not a valid position! You must flip at least one piece.");
			}
		}
		input.close();
		printBoard();
		endGame();
	}
}
