/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table2kb;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import tools.Parser;

/**
 *
 * @author vefthym
 */
public class WebTable {    
    private Table<Integer,Integer,String> contents;
    
    public WebTable(String csvFile){
        contents = TreeBasedTable.create();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            int lineNo = 0;
            while ((line = br.readLine()) != null) {                
                String[] cells = line.split(",");
                for (int i =0; i < cells.length; ++i) {
                    contents.put(lineNo, i, cells[i]);
                }
                lineNo++;
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);            
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
    
    
    
    public Table getContents(){
        return contents;
    }
    
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < contents.rowKeySet().size(); ++i) {
            result.append(contents.row(i));
            result.append("\n");
        }
        return result.toString();
    }
    
    
    public void toHTML(String filePath) throws UnsupportedEncodingException, FileNotFoundException, IOException, URISyntaxException {        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"))) {
            writer.write("<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "<body>\n"
                    + "The table after linking:\n"
                    + "<table border=\"1\" style=\"width:100%\">\n");
                
            for (int i = 0; i < contents.rowKeySet().size(); ++i) { // for each row
                writer.write("<tr>\n");
                for (int j = 0; j < contents.row(i).size(); ++j) { // for each cell in this row
                    writer.write("\t<td>");
                    //lookup the table cell
                    String rawCell = contents.get(i,j);
                    rawCell = rawCell.replaceAll("^\"|\"$", "");
                    if (!Parser.isNumeric(rawCell)) { //do the same checks for dates and geo-coordinates
                        DBpediaLookup lookup = new DBpediaLookup(rawCell);
                        URI result = lookup.getResult();
                        if (result != null) {
                            writer.write("<a href=\""+result+"\">"+rawCell+"</a>");
                        } else { //no result found
                            writer.write(rawCell);
                        }                     
                    } else { //cell is numeric
                        writer.write(rawCell);
                    }                    
                    writer.write("</td>\n");
                }
                writer.write("</tr>\n");
            }
        
            writer.write("</table>\n\n");
        
            writer.write("</body>\n");
            writer.write("</html>\n");
        }
    }
}
