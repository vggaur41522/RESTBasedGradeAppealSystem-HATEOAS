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
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "appeal", namespace = Representation.RESTBUCKS_NAMESPACE)
public class AppealRepresentation extends Representation {
    
    private static final Logger LOG = LoggerFactory.getLogger(AppealRepresentation.class);

    @XmlElement(name="grades", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String grades;
    @XmlElement(name="gradeItemName", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String gradeItemName;
    @XmlElement(name="profComments", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String profComments;
    @XmlElement(name="studentAppeal", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String studentAppeal;
    @XmlElement(name="status", namespace = Representation.RESTBUCKS_NAMESPACE)
    private AppealState status = AppealState.EXPECTED;

    /**
     * For JAXB :-(
     */
    AppealRepresentation() {
        LOG.debug("In AppealRepresentation Constructor");
    }

    public static AppealRepresentation fromXmlString(String xmlRepresentation) {
        LOG.info("Creating an Appeal object from the XML = {}", xmlRepresentation);
                
        AppealRepresentation appealRepresentation = null;     
        try {
            JAXBContext context = JAXBContext.newInstance(AppealRepresentation.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            appealRepresentation = (AppealRepresentation) unmarshaller.unmarshal(new ByteArrayInputStream(xmlRepresentation.getBytes()));
        } catch (Exception e) {
            //throw new InvalidAppealException(e);
        }
        
        LOG.debug("Generated the object {}", appealRepresentation);
        return appealRepresentation;
    }
    
    public static AppealRepresentation createResponseAppealRepresentation(Appeal appeal, GradeBookUri appealUri) {
        LOG.info("Creating a Response Appeal for Appeal = {} and Appeal URI", appeal.toString(), appealUri.toString());
        
        AppealRepresentation AppealRepresentation;     
        
        GradeBookUri gradeUpdateUri = new GradeBookUri(appealUri.getBaseUri() + "/grade/" + appealUri.getId().toString());
        GradeBookUri appealAbandonUri = new GradeBookUri(appealUri.getBaseUri() + "/abandon/" + appealUri.getId().toString());
        GradeBookUri appealSubmitUri = new GradeBookUri(appealUri.getBaseUri() + "/submit/" + appealUri.getId().toString());
        GradeBookUri appealCreateUri = new GradeBookUri(appealUri.getBaseUri() + "/createAppeal/" + appealUri.getId().toString());
        GradeBookUri appealUpdateUri = new GradeBookUri(appealUri.getBaseUri() + "/update/" + appealUri.getId().toString());
        GradeBookUri appealGradeUpdateUri = new GradeBookUri(appealUri.getBaseUri() + "/grupdate/" + appealUri.getId().toString());
        GradeBookUri appealProfCommentUri = new GradeBookUri(appealUri.getBaseUri() + "/ProfComment/" + appealUri.getId().toString());
        GradeBookUri appealFollowUpUri = new GradeBookUri(appealUri.getBaseUri() + "/followUp/" + appealUri.getId().toString());
        LOG.debug("Grade Update URI = {}", gradeUpdateUri);
        
        if(appeal.getStatus() == AppealState.UNGRADED) {
            LOG.debug("The appeal status is {}", AppealState.UNGRADED);
            AppealRepresentation = new AppealRepresentation(appeal, 
                    new Link(RELATIONS_URI + "gradeChange", appealUri));
        }else if(appeal.getStatus() == AppealState.EXPECTED){
            LOG.debug("The Appeal status is {}", AppealState.EXPECTED);
            AppealRepresentation = new AppealRepresentation(appeal, 
                    new Link(RELATIONS_URI + "addAppeal", appealCreateUri));
        }else if(appeal.getStatus() == AppealState.PEND_SUBMISSION){
            LOG.debug("The Appeal status is {}", AppealState.PEND_SUBMISSION);
            AppealRepresentation = new AppealRepresentation(appeal, 
                    new Link(RELATIONS_URI + "update", appealUpdateUri), 
                    new Link(RELATIONS_URI + "submit", appealSubmitUri), 
                    new Link(RELATIONS_URI + "abandon", appealAbandonUri),
                    new Link(Representation.SELF_REL_VALUE, appealUri));
        }else if(appeal.getStatus() == AppealState.PEND_REVIEW){
            LOG.debug("The Appeal status is {}", AppealState.PEND_REVIEW);
            AppealRepresentation = new AppealRepresentation(appeal, 
                    new Link(RELATIONS_URI + "grupdate", appealGradeUpdateUri), 
                    new Link(RELATIONS_URI + "ProfComment", appealProfCommentUri),
                    new Link(RELATIONS_URI + "followUp", appealFollowUpUri));
        }else if(appeal.getStatus() == AppealState.ACKNOWLEDGE) {
            LOG.debug("The Appeal status is {}", AppealState.ACKNOWLEDGE);
            AppealRepresentation = new AppealRepresentation(appeal,
                    new Link(Representation.SELF_REL_VALUE, appealUri));            
        }else if(appeal.getStatus() == AppealState.ABANDON) {
            LOG.debug("The Appeal status is {}", AppealState.ABANDON);
            AppealRepresentation = new AppealRepresentation(appeal,
                    new Link(Representation.SELF_REL_VALUE, appealUri));            
        } else if(appeal.getStatus() == AppealState.FOLLOWUP){
            LOG.debug("The Appeal status is {}", AppealState.PEND_REVIEW);
            AppealRepresentation = new AppealRepresentation(appeal, 
                    new Link(RELATIONS_URI + "grupdate", appealGradeUpdateUri), 
                    new Link(RELATIONS_URI + "ProfComment", appealProfCommentUri),
                    new Link(RELATIONS_URI + "followUp", appealFollowUpUri));
        }else {
            LOG.debug("The Appeal status is in an unknown status");
            throw new RuntimeException("Unknown Appeal Status");
        }
        LOG.debug("The Appeal representation created for the Create Response Appeal Representation is {}", AppealRepresentation);
        
        return AppealRepresentation;
    }
    public AppealRepresentation(Appeal appeal, Link... links) {
        LOG.info("Creating an Appeal Representation for Appeal = {} and links = {}", appeal.toString(), links.toString());
        try {
            this.grades = appeal.getGrades();
            this.gradeItemName = appeal.getGradeItemName();
            this.profComments = appeal.getProfComments();
            this.studentAppeal = appeal.getStudentAppeal();
            this.status = appeal.getStatus();
            this.links = java.util.Arrays.asList(links);
        } catch (Exception e) {
            throw new InvalidAppealStatusException();
        }
        LOG.debug("Created the AppealRepresentation {}", this);
    }

    @Override
    public String toString() {
        LOG.info("Converting Appeal Representation object to string");
        try {
            JAXBContext context = JAXBContext.newInstance(AppealRepresentation.class);
            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Appeal getAppeal() {
        LOG.info("Retrieving the Appeal Representation");
        LOG.debug("grades = {}", grades);
        LOG.debug("gradeItemName = {}", gradeItemName);
        if (grades == null || gradeItemName == null) {
            //throw new InvalidAppealException();
        }
        Appeal appeal = new Appeal(grades,gradeItemName,profComments, studentAppeal,status);
        
        LOG.debug("Retrieving the Appeal Representation {}", appeal);

        return appeal;
    }

    public Link getAbandonLink() {
        LOG.info("Retrieving the Cancel link ");
        return getLinkByName(RELATIONS_URI + "abandon");
    }
    
    public Link getGradeChangeLink() {
        LOG.info("Retrieving the Cancel link ");
        return getLinkByName(RELATIONS_URI + "grupdate");
    }

    public Link getAddAppealLink() {
        LOG.info("Retrieving the Payment link ");
        return getLinkByName(RELATIONS_URI + "addAppeal");
    }
    
    public Link getProfCommentLink() {
        LOG.info("Retrieving the Cancel link ");
        return getLinkByName(RELATIONS_URI + "ProfComment");
    }
    public Link getUpdateLink() {
        LOG.info("Retrieving the Update link ");
        return getLinkByName(RELATIONS_URI + "update");
    }
    
    public Link getFollowUpLink() {
        LOG.info("Retrieving the Update link ");
        return getLinkByName(RELATIONS_URI + "followUp");
    }
    
    public Link getAppealSubmitLink() {
        LOG.info("Retrieving the Cancel link ");
        return getLinkByName(RELATIONS_URI + "submit");
    }
    
    public Link getSelfLink() {
        LOG.info("Retrieving the Self link ");
        return getLinkByName("self");
    }
    
    public AppealState getStatus() {
        LOG.info("Retrieving the Appeal status {}", status);
        return status;
    }

    public String getGrades() {
        LOG.info("Retrieving the Appeal grades {}", grades);
        return grades;
    }
}
