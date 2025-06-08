package com.auction.mapper;

import com.auction.dto.CategoryDTO;
import com.auction.model.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getSubcategories() != null ?
                        category.getSubcategories().stream()
                                .map(Category::getId)
                                .collect(Collectors.toList()) :
                        new ArrayList<>()
        );
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }
}