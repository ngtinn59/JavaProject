//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import javax.ws.rs.HeaderParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.PUT;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//@Path("/projects")
//public class ProjectResource {
//    private ProjectRepository repository = new ProjectRepository();
//
//    // GET all projects
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getProjects() {
//        // Implement your logic to retrieve all projects from the repository
//        // and return a JSON response
//    }
//
//    // GET a project by ID
//    @GET
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getProjectById(@PathParam("id") String id) {
//        // Implement your logic to retrieve a project by ID from the repository
//        // and return a JSON response
//    }
//
//    // POST a new project
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createProject(@Context HttpHeaders headers, Project newProject) {
//        // Implement your logic to create a new project using the provided data
//        // and return a JSON response
//    }
//
//    // PUT (update) a project by ID
//    @PUT
//    @Path("/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateProject(@Context HttpHeaders headers, @PathParam("id") String id, Project updatedProject) {
//        // Implement your logic to update an existing project by ID using the provided data
//        // and return a JSON response
//    }
//
//    // DELETE a project by ID
//    @DELETE
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response deleteProject(@PathParam("id") String id) {
//        // Implement your logic to delete a project by ID from the repository
//        // and return a JSON response
//    }
//}