/******************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   https://algs4.cs.princeton.edu/55compression/abraLZW.txt
 *                https://algs4.cs.princeton.edu/55compression/ababLZW.txt
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 ******************************************************************************/

//package edu.princeton.cs.algs4;

/**
 *  The {@code LZW} class provides static methods for compressing
 *  and expanding a binary input using LZW compression over the 8-bit extended
 *  ASCII alphabet with 12-bit codewords.
 *  <p>
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/55compression">Section 5.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width = 12 bits // modified to be 9 bits for project
    private static char Mode = 'n';
    private static final double COMP_RTO = 1.1;

    // Do not instantiate.
    private MyLZW() { }

    /**
     * Reads a sequence of 8-bit bytes from standard input; compresses
     * them using LZW compression with 12-bit codewords; and writes the results
     * to standard output.
     */
    public static void compress() {
        int uncompSize = 0;
        int compSize = 0;
        double oldRatio = 0.0;
        double newRatio = 0.0;

        String input = BinaryStdIn.readString(); // i think this is the whole file represented as a large string
        TST<Integer> st = new TST<Integer>(); //this is the codebook
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i); // filling the symbol table with ASCII 256 characters. (pattern, code)
        int code = R+1;  // R is codeword for EOF

        //what mode are we in?
        BinaryStdOut.write((byte) MyLZW.Mode);

        while (input.length() > 0) {
            oldRatio = newRatio; // used to monitor compression ratios
            String s = st.longestPrefixOf(input);  // Find max prefix match s. searching the codebook for pattern.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding. get the code and write it to the file with W bits.
            int t = s.length(); // t = how long was the pattern.

            uncompSize += t*16; //data in is length of the string * size of char

            //if the codebook is not full:
            if (t < input.length() && code < L){    // Add s(the pattern) to symbol table if the length of the pattern is smaller than the length of the input.
                st.put(input.substring(0, t + 1), code++);
                compSize += W; // size of data written
            }

            // codebook is full but we can still expand:
            else if(t< input.length() && code >= L && W != 16){
              MyLZW.W = MyLZW.W + 1; // increase our bitsize and thus...
              MyLZW.L = (1 << MyLZW.W); // increase the number of codes we can store
              st.put(input.substring(0, t + 1), code++); // add the codeword to the new expanded codebook
              compSize += W; // size of data written
            }

            // codebook is full and we cannot expand
            else if(Mode == 'r' && t < input.length() && code == L && W == 16){
              st = resetCompression(st);
              code = R+1;
              st.put(input.substring(0, t + 1), code++); //add the codeword to the new fresh codebook
              compSize += W; // size of data written
              }

            //if the compression ratios are above the limit then we need to reset the codebook
            newRatio = (double) uncompSize/compSize;
            if(Mode == 'm' && oldRatio/newRatio > COMP_RTO){
              st = resetCompression(st);
              code = R+1;
            }

            input = input.substring(t);// Scan past s in input. the new input is the original input minus what we just coded.
        }
        BinaryStdOut.write(R, W); //we have compressed the whole file and now need to write end of file with W bits.
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bit encoded using LZW compression with
     * 12-bit codewords from standard input; expands them; and writes
     * the results to standard output.
     */
    public static void expand() {
        int uncompSize = 0;
        int compSize = 0;
        double oldRatio = 0.0;
        double newRatio = 0.0;

        String[] st = new String[65536];
        int i; // next available codeword value <- i is where we would insert our newly discovered code

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i; // fill the array with the ASCII 256 characters.
        st[i++] = "";                        // (unused) lookahead for EOF

        MyLZW.Mode = BinaryStdIn.readChar();
        int codeword = BinaryStdIn.readInt(W); // this is going to start off at 9 for the project but will increase when necessary all the way up to 16.
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            oldRatio = newRatio;
            BinaryStdOut.write(val);
            uncompSize += val.length()*16;

            //if the codebook is full but we can still expand:
            if(i >= L && W != 16){
              MyLZW.W = MyLZW.W + 1; // increase our bitsize and thus...
              MyLZW.L = (1 << MyLZW.W); // increase the number of codes we can store
            }

            // the codebook is full but we cant expand
            else if(Mode == 'r' && i >= L && W == 16){
              System.err.println("We Reset the Book");
              st = resetDecompression(st);
              i = R+1;
            }

            // codebook is not full
            codeword = BinaryStdIn.readInt(W); //get the next codeword in the file.
            if (codeword == R) break; //if EOF break.
            compSize += W;

            String s = st[codeword]; //get the pattern associated with the code.
            if (i == codeword) s = val + val.charAt(0);   // special case hack, this is the case where the code is not one that we have created yet.
            if (i < L) st[i++] = val + s.charAt(0); //add the new pattern (created from the previous pattern and the first character of the one we just read in)

            val = s; //old pattern = the one we just discovered

            newRatio = (double) uncompSize/compSize;
            if(Mode == 'm' && oldRatio/newRatio > COMP_RTO){
              st = resetDecompression(st);
              i = R+1;
            }

        }
        System.err.println("Ending Value of i: "+i);
        BinaryStdOut.close();
    }

    public static String[] resetDecompression(String[] st){
      int i;
      st = new String[65536];
      for (i = 0; i < R; i++)
          st[i] = "" + (char) i; // fill the array with the ASCII 256 characters.
      st[i++] = "";                        // (unused) lookahead for EOF
      MyLZW.W = 9;
      MyLZW.L = 512;
      return st;
    }

    public static TST<Integer> resetCompression(TST<Integer> st){
      int i;
      st = new TST<Integer>(); // throw out the codebook
      for (i = 0; i < R; i++) // reconstruct the begining of the codebook
          st.put("" + (char) i, i); // filling the symbol table with ASCII 256 characters. (pattern, code)
      MyLZW.W = 9;
      MyLZW.L = 512;
      return st;
    }

    /**
     * Sample client that calls {@code compress()} if the command-line
     * argument is "-" an {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")){
          if (args[1].equals("r"))
            MyLZW.Mode = 'r';
          if (args[1].equals("m"))
            MyLZW.Mode = 'm';
          compress();
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}

/******************************************************************************
 *  Copyright 2002-2020, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
