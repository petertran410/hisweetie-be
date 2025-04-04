package com.enterprise.backend.controller;

import com.enterprise.backend.auth.AuthoritiesConstants;
import com.enterprise.backend.model.request.CategoriesRequest;
import com.enterprise.backend.model.request.CategoryRequest;
import com.enterprise.backend.model.response.CategoryResponse;
import com.enterprise.backend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CategoryResponse>> getAllSortByPriority(@RequestParam(value = "parentId", required = false) Long parentId) {
        return ResponseEntity.ok(categoryService.getAllSortByPriority(parentId));
    }

    @GetMapping("/v2/get-all")
    public List<CategoryResponse> getAllSortByPriorityV2(@RequestParam(value = "parentId", required = false) Long parentId) {
        return categoryService.getAllSortByPriorityV2(parentId);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PatchMapping("/{categoryId}")
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody CategoryRequest request,
                                                           @PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.updateCategory(request, categoryId));
    }

    @PatchMapping()
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public void updateCategories(@Valid @RequestBody List<CategoriesRequest> request) {
        categoryService.updateCategories(request);
    }

    @DeleteMapping("/{categoryId}")
    @Transactional
    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.ROLE_SUPER_ADMIN})
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Bearer")})
    public ResponseEntity<String> deleteById(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
