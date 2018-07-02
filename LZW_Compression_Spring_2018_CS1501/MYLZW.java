public class MYLZW {
	private static String M;                // mode of operation when book fills up
	private static final int R = 256;       // number of input chars
	private static double RawData = 0.0;		// uncompressed data size;
	private static double ModData = 0.0;		// compressed data size;
	private static double Ratio = 0.0;			// compression ratio;
	private static int L;                   // number of codewords = 2^W
	private static int W;                   // codeword width
	private static int I; 					// next available codeword value for expansion
	private static int Code;				// codeword for EOF for compression
	
	

	public static void compress() { 

		String input = BinaryStdIn.readString();
		TST<Integer> st = initCompressBook();
		BinaryStdOut.write(st.get(M), W);									// Write mode of compression to begining of file

		while (input.length() > 0) {
			String s = st.longestPrefixOf(input);							// Find max prefix match s.
			BinaryStdOut.write(st.get(s), W);								// Print s's encoding.
			int t = s.length();
			
			RawData += s.getBytes().length * 8.0;							// get size of s in bytes and convert to bits and add to uncompressed data size.
			ModData += W;													// add size of codeword in bits to compressed data size.
			
			if (t < input.length()) {										// Add s to symbol table.
				if (Code == L) {
					if (W < 16) upCodeWordSize();							// Increase Codeword Size up to 16 bits
					else if (M.equals("r")) st = initCompressBook();		// Reset book to inital state.
					else if (M.equals("m")) 
						if (monitor()) st = initCompressBook();
				}
				if (Code < L) st.put(input.substring(0, t + 1), Code++);
			}
			input = input.substring(t);										// Scan past s in input.
		}
		BinaryStdOut.write(R, W);
		BinaryStdOut.close();
	} 



	public static void expand() {
		String[] st = initExpandBook();						// initalize symbol table
		M = "" + BinaryStdIn.readChar(W);					// get mode from first char of file
		int codeword = BinaryStdIn.readInt(W);
		if (codeword == R) return;							// expanded message is empty string
		String val = st[codeword];

		while (true) {
			RawData += val.getBytes().length * 8.0;			// get size of s in bytes and convert to bits and add to uncompressed data size.
			ModData += W;									// add size of codeword in bits to compressed data size.

			if (I == L) {
				if (W < 16) upCodeWordSize();
				else if (M.equals("r")) st = initExpandBook();
				else if (M.equals("m"))
					if (monitor()) st = initExpandBook();
			}

			BinaryStdOut.write(val);
			codeword = BinaryStdIn.readInt(W);
			if (codeword == R) break;
			String s = st[codeword];
			if (I == codeword) s = val + val.charAt(0);		// special case hack
			if (I < L) st[I++] = val + s.charAt(0);
			val = s;
		}
		BinaryStdOut.close();
	}



	public static void upCodeWordSize() {
		W++;  
		L *= 2;
	}

	public static TST<Integer> initCompressBook() {
		TST<Integer> st = new TST<Integer>();
		W = 9;
		L = 512;
		Code = R + 1;
		for (int i = 0; i < R; i++)
			st.put("" + (char) i, i);
		return st;
	}

	public static String[] initExpandBook() {
		String [] st = new String[65536];
		W = 9;
		L = 512;
		for (I = 0; I < R; I++)			// initialize symbol table with all 1-character strings
			st[I] = "" + (char) I;
		st[I++] = "";					// (unused) lookahead for EOF

		return st;
	}

	public static boolean monitor() {
		if (Ratio == 0.0) Ratio = RawData / ModData;
		else {
			double current = RawData / ModData;
			if (Ratio / current > 1.1) {
				Ratio = 0.0;
				return true;
			}
		}
		return false;
	}


	public static void main(String[] args) {
		if (args[0].equals("-")) {
			M = args[1];
			compress();
		}
		else if (args[0].equals("+")) expand();
		else throw new IllegalArgumentException("Illegal command line argument");
	}
}
