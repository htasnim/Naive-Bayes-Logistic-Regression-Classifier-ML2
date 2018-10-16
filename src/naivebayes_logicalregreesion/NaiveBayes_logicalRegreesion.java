/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes_logicalregreesion;

/**
 *
 * @author htasn
 * 
 * 
 */

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

public class NaiveBayes_logicalRegreesion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        
        int [] classes= new int[12000];
        int [] freqLabels_y= new int[20];
        double [] probY= new double[20];
        int [] totalWords_y= new int[20];
        int [][] couontOfXinY= new int[61188][20];
        double [][] probOfXgivenY= new double[61189][20];
        int [][] trainInput= new int[12000][61188];
        int [][] testInput=new int[6774][61189];
         
        FileReader fileReader = new FileReader("Data/training.csv");

        // create csvReader object passing 
        // file reader as a parameter 
        CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(0).build();
        String[] lineEntry;

        // we are going to read data line by line
        int c=0;
        while ((lineEntry = csvReader.readNext()) != null) {
        
            for(int i=1;i<61189;i++){
                trainInput[c][i-1]=Integer.parseInt(lineEntry[i]);
            }
            
            classes[c]=Integer.parseInt(lineEntry[61189]);
            c++;
        }
        
        
        
        System.out.println(trainInput[6413][61187]);
      
        java.util.Arrays.fill(freqLabels_y,0);
        System.out.println();
        for(int i=0;i<12000;i++){
            
            freqLabels_y[classes[i]-1]++;
        }
        
      
        for(int i=0;i<20;i++){
           probY[i]=Math.log((double)freqLabels_y[i]/(double)12000)/Math.log(2.0);
           
            System.out.println(probY[i]+" ");
        }
        System.out.println();
        
        for(int i=1;i<=20;i++){
            
            int totalWordSum=0;
            
            for(int j=0;j<12000;j++){
                
                if (classes[j]==i){
                    
                    for(int k=0;k<61188;k++){
                        totalWordSum=totalWordSum+trainInput[j][k];
                    }
                }
            }
            
            totalWords_y[i-1]=totalWordSum;
            //System.out.print(totalWords_y[i-1]+" ");
        }
        
        System.out.println();
        
          
        for(int i=0;i<12000;i++){
            
            for(int j=0;j<61188;j++){
                couontOfXinY[j][classes[i]-1]=couontOfXinY[j][classes[i]-1]+trainInput[i][j];
            }
        }
       // int sum=0;
        //for(int i=0;i<61188;i++){
            
          //  for(int j=0;j<20;j++){
            //    sum=sum+couontOfXinY[i][j];
            //}
        //}
           
        //System.out.println(sum);
        int v= 61188;
        //double beta= 1/(double)v;
        double beta= 0.015;
        
        for(int i=0;i<20;i++){
            probOfXgivenY[0][i]= probY[i];
        }
            
        
        for(int i=0;i<61188;i++){
            for(int j=0;j<20;j++){
                
                probOfXgivenY[i+1][j]= Math.log(((double)couontOfXinY[i][j]+beta)/((double)totalWords_y[j]+(beta * (double)v)))/Math.log(2.0);
            }
        }
        
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
         
                System.out.print(probOfXgivenY[i][j]);
            }
            System.out.println();
        }
        //int [] numXNew= new int[61188];
        
        
        for(int i=0;i<6774;i++)
            testInput[i][0]=1;
        
        FileReader fileReader2 = new FileReader("Data/testing.csv");

        // create csvReader object passing 
        // file reader as a parameter 
        CSVReader csvReader2 = new CSVReaderBuilder(fileReader2).withSkipLines(0).build();
        String[] lineEntry2;

        
        
        // we are going to read data line by line
        c=0;
        while ((lineEntry2 = csvReader2.readNext()) != null) {
        
            for(int i=1;i<61189;i++){
                testInput[c][i]=Integer.parseInt(lineEntry2[i]);
            }
            
        c++;  
        
        }
        
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
         
                System.out.print(testInput[i][j]+" ");
            }
            System.out.println();
        }
        
        int firstRow=6774,firstCol=61189,secondRow=61189,secondCol=20;
        double sum=0.0;
        double [][]multiply= new double[firstRow][secondCol];
        
        for(int i=0; i<firstRow; i++)
         {
            for(int j=0; j<secondCol; j++)
            {   
               for(int k=0; k<secondRow; k++)
               {
                  sum = sum + ((double)testInput[i][k]*probOfXgivenY[k][j]);
               }
 
               multiply[i][j] = sum;
               sum = 0.0;
            }
         }
        
        
        
        int [] yNewList= new int[firstRow];
        for(int i=0;i<multiply.length;i++){
            int maxAt = 0;
            for (int j = 0; j < multiply[i].length; j++) {
                maxAt = multiply[i][j] > multiply[i][maxAt] ? j : maxAt;
            }
            
            yNewList[i]= maxAt+1;
        }
        
        int [] id = new int[firstRow];
        int id1=12001;
        for(int i=0;i<firstRow;i++){
        
            id[i]= id1++;
        }
        
        try (FileOutputStream fos = new FileOutputStream("resultnb_015.csv");
                OutputStreamWriter osw = new OutputStreamWriter(fos,
                        StandardCharsets.UTF_8);
                CSVWriter writer = new CSVWriter(osw)) {

            List<String[]> entries = new ArrayList<String[]>();
            String[] header = {"ID", "Class"};
            entries.add(header);

            for (int i=0;i<firstRow;i++) {
                String[] entryArr = new String[2];
                entryArr[0] = Integer.toString(id[i]);
                entryArr[1] = Integer.toString(yNewList[i]);
                entries.add(entryArr);
            }

            writer.writeAll(entries);
        }
        
        
        
    }
    
}
