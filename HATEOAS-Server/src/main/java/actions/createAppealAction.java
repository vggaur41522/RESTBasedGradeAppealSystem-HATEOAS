/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actions;

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

public class createAppealAction {
    public AppealRepresentation addAppeal(Appeal appeal, GradeBookUri requestUri) {
        AppealRepository repository = AppealRepository.current();
        StudentIDGenerator appealIdentifier = requestUri.getId(); 
        Appeal dbAppeal = repository.get(appealIdentifier);

        if(dbAppeal == null)
            throw new AppealNotFoundException();
        
        if((dbAppeal.getStatus() != AppealState.EXPECTED) && (dbAppeal.getStatus() != AppealState.PEND_SUBMISSION))
            throw new InvalidAppealStatusException();
        
        dbAppeal.setStatus(AppealState.PEND_SUBMISSION);
        dbAppeal.setStudentAppeal(appeal.getStudentAppeal());
        
        repository.store(appealIdentifier, dbAppeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + appealIdentifier.toString());
        GradeBookUri appealUpdateUri = new GradeBookUri(requestUri.getBaseUri() + "/update/" + appealIdentifier.toString());
        GradeBookUri appealAbandonUri = new GradeBookUri(requestUri.getBaseUri() + "/abandon/" + appealIdentifier.toString());
        GradeBookUri appealSubmitUri = new GradeBookUri(requestUri.getBaseUri() + "/submit/" + appealIdentifier.toString());
        //GradeBookUri gradingUri = new GradeBookUri(requestUri.getBaseUri() + "/grade/" + stidGen.toString());
        return new AppealRepresentation(dbAppeal, 
                new Link(Representation.RELATIONS_URI + "abandon", appealAbandonUri), 
                new Link(Representation.RELATIONS_URI + "submit", appealSubmitUri), 
                new Link(Representation.RELATIONS_URI + "update", appealUpdateUri),
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
    
    public AppealRepresentation submitAppeal(Appeal appeal, GradeBookUri requestUri) {
        StudentIDGenerator appealIdentifier = requestUri.getId();        
        AppealRepository repository = AppealRepository.current();
        appeal = repository.get(appealIdentifier);
        if(appeal == null)
            throw new AppealNotFoundException();
        
        if(appeal.getStatus() != AppealState.PEND_SUBMISSION)
            throw new InvalidAppealStatusException();
        
        appeal.setStatus(AppealState.PEND_REVIEW);
        repository.store(appealIdentifier, appeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + appealIdentifier.toString());
        GradeBookUri appealGradeUpdateUri = new GradeBookUri(requestUri.getBaseUri() + "/grupdate/" + appealIdentifier.toString());
        GradeBookUri appealProfCommentUri = new GradeBookUri(requestUri.getBaseUri() + "/ProfComment/" + appealIdentifier.toString());
        GradeBookUri appealFollowUpUri = new GradeBookUri(requestUri.getBaseUri() + "/followUp/" + appealIdentifier.toString());
        
        return new AppealRepresentation(appeal, 
                new Link(Representation.RELATIONS_URI + "grupdate", appealGradeUpdateUri), 
                new Link(Representation.RELATIONS_URI + "ProfComment", appealProfCommentUri), 
                new Link(Representation.RELATIONS_URI + "followUp", appealFollowUpUri),
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
    
    public AppealRepresentation followUpAppeal(Appeal appeal, GradeBookUri requestUri) {
        StudentIDGenerator appealIdentifier = requestUri.getId();        
        AppealRepository repository = AppealRepository.current();
        Appeal dbAppeal = repository.get(appealIdentifier);
        if(dbAppeal == null)
            throw new AppealNotFoundException();
        
        dbAppeal.setStudentAppeal("Follow-Up Comments: \n\t\t"+ appeal.getStudentAppeal() + "\nOrininal Appeal Statement: \n\t\t"+dbAppeal.getStudentAppeal());
        
        if(appeal.getStatus() != AppealState.PEND_REVIEW)
            throw new InvalidAppealStatusException();
        
        dbAppeal.setStatus(AppealState.FOLLOWUP);
        repository.store(appealIdentifier, appeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + appealIdentifier.toString());
        GradeBookUri appealGradeUpdateUri = new GradeBookUri(requestUri.getBaseUri() + "/grupdate/" + appealIdentifier.toString());
        GradeBookUri appealProfCommentUri = new GradeBookUri(requestUri.getBaseUri() + "/ProfComment/" + appealIdentifier.toString());
        GradeBookUri appealFollowUpUri = new GradeBookUri(requestUri.getBaseUri() + "/followUp/" + appealIdentifier.toString());
        
        return new AppealRepresentation(dbAppeal, 
                new Link(Representation.RELATIONS_URI + "grupdate", appealGradeUpdateUri), 
                new Link(Representation.RELATIONS_URI + "ProfComment", appealProfCommentUri), 
                new Link(Representation.RELATIONS_URI + "followUp", appealFollowUpUri),
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
    
    public AppealRepresentation updateGradeForAppeal(Appeal appeal, GradeBookUri requestUri) {
        AppealRepository repository = AppealRepository.current();
        StudentIDGenerator appealIdentifier = requestUri.getId(); 
        Appeal dbAppeal = repository.get(appealIdentifier);
        
        if(dbAppeal == null)
            throw new AppealNotFoundException();
        
        if((dbAppeal.getStatus() != AppealState.PEND_REVIEW) && (dbAppeal.getStatus() != AppealState.FOLLOWUP))
            throw new InvalidAppealStatusException();
        
        dbAppeal.setStatus(AppealState.ACKNOWLEDGE);
        dbAppeal.setProfComments(appeal.getProfComments());
        dbAppeal.setGrades(appeal.getGrades());
        
        repository.store(appealIdentifier, dbAppeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + appealIdentifier.toString());
        return new AppealRepresentation(dbAppeal,
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
    
    public AppealRepresentation updateComment(Appeal appeal, GradeBookUri requestUri) {
        AppealRepository repository = AppealRepository.current();
        StudentIDGenerator appealIdentifier = requestUri.getId(); 
        Appeal dbAppeal = repository.get(appealIdentifier);
          
        if(dbAppeal == null)
            throw new AppealNotFoundException();
        
        if((dbAppeal.getStatus() != AppealState.PEND_REVIEW) && (dbAppeal.getStatus() != AppealState.FOLLOWUP))
            throw new InvalidAppealStatusException();
            
        dbAppeal.setStatus(AppealState.PEND_SUBMISSION);
        dbAppeal.setProfComments(appeal.getProfComments());
        
        repository.store(appealIdentifier, dbAppeal);
        
        GradeBookUri appealUri = new GradeBookUri(requestUri.getBaseUri() + "/appeal/" + appealIdentifier.toString());
        GradeBookUri appealUpdateUri = new GradeBookUri(requestUri.getBaseUri() + "/update/" + appealIdentifier.toString());
        GradeBookUri appealAbandonUri = new GradeBookUri(requestUri.getBaseUri() + "/abandon/" + appealIdentifier.toString());
        GradeBookUri appealSubmitUri = new GradeBookUri(requestUri.getBaseUri() + "/submit/" + appealIdentifier.toString());
        //GradeBookUri gradingUri = new GradeBookUri(requestUri.getBaseUri() + "/grade/" + stidGen.toString());
        return new AppealRepresentation(dbAppeal, 
                new Link(Representation.RELATIONS_URI + "abandon", appealAbandonUri), 
                new Link(Representation.RELATIONS_URI + "submit", appealSubmitUri), 
                new Link(Representation.RELATIONS_URI + "update", appealUpdateUri),
                new Link(Representation.SELF_REL_VALUE, appealUri));
    }
}
