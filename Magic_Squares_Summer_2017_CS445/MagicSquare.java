package Magic_Squares_Summer_2017_CS445;

import java.util.Scanner;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;


public class MagicSquare {
    // This static value represents the width of the current square
    static int size = -1; 
    static Stack numbers = new Stack();
    static int magicNum = 0;

    public static boolean isFullSolution(int[][] square) {
        
        // Check all spaces in square have a value
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++){
                if(square[i][j] == 0) {
                    return false;
                }
            }
        }

        // The solution is known to be complete, check if it is valid
        if (reject(square)) {
            return false;
        }
        // The solution is complete and valid
        for(int i=0; i < size; i++) {
            for(int j=0; j < size; j++) {
                if (square[i][j] < 0) {
                    square[i][j] = square[i][j] * -1;
                }
            }
        }
        return true;
    }

    public static boolean reject(int[][] square) {
        int solve = 0;

        // tests each row to see if it equals magicNum
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++){
                if (square[i][j] < 0)
                    solve = solve + (square[i][j] * -1);
                else
                    solve = solve + square[i][j];

                if (square[i][j] == 0)
                    break;
                else if (j == size-1)
                    if (solve != magicNum)
                        return true;
            }
            solve = 0;
        }

        solve = 0;
        // test each col to see if it equals magicNum
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++){
                if (square[j][i] < 0)
                    solve = solve + (square[j][i] * -1);
                else
                    solve = solve + square[j][i];
                
                if (square[j][i] == 0)
                    break;
                else if (j == size-1)
                    if (solve != magicNum)
                        return true;
            }
            solve = 0;
        }

        return false;
    }

    public static int[][] extend(int[][] square) {
        int[][] tempSQ = new int[size][size];
        int numToUse = 0;
        boolean flag = false; 

        // fill tempSQ & find used numbers
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                if (square[i][j] != 0) {
                    if (square[i][j] > 0) {
                        tempSQ[i][j] = square[i][j];
                    }
                    else if (square[i][j] < 0){
                        tempSQ[i][j] = square[i][j] * -1;
                    }
                    
                }
                else {
                    tempSQ[i][j] = 0;
                }
            }
        }
        for(int i = size*size; i>0; i--) {
            if (!numbers.contains(i)) {
                numToUse = i;
                numbers.push(numToUse);
                break;
            }
        }

        // find a zero and replace it w/ a  int from numToUse
        cont:
            for(int i=0; i<size; i++) {
                for(int j=0; j<size; j++) {
                    if (tempSQ[i][j] == 0) {
                        if (checkProg(tempSQ, numToUse, i, j)) {
                            tempSQ[i][j] = numToUse * -1;
                            flag = true;
                            break cont;
                        }
                    }
                }
            }

        if (flag == true){
            return tempSQ; 
        }
        else {
            return null;
        }
    }

    public static int[][] next(int[][] square) {
        int[][] tempSQ = new int[size][size];
        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                tempSQ[i][j] = square[i][j];
            }
        }

        find:
            for(int i=0; i<size; i++) {
                for(int j=0; j<size; j++) {
                    if (tempSQ[i][j] < 0) {
                        for(int k = 0; k<size; k++) {
                            for(int h = 0; h<size; h++) {
                                if (tempSQ[k][h] == 0) {
                                    if ((k == i && h > j) || (k > i)) {
                                        if (checkProg(tempSQ, tempSQ[i][j]*-1, k, h)) {
                                            tempSQ[k][h] = tempSQ[i][j];
                                            tempSQ[i][j] = 0;
                                            return tempSQ;
                                        }
                                    }
                                }
                                else if (k == size-1 && h == size-1) {
                                    break find;
                                }
                            }
                        }
                    }
                }
            }

        return null;
    }

    static boolean checkProg(int[][] square, int num, int row, int col) {
        int total = num;
        int zCount = 0;

        for(int i=0; i<size; i++) {
            total = total + square[row][i];
            if (square[row][i] == 0){
                zCount++;
            }
            if (total > magicNum) {
                return false;
            }
            if (i == size-1 && ((zCount == 1 && total == magicNum) || (zCount > 1))) {
                return true;
            }
        }

        total = num;
        zCount = 0;
        for(int i=0; i<size; i++) {
            total = total + square[i][col];
            if (square[i][col] == 0){
                zCount++;
            }
            if (total > magicNum) {
                return false;
            }
            if (i == size-1 && ((zCount == 1 && total == magicNum) || (zCount > 1))) {
                return true;
            }
        }
        return false;
    }

    static void testIsFullSolution() {

        int[][][] fullSolutions = new int[][][] {   
            {{8,1,6},{3,5,7},{4,9,2}},
            {{5,1,9},{7,6,2},{3,8,4}},
        };
        int[][][] notFullSolutions = new int [][][] {
            {{0,0,0},{0,0,0},{0,0,0}},
            {{8,1,6},{3,5,7},{4,9,0}},
            {{0,1,6},{3,5,7},{4,9,0}},
            {{8,1,6},{3,0,7},{4,9,2}},
            {{8,8,1},{0,0,0},{2,6,7}},
            {{8,6,1},{1,5,7},{6,9,2}},
            {{8,1,6},{3,7,7},{4,9,2}}
        };

        System.out.println("These should be full:");
        for (int[][] test : fullSolutions) {
            if (isFullSolution(test)) {
                System.out.println("\tFull sol'n:\t");
                printSquare(test);
            } else {
                System.out.println("\tNot full sol'n:\t");
                printSquare(test);
            }
        }

        System.out.println("These should NOT be full:");
        for (int[][] test : notFullSolutions) {
            if (isFullSolution(test)) {
                System.out.println("\tFull sol'n:\t");
                printSquare(test);
            } else {
                System.out.println("\tNot full sol'n:\t");
                printSquare(test);
            }
        }
    }

    static void testReject() {

        int[][][] notRejected = new int[][][] {
            {{8,1,6},{3,5,7},{4,9,2}},
            {{5,1,9},{7,6,2},{3,8,4}},
            {{0,0,0,},{0,0,0},{0,0,0}},
            {{0,1,9},{7,6,2},{3,8,4}},
            {{5,1,9},{7,6,2},{3,8,0}},
            {{5,1,9},{7,0,2},{3,8,4}},
        };

        int[][][] rejected = new int[][][] {
            {{8,1,6},{3,5,7},{4,2,9}},
            {{5,6,9},{7,1,2},{3,8,4}},
            {{3,1,6},{8,5,7},{4,9,2}},
            {{5,8,9},{7,6,2},{3,1,4}},
        };

        System.out.println("These should NOT be rejected:");
        for (int[][] test : notRejected) {
            if (reject(test)) {
                System.out.println("\tRejected:\t");
                printSquare(test);
            } else {
                System.out.println("\tNot rejected:\t");
                printSquare(test);
            }
        }

        System.out.println("These should be rejected:");
        for (int[][] test : rejected) {
            if (reject(test)) {
                System.out.println("\tRejected:\t");
                printSquare(test);
            } else {
                System.out.println("\tNot rejected:\t");
                printSquare(test);
            }
        }
    }

    static void testExtend() {
        
        int[][][] noExtend = new int[][][] {
            {{8,1,6},{3,5,7},{4,9,2}},
            {{5,1,9},{7,6,2},{3,8,4}},
            {{8,1,6},{7,0,5},{3,8,4}},
            {{8,1,6},{7,8,5},{3,0,4}},
        };

        int[][][] extend = new int[][][] {
            {{0,0,0},{0,0,0},{0,0,0}},
            {{0,1,9},{7,6,2},{3,8,4}},
            {{8,0,6},{3,0,7},{4,9,2}},
            {{5,1,9},{7,6,2},{0,0,4}},
            {{8,1,0},{3,0,7},{0,9,2}},
            {{5,1,0},{7,-6,2},{3,8,0}},
            {{8,1,6},{3,5,7},{4,0,2}},
        };

        System.out.println("These can NOT be extended:");
        for (int[][] test : noExtend) {
            System.out.println("\tExtended ");
            printSquare(test);
            System.out.println(" to ");
            printSquare(extend(test));
        }

        System.out.println("These can be extended:");
        for (int[][] test : extend) {
            System.out.println("\tExtended ");
            printSquare(test);
            System.out.println(" to ");
            printSquare(extend(test));
        }
    }

    static void testNext() {

        int[][][] noNext = new int[][][] {
            {{8,1,6},{3,5,7},{4,9,2}},
            {{5,0,0},{0,0,2},{3,8,4}},
            {{8,1,6},{3,-5,7},{4,9,2}},
            {{0,0,0},{0,0,0},{0,0,-2}},
            {{-8,1,6},{3,5,0},{4,9,2}},
        };

        int[][][] next = new int[][][] {
            
            {{0,0,0},{-8,6,2},{3,0,4}},
            {{8,-1,6},{3,5,7},{0,0,0}},
            {{0,-1,0},{3,5,7},{0,0,0}},
            {{8,-1,6},{3,0,7},{4,9,2}},
        };

        System.out.println("These can NOT be next'd:");
        for (int[][] test : noNext) {
            System.out.println("\tNexted ");
            printSquare(test);
            System.out.println(" to ");
            printSquare(next(test));
        }

        System.out.println("These can be next'd:");
        for (int[][] test : next) {
            System.out.println("\tNexted ");
            printSquare(test);
            System.out.println(" to ");
            printSquare(next(test));
        }
    }

    static String pad(int num) {
        int sum = size * (size * size + 1) / 2;
        int width = Integer.toString(sum).length();
        String result = Integer.toString(num);
        while (result.length() < width) {
            result = " " + result;
        }
        return result;
    }

    public static void printSquare(int[][] square) {
        while (!numbers.empty()) {
            int pop = (Integer)numbers.pop();
        }
        if (square == null) {
            System.out.println("No solution");
            return;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(pad(square[i][j]) + " ");
                if (square[i][j] != 0) {
                    numbers.push(square[i][j]);
                }
            }
            
            System.out.print("\n");
        }
    }

    public static int[][] readSquare(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        int[][] square = new int[size][size];
        int val = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                square[i][j] = scanner.nextInt();
            }
        }
        return square;
    }

    public static int[][] solve(int[][] square) {
        int pop = 0;
        if (reject(square)) {
            return null;
        }
        if (isFullSolution(square)) {
            return square;
        }
        int[][] attempt = extend(square);
        while (attempt != null) {
            int[][] solution;
            solution = solve(attempt);
            if (solution != null) 
                return solution;
            attempt = next(attempt);
        }
        pop = (Integer)numbers.pop();
        return null;
    }

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equals("-t")) {
            System.out.println("Running tests...");
            size = 3;
            magicNum = size*(size*size + 1)/2;

            testIsFullSolution();
            testReject();
            testExtend();
            testNext();
        } else if (args.length >= 2) {
            try {
                // First get the specified size
                size = Integer.parseInt(args[0]);
                magicNum = size*(size*size + 1)/2;
                // Then read the square from the file
                int[][] square = readSquare(args[1]);

                System.out.println("Starting square:");
                printSquare(square);
                System.out.println("\nSolution:");
                printSquare(solve(square));
            } catch (NumberFormatException e) {
                // This happens if the first argument isn't an int
                System.err.println("First argument must be size");
            } catch (FileNotFoundException e) {
                // This happens if the second argument isn't an existing file
                System.err.println("File " + args[1] + " not found");
            }
        } else if (args.length >= 1) {
            try {
                // First get the specified size
                size = Integer.parseInt(args[0]);
                magicNum = size*(size*size + 1)/2;

                // Then initialize to a blank square
                int[][] square = new int[size][size];

                System.out.println("Starting square:");
                printSquare(square);
                System.out.println("\nSolution:");
                printSquare(solve(square));
            } catch (NumberFormatException e) {
                // This happens if the first argument isn't an int
                System.err.println("First argument must be size");
            }
        } else {
            System.err.println("See usage in assignment description");
        }
    }
}

