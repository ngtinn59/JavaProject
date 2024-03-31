package ctu.cit;


import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
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

@Path("/education")
public class EducationRespone {
  private EducationRepository repository = new EducationRepository();
  

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllProjectsByToken(@Context HttpHeaders headers) {
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

      // Fetch the projects for the profile ID extracted from the token
      List<Education> projects = repository.getAllEducationByProfileId(profileId);

      // Convert the list of projects to a JSON array
      JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
      for (Education project : projects) {
          JsonObject projectJson = Json.createObjectBuilder()
                  .add("id", project.getId())
                  .add("degree", project.getDegree())
                  .add("institution", project.getInstitution())
                  	.add("additionaldetail", project.getAdditionalDetail())

                  .add("start_date", project.getStartDate())
                  .add("end_date", project.getEndDate())
                  .build();
          jsonArrayBuilder.add(projectJson);
      }

      // Create the final JSON response object
      JsonObject jsonResponse = Json.createObjectBuilder()
              .add("success", true)
              .add("data", jsonArrayBuilder.build())
              .build();

      // Return the response with the list of projects
      return Response.ok(jsonResponse).build();
  }
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createEducation(@Context HttpHeaders headers, Education newEducation) {
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

//    if (!validateProject(newProject)) {
//      JsonObject jsonError = Json.createObjectBuilder()
//          .add("error", "Invalid project data")
//          .build();
//      return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
//    }

    Education createdEducation = repository.insertEducation(newEducation, profileId);
    if (createdEducation == null) {
      JsonObject jsonError = Json.createObjectBuilder()
          .add("error", "Failed to create a new project"+profileId)
          .build();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
    }

    JsonObject projectJson = Json.createObjectBuilder()
        .add("degree", createdEducation.getDegree())
        .add("institution", createdEducation.getInstitution())
        .add("start_date",createdEducation.getStartDate())
        .add("end_date",  createdEducation.getEndDate())
        .add("profiles_id", profileId)
        .add("id", createdEducation.getId())

        .build();

    JsonObject jsonResponse = Json.createObjectBuilder()
        .add("success", true)
        .add("message", "Project created successfully")
        .add("project", projectJson)
        .build();

    return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
  }

  @GET
  @Path("{educationId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProjectById(@Context HttpHeaders headers, @PathParam("educationId") int educationId) {
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
      
      Education education = repository.getEducationById(educationId, profileId);

      // Check if the project exists
      if (education == null) {
          JsonObject jsonError = Json.createObjectBuilder()
                  .add("error", "Project with ID " + educationId + " not found")
                  .build();
          return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
      }

      // Create the JSON response for the project
//      JsonObject projectJson = Json.createObjectBuilder()
//    		  
//              .add("id", project.getId())
//              .add("title", project.getTitle())
//              .add("description", project.getDescription())
//              .add("start_date", project.getStartDate())
//              .add("end_date", project.getEndDate())
//              .build();
      JsonObject projectJson = Json.createObjectBuilder()
    	        .add("degree", education.getDegree())
    	        .add("institution", education.getInstitution())
    	        .add("additionaldetail", education.getAdditionalDetail())

    	        .add("start_date", education.getStartDate())
    	        .add("end_date", education.getEndDate())
    	        .add("id", education.getId())
    	        .build();

    	    JsonObject jsonResponse = Json.createObjectBuilder()
    	        .add("success", true)
    	        .add("message", "success")
    	        .add("data", projectJson)
    	        .add("status_code", 200)
    	        .build();

      // Return the response with the project details
      return Response.ok(jsonResponse).build();
  }
  
  @PUT
  @Path("{educationId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateProject(@Context HttpHeaders headers, @PathParam("educationId") int educationId, Education updatedProject) {
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

      // Fetch the project by its ID
      Education existingEducation = repository.getEducationById(educationId, profileId);

      // Check if the project exists
      if (existingEducation == null) {
          JsonObject jsonError = Json.createObjectBuilder()
                  .add("error", "Project with ID " + educationId + " not found")
                  .build();
          return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
      }

      // Update the project details
      existingEducation.setDegree(updatedProject.getDegree());
      existingEducation.setInstitution(updatedProject.getInstitution());
      existingEducation.setAdditionalDetail((updatedProject.getAdditionalDetail()));
      existingEducation.setStartDate(updatedProject.getStartDate());
      existingEducation.setEndDate(updatedProject.getEndDate());

      // Perform the update in the database
      boolean success = repository.updateEducation(existingEducation) != null;

      if (success) {
          // Create the JSON response for the updated project
          JsonObject projectJson = Json.createObjectBuilder()
        		.add("degree", existingEducation.getDegree())
      	        .add("institution", existingEducation.getInstitution())
      	        .add("additionaldetail", existingEducation.getAdditionalDetail())
      	        .add("start_date", existingEducation.getStartDate())
      	        .add("end_date", existingEducation.getEndDate())
      	        .add("id", existingEducation.getId())
      	        .build();

          JsonObject jsonResponse = Json.createObjectBuilder()
                  .add("success", true)
                  .add("message", "Education updated successfully")
                  .add("project", projectJson)
                  .build();

          // Return the response with the updated project details
          return Response.ok(jsonResponse).build();
      } else {
          // Return an internal server error response if the update fails
          JsonObject jsonError = Json.createObjectBuilder()
                  .add("error", "Failed to update the project")
                  .build();
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
      }
  }

  @DELETE
  @Path("{projectId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteProject(@Context HttpHeaders headers, @PathParam("projectId") int projectId) {
      // Retrieve the token from the Authorization header
      String token = headers.getHeaderString("Authorization");

      // Check if the token is present and valid
      if (token == null || !isValidToken(token)) {
          JsonObject jsonError = Json.createObjectBuilder()
                  .add("error", "Unauthorized access")
                  .build();
          return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
      }

      // Fetch the project by its ID
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

      Education existingProject = repository.getEducationById(projectId,profileId);

      // Check if the project exists
      if (existingProject == null) {
          JsonObject jsonError = Json.createObjectBuilder()
                  .add("error", "Project with ID " + projectId + " not found")
                  .build();
          return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
      }

      // Perform the deletion in the database
      boolean success = repository.deleteEducation(projectId);

      if (success) {
          // Create the JSON response for the deletion confirmation
          JsonObject jsonResponse = Json.createObjectBuilder()
                  .add("success", true)
                  .add("message", "Education deleted successfully")
                  .build();

          // Return the response with the deletion confirmation
          return Response.ok(jsonResponse).build();
      } else {
          // Return an internal server error response if the deletion fails
          JsonObject jsonError = Json.createObjectBuilder()
                  .add("error", "Failed to delete the Education")
                  .build();
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
      }
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
  
}

  

