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
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.Appeal;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.AppealState;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.client.Formats.Representation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "appeal", namespace = Representation.RESTBUCKS_NAMESPACE)
public class ClientAppeal {
    
    private static final Logger LOG = LoggerFactory.getLogger(ClientAppeal.class);
    
    @XmlElement(name="grades", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String grades;
    @XmlElement(name="gradeItemName", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String gradeItemName;
    @XmlElement(name="profComments", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String profComments;
    @XmlElement(name="studentAppeal", namespace = Representation.RESTBUCKS_NAMESPACE)
    private String studentAppeal;
    @XmlElement(name="status", namespace = Representation.RESTBUCKS_NAMESPACE)
    private AppealState status = AppealState.UNGRADED;
    
    private ClientAppeal(){}
    
    public ClientAppeal(Appeal appeal) {
        LOG.debug("Executing ClientOrder constructor");
        this.grades = appeal.getGrades();
        this.gradeItemName = appeal.getGradeItemName();
        this.profComments = appeal.getProfComments(); 
        this.studentAppeal = appeal.getStudentAppeal();
        this.status = appeal.getStatus();
    }
    
    public Appeal getAppeal() {
        LOG.debug("Executing ClientOrder.getOrder");
        return new Appeal( grades, gradeItemName,  profComments, studentAppeal, status);
    }
    
    public String getGrades() {
        return grades;
    }
    public String getGradeItemName() {
        return gradeItemName;
    }
    public String getProfComments() {
        return profComments;
    }
    public String getStudentAppeal() {
        return studentAppeal;
    }
    public AppealState getStatus() {
        return status;
    }

    @Override
    public String toString() {
        LOG.debug("Executing ClientOrder.toString");
        try {
            JAXBContext context = JAXBContext.newInstance(ClientAppeal.class);
            Marshaller marshaller = context.createMarshaller();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(this, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}