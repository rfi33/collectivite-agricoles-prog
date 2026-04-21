package com.collectivity.controller;

import com.collectivity.dto.request.CreateCollectivityRequest;
import com.collectivity.dto.response.CollectivityResponse;
import com.collectivity.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;

    public CollectivityController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping
    public ResponseEntity<List<CollectivityResponse>> create(
            @RequestBody List<CreateCollectivityRequest> requests) {

        List<CollectivityResponse> responses = collectivityService.createAll(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}