package csvscript;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

    public class CsvScript {
        private static StringBuffer fileOne = new StringBuffer("src/test/resources/testdata/customers-100.csv");
        private static StringBuffer fileTwo = new StringBuffer("src/test/resources/testdata/test2.csv");
        private static String DIFFERENCES_FILE = "src/test/resources/output/differences.txt";
        
        public static void main(String[] args) throws IOException {
        	if(args.length == 4) {
                if (!(processArgs(args))) {
                    outputHelpMessage();                   
                }
                else {
                		compareCsvFiles(fileOne, fileTwo);                		               	                   
                }
            }
            else if (args.length == 0) { 
            	compareCsvFiles(fileOne, fileTwo);
            }
            else {
            	outputHelpMessage();
            }

        }

        private static void compareCsvFiles(StringBuffer file12, StringBuffer file22) {
        	try {
        		Map<Integer, String[]> file1Data = readCSV(fileOne.toString());
                Map<Integer, String[]> file2Data = readCSV(fileTwo.toString());
                compareFiles(file1Data, file2Data);
            } catch (IOException e) {
                e.printStackTrace();
            }
			
		}
        
        private static Map<Integer, String[]> readCSV(String filePath) throws IOException {
            Map<Integer, String[]> records = new HashMap<>();
            String [] nextLine;
            int j = 0;
            try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            	while ((nextLine = reader.readNext()) != null) {
            		records.put(j++, nextLine);
                 }
            } catch (CsvValidationException e) {
            	e.printStackTrace();
            }
			
            return records;
        }

        private static void compareFiles(Map<Integer, String[]> file1, Map<Integer, String[]> file2) {
        	boolean isDifferent = (file1.size() != file2.size());
        	List<String> differences = new ArrayList<String>();
        	differences.add("FILE 1 is \"" + fileOne.toString() + "\", FILE 2 is \"" + fileTwo.toString() + "\"\n");
        	
            for (Integer key : file1.keySet()) {
            	String[] row1 = file1.get(key);
                String[] row2 = file2.get(key);
                if (row2 == null) {
                	isDifferent = true;
                	differences.add("Record \"" + key + "\" exists in FILE 1 but not in FILE 2.");
                   
                } else if (!Arrays.equals(row1, row2)) {
                	isDifferent = true;
                	differences.add("Difference found for record \"" + key + "\": FILE 1 columns " + Arrays.toString(row1) + ", FILE 2 columns " + Arrays.toString(row2));
                    
                }
            }
            for (Integer key : file2.keySet()) {
                if (!file1.containsKey(key)) {
                	isDifferent = true;
                	differences.add("Record \"" + key + "\" exists in FILE 2 but not in FILE 1.");
                    
                }
            }
            if (!isDifferent) {
            	differences.add("The two CSV files are the same!\n");
            	
            }
            writeToFile(differences);
        }
        
		private static boolean processArgs(String[] args) {
            boolean err = false;
            
            for (int i = 0; i < args.length; i++) {
            	switch (args[i]) {
                    case "-f1":
                    	fileOne.setLength(0);
                    	fileOne.append(args[++i]);
                    	break;
                    case "-f2":
                    	fileTwo.setLength(0);
                    	fileTwo.append(args[++i]);
                    	break;
                    default:
                        err = true;

                }
                if (err) {
                    break;
                }

            }           
            return ((err) ? false : true);
        }
        
        public static boolean writeToFile(List<String> output ) {
        	boolean success = false;
        	try {
        		BufferedWriter writer = new BufferedWriter(new FileWriter(DIFFERENCES_FILE));
        			for (String outputLine : output) {
        				writer.write(outputLine + "\n");
        			}
        			writer.close();
        			success = true;
                    
                } catch (IOException e) {
                    System.err.println(e);
                }
        
        	System.out.printf("Test results are contained in the differences.txt file located at %s\n", DIFFERENCES_FILE);
        	return success;
        }

        private static void outputHelpMessage() {
            System.out.println("Options:");
            System.out.println("no arguments passed [default csv files located in src/test/resources/testdata/ will be used]");
            System.out.println("Or (both of the following)");
            System.out.println("-f1 [first csv file]");
            System.out.println("-f2 [second csv file]");
            
        }


    }
