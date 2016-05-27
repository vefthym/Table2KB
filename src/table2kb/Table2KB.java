/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table2kb;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import tools.Parser;

/**
 *
 * @author vefthym
 */
public class Table2KB {
    
    private final String HTML_HEADER = "<!DOCTYPE html>\n<html>\n<body>\nThe table after linking:\n<table border=\"1\" style=\"width:100%\">\n";
    private final String HTML_FOOTER = "</table>\n\n</body>\n</html>\n";
    
    private final String csvInputPath;
    private final String outputPath;
    
    public Table2KB(String inputPath, String outputPath) {
        this.csvInputPath = inputPath;
        this.outputPath = outputPath;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        //choose input file
        String inputPath = "webtable.csv"; //the default input file
        String outputPath = "output.html";
        
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        JPanel panel = new JPanel();
        int returnVal = chooser.showOpenDialog(panel);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            inputPath = chooser.getSelectedFile().getName();
            System.out.println("The input file is: " + inputPath);
        }
        
        Table2KB t2kb = new Table2KB(inputPath, outputPath);
        
        //Alternative 1: Store output to an html table (while reading the input)
        try {
            t2kb.csvToResolvedHTML(inputPath, outputPath);                     
            openWebpage(new File(outputPath).toURI());            
            System.out.println("Created file: "+outputPath);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Table2KB.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Alternative 2: Store input as a WebTable and check the cells of the table for mappings to dbpedia
        /*
        //parse input webtable
        WebTable tab = new WebTable(inputPath);
        System.out.println(tab.toString()); //prints the input table
        String cell;
        Table<Integer,Integer,String> table = tab.getContents();
        for (int i = 0; i < table.rowKeySet().size(); ++i) { // for each row
        for (int j = 0; j < table.row(i).size(); ++j) { // for each cell in this row
        cell = table.get(i,j).replaceAll("^\"|\"$", "");
        if (!Parser.isNumeric(cell)) { //do the same checks for dates and geo-coordinates
        DBpediaLookup lookup = new DBpediaLookup(cell);
        System.out.println(lookup.getResult());
        }
        }
        }
         */
        //Alternative 2: Store input as a WebTable and check the cells of the table for mappings to dbpedia
        /*
        //parse input webtable
        WebTable tab = new WebTable(inputPath);
        System.out.println(tab.toString()); //prints the input table
        String cell;
        Table<Integer,Integer,String> table = tab.getContents();
        for (int i = 0; i < table.rowKeySet().size(); ++i) { // for each row
        for (int j = 0; j < table.row(i).size(); ++j) { // for each cell in this row
        cell = table.get(i,j).replaceAll("^\"|\"$", "");
        if (!Parser.isNumeric(cell)) { //do the same checks for dates and geo-coordinates
        DBpediaLookup lookup = new DBpediaLookup(cell);
        System.out.println(lookup.getResult());
        }
        }
        }
         */
        
        
        //Alternative 2: Store input as a WebTable and check the cells of the table for mappings to dbpedia
        /*
        //parse input webtable
        WebTable tab = new WebTable(inputPath);
        System.out.println(tab.toString()); //prints the input table
        String cell;
        Table<Integer,Integer,String> table = tab.getContents();
        for (int i = 0; i < table.rowKeySet().size(); ++i) { // for each row
        for (int j = 0; j < table.row(i).size(); ++j) { // for each cell in this row
        cell = table.get(i,j).replaceAll("^\"|\"$", "");
        if (!Parser.isNumeric(cell)) { //do the same checks for dates and geo-coordinates
        DBpediaLookup lookup = new DBpediaLookup(cell);
        System.out.println(lookup.getResult());
        }
        }
        }
         */
                
        
        
        //Alternative 2: Store input as a WebTable and check the cells of the table for mappings to dbpedia
        /*
        //parse input webtable
        WebTable tab = new WebTable(inputPath);
        System.out.println(tab.toString()); //prints the input table
        String cell;
        Table<Integer,Integer,String> table = tab.getContents();        
        for (int i = 0; i < table.rowKeySet().size(); ++i) { // for each row
            for (int j = 0; j < table.row(i).size(); ++j) { // for each cell in this row
                cell = table.get(i,j).replaceAll("^\"|\"$", "");
                if (!Parser.isNumeric(cell)) { //do the same checks for dates and geo-coordinates
                    DBpediaLookup lookup = new DBpediaLookup(cell);
                    System.out.println(lookup.getResult());
                }            
            }
        }
        */
        
    }
    
    /**
     * Reads a csv file, corresponding to a web table, and calls the DBpediaLookup service for each cell of the table. 
     * It creates a new HTML page in the specified output file, in which a new webtable is constructed. 
     * The new table contains a hyperlink from each cell of the input table to its corresponding DBpedia URI. 
     * @param csvInputPath the input table in csv format
     * @param htmlOutputPath the path of the output HTML file
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException 
     * @throws java.net.URISyntaxException 
     */
    public void csvToResolvedHTML(String csvInputPath, String htmlOutputPath) throws UnsupportedEncodingException, FileNotFoundException, IOException, URISyntaxException {               
        String line;
        
        Map<String,URI> cache = new HashMap<>(); //to store the results of repeated queries
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvInputPath));
                    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlOutputPath), "utf-8"))) {
            writer.write(HTML_HEADER);      
            while ((line = br.readLine()) != null) {                
                writer.write("<tr>\n"); //new row
                String[] cells = line.split(",");                    
                for (String cell : cells) {
                    writer.write("\t<td>"); //new column
                    //lookup the table cell                    
                    cell = cell.replaceAll("^\"|\"$", "");
                    if (cache.containsKey(cell)) { //already cached
                        URI result = cache.get(cell);
                        //System.out.println(cell+" is cached!");
                        if (result == null) { //..but with no matched URI
                            writer.write(cell);
                        } else {    //already cached with a matched URI
                            writer.write("<a href=\""+result+"\">"+cell+"</a>");
                        }
                    } else {    //not cached yet => seen for the first time
                        //System.out.println(cell+" is NOT cached!");
                        if (cell.length() < 3 || Parser.isNumeric(cell)) { //add checks for dates and geo-coordinates
                            writer.write(cell);                            
                        } else {
                            DBpediaLookup lookup = new DBpediaLookup(cell); //expensive call! => minimize
                            URI result = lookup.getResult();
                            cache.put(cell, result);
                            //System.out.println("Now caching "+cell+" to "+cache.get(cell));
                            if (result != null) {
                                writer.write("<a href=\""+result+"\">"+cell+"</a>");
                            } else { //no result found
                                writer.write(cell);
                            }       
                        }
                    }
                    writer.write("</td>\n"); //end of column
                }
                writer.write("</tr>\n"); //end of row
            }              

            writer.write(HTML_FOOTER);                    
            
        }
    }
    
    /**
     * from http://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
     * @param uri 
     */
    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
        
}
