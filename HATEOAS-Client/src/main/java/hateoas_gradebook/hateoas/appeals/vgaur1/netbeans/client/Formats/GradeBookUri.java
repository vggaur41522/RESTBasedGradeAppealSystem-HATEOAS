/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats;

/**
 *
 * @author Varun_Gaur
 */
import java.net.URI;
import java.net.URISyntaxException;

public class GradeBookUri {
    private URI uri;
    
    public GradeBookUri(String uri) {
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public GradeBookUri(URI uri) {
        this(uri.toString());
    }

    public GradeBookUri(URI uri, StudentIDGenerator studentidgenerator) {
        this(uri.toString() + "/" + studentidgenerator.toString());
    }

    public StudentIDGenerator getId() {
        String path = uri.getPath();
        return new StudentIDGenerator(path.substring(path.lastIndexOf("/") + 1, path.length()));
    }

    public URI getFullUri() {
        return uri;
    }
    
    @Override
    public String toString() {
        return uri.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GradeBookUri) {
            return ((GradeBookUri)obj).uri.equals(uri);
        }
        return false;
    }

    public String getBaseUri() {

        String uriString = uri.toString();
        String baseURI   = uriString.substring(0, uriString.lastIndexOf("gradeAppealResource/")+"gradeAppealResource".length());
        //baseURI   = uriString.substring(0, 67);
        
        return baseURI;
    }
}
