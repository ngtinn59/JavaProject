import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserRespone {

    private UserResponsitory repository = new UserResponsitory();

    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<Users> users = repository.getUsers();
        if (users.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No users found").build();
        } else {
            return Response.ok(users).build();
        }
    }
}
