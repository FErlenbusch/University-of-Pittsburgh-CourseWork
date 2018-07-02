import java.util.*;
import java.io.*;


class Node implements Comparable<Node> {
	Node level;
	Node child;
	char key;		// actual char node represents
	int value = 0;	// frequency of how many times user has used. 

	// Constructors
	public Node() {		
		this('\0', null, null);
	}

	public Node(char key) {
		this(key, null, null);
	}
	
	public Node(char key, Node level, Node child) {
		this.key = key;
		this.level = level;
		this.child = child;
	}

	// To allow sorting
	@Override
	public int compareTo(Node n) {
		return n.value - this.value;
	}
}

class Dictionary {
	char endChar = '^';		// termination char
	Node root;		
	Node last;			// Variable used in predicting, stores last char used in searching, so program can start at this node when searching for predictions rather then the root.


	// constructor
	public Dictionary() {
		root = new Node();
		last = root;
	}
	
	// method for adding words to the dictionary from dictionary.txt
	public void add(String str) {
		Node current = root;
		str = str + endChar;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (current.key == '\0') {		// If current node is empty add current character here and move to next.
				current.key = c;

				current.child = new Node();
				current = current.child;
			}
			else {
				while (current.key != c) {		// if current node is not for c look at nodes on same level.
					if (current.level == null) {	// if next level node is null then add node with current character and move to next.
						current.level = new Node(c);
	
						current = current.level;
						current.child = new Node();

						break;
					}
					else {		// if next level node is not null then loop to examine if it's a node for current character.
						current = current.level;
					}
				}
				current = current.child;
			}
		}
	}
	
	// method for loading values from user_history.txt increments frequency used value per each node. 
	public void loadValue(String str) {
		Node current = root;
		Node parent = root;

		str = str + endChar;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (current.key == '\0') {		// If current node is empty add current character here and move to next.
				current.key = c;
				current.value++;

				current.child = new Node();
				current = current.child;
			}
			else {
				while (current.key != c) {		// if current node is not for c look at nodes on same level.
					if (current.level == null) {	// if next level node is null then add node with current character and move to next.
						current.level = new Node(c);
	
						current = current.level;
						current.child = new Node();

						break;
					}
					else {
						current = current.level; 
					}
				}
				current.value++;
				
				levelSort(parent, i);	// sort level to bring nodes w/ user value to front of level 
				
				parent = current;
				current = current.child;
			}			
		}

		last = root;	// reset last variable for next word. 
	}

	// method used by loadValue method to sort the levels where user words are being added to the front of the level.
	private void levelSort(Node parent, int flag) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node current; 

		if (flag == 0) {
			current = parent;
		}
		else {
			current = parent.child;
		}

		while (current != null) {
			nodes.add(current);
			current = current.level;
		}

		Collections.sort(nodes);
		
		current = nodes.remove(0);

		if (flag == 0) {
			root = current;
		}
		else {
			parent.child = current;
		}

		while (nodes.size() != 0) {
			current.level = nodes.remove(0);
			current = current.level;

			if (nodes.size() == 0) {
				current.level = null;
			}
		}
	}

	// method to generate predictions.
	public ArrayList<String> predict(char c) { 
		ArrayList<String> predictions = new ArrayList<String>(); // predictions are actually just suffixs of what user has already entered, not full words.
		ArrayList<Node> path = new ArrayList<Node>();

		Node current = last;	// last is the level we are working on in the DLB. Saves time by not having to start from root and get here.
		Node start = current;

		char[] word = new char[50];
		int wordCnt = 0;
		boolean flag = false;

		while (current != null) { 		// find child node of current character in word.
			if (current.key == c) {
				start = current.child;
				last = start;
				break;
			}
			current = current.level;
		}

		if (current == null) {
			start = null;
		}

		while (predictions.size() != 5) {		// Generate predictions
			if (start == null) {	// if no other predictions
				break;
			}

			current = start;	// node to build word from

			if(current.value > 0) {
				flag = true;
			}

			while (current.key != endChar) {	// build word
				word[wordCnt++] = current.key;
				
				path.add(current);
				
				current = current.child;
			}

			String output = String.valueOf(word, 0, wordCnt);

			predictions.add(output); 	// save word
			
			if (flag) {		// first go through all words in user history
				if (current.level != null && current.level.value > 0) { 	// when end of last word can continue to be built
					start = current.level;
				}
				else {		// when a level has no more nodes on level
					start = null;

					while (start == null) {		// backtracks to the closest previous level to find a unused node to build a word.
						if (path.size() == 0) {
							flag = false;
							start = last;

							while (start != null && start.value > 0) { // resets start to beginning of unused words
								start = start.level;
							}

							break;
						}

						start = path.remove(path.size() - 1);
						start = start.level;

						word[wordCnt--] = '\0';

						if (start != null) {
							if (start.value == 0) {
								start = null;
							}
						}
					}
				}
			}
			else { 		// is still need predictions get from unused user words.
				if (current.level != null) { 	// when end of last word can continue to be built
					start = current.level;
				}
				else {		// when a level has no more nodes on level
					start = null;

					while (start == null) {		// backtracks to the closest previous level to find a unused node to build a word.
						if (path.size() == 0) {
							break;
						}

						start = path.remove(path.size() - 1);
						start = start.level;

						word[wordCnt--] = '\0';
					}
				}
			}
		}

		return predictions;
	}
}


public class ac_test {
	public static void main(String[] args) throws IOException {
		Dictionary dict = new Dictionary();
		ArrayList<String> predictions = new ArrayList<String>();
		ArrayList<Double> times = new ArrayList<Double>();

		Scanner userIn = new Scanner(System.in);	// for user input
		BufferedWriter out = new BufferedWriter(new FileWriter("user_history.txt", true)); // for output to user_history.txt

		char[] word = new char[50];
		int wordCnt = 0;
		char c;

		loadDictionary(dict);
		loadUserWords(dict);

		System.out.print("\nEnter your first character: ");
		c = userIn.next().charAt(0);

		while(true) {
			if (c == '!') {		// breaks loop and ends program.
				break;
			}
			else if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '$') { // to select a prediction or submit a word, and add it to user history.
				String str;

				if (c == '$') {
					str = String.valueOf(word, 0, wordCnt); // when submit word string is just what user enters.
				}
				else {
					str = String.valueOf(word, 0, wordCnt) + predictions.get(Character.getNumericValue(c) - 1); // appends suffix in prediction to what user has entered.
				}

				System.out.println("\n  WORD COMPLETED:  " + str + "\n");
				
				out.write(str, 0, str.length());	// saves word in user_history.txt
				out.write('\n');
				out.flush();

				dict.loadValue(str);		// loads word into dictionary if doesn't exist, increments frequency value for word in dictionary.
				word = new char[50];	
				wordCnt = 0;
				
				System.out.print("\nEnter first character of the next word: ");
				c = userIn.next().charAt(0);
			}
			else {		// generate predictions based off input.
				word[wordCnt++] = c;
				predictions.clear();
				
				long startTime = System.nanoTime();
				predictions.addAll(dict.predict(c));	// get predictions
				double timeTaken = (System.nanoTime() - startTime);
				times.add(timeTaken);	// for calculating average time at end of program.

				System.out.format("%n(%.6f s)%n", (timeTaken/1000000000));
				
				if (predictions.size() > 0) { 
					System.out.println("Predictions:");

					for (int i = 0; i < predictions.size(); i++) { // print out predictions. 
						String strPrint = String.valueOf(word, 0, wordCnt) + predictions.get(i);
						System.out.print("(" + (i + 1) + ") " + strPrint + "     ");
					}
					System.out.print("\n");
				}
				else {
					System.out.println("No predictions available.\n");
				}

				System.out.print("\nEnter the next character: ");
				c = userIn.next().charAt(0);
			}
		}

		double avg = 0;

		for (int i = 0; i < times.size(); i++) {	// calculate averages
			avg += times.get(i);
		}
		avg /= times.size();

		System.out.format("Average Time: %.6f s %n", (avg/1000000000));
		System.out.println("Bye!");
		out.close();
	}

	private static void loadDictionary(Dictionary dict) throws IOException {	// Method loads dictionary from file.
		String str;
		BufferedReader reader = new BufferedReader(new FileReader("dictionary.txt"));

		while ((str = reader.readLine()) != null ) {
			dict.add(str);
		}

		reader.close();
	}

	private static void loadUserWords(Dictionary dict) throws IOException { // loads user words into fictionary from file.
		String str;
		BufferedReader reader = new BufferedReader(new FileReader("user_history.txt"));

		while ((str = reader.readLine()) != null ) {
			dict.loadValue(str);
		}

		reader.close();
	}
}