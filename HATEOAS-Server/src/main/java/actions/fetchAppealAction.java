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
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealNotFoundException;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealRepository;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealRepresentation;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.GradeBookUri;

public class fetchAppealAction {
    public AppealRepresentation retrieveByUri(GradeBookUri appealUri) {
        StudentIDGenerator identifier  = appealUri.getId();
        
        Appeal appeal = AppealRepository.current().get(identifier);
        
        if(appeal == null) {
            throw new AppealNotFoundException();
        }
        
        return AppealRepresentation.createResponseAppealRepresentation(appeal, appealUri);
    }
}
