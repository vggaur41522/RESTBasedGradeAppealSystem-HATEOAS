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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class Appeal {
    
//    private final Location location;
//    private final List<Item> items;
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

    public Appeal(String grades,String gradeItemName, String profComments,String studentAppeal,AppealState status) {
        this.grades = grades;
        this.gradeItemName = gradeItemName;
        this.profComments = profComments;
        this.studentAppeal = studentAppeal;
        this.status = status;
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
    public void setStatus(AppealState status) {
        this.status = status;
    }
    public void setStudentAppeal(String studentAppeal) {
        this.studentAppeal = studentAppeal;
    }
    public void setProfComments(String profComments) {
        this.profComments = profComments;
    }
    public void setGrades(String grades) {
        this.grades = grades;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grades: " + grades + "\n");
        sb.append("Grade Item Name: " + gradeItemName + "\n");
        sb.append("Professor Comments: " + profComments + "\n");
        sb.append("Student Appeal: " + studentAppeal + "\n");
        sb.append("Status: " + status + "\n");
        
        return sb.toString();
    }
}
