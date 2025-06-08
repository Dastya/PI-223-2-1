package com.auction.mapper;

import com.auction.dto.CategoryDTO;
import com.auction.model.Category;

public class CategoryMapper {
    public static CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getParent() != null ? category.getParent().getId() : null);
    }

    public static Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }
}