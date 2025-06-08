package com.auction.controller;

import com.auction.dto.CategoryDTO;
import com.auction.model.Category;
import com.auction.service.IAuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Endpoints for managing categories")
public class CategoryController {
    private final IAuctionService auctionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        if (categoryDTO.getParentId() != null) {
            category.setParent(auctionService.getCategoryById(categoryDTO.getParentId()));
        }
        
        auctionService.addCategory(category);
        return new ResponseEntity<>(convertToDTO(category), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        Category category = auctionService.getCategoryById(id);
        return category != null ? ResponseEntity.ok(convertToDTO(category)) 
                              : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = auctionService.getAllCategories().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        Category category = auctionService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        
        category.setName(categoryDTO.getName());
        if (categoryDTO.getParentId() != null) {
            category.setParent(auctionService.getCategoryById(categoryDTO.getParentId()));
        }
        
        auctionService.updateCategory(category);
        return ResponseEntity.ok(convertToDTO(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Category category = auctionService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        
        auctionService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }
        if (category.getSubcategories() != null) {
            dto.setSubcategoryIds(category.getSubcategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}