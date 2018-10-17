/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes_logicalregreesion;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ashraf
 */
public class LogisticRegression {

    static double[][] X = new double[Defs.NO_OF_EXAMPLES_TRAINING][Defs.NO_OF_ATTRIBUTES + 1];
    static double[][] X_new = new double[1][1];
    static int[][] testInput = new int[1][1];
    static int[] trainingClasses = new int[Defs.NO_OF_EXAMPLES_TRAINING];
    static double[][] delta = new double[Defs.NO_OF_CLASSES][Defs.NO_OF_EXAMPLES_TRAINING];
    static double[][] W = new double[Defs.NO_OF_CLASSES][Defs.NO_OF_ATTRIBUTES + 1];
    static double[][] W1 = new double[1][1];

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            loadTrainingData();
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);            
        }

        calculateDelta();
        initW();
        double[][] P = calculateP_training();

        System.out.println("Trace 0");
        double[][] delMinusP = substruct(delta, P);
        System.out.println("Trace 1");

        for (int i = 0; i < Defs.NO_OF_ITER; i++) {
            W1 = addition(W, multiply(substruct(multiply(delMinusP, X), multiply(W, Defs.LAMBDA)), Defs.ETA));
            W = W1;
        }
        // Free unnecessary memory
        W = null;
        delMinusP = null;
        X = null;
        P = null;

        //Allowcate new memory
        testInput = new int[Defs.NO_OF_EXAMPLES_TESTING][Defs.NO_OF_ATTRIBUTES];
        X_new = new double[Defs.NO_OF_EXAMPLES_TESTING][Defs.NO_OF_ATTRIBUTES + 1];

        System.out.println("Training Done!");
        System.out.println(W1);

        // Testing start
        P = calculateP_testing();

        X_new = null;
        testInput = null;

        int[] testClasses = new int[Defs.NO_OF_EXAMPLES_TESTING];
        for (int i = 0; i < Defs.NO_OF_EXAMPLES_TESTING; i++) {
            double currMax = Double.MIN_VALUE;
            int maxIndex = -1;
            for (int j = 0; j < Defs.NO_OF_CLASSES; j++) {
                if (P[j][i] > currMax) {
                    currMax = P[j][i];
                    maxIndex = j + 1;
                }
            }
            testClasses[i] = maxIndex;
        }
        try {
            writeToCsvFile(testClasses);
        } catch (IOException ex) {
//            Logger.getLogger(LogisticRegression.class.getName()).log(Level.SEVERE, null, ex);
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }

    static void writeToCsvFile(int[] labels) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("/home/ashraf/Desktop/ML_Project_2/Naive-Bayes-Logistic-Regression-Classifier-ML2/Data/result_reg.csv");
                OutputStreamWriter osw = new OutputStreamWriter(fos,
                        StandardCharsets.UTF_8);
                CSVWriter writer = new CSVWriter(osw)) {

            List<String[]> entries = new ArrayList<String[]>();
            String[] header = {"ID", "Class"};
            entries.add(header);

            for (int i = 0; i < Defs.NO_OF_EXAMPLES_TESTING; i++) {
                String[] entryArr = new String[2];
                entryArr[0] = Integer.toString(i + 12001);
                entryArr[1] = Integer.toString(labels[i]);
                entries.add(entryArr);
            }

            writer.writeAll(entries);
        }
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

    static void calculateDelta() {
        for (int i = 0; i < Defs.NO_OF_EXAMPLES_TRAINING; i++) {
            delta[trainingClasses[i] - 1][i] = 1.0;
        }
    }

    static void loadTestingData() throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader("Data/testing.csv");
        // int[][] tempX = new int[Defs.NO_OF_EXAMPLES_TRAINING][Defs.NO_OF_ATTRIBUTES + 1];

        // create csvReader object passing 
        // file reader as a parameter 
        CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(0).build();
        String[] lineEntry;

        // we are going to read data line by line
        for (int i = 0; i < Defs.NO_OF_EXAMPLES_TESTING; i++) {
            X_new[i][0] = 1.0;
        }
        int c = 0;
        while ((lineEntry = csvReader.readNext()) != null) {
            double wordCount = 0;
            for (int i = 1; i < Defs.NO_OF_ATTRIBUTES + 1; i++) {
                X_new[c][i] = Integer.parseInt(lineEntry[i]);
                wordCount += X[c][i];
            }

            for (int i = 1; i < Defs.NO_OF_ATTRIBUTES + 1; i++) {
                X_new[c][i] = X[c][i] / wordCount;
            }
            c++;
        }
    }

    static void loadTrainingData() throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader("Data/training.csv");
        // int[][] tempX = new int[Defs.NO_OF_EXAMPLES_TRAINING][Defs.NO_OF_ATTRIBUTES + 1];

        // create csvReader object passing 
        // file reader as a parameter 
        CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(0).build();
        String[] lineEntry;

        // we are going to read data line by line
        for (int i = 0; i < Defs.NO_OF_EXAMPLES_TRAINING; i++) {
            X[i][0] = 1.0;
        }
        int c = 0;
        int iter = 0;
        while ((lineEntry = csvReader.readNext()) != null) {
            iter++;
            double wordCount = 0;
            for (int i = 1; i < Defs.NO_OF_ATTRIBUTES + 1; i++) {
                X[c][i] = Integer.parseInt(lineEntry[i]);
                wordCount += X[c][i];
            }

            for (int i = 1; i < Defs.NO_OF_ATTRIBUTES + 1; i++) {
                X[c][i] = X[c][i] / wordCount;
            }

            trainingClasses[c] = Integer.parseInt(lineEntry[Defs.NO_OF_ATTRIBUTES + 1]);
            c++;
            if (iter == Defs.NO_OF_EXAMPLES_TRAINING) {
                break;
            }
        }
    }

    static void initW() {
        for (int i = 0; i < Defs.NO_OF_CLASSES; i++) {
            for (int j = 0; j < Defs.NO_OF_ATTRIBUTES + 1; j++) {
                W[i][j] = 1e-6;
            }
        }
    }

    static double[][] transposeMatrix(double[][] m) {
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                temp[j][i] = m[i][j];
            }
        }
        return temp;
    }

    static double[][] calculateP_training() {
        double[][] X_trans = transposeMatrix(X);
        double[][] P = multiply(W, X_trans);
        P = expMattrix(P);
        for (int i = 0; i < P[0].length; i++) {
            double colSum = 0.0;

            for (int j = 0; j < P.length; j++) {
                colSum += P[j][i];
            }

            for (int j = 0; j < P.length; j++) {
                P[j][i] = P[j][i] / colSum;
            }
        }
        return P;
    }

    static double[][] calculateP_testing() {
        double[][] X_trans = transposeMatrix(X_new);
        double[][] P = multiply(W1, X_trans);
        P = expMattrix(P);
        for (int i = 0; i < P[0].length; i++) {
            double colSum = 0.0;

            for (int j = 0; j < P.length; j++) {
                colSum += P[j][i];
            }

            for (int j = 0; j < P.length; j++) {
                P[j][i] = P[j][i] / colSum;
            }
        }
        return P;
    }

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
