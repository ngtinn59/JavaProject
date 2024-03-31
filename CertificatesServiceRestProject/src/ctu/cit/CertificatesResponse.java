package ctu.cit;

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

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Path("/certificates")
public class CertificatesResponse {
    private CertificatesRepository repository = new CertificatesRepository();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCertificatesByToken(@Context HttpHeaders headers) {
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

        // Fetch the certificates for the profile ID extracted from the token
        List<Certificate> certificates = repository.getAllCertificatesByProfileId(profileId);

        // Convert the list of certificates to a JSON array
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Certificate certificate : certificates) {
            JsonObject certificateJson = Json.createObjectBuilder()
                    .add("id", certificate.getId())
                    .add("title", certificate.getTitle())
                    .add("provider", certificate.getProvider())
                    .add("issueDate", certificate.getIssueDate())
                    .add("description", certificate.getDescription())
                    .add("certificateUrl", certificate.getCertificateUrl())
                    .build();
            jsonArrayBuilder.add(certificateJson);
        }

        // Create the final JSON response object
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("data", jsonArrayBuilder.build())
                .build();

        // Return the response with the list of certificates
        return Response.ok(jsonResponse).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCertificate(@Context HttpHeaders headers, Certificate newCertificate) {
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

        Certificate createdCertificate = repository.insertCertificate(newCertificate, profileId);
        if (createdCertificate == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Failed to create a new certificate")
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }

        JsonObject certificateJson = Json.createObjectBuilder()
            .add("id", createdCertificate.getId())
            .add("title", createdCertificate.getTitle())
            .add("provider", createdCertificate.getProvider())
            .add("issueDate", createdCertificate.getIssueDate())
            .add("description", createdCertificate.getDescription())
            .add("certificateUrl", createdCertificate.getCertificateUrl())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Certificate created successfully")
            .add("certificate", certificateJson)
            .build();

        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }

    @PUT
    @Path("{certificateId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCertificate(@Context HttpHeaders headers, @PathParam("certificateId") int certificateId, Certificate updatedCertificate) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Extract profile ID from the token
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

        // Check if the profile ID is valid
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Fetch the existing certificate from the repository
        Certificate existingCertificate = repository.getCertificateById(certificateId, profileId);

        // Check if the certificate exists
        if (existingCertificate == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Certificate with ID " + certificateId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Update the existing certificate with the new information
        existingCertificate.setTitle(updatedCertificate.getTitle());
        existingCertificate.setProvider(updatedCertificate.getProvider());
        existingCertificate.setIssueDate(updatedCertificate.getIssueDate());
        existingCertificate.setDescription(updatedCertificate.getDescription());
        existingCertificate.setCertificateUrl(updatedCertificate.getCertificateUrl());

        // Perform the update operation in the repository
        boolean success = repository.updateCertificate(existingCertificate);

        if (success) {
            // Create the JSON response for the update confirmation
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Certificate updated successfully")
                    .add("data", Json.createObjectBuilder()
                            .add("id", existingCertificate.getId())
                            .add("title", existingCertificate.getTitle())
                            .add("provider", existingCertificate.getProvider())
                            .add("issueDate", existingCertificate.getIssueDate())
                            .add("description", existingCertificate.getDescription())
                            .add("certificateUrl", existingCertificate.getCertificateUrl())
                            .build())
                    .build();

            // Return the response with the update confirmation
            return Response.ok(jsonResponse).build();
        } else {
            // Return an internal server error response if the update fails
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Failed to update the certificate")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }
    }

    @GET
    @Path("{certificateId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCertificateById(@Context HttpHeaders headers, @PathParam("certificateId") int certificateId) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Extract profile ID from the token
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

        Certificate certificate = repository.getCertificateById(certificateId, profileId);
        // Check if the certificate exists
        if (certificate == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Certificate with ID " + certificateId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Create the JSON response for the certificate
        JsonObject certificateJson = Json.createObjectBuilder()
            .add("id", certificate.getId())
            .add("title", certificate.getTitle())
            .add("provider", certificate.getProvider())
            .add("issueDate", certificate.getIssueDate())
            .add("description", certificate.getDescription())
            .add("certificateUrl", certificate.getCertificateUrl())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Success")
            .add("data", certificateJson)
            .add("status_code", 200)
            .build();

        // Return the response with the certificate details
        return Response.ok(jsonResponse).build();
    }

    @DELETE
    @Path("{certificateId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCertificate(@Context HttpHeaders headers, @PathParam("certificateId") int certificateId) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Fetch the certificate by its ID
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

        Certificate existingCertificate = repository.getCertificateById(certificateId, profileId);

        // Check if the certificate exists
        if (existingCertificate == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Certificate with ID " + certificateId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Perform the deletion in the database
        boolean success = repository.deleteCertificate(certificateId);

        if (success) {
            // Create the JSON response for the deletion confirmation
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Certificate deleted successfully")
                    .build();

            // Return the response with the deletion confirmation
            return Response.ok(jsonResponse).build();
        } else {
            // Return an internal server error response if the deletion fails
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Failed to delete the certificate")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }
    }

    // Other methods can be similarly updated

    // Helper methods for token extraction and validation

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
