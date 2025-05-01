package org.codigo.middleware.mwbooking.api.controller;

import org.codigo.middleware.mwbooking.api.input.class_.*;
import org.codigo.middleware.mwbooking.api.output.class_.*;
import org.codigo.middleware.mwbooking.service.ClassService;
import org.codigo.middleware.mwbooking.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
public class ClassApi {

    private final ClassService classService;

    public ClassApi(ClassService classService) {
        this.classService = classService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClassRegisterResponse>> registerClass(@Validated @RequestBody ClassRegisterRequest classRegisterRequest, BindingResult result) {
        ClassRegisterResponse classRegisterResponse = classService.registerClass(classRegisterRequest);
        return ApiResponse.of(classRegisterResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAvailableClassesByCountry(@RequestParam String country) {
        List<ClassResponse> availableClassesByCountry = classService.getAvailableClassesByCountry(country);
        return ApiResponse.of(availableClassesByCountry);
    }
}
