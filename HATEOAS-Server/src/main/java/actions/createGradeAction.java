/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actions;

/**
 *
 * @author Varun_Gaur
 */
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.StudentIDGenerator;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.Appeal;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealRepository;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealState;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.Link;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealRepresentation;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.Representation;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.GradeBookUri;

public class createGradeAction {
    public AppealRepresentation create(Appeal appeal, GradeBookUri requestUri) {
        appeal.setStatus(AppealState.EXPECTED);
                
        StudentIDGenerator stidGen = AppealRepository.current().store(appeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + stidGen.toString());
        GradeBookUri appealCreateUri = new GradeBookUri(requestUri.getBaseUri() + "/createAppeal/" + stidGen.toString());
        GradeBookUri selfUri = new GradeBookUri(requestUri.getBaseUri()  + "/" + stidGen.toString());
        //GradeBookUri gradingUri = new GradeBookUri(requestUri.getBaseUri() + "/grade/" + stidGen.toString());
        return new AppealRepresentation(appeal, 
                new Link(Representation.RELATIONS_URI + "addAppeal", appealCreateUri),
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
}
