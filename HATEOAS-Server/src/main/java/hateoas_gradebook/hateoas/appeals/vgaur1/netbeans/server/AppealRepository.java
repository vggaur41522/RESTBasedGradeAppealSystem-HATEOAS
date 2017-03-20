/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server;

/**
 *
 * @author Varun_Gaur
 */
import java.util.HashMap;
import java.util.Map.Entry;

import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.StudentIDGenerator;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.Appeal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppealRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(AppealRepository.class);

    private static final AppealRepository theRepository = new AppealRepository();
    private HashMap<String, Appeal> backingStore = new HashMap<>(); // Default implementation, not suitable for production!

    public static AppealRepository current() {
        return theRepository;
    }
    
    private AppealRepository(){
        LOG.debug("AppealRepository Constructor");
    }
    
    public Appeal get(StudentIDGenerator identifier) {
        LOG.debug("Retrieving Appeal object for identifier {}", identifier);
        return backingStore.get(identifier.toString());
     }
    
    public Appeal take(StudentIDGenerator identifier) {
        LOG.debug("Removing the Appeal object for identifier {}", identifier);
        Appeal appeal = backingStore.get(identifier.toString());
        remove(identifier);
        return appeal;
    }

    public StudentIDGenerator store(Appeal appeal) {
        LOG.debug("Storing a new Appeal object");
                
        StudentIDGenerator id = new StudentIDGenerator();
        LOG.debug("New Appeal object id is {}", id);
                
        backingStore.put(id.toString(), appeal);
        return id;
    }
    
    public void store(StudentIDGenerator identifier, Appeal appeal) {
        LOG.debug("Storing again the Appeal object with id", identifier);
        backingStore.put(identifier.toString(), appeal);
    }

    public boolean has(StudentIDGenerator identifier) {
        LOG.debug("Checking to see if there is an Appeal object associated with the id {} in the Appeal store", identifier);
        
        boolean result =  backingStore.containsKey(identifier.toString());
        LOG.debug("The result of the search is {}", result);
        
        return result;
    }

    public void remove(StudentIDGenerator identifier) {
        LOG.debug("Removing from storage the Appeal object with id", identifier);
        backingStore.remove(identifier.toString());
    }
    
    public boolean AppealPlaced(StudentIDGenerator identifier) {
        LOG.debug("Checking to see if the Appeal with id = {} has been place", identifier);
        return AppealRepository.current().has(identifier);
    }
    
    public boolean AppealNotPlaced(StudentIDGenerator identifier) {
        LOG.debug("Checking to see if the Appeal with id = {} has not been place", identifier);
        return !AppealPlaced(identifier);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Entry<String, Appeal> entry : backingStore.entrySet()) {
            sb.append(entry.getKey());
            sb.append("\t:\t");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }

    public synchronized void clear() {
        backingStore = new HashMap<>();
    }

    public int size() {
        return backingStore.size();
    }
}
