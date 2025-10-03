package com.github.renancvitor.inventory.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.renancvitor.inventory.domain.entity.user.User;
import com.github.renancvitor.inventory.dto.person.PersonDetailData;
import com.github.renancvitor.inventory.dto.person.PersonListiningData;
import com.github.renancvitor.inventory.dto.person.PersonUserCreationData;
import com.github.renancvitor.inventory.service.PersonService;
import com.github.renancvitor.inventory.util.CustomPage;
import com.github.renancvitor.inventory.util.PageMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<CustomPage<PersonListiningData>> list(@RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = ("personName")) Pageable pageable,
            @AuthenticationPrincipal User loggedInUser) {
        var page = personService.list(pageable, loggedInUser, active);
        return ResponseEntity.ok(PageMapper.toCustomPage(page));
    }

    @PostMapping
    public ResponseEntity<PersonDetailData> create(
            @RequestBody @Valid PersonUserCreationData creationData,
            UriComponentsBuilder uriComponentsBuilder, @AuthenticationPrincipal User loggedInUser) {
        PersonDetailData personDetailData = personService.create(creationData.person(),
                creationData.user(),
                loggedInUser);
        URI uri = uriComponentsBuilder.path("/person/{id}")
                .buildAndExpand(personDetailData.id())
                .toUri();

        return ResponseEntity.created(uri).body(personDetailData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User loggedInUser) {
        personService.delete(id, loggedInUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id, @AuthenticationPrincipal User loggedInUser) {
        personService.activate(id, loggedInUser);
        return ResponseEntity.noContent().build();
    }

}
