package ctu.cit;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Path("/experience")
public class ExperienceResponse {
    private ExperienceRepository repository = new ExperienceRepository();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllExperiencesByToken(@Context HttpHeaders headers) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Fetch the experiences for the profile ID extracted from the token
        List<Experience> experiences = repository.getAllExperienceByProfileId(profileId);

        // Convert the list of experiences to a JSON array
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Experience experience : experiences) {
            JsonObject experienceJson = Json.createObjectBuilder()
                    .add("id", experience.getId())
                    .add("position", experience.getPosition())
                    .add("company", experience.getCompany())
                    .add("start_date", experience.getStartDate())
                    .add("end_date", experience.getEndDate())
                    .add("responsibilities", experience.getResponsibilities())
                    .build();
            jsonArrayBuilder.add(experienceJson);
        }

        // Create the final JSON response object
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("data", jsonArrayBuilder.build())
                .build();

        // Return the response with the list of experiences
        return Response.ok(jsonResponse).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExperience(@Context HttpHeaders headers, Experience newExperience) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Experience createdExperience = repository.insertExperience(newExperience, profileId);
        if (createdExperience == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Failed to create a new experience")
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }

        JsonObject experienceJson = Json.createObjectBuilder()
            .add("id", createdExperience.getId())
            .add("position", createdExperience.getPosition())
            .add("company", createdExperience.getCompany())
            .add("start_date", createdExperience.getStartDate())
            .add("end_date", createdExperience.getEndDate())
            .add("responsibilities", createdExperience.getResponsibilities())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Experience created successfully")
            .add("experience", experienceJson)
            .build();

        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }

    @GET
    @Path("/{experienceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExperienceById(@Context HttpHeaders headers, @PathParam("experienceId") int experienceId) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Experience experience = repository.getExperienceById(experienceId, profileId);
        if (experience == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Experience not found")
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        JsonObject experienceJson = Json.createObjectBuilder()
            .add("id", experience.getId())
            .add("position", experience.getPosition())
            .add("company", experience.getCompany())
            .add("start_date", experience.getStartDate())
            .add("end_date", experience.getEndDate())
            .add("responsibilities", experience.getResponsibilities())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Success")
            .add("experience", experienceJson)
            .build();

        return Response.ok(jsonResponse).build();
    }

    @PUT
    @Path("/{experienceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExperience(@Context HttpHeaders headers, @PathParam("experienceId") int experienceId, Experience updatedExperience) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Experience existingExperience = repository.getExperienceById(experienceId, profileId);
        if (existingExperience == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Experience not found")
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        existingExperience.setPosition(updatedExperience.getPosition());
        existingExperience.setCompany(updatedExperience.getCompany());
        existingExperience.setStartDate(updatedExperience.getStartDate());
        existingExperience.setEndDate(updatedExperience.getEndDate());
        existingExperience.setResponsibilities(updatedExperience.getResponsibilities());

        Experience updatedExp = repository.updateExperience(existingExperience);
        if (updatedExp == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Failed to update experience")
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }

        JsonObject experienceJson = Json.createObjectBuilder()
            .add("id", updatedExp.getId())
            .add("position", updatedExp.getPosition())
            .add("company", updatedExp.getCompany())
            .add("start_date", updatedExp.getStartDate())
            .add("end_date", updatedExp.getEndDate())
            .add("responsibilities", updatedExp.getResponsibilities())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Experience updated successfully")
            .add("experience", experienceJson)
            .build();

        return Response.ok(jsonResponse).build();
    }

    @DELETE
    @Path("/{experienceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteExperience(@Context HttpHeaders headers, @PathParam("experienceId") int experienceId) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Experience existingExperience = repository.getExperienceById(experienceId, profileId);
        if (existingExperience == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Experience not found")
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        boolean deleted = repository.deleteExperience(experienceId);
        if (!deleted) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Failed to delete experience")
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Experience deleted successfully")
            .build();

        return Response.ok(jsonResponse).build();
    }

    private int extractProfileIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String tokenString = token.substring(7).trim();
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey("your-secret-key").parseClaimsJws(tokenString);
                String subject = claims.getBody().getSubject();
                String[] parts = subject.split("_");
                return Integer.parseInt(parts[0]); // First part is the profile ID
            } catch (JwtException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    private boolean isValidToken(String token) {
        // Implement your token validation logic here
        // For demonstration purposes, we'll assume the token is always valid
        return true;
    }
}
