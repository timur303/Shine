package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import kg.kadyrbekov.dto.UserResponse;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header")
})
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ADMIN')")
@Api(tags = "Admin API")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/manager")
    @ApiOperation("Assign Manager Role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Manager role assigned successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public ResponseEntity<Void> assignManagerRole(@RequestParam("email") String userEmail) throws NotFoundException {
        adminService.givesRoles(userEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/block")
    @ApiOperation("Block User")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User blocked successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public String blockUser(@PathVariable Long userId) throws NotFoundException {
        adminService.blockUser(userId);
        return "Successful blocked " + userId;
    }

    @PostMapping("/{userId}/unblock")
    @ApiOperation("Unblock User")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User unblocked successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public String unblockUser(@PathVariable Long userId) throws NotFoundException {
        adminService.unblockUser(userId);
        return "Successful unblocked " + userId;
    }

    @GetMapping("/getUser/{userId}")
    @ApiOperation("Get User by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved the user"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        User user = adminService.getUserById(userId);

        if (user != null) {
            UserResponse response = new UserResponse();
//            response.setFirstName(user.getFirstName());
//            response.setLastName(user.getLastName());
//            response.setEmail(user.getEmail());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    @ApiOperation("Delete User by ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public String delete(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return "Successful removed " + id;
    }
}
