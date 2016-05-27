/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package table2kb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vefthym
 */
public class DBpediaLookup {
    
    //TODO (maybe) add variable QueryClass in the PREFIX (based on column header)
    public final String PREFIX = "http://lookup.dbpedia.org/api/search/KeywordSearch?QueryClass=&MaxHits=1&QueryString=";    
    private final String keyword;
    private String uri;
    
    public DBpediaLookup(String keyword) {
        keyword = keyword.replaceAll(" +", "_"); //replace white spaces with a '_', to properly call the DBpedia lookup service
        //System.out.println("Keyword:"+keyword);
        this.keyword = keyword;
        uri = null;
    }
        
    /**
     * Return the first URI that the DBpedia lookup service returns for the given keyword query.
     * @return the URI result of the DBpedia lookup for the given query
     */
    public URI getResult() throws URISyntaxException {
        BufferedReader in = null;
        try {
            URL query = new URL(PREFIX+keyword);            
            in = new BufferedReader(new InputStreamReader(query.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                inputLine = inputLine.trim();                
                if (inputLine.startsWith("<URI>")) {
                    uri = inputLine.trim().substring(5,inputLine.lastIndexOf("<"));
                    break;
                }
            }                        
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(DBpediaLookup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return uri!= null ? new URI(uri): null;
    }
    
}
