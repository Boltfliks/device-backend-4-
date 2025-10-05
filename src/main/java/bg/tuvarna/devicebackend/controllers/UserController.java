package bg.tuvarna.devicebackend.controllers;

import bg.tuvarna.devicebackend.controllers.execptions.ErrorResponse;
import bg.tuvarna.devicebackend.models.dtos.*;
import bg.tuvarna.devicebackend.models.entities.User;
import bg.tuvarna.devicebackend.services.UserService;
import bg.tuvarna.devicebackend.utils.CustomPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register user.",
            description = "Body of user should have an unique and not blank email and phone number, " +
                    "password should not be blank as well.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully, register the profile."),
            @ApiResponse(responseCode = "400", description = "Email already registered or phone already registered.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/registration")
    public ResponseEntity<Void> registration(@RequestBody @Valid UserCreateVO userRegistration) {
        userService.register(userRegistration);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Login user.",
            description = "User can log in with email/phone and password. " +
                    "If user is already logged in, it will return the user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserVO.class)),
                    headers = {
                            @Header(name = "Authorization", description = "Bearer \"token\"")})
    })
    @PostMapping("/login")
    public ResponseEntity<UserVO> userLogin(@RequestBody(required = false) UserLoginDTO dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserVO(user));
    }

    @Operation(summary = "Returns users.",
            description = "Returns users based on search.")
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomPage<UserListing>> getUsers(@RequestParam(required = false) String searchBy,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getUsers(searchBy, page, size));
    }

    @Operation(summary = "Update user.",
            description = "Update user.")
    @PutMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserUpdateVO userUpdateVO) {
        userService.updateUser(userUpdateVO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update password.",
            description = "Update password.")
    @PutMapping("/changePassword")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changePassword(@RequestHeader("userId") Long userId, @RequestBody @Valid ChangePasswordVO changePasswordVO) {
        userService.updatePassword(userId, changePasswordVO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Login user.",
            description = "it will returns the user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserVO.class)))
    })
    @GetMapping("/getUser")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserVO> getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserVO(user));
    }
}