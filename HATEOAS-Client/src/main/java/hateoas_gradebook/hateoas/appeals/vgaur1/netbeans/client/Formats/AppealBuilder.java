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

import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppealBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(AppealBuilder.class);
    public ArrayList<String> gradeItemNameList = new ArrayList<String>();
    
    public  AppealBuilder appeal() {
        return new AppealBuilder();
    }

    private String grades = "";
    private String gradeItemName = "";
    private String profComments = "";
    private String studentAppeal = "";
    private AppealState status = AppealState.UNGRADED;
    
    public String getRandomRange(int max, int min)
    {
        Random rn = new Random();
        return (Integer.toString(rn.nextInt(max - min + 1) + min) );
    }
    
    public AppealBuilder defaultItems(String mode) {
        LOG.debug("Executing OrderBuilder.defaultItems");
        String grades = "56";
        gradeItemNameList.add("MidTerm");
        gradeItemNameList.add("Finals");
        gradeItemNameList.add("Quiz");
        gradeItemNameList.add("Assignment1");
        gradeItemNameList.add("Assignment2");
        gradeItemNameList.add("Assignment3");
        gradeItemNameList.add("Projects");
        gradeItemNameList.add("Class Tests");
        this.grades = grades;
        this.gradeItemName = gradeItemNameList.get(5);
        this.status = AppealState.UNGRADED;
        this.profComments = "Marks Evaluated !!!";
        if(mode.equalsIgnoreCase("AppealAdd"))
            this.studentAppeal = "Dear Dr. Calliss, Please reevaluate my Marks !!! ";
        return this;
    }
    
    private void corruptItems() {
        LOG.debug("Executing OrderBuilder.corruptItems");
        this.studentAppeal = "";
        this.gradeItemName = "";
        this.grades = "";
        this.profComments = "";
    }
    
    public Appeal build() {
        LOG.debug("Executing OrderBuilder.build");
        //defaultItems();
        return new Appeal( grades, gradeItemName,  profComments, studentAppeal, status);
    }

}
