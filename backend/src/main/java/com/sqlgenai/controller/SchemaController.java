package com.sqlgenai.controller;

import com.sqlgenai.dto.response.SchemaResponse;
import com.sqlgenai.service.SchemaIntrospectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/schema")
public class SchemaController {

    private final SchemaIntrospectionService schemaIntrospectionService;

    public SchemaController(SchemaIntrospectionService schemaIntrospectionService) {
        this.schemaIntrospectionService = schemaIntrospectionService;
    }

    @GetMapping
    public ResponseEntity<SchemaResponse> getSchema(
            @RequestParam(name = "schema", required = false, defaultValue = "public") String schemaName
    ) {
        SchemaResponse response = schemaIntrospectionService.introspectSchema(schemaName);
        return ResponseEntity.ok(response);
    }
}
