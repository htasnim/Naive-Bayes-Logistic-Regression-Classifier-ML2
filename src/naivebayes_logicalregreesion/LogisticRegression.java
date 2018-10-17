/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes_logicalregreesion;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Ashraf
 */
public class LogisticRegression {

    static int[][] X = new int[Defs.NO_OF_EXAMPLES_TRAINING][Defs.NO_OF_ATTRIBUTES + 1];
//    static int[][] testInput = new int[Defs.NO_OF_EXAMPLES_TESTING][Defs.NO_OF_ATTRIBUTES];
    static int[] trainingClasses = new int[Defs.NO_OF_EXAMPLES_TRAINING];
//    static int[][] delta = new int[Defs.NO_OF_CLASSES][Defs.NO_OF_EXAMPLES_TRAINING];
    static double[][] W = new double[Defs.NO_OF_CLASSES][Defs.NO_OF_ATTRIBUTES + 1];

    public static void main(String[] args) {
        try {
            loadTrainingData();
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);            
        }

//        calculateDelta();
        initW();
//        double[][] P = calculateP();

        System.out.println("Trace 0");
//        double[][] delMinusP = substruct(delta, P);
        System.out.println("Trace 1");

//        double[][] W1 = addition(W, multiply(substruct(multiply(delMinusP, X), multiply(W, Defs.LAMBDA)), Defs.ETA));
        System.out.println("Done!");
//        System.out.println(W1);
    }

    static double[][] substruct(double[][] A, double[][] B) {
        double[][] C = new double[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    static double[][] substruct(int[][] A, double[][] B) {
        double[][] C = new double[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                C[i][j] = (double) A[i][j] - B[i][j];
            }
        }
        return C;
    }

    static double[][] addition(double[][] A, double[][] B) {
        double[][] C = new double[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

//    static void calculateDelta() {
//        for (int i = 0; i < Defs.NO_OF_EXAMPLES_TESTING; i++) {
//            delta[trainingClasses[i] - 1][i] = 1;
//        }
//    }

    static void loadTrainingData() throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader("Data/training.csv");

        // create csvReader object passing 
        // file reader as a parameter 
        CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(0).build();
        String[] lineEntry;

        // we are going to read data line by line
        for (int i = 0; i < Defs.NO_OF_EXAMPLES_TRAINING; i++) {
            X[i][0] = 1;
        }
        int c = 0;
        while ((lineEntry = csvReader.readNext()) != null) {

            for (int i = 1; i < 61189; i++) {
                X[c][i] = Integer.parseInt(lineEntry[i]);
            }

            trainingClasses[c] = Integer.parseInt(lineEntry[61189]);
            c++;
        }
    }

    static void initW() {
        for (int i = 0; i < Defs.NO_OF_CLASSES; i++) {
            for (int j = 0; j < Defs.NO_OF_ATTRIBUTES + 1; j++) {
                W[i][j] = 1e-6;
            }
        }
    }

    static double[][] transposeMatrix(int[][] m) {
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                temp[j][i] = (double) m[i][j];
            }
        }
        return temp;
    }

//    static double[][] calculateP() {
//        double[][] X_trans = transposeMatrix(X);
//        double[][] P = multiply(W, X_trans);
//        P = expMattrix(P);
//        for (int i = 0; i < P[0].length; i++) {
//            double colSum = 0.0;
//
//            for (int j = 0; j < P.length; j++) {
//                colSum += P[j][i];
//            }
//
//            for (int j = 0; j < P.length; j++) {
//                P[j][i] = P[j][i] / colSum;
//            }
//        }
//        return P;
//    }

    static double[][] expMattrix(double[][] P) {

        for (int i = 0; i < P.length; i++) {
            for (int j = 0; j < P[0].length; j++) {
                P[i][j] = Math.exp(P[i][j]);
            }
        }
        return P;
    }

    public static double[][] multiply(double A[][], double B[][]) {
        int rowA = A.length;
        int colA = A[0].length;

        int rowB = B.length;
        int colB = B[0].length;

        double[][] C = new double[rowA][colB];

        int i, j, k;
        for (i = 0; i < C.length; i++) {
            for (j = 0; j < C[0].length; j++) {
                C[i][j] = 0.0;
            }
        }
        for (i = 0; i < rowA; i++) {
            for (j = 0; j < colB; j++) {
                for (k = 0; k < rowB; k++) {
                    C[i][j] += (A[i][k] * B[k][j]);
                }
            }
        }
        return C;
    }

    public static double[][] multiply(double A[][], int B[][]) {
        int rowA = A.length;
        int colA = A[0].length;

        int rowB = B.length;
        int colB = B[0].length;

        double[][] C = new double[rowA][colB];

        int i, j, k;
        for (i = 0; i < C.length; i++) {
            for (j = 0; j < C[0].length; j++) {
                C[i][j] = 0.0;
            }
        }
        for (i = 0; i < rowA; i++) {
            for (j = 0; j < colB; j++) {
                for (k = 0; k < rowB; k++) {
                    C[i][j] += (A[i][k] * (double) B[k][j]);
                }
            }
        }
        return C;
    }

    public static double[][] multiply(double A[][], double scaler) {
        int rowA = A.length;
        int colA = A[0].length;

        double[][] C = new double[rowA][colA];

        for (int i = 0; i < rowA; i++) {
            for (int j = 0; j < colA; j++) {
                C[i][j] = A[i][j] * scaler;
            }
        }
        return C;
    }
}
