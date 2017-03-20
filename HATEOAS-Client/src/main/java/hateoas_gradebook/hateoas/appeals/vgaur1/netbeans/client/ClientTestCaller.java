/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client;

/**
 *
 * @author Varun_Gaur
 */

import java.net.URI;
import java.net.URISyntaxException;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.Appeal;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.AppealState;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.Link;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.AppealRepresentation;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.GradeBookUri;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.AppealBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTestCaller {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClientTestCaller.class);
    
    private static final String RESTBUCKS_MEDIA_TYPE = "application/vnd.gradebook+xml";
    private static final long ONE_MINUTE = 56000; 
    
    private static final String ENTRY_POINT_URI = "http://localhost:8080/HATEOAS-Appeals-vgaur1-NetBeans-Server/webapi/gradeAppealResource/";

    public static void main(String[] args) throws Exception {
        URI serviceUri = new URI(ENTRY_POINT_URI);
        happyPathTest1(serviceUri);
        happyPathTest2(serviceUri);
        abandonPathTest(serviceUri);
        forgottenPathTest1(serviceUri);
        forgottenPathTest2(serviceUri);
        badStartTestCase(serviceUri);
        badIdTestCase(serviceUri);
        /*This is an additional Test Case added to show that 
         * when a process initiated when it is not allowed on a particular state
         * For e.g. An Appeal is not allowed to Follow Up till the time it is submitted !!!!
        */
        badIdStateTestCase(serviceUri);
        
    }

    private static void hangAround(long backOffTimeInMillis) {
        try {
            Thread.sleep(backOffTimeInMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void happyPathTest1(URI serviceUri) throws Exception {
        LOG.info("************************* HAPPY TEST -1 [STATES 1->2->3->5->6->E]************************");
        System.out.println(String.format("\n\n************************* HAPPY TEST -1 [1->2->3->5->6->E]*********** via POST\n[%s]", serviceUri.toString()));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/       
        
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-3 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **UPDATE** Appeal for a Grade Item like Mid Term.  
*/      
        Appeal updateAppeal = apppealRepresentation.getAppeal();
        updateAppeal.setStudentAppeal("Please Update Our marks. Adding few more details to my appeal .. !!!!");
        addAppealUri  = apppealRepresentation.getUpdateLink().getUri();
        System.out.println("\n** APPEAL-STATE~3: UPDATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-5 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **SUBMIT** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal submitAppeal = apppealRepresentation.getAppeal();
        URI submitAppealUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        System.out.println("\n** APPEAL-STATE~5: SUBMITTING APPEAL @"+submitAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+submitAppeal.getStatus()+"] to --> [PEND_REVIEW]");
        apppealRepresentation = client.resource(submitAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(submitAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getFollowUpLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal submission: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-6 [Current Status: PEND_REVIEW (Appeal pending for review!!) ]: 
 * This REST call will **UPDATE GRADES** for a Grade Item like Mid Term against an Appeal
*/        
        Appeal updateGrade = apppealRepresentation.getAppeal();
        int prvGrades = Integer.parseInt(updateGrade.getGrades()) ;
        updateGrade.setGrades(Integer.toString(prvGrades+10));
        URI gradeChangeUri  = apppealRepresentation.getGradeChangeLink().getUri();
        System.out.println("\n** APPEAL-STATE~6: PROF UPDATING GRADES @"+gradeChangeUri+" using PUT");
        System.out.println("** From Current Status: ["+updateGrade.getStatus()+"] to --> [APPEAL ACKNOWLEDGED]");
        apppealRepresentation = client.resource(gradeChangeUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateGrade));
        System.out.println(String.format("** Links available: \n\t\t"
                       +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after Grade Update: ["+apppealRepresentation.getAppeal().getStatus()+"]");
 
    System.out.println("\n\n************************* HAPPY TEST -1 Completed Successfully ************************\n\n");

    }
    
    private static void happyPathTest2(URI serviceUri) throws Exception {
        LOG.info("************************* HAPPY TEST -1 [STATES 1->2->3->5->6->E]************************");
        System.out.println(String.format("************************* HAPPY TEST -2 [1->2->3->5->7->5->6->E]*********** via POST\n[%s]", serviceUri.toString()));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/       
        
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-3 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **UPDATE** Appeal for a Grade Item like Mid Term.  
*/      
        Appeal updateAppeal = apppealRepresentation.getAppeal();
        updateAppeal.setStudentAppeal("Please Update Our marks. Adding few more details to my appeal .. !!!!");
        addAppealUri  = apppealRepresentation.getUpdateLink().getUri();
        System.out.println("\n** APPEAL-STATE~3: UPDATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-5 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **SUBMIT** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal submitAppeal = apppealRepresentation.getAppeal();
        URI submitAppealUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        System.out.println("\n** APPEAL-STATE~5: SUBMITTING APPEAL @"+submitAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+submitAppeal.getStatus()+"] to --> [PEND_REVIEW]");
        apppealRepresentation = client.resource(submitAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(submitAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getFollowUpLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal submission: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-7 [Current Status: PEND_REVIEW (Appeal pending for review!!)  ]: 
 * This REST call will **COMMENT** on appeal for a Grade Item like Mid Term. [PROFESSOR] 
*/        
        Appeal commentAppeal = apppealRepresentation.getAppeal();
        URI commentAppealUri  = apppealRepresentation.getProfCommentLink().getUri();
        System.out.println("\n** APPEAL-STATE~7: COMMENT FROM PROFESSOR ON APPEAL @"+commentAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+commentAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(commentAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(commentAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal comment update: ["+apppealRepresentation.getAppeal().getStatus()+"]");
        
/* State-5 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **SUBMIT** Appeal for a Grade Item like Mid Term.  
*/        
        submitAppeal = apppealRepresentation.getAppeal();
        submitAppealUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        System.out.println("\n** APPEAL-STATE~5: SUBMITTING APPEAL @"+submitAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+submitAppeal.getStatus()+"] to --> [PEND_REVIEW]");
        apppealRepresentation = client.resource(submitAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(submitAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getFollowUpLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal submission: ["+apppealRepresentation.getAppeal().getStatus()+"]");

        
/* State-6 [Current Status: PEND_REVIEW (Appeal pending for review!!) ]: 
 * This REST call will **UPDATE GRADES** for a Grade Item like Mid Term against an Appeal
*/        
        Appeal updateGrade = apppealRepresentation.getAppeal();
        int prvGrades = Integer.parseInt(updateGrade.getGrades()) ;
        updateGrade.setGrades(Integer.toString(prvGrades+10));
        URI gradeChangeUri  = apppealRepresentation.getGradeChangeLink().getUri();
        System.out.println("\n** APPEAL-STATE~6: PROF UPDATING GRADES @"+gradeChangeUri+" using PUT");
        System.out.println("** From Current Status: ["+updateGrade.getStatus()+"] to --> [APPEAL ACKNOWLEDGED]");
        apppealRepresentation = client.resource(gradeChangeUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateGrade));
        System.out.println(String.format("** Links available: \n\t\t"
                       +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after Grade Update: ["+apppealRepresentation.getAppeal().getStatus()+"]");
 
    System.out.println("\n\n************************* HAPPY TEST -2 Completed Successfully ************************\n\n");

    }
    
    private static void abandonPathTest(URI serviceUri) throws Exception {
        LOG.info("Starting Abandoned  Test with Service URI {}", serviceUri);
        System.out.println(String.format("************************* ABANDONED TEST -1 [1->2->3->4->E]*********** via DELETE\n[%s] ", serviceUri.toString()));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        LOG.debug("Created base appeal {}", appeal);
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri.toString()+" using PUT");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [PEND_REVIEW]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");


/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/        
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");
        
/* State-3 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **UPDATE** Appeal for a Grade Item like Mid Term.  
*/      
        Appeal updateAppeal = apppealRepresentation.getAppeal();
        updateAppeal.setStudentAppeal("Please Update Our marks. Adding few more details to my appeal .. !!!!");
        addAppealUri  = apppealRepresentation.getUpdateLink().getUri();
        System.out.println("\n** APPEAL-STATE~3: UPDATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal update: ["+apppealRepresentation.getAppeal().getStatus()+"]");
        
/* State-4 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will ** DELETE ** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal adandonAppeal = apppealRepresentation.getAppeal();
        URI adandonAppealUri  = apppealRepresentation.getAbandonLink().getUri();
        
        System.out.println("\n** APPEAL-STATE~4: REMOVING APPEAL @"+adandonAppealUri+" using DELETE");
        System.out.println("** From Current Status: ["+adandonAppeal.getStatus()+"] to --> [ABANDON]");
        ClientResponse finalResponse = client.resource(adandonAppealUri).delete(ClientResponse.class);
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after abandon: ["+finalResponse.getEntity(AppealRepresentation.class).getStatus()+"]");
        System.out.println("\n\n************************* Abaondon Test completed Successfully !!! ************************\n\n");
    }
    
    private static void forgottenPathTest1(URI serviceUri) throws Exception {
        LOG.info("************************* FORGOTTEN PATH TEST -1 [STATES 1->2->3->5->8->10]************************");
        System.out.println(String.format("\n\n************************* FORGOTTEN TEST -1 [1->2->3->5->8->10]*********** via PUT\n[%s]", serviceUri.toString()));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/       
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-3 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **UPDATE** Appeal for a Grade Item like Mid Term.  
*/      
        Appeal updateAppeal = apppealRepresentation.getAppeal();
        updateAppeal.setStudentAppeal("Please Update Our marks. Adding few more details to my appeal .. !!!!");
        addAppealUri  = apppealRepresentation.getUpdateLink().getUri();
        System.out.println("\n** APPEAL-STATE~3: UPDATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-5 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **SUBMIT** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal submitAppeal = apppealRepresentation.getAppeal();
        URI submitAppealUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        System.out.println("\n** APPEAL-STATE~5: SUBMITTING APPEAL @"+submitAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+submitAppeal.getStatus()+"] to --> [PEND_REVIEW]");
        apppealRepresentation = client.resource(submitAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(submitAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getFollowUpLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal submission: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-8 [Current Status: PEND_REVIEW (Appeal pending for Professor Review) ]: 
 * This REST call will **FOLLOW UP** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal followUpAppeal = apppealRepresentation.getAppeal();
        followUpAppeal.setStudentAppeal("Gentle Reminder!. Please review the appeal submitted...");
        URI followUpUri  = apppealRepresentation.getFollowUpLink().getUri();
        System.out.println("\n** APPEAL-STATE~8: FOLLOWING UP FOR APPEAL @"+followUpUri+" using PUT");
        System.out.println("** From Current Status: ["+followUpAppeal.getStatus()+"] to --> [FOLLOWED_UP]");
        apppealRepresentation = client.resource(followUpUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(followUpAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal follow Up: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-10 [Current Status: PEND_REVIEW (Appeal pending for review!!) ]: 
 * This REST call will **UPDATE GRADES** for a Grade Item like Mid Term against an Appeal
*/        
        Appeal updateGrade = apppealRepresentation.getAppeal();
        int prvGrades = Integer.parseInt(updateGrade.getGrades()) ;
        updateGrade.setGrades(Integer.toString(prvGrades+10));
        URI gradeChangeUri  = apppealRepresentation.getGradeChangeLink().getUri();
        System.out.println("\n** APPEAL-STATE~10: PROF UPDATING GRADES @"+gradeChangeUri+" using PUT");
        System.out.println("** From Current Status: ["+updateGrade.getStatus()+"] to --> [APPEAL ACKNOWLEDGED]");
        apppealRepresentation = client.resource(gradeChangeUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateGrade));
        System.out.println(String.format("** Links available: \n\t\t"
                       +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after Grade Update: ["+apppealRepresentation.getAppeal().getStatus()+"]");
   
    System.out.println("************************* Forgotten Test completed Successfully !!!! ************************* "); 
        
    }
    
    private static void forgottenPathTest2(URI serviceUri) throws Exception {
        LOG.info("************************* FORGOTTEN PATH TEST -1 [STATES 1->2->3->5->8->9]************************");
        System.out.println(String.format("\n\n************************* FORGOTTEN TEST -1 [1->2->3->5->8->9]*********** via PUT\n [%s] ", serviceUri.toString()));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/       
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-3 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **UPDATE** Appeal for a Grade Item like Mid Term.  
*/      
        Appeal updateAppeal = apppealRepresentation.getAppeal();
        updateAppeal.setStudentAppeal("Please Update Our marks. Adding few more details to my appeal .. !!!!");
        addAppealUri  = apppealRepresentation.getUpdateLink().getUri();
        System.out.println("\n** APPEAL-STATE~3: UPDATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-5 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **SUBMIT** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal submitAppeal = apppealRepresentation.getAppeal();
        URI submitAppealUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        System.out.println("\n** APPEAL-STATE~5: SUBMITTING APPEAL @"+submitAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+submitAppeal.getStatus()+"] to --> [PEND_REVIEW]");
        apppealRepresentation = client.resource(submitAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(submitAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getFollowUpLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal submission: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-8 [Current Status: PEND_REVIEW (Appeal pending for Professor Review) ]: 
 * This REST call will **FOLLOW UP** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal followUpAppeal = apppealRepresentation.getAppeal();
        followUpAppeal.setStudentAppeal("Gentle Reminder!. Please review the appeal submitted...");
        URI followUpUri  = apppealRepresentation.getFollowUpLink().getUri();
        System.out.println("\n** APPEAL-STATE~8: FOLLOWING UP FOR APPEAL @"+followUpUri+" using PUT");
        System.out.println("** From Current Status: ["+followUpAppeal.getStatus()+"] to --> [FOLLOWED_UP]");
        apppealRepresentation = client.resource(followUpUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(followUpAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal follow Up: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-9 [Current Status: PEND_REVIEW (Appeal pending for review!!)  ]: 
 * This REST call will **COMMENT** on appeal for a Grade Item like Mid Term. [PROFESSOR] 
*/        
        Appeal commentAppeal = apppealRepresentation.getAppeal();
        URI commentAppealUri  = apppealRepresentation.getProfCommentLink().getUri();
        System.out.println("\n** APPEAL-STATE~9: COMMENT FROM PROFESSOR ON APPEAL @"+commentAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+commentAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(commentAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(commentAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal comment update: ["+apppealRepresentation.getAppeal().getStatus()+"]");
  
    System.out.println("************************* Forgotten Test completed Successfully !!!! *************************"); 
        
    }
    
    private static void badStartTestCase(URI serviceUri) throws Exception {
        System.out.println(String.format("\n\n************************* BADS-START ************************** via POST\n "));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED]: 
 * Adding Appeal with BAD URI. This will send a Valid Error Response !!! 
*/

        System.out.println("\n** BAD-URI OPERATION: CREATING APPEAL @"+apppealRepresentation.getAddAppealLink().getUri().toString()+"/bad-uri using PUT");
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update my marks !!!!!");
        Link badLink = new Link("bad", new GradeBookUri(apppealRepresentation.getAddAppealLink().getUri().toString() + "/bad-uri"), RESTBUCKS_MEDIA_TYPE);
        ClientResponse badUpdateResponse = client.resource(badLink.getUri()).accept(badLink.getMediaType()).type(badLink.getMediaType()).post(ClientResponse.class, new AppealRepresentation(AddAppeal));
        System.out.println(String.format("\n !!!! Server responsed with following HTTP STATUS CODE: [%d] !!!!",badUpdateResponse.getStatus()));
        
        System.out.println("\n\n************************* BAD START Test completed Successfully !!! ************************\n\n");
    }       

    private static void badIdTestCase(URI serviceUri) throws Exception {
        LOG.info("************************* FORGOTTEN PATH TEST -1 [STATES 1->2->3->5->8->10]************************");
        System.out.println(String.format("\n\n************************* BAD ID TEST -1 [1->2->3->5->8^(Bad-Id)]*********** via PUT\n[%s]", serviceUri.toString()));
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/       
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-3 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **UPDATE** Appeal for a Grade Item like Mid Term.  
*/      
        Appeal updateAppeal = apppealRepresentation.getAppeal();
        updateAppeal.setStudentAppeal("Please Update Our marks. Adding few more details to my appeal .. !!!!");
        addAppealUri  = apppealRepresentation.getUpdateLink().getUri();
        System.out.println("\n** APPEAL-STATE~3: UPDATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(updateAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-5 [Current Status: PEND_SUBMISSION (Appeal pending for Submission) ]: 
 * This REST call will **SUBMIT** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal submitAppeal = apppealRepresentation.getAppeal();
        URI submitAppealUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        System.out.println("\n** APPEAL-STATE~5: SUBMITTING APPEAL @"+submitAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+submitAppeal.getStatus()+"] to --> [PEND_REVIEW]");
        apppealRepresentation = client.resource(submitAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(submitAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getGradeChangeLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getFollowUpLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getProfCommentLink().getUri().toString()+"\t\t"
                ));
        System.out.println("** Appeal Status after appeal submission: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-8 [Current Status: PEND_REVIEW (Appeal pending for Professor Review) ]: 
 * This REST call will **FOLLOW UP** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal followUpAppeal = apppealRepresentation.getAppeal();
        followUpAppeal.setStudentAppeal("Gentle Reminder!. Please review the appeal submitted...");
        URI followUpUri  = apppealRepresentation.getFollowUpLink().getUri();
        GradeBookUri badIDUri = new GradeBookUri(followUpUri);
        String newUri = badIDUri.getBaseUri().toString() + "/followUp/1234";
        System.out.println("\n** APPEAL-STATE~8: FOLLOWING UP FOR APPEAL USING BAD-ID @"+newUri+" using PUT");
        //ClientResponse badUpdateResponse = client.resource(badLink.getUri()).accept(badLink.getMediaType()).type(badLink.getMediaType()).post(ClientResponse.class, new AppealRepresentation(AddAppeal));
        ClientResponse badIdResponse  = client.resource(newUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(ClientResponse.class, new ClientAppeal(followUpAppeal));
        
        System.out.println(String.format("\n !!!! Server responsed with following HTTP STATUS CODE: [%d] !!!!",badIdResponse.getStatus()));
        System.out.println("\n\n************************* BAD ID Test completed Successfully !!! ************************\n\n");
       
    }
    
    private static void badIdStateTestCase(URI serviceUri) throws Exception {
        LOG.info("************************* BAD STATE TEST [STATES 1->2->8]************************");
        System.out.println(String.format("\n\n************************* BAD STATE TEST [1->2->8^(Bad State)]*********** via PUT\n[%s]", serviceUri.toString()));
        System.out.println("This is an additional Test Case added to show that when a process is initiated on an Invalid State of resource.\nFor example: An Appeal is not allowed to Followed Up till the time it is submitted. If done so, CONFLICT HTTP response code should be thrown !!!!");
        AppealBuilder abuild = new AppealBuilder();
        Appeal appeal =  abuild.defaultItems("GradeAdd").build();
        Client client = Client.create();
        LOG.debug("Created client {}", client);
        
/* State-1 [Current Status: UNGRADED]: 
 * Adding Grades for  a Subject. This will generate a Unique Student Id 
*/
        System.out.println("\n** APPEAL-STATE~1: CREATING GRADES @"+serviceUri+" using POST");
        System.out.println("** From Current Status: ["+appeal.getStatus()+"] to --> [APPEAL EXPECTED]");
        AppealRepresentation apppealRepresentation = client.resource(serviceUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).post(AppealRepresentation.class, new ClientAppeal(appeal));
        LOG.debug("Created appealRepresentation {} denoted by the URI {}", apppealRepresentation, apppealRepresentation.getSelfLink().getUri().toString());
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getAddAppealLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after grade creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-2 [Current Status: EXPECTED (Appeal Expected) ]: 
 * This REST call will add Appeal for a Grade.  
*/       
        Appeal AddAppeal = apppealRepresentation.getAppeal();
        AddAppeal.setStudentAppeal("Please Update Our marks !!!!!");
        URI addAppealUri  = apppealRepresentation.getAddAppealLink().getUri();
        System.out.println("\n** APPEAL-STATE~2: CREATING APPEAL @"+addAppealUri+" using PUT");
        System.out.println("** From Current Status: ["+AddAppeal.getStatus()+"] to --> [PEND_SUBMISSION]");
        apppealRepresentation = client.resource(addAppealUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(AppealRepresentation.class, new ClientAppeal(AddAppeal));
        System.out.println(String.format("** Links available: \n\t\t"
                +apppealRepresentation.getUpdateLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAppealSubmitLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getAbandonLink().getUri().toString()+"\n\t\t"
                +apppealRepresentation.getSelfLink().getUri().toString()));
        System.out.println("** Appeal Status after appeal creation: ["+apppealRepresentation.getAppeal().getStatus()+"]");

/* State-8 [Current Status: PEND_REVIEW (Appeal pending for Professor Review) ]: 
 * This REST call will **FOLLOW UP** Appeal for a Grade Item like Mid Term.  
*/        
        Appeal followUpAppeal = apppealRepresentation.getAppeal();
        followUpAppeal.setStudentAppeal("Gentle Reminder!. Please review the appeal submitted...");
        URI followUpUri  = apppealRepresentation.getAppealSubmitLink().getUri();
        GradeBookUri grui = new GradeBookUri(followUpUri);
        String newUri = grui.getBaseUri().toString() + "/followUp/" + grui.getId().toString();
        System.out.println("\n** APPEAL-STATE~8: FOLLOWING UP FOR APPEAL WHEN PRESENT AT BAD-STATE OF RESOURCE @"+newUri+" using PUT");
        ClientResponse badIdResponse   = client.resource(newUri).accept(RESTBUCKS_MEDIA_TYPE).type(RESTBUCKS_MEDIA_TYPE).put(ClientResponse.class, new ClientAppeal(followUpAppeal));
        
        System.out.println(String.format("\n !!!! Server responsed with following HTTP STATUS CODE: [%d] !!!!",badIdResponse.getStatus()));
        System.out.println("\n\n************************* BAD STATE Test completed Successfully !!! ************************\n\n");
    }
    
}
