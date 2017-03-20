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
import actions.abandonAppealAction;
import actions.createAppealAction;
import actions.createGradeAction;
import actions.fetchAppealAction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.*;
import hateoas_gradebook.hateoas.appeals.vgaur1.netbeans.server.GradeBookUri;
import javax.ws.rs.PUT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/gradeAppealResource")
public class GradeResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradeResource.class);

    private @Context UriInfo uriInfo;

    public GradeResource() {
        LOG.info("GradeResource constructor");
    }

    /**
     * Used in test cases only to allow the injection of a mock UriInfo.
     * 
     * @param uriInfo
     */
    public GradeResource(UriInfo uriInfo) {
        LOG.info("GradeResource constructor with mock uriInfo {}", uriInfo);
        this.uriInfo = uriInfo;  
    }
    
    @DELETE
    @Path("/abandon/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    public Response abandonAppeal() {
        LOG.info("Removing an Appeal Reource");
        Response response;
        try {
            AppealRepresentation removedAppeal = new abandonAppealAction().abandonAppeal(new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(removedAppeal).build();
        } catch (InvalidAppealStatusException deletedAppeal) {
            LOG.debug("Appeal status not Valid for this operation !!");
            response = Response.status(Status.NOT_FOUND).build();
        } catch(AppealNotFoundException invalieAppeal) {
            LOG.debug("Problem deleting appeal resource");
            response = Response.status(Status.METHOD_NOT_ALLOWED).header("Allow", "GET").build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong deleting the appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Resulting response for deleting the Appeal resource is {}", response);
        
        return response;
    }

    @PUT
    @Path("/createAppeal/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response createAppeal(String appealRepresentation) {
        LOG.info("Updating an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new createAppealAction().addAppeal(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("Appeal Not found for processing {}"+appealRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(InvalidAppealStatusException invaliedAS) {
            LOG.debug("Invalid update to payment {}", appealRepresentation);
            StudentIDGenerator identifier = new GradeBookUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new GradeBookUri(uriInfo.getBaseUri().toString() + "createAppeal/" + identifier));
            response = Response.status(Status.CONFLICT).entity(link).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        LOG.debug("Resulting response for updating the Appeal resource is {}", response);
        return response;
    }
    
    @PUT
    @Path("/update/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response updateAppeal(String appealRepresentation) {
        LOG.info("Updating an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new createAppealAction().addAppeal(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("Appeal Not found for processing {}"+appealRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(InvalidAppealStatusException invaliedAS) {
            LOG.debug("Invalid update to payment {}", appealRepresentation);
            StudentIDGenerator identifier = new GradeBookUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new GradeBookUri(uriInfo.getBaseUri().toString() + "update/" + identifier));
            response = Response.status(Status.CONFLICT).entity(link).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        LOG.debug("Resulting response for updating the Appeal resource is {}", response);
        return response;
    }
    
    @PUT
    @Path("/submit/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response submitAppeal(String appealRepresentation) {
        LOG.info("Submitting an Appeal Resource");
        Response response;
        try {
            AppealRepresentation responseRepresentation = new createAppealAction().submitAppeal(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("Appeal Not found for processing {}"+appealRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(InvalidAppealStatusException invaliedAS) {
            LOG.debug("Invalid update to payment {}", appealRepresentation);
            StudentIDGenerator identifier = new GradeBookUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new GradeBookUri(uriInfo.getBaseUri().toString() + "submit/" + identifier));
            response = Response.status(Status.CONFLICT).entity(link).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        LOG.debug("Resulting response for updating the Appeal resource is {}", response);
        return response;
    }
    
    @PUT
    @Path("/grupdate/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response updateGradeForAppeal(String appealRepresentation) {
        LOG.info("Submitting an Appeal Resource");
        Response response;
        try {
            AppealRepresentation responseRepresentation = new createAppealAction().updateGradeForAppeal(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("Appeal Not found for processing {}"+appealRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(InvalidAppealStatusException invaliedAS) {
            LOG.debug("Invalid update to payment {}", appealRepresentation);
            StudentIDGenerator identifier = new GradeBookUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new GradeBookUri(uriInfo.getBaseUri().toString() + "grupdate/" + identifier));
            response = Response.status(Status.CONFLICT).entity(link).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        LOG.debug("Resulting response for updating the Appeal resource is {}", response);
        return response;
    }
    
    @PUT
    @Path("/ProfComment/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response updateCommentByProf(String appealRepresentation) {
        LOG.info("Submitting an Appeal Resource");
        Response response;
        try {
            AppealRepresentation responseRepresentation = new createAppealAction().updateComment(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("Appeal Not found for processing {}"+appealRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(InvalidAppealStatusException invaliedAS) {
            LOG.debug("Invalid update to payment {}", appealRepresentation);
            StudentIDGenerator identifier = new GradeBookUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new GradeBookUri(uriInfo.getBaseUri().toString() + "ProfComment/" + identifier));
            response = Response.status(Status.CONFLICT).entity(link).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        LOG.debug("Resulting response for updating the Appeal resource is {}", response);
        return response;
    }
    
    @PUT
    @Path("/followUp/{studentId}")
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response followUpByStudent(String appealRepresentation) {
        LOG.info("Submitting an Appeal Resource");
        Response response;
        try {
            AppealRepresentation responseRepresentation = new createAppealAction().followUpAppeal(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("Appeal Not found for processing {}"+appealRepresentation);
            response = Response.status(Status.NOT_FOUND).build();
        } catch(InvalidAppealStatusException invaliedAS) {
            LOG.debug("Invalid update to payment {}", appealRepresentation);
            StudentIDGenerator identifier = new GradeBookUri(uriInfo.getRequestUri()).getId();
            Link link = new Link(Representation.SELF_REL_VALUE, new GradeBookUri(uriInfo.getBaseUri().toString() + "followUp/" + identifier));
            response = Response.status(Status.CONFLICT).entity(link).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong updating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } 
        LOG.debug("Resulting response for updating the Appeal resource is {}", response);
        return response;
    }
    
    // Creating STUDENT ID AND ADDING GRADE TO IT !!!!!!
    @POST
    @Consumes("application/vnd.gradebook+xml")
    @Produces("application/vnd.gradebook+xml")
    public Response createGrade(String appealRepresentation) {
        LOG.info("Creating an Appeal Resource");
        
        Response response;
        try {
            AppealRepresentation responseRepresentation = new createGradeAction().create(AppealRepresentation.fromXmlString(appealRepresentation).getAppeal(), new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.created(responseRepresentation.getAddAppealLink().getUri()).entity(responseRepresentation).build();
        }catch (InvalidAppealStatusException invalidAS) {
            LOG.debug("Invalid Appeal {}", appealRepresentation);
            response = Response.status(Status.BAD_REQUEST).build();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            LOG.debug("Someting went wrong creating the Appeal resource");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        LOG.debug("Resulting response for creating the Appeal resource is {}", response);
        return response;
    }
    
    // FETCHING STUDENT DETAILS HERE AND CORRESPONDING APPEAL STATUS !!!!!
    @GET
    @Path("/appeal/{studentId}")
    @Produces("application/vnd.gradebook+xml")
    public Response getAppeal() {
        LOG.info("Retrieving an Appeal Resource");
        
        Response response;
        
        try {
            AppealRepresentation responseRepresentation = new fetchAppealAction().retrieveByUri(new GradeBookUri(uriInfo.getRequestUri()));
            response = Response.status(Status.OK).entity(responseRepresentation).build();
        } catch(AppealNotFoundException appealNF) {
            LOG.debug("No Such appeal found for processing ");
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception ex) {
            LOG.debug("Something went wrong retriveing the Appeal");
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        LOG.debug("Retrieved the Appeal resource", response);
        
        return response;
    }
}
