import java.util.*;
import java.io.*;

public class Boggle
{
	public static void main ( String args[] ) throws Exception
	{
		// Error Check input
		if (args.length < 1 )
		{
			System.out.println("\nusage: C:\\> java Boggle <input filename>\n\n");
			System.exit(0);
		}

		// Initalize variables
		HashMap<String, Integer> dictTree = new  HashMap<String, Integer>();
		HashSet<String> dictHash = new HashSet<String>();
		loadDictionary( dictTree, dictHash );
		TreeSet<String> answers = new TreeSet<String>();
		String[][] board = loadBoard( args[0] );

		// Find all possible words in Boggle board
		String word = new String();
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++)
			{
				word = board[i][j];
				board[i][j] = board[i][j].toUpperCase();
				if ( findWords( board, i, j, word, "", dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[i][j] = board[i][j].toLowerCase();
			}

		// Sort all words found and print to screen.
		while ( !answers.isEmpty() )
			System.out.println( answers.pollFirst() );
	}

	// Scan in Dictionary to Tree for determination if prefixes exist.
	private static void loadDictionary( HashMap<String, Integer> dictTree, HashSet<String> dictHash ) throws Exception
	{
		String line;
		BufferedReader reader = new BufferedReader( new FileReader( "dictionary.txt" ) );
	    while ( ( line = reader.readLine() ) != null ) 
	    {
	    	if (line.length() > 2)
	    	{
				dictHash.add( line );
		    	String lineBuild = new String();
	       		for (int i = 0; i < line.length(); i++)
	       		{
	       			lineBuild = lineBuild + line.charAt( i );
	       			if ( dictTree.containsKey( lineBuild ) )
	       				dictTree.put( lineBuild, dictTree.get( lineBuild )+1 );
	       			else
	       				dictTree.put( lineBuild, 1 );
	       		}
	       	}
       	}
       	reader.close();
	}

	// Load Boggle board from given file.
	private static String[][] loadBoard( String inFileName ) throws Exception
	{
		Scanner infile = new Scanner ( new File( inFileName ) );
		int rows = infile.nextInt();
		int cols = rows; 
		String[][] board = new String[rows][cols];
		for(int r = 0; r < rows ; r++)
			for(int c = 0; c < cols; c++)
				board[r][c] = infile.next();
		infile.close();
		return board;
	}

	// Find all possible words within Boggle Board.
	private static Boolean findWords( String[][] board, int x, int y, String word, String letter,
		HashMap<String, Integer> dictTree, HashSet<String> dictHash, TreeSet<String> answers )
	{		
		Boolean track = false;
		word = word + letter.toLowerCase();

		// Test if word is still in the dictionary.
		if ( dictHash.contains( word ) )
			{
				answers.add( word );
				dictHash.remove( word );
				dictTree.put( word, dictTree.get( word )-1 );
				track = true;
			}

		if ( dictTree.containsKey( word ) && dictTree.get( word ) != 0 )
		{
			// does N make a word?
			if ( !( x-1 == -1 || y == -1 || x-1 == board.length || y == board[0].length ) 
				&& board[ x-1 ][ y ].equals( board[ x-1][ y ].toLowerCase() ) )
			{
				board[ x-1 ][ y ] = board[ x-1 ][ y ].toUpperCase();
				if ( track = findWords( board, x-1, y, word, board[ x-1 ][ y ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x-1 ][ y ] = board[ x-1 ][ y ].toLowerCase();
			}
			// does NE make a word?
			if ( !( x-1 == -1 || y+1 == -1 || x-1 == board.length || y+1 == board[0].length ) 
				&& board[ x-1 ][ y+1 ].equals( board[ x-1 ][ y+1 ].toLowerCase() ) )
			{
				board[ x-1 ][ y+1 ] = board[ x-1 ][ y+1 ].toUpperCase();
				if ( track = findWords( board, x-1, y+1, word, board[ x-1 ][ y+1 ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x-1 ][ y+1 ] = board[ x-1 ][ y+1 ].toLowerCase();
			}
			// does E make?
			if ( !( x == -1 || y+1 == -1 || x == board.length || y+1 == board[0].length ) 
				&& board[ x ][y+1].equals( board[ x ][y+1].toLowerCase() ) )
			{
				board[ x ][ y+1 ] = board[ x ][y+1].toUpperCase();
				if ( track = findWords( board, x, y+1, word, board[ x ][y+1], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x ][ y+1 ] = board[ x ][y+1].toLowerCase();
			}
			// does SE?
			if ( !( x+1 == -1 || y+1 == -1 || x+1 == board.length || y+1 == board[0].length ) 
				&& board[ x+1 ][ y+1 ].equals( board[ x+1 ][ y+1 ].toLowerCase() ))
			{
				board[ x+1 ][ y+1 ] = board[ x+1 ][ y+1 ].toUpperCase();
				if ( track = findWords( board, x+1, y+1, word, board[ x+1 ][ y+1 ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x+1 ][ y+1 ] = board[ x+1 ][ y+1 ].toLowerCase();
			}
			// does S?
			if ( !( x+1 == -1 || y == -1 || x+1 == board.length || y == board[0].length ) 
				&& board[ x+1 ][ y ].equals( board[ x+1 ][ y ].toLowerCase() ) )
			{
				board[ x+1 ][ y ] = board[ x+1 ][ y ].toUpperCase();
				if ( track = findWords( board, x+1, y, word, board[ x+1 ][ y ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x+1 ][ y ] = board[ x+1 ][ y ].toLowerCase();
			}
			// does SW?
			if ( !( x+1 == -1 || y-1 == -1 || x+1 == board.length || y-1 == board[0].length ) 
				&& board[ x+1 ][ y-1 ].equals( board[ x+1 ][ y-1 ].toLowerCase() ) )
			{
				board[ x+1 ][ y-1 ] = board[ x+1 ][ y-1 ].toUpperCase();
				if ( track = findWords( board, x+1, y-1, word, board[ x+1 ][ y-1 ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x+1 ][ y-1 ] = board[ x+1 ][ y-1 ].toLowerCase();
			}
			// does W?
			if ( !( x == -1 || y-1 == -1 || x == board.length || y-1 == board[0].length ) 
				&& board[ x ][ y-1 ].equals( board[ x ][ y-1 ].toLowerCase() ) )
			{
				board[ x ][ y-1 ] = board[ x ][ y-1 ].toUpperCase();
				if ( track = findWords( board, x, y-1, word, board[ x ][ y-1 ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x ][ y-1 ] = board[ x ][ y-1 ].toLowerCase();
			}
			// does NW?
			if ( !( x-1 == -1 || y-1 == -1 || x-1 == board.length || y-1 == board[0].length ) 
				&& board[ x-1 ][ y-1 ].equals( board[ x-1 ][ y-1 ].toLowerCase() ))
			{
				board[ x-1 ][ y-1 ] = board[ x-1 ][ y-1 ].toUpperCase();
				if ( track = findWords( board, x-1, y-1, word, board[ x-1 ][ y-1 ], dictTree, dictHash, answers ) )
					dictTree.put( word, dictTree.get( word )-1 );
				board[ x-1 ][ y-1 ] = board[ x-1 ][ y-1 ].toLowerCase();
			}
		}
		return track;
	}

}