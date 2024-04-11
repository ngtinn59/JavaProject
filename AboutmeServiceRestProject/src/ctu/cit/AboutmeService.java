package ctu.cit;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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

@Path("/aboutme")
public class AboutmeService {

    // Sử dụng ConcurrentHashMap để lưu trữ dữ liệu cache với key là profileId và value là danh sách Aboutme
    private ConcurrentMap<Integer, List<Aboutme>> cache = new ConcurrentHashMap<>();
    private AboutmeRepository repository = new AboutmeRepository();
    public static class Cache {
        private static ConcurrentMap<Integer, List<Aboutme>> cache = new ConcurrentHashMap<>();

        public static List<Aboutme> get(int profileId) {
            return cache.get(profileId);
        }

        public static void put(int profileId, List<Aboutme> aboutmes) {
            cache.put(profileId, aboutmes);
        }

        public static void invalidate(int profileId) {
            cache.remove(profileId);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAboutmeByToken(@Context HttpHeaders headers) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Unauthorized access")
                                         .build())
                           .build();
        }

        int profileId = extractIdFromToken(token);
        if (profileId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Invalid token: No profile ID found")
                                         .build())
                           .build();
        }

        // Kiểm tra cache trước khi truy vấn CSDL
        List<Aboutme> cachedAboutmes = Cache.get(profileId); // Sử dụng lớp Cache cục bộ
        if (cachedAboutmes != null) {
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            cachedAboutmes.forEach(aboutme -> jsonArrayBuilder.add(Json.createObjectBuilder()
                                                           .add("id", aboutme.getId())
                                                           .add("description", aboutme.getDescription())));

            JsonObject jsonResponse = Json.createObjectBuilder()
                                       .add("success", true)
                                       .add("data", jsonArrayBuilder.build())
                                       .add("cached", true) // Thêm thông tin này để chỉ ra rằng dữ liệu được lấy từ cache
                                       .build();

            return Response.ok(jsonResponse).build();
        } else {
            // Nếu không có trong cache, truy vấn CSDL
            List<Aboutme> aboutmes = repository.getAllAboutmeByProfileId(profileId);
            // Lưu vào cache
            Cache.put(profileId, aboutmes);

            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            aboutmes.forEach(aboutme -> jsonArrayBuilder.add(Json.createObjectBuilder()
                                                           .add("id", aboutme.getId())
                                                           .add("description", aboutme.getDescription())));

            JsonObject jsonResponse = Json.createObjectBuilder()
                                       .add("success", true)
                                       .add("data", jsonArrayBuilder.build()) // Chỉnh sửa thành "data" thay vì "datas"
                                       .add("cached", false) // Thêm thông tin này để chỉ ra rằng dữ liệu không được lấy từ cache
                                       .build();

            return Response.ok(jsonResponse).build();
        }
    }

    // Các phương thức khác không thay đổi

//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response createOrUpdateAboutme(@Context HttpHeaders headers, Aboutme newAboutme) {
//        String token = headers.getHeaderString("Authorization");
//
//        if (token == null || !isValidToken(token)) {
//            return Response.status(Response.Status.UNAUTHORIZED)
//                           .entity(Json.createObjectBuilder()
//                                         .add("error", "Unauthorized access")
//                                         .build())
//                           .build();
//        }
//
//        int profileId = extractIdFromToken(token);
//        if (profileId <= 0) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                           .entity(Json.createObjectBuilder()
//                                         .add("error", "Invalid token: No profile ID found")
//                                         .build())
//                           .build();
//        }
//
//        // Kiểm tra xem có dữ liệu Aboutme đã tồn tại không
//        List<Aboutme> aboutmes = repository.getAboutmeByprofileId(profileId);
//        if (!aboutmes.isEmpty()) {
//            Aboutme existingAboutme = aboutmes.get(0);
//            // Nếu có, cập nhật dữ liệu
//            existingAboutme.setDescription(newAboutme.getDescription());
//            boolean success = repository.updateAboutme(existingAboutme, profileId); // Update the Aboutme
//            if (success) {
//                JsonObject projectJson = Json.createObjectBuilder()
//                                            .add("description", existingAboutme.getDescription())
//                                            .add("profiles_id", profileId)
//                                            .add("id", existingAboutme.getId())
//                                            .build();
//
//                JsonObject jsonResponse = Json.createObjectBuilder()
//                                               .add("success", true)
//                                               .add("message", "Aboutme updated successfully")
//                                               .add("aboutme", projectJson)
//                                               .build();
//
//                return Response.ok(jsonResponse).build();
//            } else {
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                               .entity(Json.createObjectBuilder()
//                                             .add("error", "Failed to update the Aboutme")
//                                             .build())
//                               .build();
//            }
//        } else {
//            // Nếu không, tạo mới dữ liệu
//            Aboutme createdAboutme = repository.insertAboutme(newAboutme, profileId);
//            if (createdAboutme == null) {
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                               .entity(Json.createObjectBuilder()
//                                             .add("error", "Failed to create a new Aboutme")
//                                             .build())
//                               .build();
//            }
//
//            JsonObject projectJson = Json.createObjectBuilder()
//                                        .add("description", createdAboutme.getDescription())
//                                        .add("profiles_id", profileId)
//                                        .add("id", createdAboutme.getId())
//                                        .build();
//
//            JsonObject jsonResponse = Json.createObjectBuilder()
//                                           .add("success", true)
//                                           .add("message", "Aboutme created successfully")
//                                           .add("aboutme", projectJson)
//                                           .build();
//
//            return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
//        }
//    }



    @GET
    @Path("{aboutmeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAboutmeById(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Unauthorized access")
                                         .build())
                           .build();
        }

        int profileId = extractIdFromToken(token);
        if (profileId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Invalid token: No profile ID found")
                                         .build())
                           .build();
        }

        Aboutme aboutme = repository.getAboutmeById(aboutmeId, profileId);
        if (aboutme == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Aboutme with ID " + aboutmeId + " not found")
                                         .build())
                           .build();
        }

        JsonObject aboutmeJson = Json.createObjectBuilder()
                                      .add("id", aboutme.getId())
                                      .add("description", aboutme.getDescription())
                                      .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
                                       .add("success", true)
                                       .add("data", aboutmeJson)
                                       .build();

        return Response.ok(jsonResponse).build();
    }

//    @PUT
//    @Path("{aboutmeId}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateAboutme(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId, Aboutme updatedAboutme) {
//        String token = headers.getHeaderString("Authorization");
//
//        if (token == null || !isValidToken(token)) {
//            return Response.status(Response.Status.UNAUTHORIZED)
//                           .entity(Json.createObjectBuilder()
//                                         .add("error", "Unauthorized access")
//                                         .build())
//                           .build();
//        }
//
//        int profileId = extractIdFromToken(token);
//        if (profileId <= 0) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                           .entity(Json.createObjectBuilder()
//                                         .add("error", "Invalid token: No profile ID found")
//                                         .build())
//                           .build();
//        }
//
//        Aboutme existingAboutme = repository.getAboutmeById(aboutmeId, profileId);
//        if (existingAboutme == null) {
//            return Response.status(Response.Status.NOT_FOUND)
//                           .entity(Json.createObjectBuilder()
//                                         .add("error", "Aboutme with ID " + aboutmeId + " not found")
//                                         .build())
//                           .build();
//        }
//
//        existingAboutme.setDescription(updatedAboutme.getDescription());
//
//        boolean success = repository.updateAboutme(existingAboutme, profileId);
//        if (success) {
//            return Response.ok(Json.createObjectBuilder()
//                                  .add("success", true)
//                                  .add("message", "Aboutme updated successfully")
//                                  .build())
//                           .build();
//        } else {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                           .entity(Json.createObjectBuilder()
//                                         .add("error", "Failed to update the Aboutme")
//                                         .build())
//                           .build();
//        }
//    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrUpdateAboutme(@Context HttpHeaders headers, Aboutme newAboutme) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Unauthorized access")
                                         .build())
                           .build();
        }

        int profileId = extractIdFromToken(token);
        if (profileId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Invalid token: No profile ID found")
                                         .build())
                           .build();
        }

        // Kiểm tra cache trước khi truy vấn CSDL
        List<Aboutme> cachedAboutmes = Cache.get(profileId);
        if (cachedAboutmes != null) {
            // Nếu có trong cache, invalid cache trước khi thêm mới
            Cache.invalidate(profileId);
        }

        // Thêm mới dữ liệu vào CSDL
        Aboutme createdAboutme = repository.insertAboutme(newAboutme, profileId);
        if (createdAboutme == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Failed to create a new Aboutme")
                                         .build())
                           .build();
        }

        // Lưu dữ liệu mới vào cache
        List<Aboutme> aboutmes = repository.getAllAboutmeByProfileId(profileId);
        Cache.put(profileId, aboutmes);

        JsonObject projectJson = Json.createObjectBuilder()
                                    .add("description", createdAboutme.getDescription())
                                    .add("profiles_id", profileId)
                                    .add("id", createdAboutme.getId())
                                    .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
                                       .add("success", true)
                                       .add("message", "Aboutme created successfully")
                                       .add("aboutme", projectJson)
                                       .build();

        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }


    @PUT
    @Path("{aboutmeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAboutme(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId, Aboutme updatedAboutme) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Unauthorized access")
                                         .build())
                           .build();
        }

        int profileId = extractIdFromToken(token);
        if (profileId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Invalid token: No profile ID found")
                                         .build())
                           .build();
        }

        // Kiểm tra cache trước khi cập nhật dữ liệu
        List<Aboutme> cachedAboutmes = Cache.get(profileId);
        if (cachedAboutmes != null) {
            // Nếu có trong cache, invalid cache trước khi cập nhật
            Cache.invalidate(profileId);
        }

        // Cập nhật dữ liệu trong CSDL
        Aboutme existingAboutme = repository.getAboutmeById(aboutmeId, profileId);
        if (existingAboutme == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Aboutme with ID " + aboutmeId + " not found")
                                         .build())
                           .build();
        }

        existingAboutme.setDescription(updatedAboutme.getDescription());

        boolean success = repository.updateAboutme(existingAboutme, profileId);
        if (success) {
            // Sau khi cập nhật thành công, lấy dữ liệu mới từ CSDL và cập nhật vào cache
            List<Aboutme> aboutmes = repository.getAllAboutmeByProfileId(profileId);
            Cache.put(profileId, aboutmes);

            return Response.ok(Json.createObjectBuilder()
                                  .add("success", true)
                                  .add("message", "Aboutme updated successfully")
                                  .build())
                           .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Failed to update the Aboutme")
                                         .build())
                           .build();
        }
    }



    @DELETE
    @Path("{aboutmeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAboutme(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Unauthorized access")
                                         .build())
                           .build();
        }

        int profileId = extractIdFromToken(token);
        if (profileId <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Invalid token: No profile ID found")
                                         .build())
                           .build();
        }

        Aboutme existingAboutme = repository.getAboutmeById(aboutmeId, profileId);
        if (existingAboutme == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Aboutme with ID " + aboutmeId + " not found")
                                         .build())
                           .build();
        }

        boolean success = repository.deleteAboutme(aboutmeId);
        if (success) {
            return Response.ok(Json.createObjectBuilder()
                                  .add("success", true)
                                  .add("message", "Aboutme deleted successfully")
                                  .build())
                           .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Json.createObjectBuilder()
                                         .add("error", "Failed to delete the Aboutme")
                                         .build())
                           .build();
        }
    }

    private int extractIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String tokenString = token.substring(7).trim();
            try {
                Jws<Claims> claims = Jwts.parser()
                                         .setSigningKey("your-secret-key")
                                         .parseClaimsJws(tokenString);
                String subject = claims.getBody().getSubject();
                String[] parts = subject.split("_");
                return Integer.parseInt(parts[0]);
            } catch (JwtException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    private boolean isValidToken(String token) {
        return true; // Implement your token validation logic here
    }
    
   

}
