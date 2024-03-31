package ctu.cit;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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

@Path("/skills")
public class SkillRespone {
    private SkillRepository repository = new SkillRepository();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkillsByToken(@Context HttpHeaders headers) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Invalid profile ID format in token")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Fetch the skills for the profile ID extracted from the token
        List<Skill> skills = repository.getAllSkillsByProfileId(profileId);

        // Convert the list of skills to a JSON array
     // Convert the list of skills to a JSON array
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Skill skill : skills) {
            String levelDescription;
            switch (skill.getLevel()) {
                case 1:
                    levelDescription = "Beginner";
                    break;
                case 2:
                    levelDescription = "Intermediate";
                    break;
                case 3:
                    levelDescription = "Excellent";
                    break;
                default:
                    levelDescription = "Unknown";
            }

            JsonObject skillJson = Json.createObjectBuilder()
                    .add("id", skill.getId())
                    .add("name", skill.getName())
                    .add("level", levelDescription) // Thay đổi ở đây
                    .add("profiles_id", skill.getProfilesId())
                    .build();
            jsonArrayBuilder.add(skillJson);
        }

        // Create the final JSON response object
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("skills", jsonArrayBuilder.build())
                .build();

        // Return the response with the list of skills
        return Response.ok(jsonResponse).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSkill(@Context HttpHeaders headers, Skill newSkill) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Unauthorized access")
                .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            System.out.println("Error parsing profileId: " + e.getMessage());
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid profile ID format in token")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Skill createdSkill = repository.insertSkill(newSkill, profileId);
        if (createdSkill == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Failed to create a new skill")
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }

        JsonObject skillJson = Json.createObjectBuilder()
            .add("id", createdSkill.getId())
            .add("name", createdSkill.getName())
            .add("level", createdSkill.getLevel())
            .add("profiles_id", createdSkill.getProfilesId())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Skill created successfully")
            .add("skill", skillJson)
            .build();

        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }

   

    private String extractIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String tokenString = token.substring(7).trim();

            try {
                Jws<Claims> claims = Jwts.parser()
                                         .setSigningKey("your-secret-key")
                                         .parseClaimsJws(tokenString);
                String subject = claims.getBody().getSubject();
                // Split the subject to extract ID
                String[] parts = subject.split("_");
                return parts[0]; // First part is the ID
            } catch (JwtException e) {
                // Handle token parsing exception
                e.printStackTrace(); // Or log the exception
                return null; // Return null if unable to extract ID
            }
        }
        return null;
    }

    private boolean isValidToken(String token) {
      // Implement your token validation logic here
      // For demonstration purposes, we'll assume the token is always valid
      return true;
    }
    
    @GET
    @Path("{skillId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkillById(@Context HttpHeaders headers, @PathParam("skillId") long skillId) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
        }   
        
        if (profileId <= 0) {
          JsonObject jsonError = Json.createObjectBuilder()
              .add("error", "Invalid token: No profile ID found")
              .build();
          return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }
        
        // Fetch the skill by its ID
        Skill skill = repository.getSkillById(skillId, profileId);

        // Check if the skill exists
        if (skill == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Skill with ID " + skillId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        String levelDescription;
        switch (skill.getLevel()) {
            case 1:
                levelDescription = "Beginner";
                break;
            case 2:
                levelDescription = "Intermediate";
                break;
            case 3:
                levelDescription = "Excellent";
                break;
            default:
                levelDescription = "Unknown";
        }

        // Create the JSON response for the skill
        JsonObject skillJson = Json.createObjectBuilder()
                .add("id", skill.getId())
                .add("name", skill.getName())
                .add("level", levelDescription)
                .add("profiles_id", skill.getProfilesId())
                .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("message", "success")
                .add("data", skillJson)
                .add("status_code", 200)
                .build();

        // Return the response with the skill details
        return Response.ok(jsonResponse).build();
    }


    @PUT
    @Path("{skillId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSkill(@Context HttpHeaders headers, @PathParam("skillId") long skillId, Skill updatedSkill) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }
        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
        }   
        
        if (profileId <= 0) {
          JsonObject jsonError = Json.createObjectBuilder()
              .add("error", "Invalid token: No profile ID found")
              .build();
          return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Fetch the skill by its ID
        Skill existingSkill = repository.getSkillById(skillId, profileId);

        // Check if the skill exists
        if (existingSkill == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Skill with ID " + skillId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Update the skill details
        existingSkill.setName(updatedSkill.getName());
        existingSkill.setLevel(updatedSkill.getLevel());

        // Perform the update in the database
        boolean success = repository.updateSkill(existingSkill) != null;

        if (success) {
            // Create the JSON response for the updated skill
            JsonObject skillJson = Json.createObjectBuilder()
                    .add("id", existingSkill.getId())
                    .add("name", existingSkill.getName())
                    .add("level", existingSkill.getLevel())
                    .add("profiles_id", existingSkill.getProfilesId())
                    .build();

            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Skill updated successfully")
                    .add("data", skillJson)
                    .add("status_code", 200)
                    .build();

            // Return the response with the updated skill details
            return Response.ok(jsonResponse).build();
        } else {
            // Return an internal server error response if the update fails
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Failed to update the skill")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }
    }

    @DELETE
    @Path("{skillId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSkill(@Context HttpHeaders headers, @PathParam("skillId") long skillId) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Fetch the skill by its ID
        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
        }   
        
        if (profileId <= 0) {
          JsonObject jsonError = Json.createObjectBuilder()
              .add("error", "Invalid token: No profile ID found")
              .build();
          return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Skill existingSkill = repository.getSkillById(skillId, profileId);

        // Check if the skill exists
        if (existingSkill == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Skill with ID " + skillId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Perform the deletion in the database
        boolean success = repository.deleteSkill(skillId);

        if (success) {
            // Create the JSON response for the deletion confirmation
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Skill deleted successfully")
                    .add("status_code", 200)
                    .build();

            // Return the response with the deletion confirmation
            return Response.ok(jsonResponse).build();
        } else {
            // Return an internal server error response if the deletion fails
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Failed to delete the skill")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }
    }

}
