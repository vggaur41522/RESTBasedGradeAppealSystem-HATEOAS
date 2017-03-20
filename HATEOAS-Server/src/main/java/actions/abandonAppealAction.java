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
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealState;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.Link;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.AppealRepresentation;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.Representation;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.GradeBookUri;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.InvalidAppealStatusException;

public class abandonAppealAction {
    public AppealRepresentation abandonAppeal(GradeBookUri requestUri) {
        
        StudentIDGenerator appealIdentifier = requestUri.getId();        
        AppealRepository repository = AppealRepository.current();
        Appeal appeal = repository.get(appealIdentifier);
        if(appeal == null)
            throw new AppealNotFoundException();
        
        if(appeal.getStatus() != AppealState.PEND_SUBMISSION)
            throw new InvalidAppealStatusException();
        
        appeal.setStatus(AppealState.ABANDON);
        appeal.setStudentAppeal("**DELETED BY STUDENT**");
        
        repository.store(appealIdentifier, appeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + appealIdentifier.toString());
        return new AppealRepresentation(appeal,
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
}
