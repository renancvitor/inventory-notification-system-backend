package com.github.renancvitor.inventory.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.dto.user.UserDetailData;
import com.github.renancvitor.inventory.dto.user.UserListiningData;
import com.github.renancvitor.inventory.dto.user.UserPasswordUpdateData;
import com.github.renancvitor.inventory.dto.user.UserTypeUpdateData;
import com.github.renancvitor.inventory.service.UserService;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.util.PageMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<CustomPage<UserListiningData>> list(@RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = ("person.personName")) Pageable pageable,
            @AuthenticationPrincipal User loggedInUser) {
        var page = userService.list(pageable, loggedInUser, active);

        return ResponseEntity.ok(PageMapper.toCustomPage(page));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserDetailData> updatePassword(@PathVariable Long id,
            @RequestBody @Valid UserPasswordUpdateData data, @AuthenticationPrincipal User loggedInUser) {
        UserDetailData userDetailData = userService.updatePassword(id, data, loggedInUser);

        return ResponseEntity.ok(userDetailData);
    }

    @PutMapping("/type/{id}")
    public ResponseEntity<UserDetailData> updateUserType(@PathVariable Long id,
            @RequestBody @Valid UserTypeUpdateData data, @AuthenticationPrincipal User loggedInUser) {
        UserDetailData userDetailData = userService.updateUserType(id, data, loggedInUser);

        return ResponseEntity.ok(userDetailData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User loggedInUser) {
        userService.delete(id, loggedInUser);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id, @AuthenticationPrincipal User loggedInUser) {
        userService.activate(id, loggedInUser);

        return ResponseEntity.noContent().build();
    }

}
