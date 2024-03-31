package ctu.cit;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/users")
public class UserRespone {

    private UserResponsitory repository = new UserResponsitory();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User user) {
        if (repository.getUserByEmail(user.getEmail()) != null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Email already taken")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Insert user
        repository.insertUser(user);

        // Retrieve the newly created user
        User newUser = repository.getUserByEmail(user.getEmail());

        // Insert profile
        Profile profile = new Profile();
        profile.setUser(newUser); // Set the user for the profile
        profile.setName(newUser.getName());
        profile.setEmail(newUser.getEmail());
        profile.setImage("default-image-url"); // You can set a default image URL here
        profile.setGender(true); // Assuming true represents male and false represents female
        profile.setLocation("Your default location"); // Set the default location
        profile.setWebsite("Your default website"); // Set the default website

        repository.insertProfile(profile);

        String token = Jwts.builder()
                .setSubject(newUser.getId() + "_" + newUser.getEmail()) // Combine ID and email into one string
                .signWith(SignatureAlgorithm.HS256, "your-secret-key")
                .compact();
        JsonObject userJson = Json.createObjectBuilder()
                .add("name", newUser.getName())
                .add("email", newUser.getEmail())
                .add("id", newUser.getId())
                .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
        		.add("access_token", token)
                .add("token_type", "Bearer")
                .add("user", userJson)
                .add("status_code", 200)
                .add("message", "Registration successful.")
                .build();

        return Response.ok().entity(jsonResponse).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(User user) {
        User existingUser = repository.getUserByEmail(user.getEmail());

        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Invalid email or password")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Generate a token
        String token = generateToken(user.getEmail());
        
   

        JsonObject jsonResponse = Json.createObjectBuilder()
        		 .add("success", true)
                 .add("access_token", token)
                 .add("status_code", 200)
                 .add("token_type", "Bearer")
                 .build();

        return Response.ok().entity(jsonResponse).build();
    }

    private String generateToken(String email) {
        // Implement your token generation logic here
        // ...
        // Example code using JWT
        String token = Jwts.builder()
                .setSubject(email)
                .signWith(SignatureAlgorithm.HS256, "your-secret-key")
                .compact();
        return token;
    }
}
