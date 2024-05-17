package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.annotations.UserAccess;
import com.devcommunity.infyStack.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { GET, POST, PUT, DELETE }, allowCredentials = "true")
@RequestMapping("/path")
public class PathController {

    private final RequestMappingHandlerMapping requestMapping;

    @Autowired
    public PathController(RequestMappingHandlerMapping requestMapping){
        this.requestMapping = requestMapping;
    }

    @UserAccess(value = UserRole.ADMIN)
    @GetMapping("/getAll")
    public ResponseEntity<?> getPaths() {
        Map<String, String> paths = new HashMap<>();
        Map<RequestMappingInfo, HandlerMethod> mappings = requestMapping.getHandlerMethods();

        for (RequestMappingInfo mapping : mappings.keySet()) {

            HandlerMethod handlerMethod = mappings.get(mapping);
            String methodName = handlerMethod.getMethod().getName();
            assert mapping.getPathPatternsCondition() != null;
            String path = Objects.requireNonNull(mapping.getPathPatternsCondition()
                            .getPatterns()
                            .stream()
                            .findFirst()
                            .orElse(null)).toString();

            paths.put(methodName, path);
        }

        return ResponseEntity.ok(paths);
    }
}
