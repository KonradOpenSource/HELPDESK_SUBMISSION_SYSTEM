package com.helpdesk.service;

import com.helpdesk.dto.CategoryDto;
import com.helpdesk.entity.Category;
import com.helpdesk.mapper.CategoryMapper;
import com.helpdesk.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryMapper categoryMapper = new CategoryMapper();

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Technical Support");
        testCategory.setDescription("Technical issues and software problems");

        categoryDto = new CategoryDto(1L, "Technical Support", "Technical issues and software problems", null);
        
        categoryService = new CategoryService(categoryRepository, categoryMapper);
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByName("Technical Support")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDto result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("Technical Support", result.getName());
        assertEquals("Technical issues and software problems", result.getDescription());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_NameExists_ThrowsException() {
        when(categoryRepository.existsByName("Technical Support")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> categoryService.createCategory(categoryDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getAllCategories_Success() {
        List<Category> categories = List.of(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Technical Support", result.get(0).getName());
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        CategoryDto result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Technical Support", result.getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void updateCategory_Success() {
        CategoryDto updatedDto = new CategoryDto(1L, "Updated Support", "Updated description", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Updated Support")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toDto(testCategory)).thenReturn(updatedDto);

        CategoryDto result = categoryService.updateCategory(1L, updatedDto);

        assertNotNull(result);
        assertEquals("Updated Support", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        CategoryDto updatedDto = new CategoryDto(1L, "Updated Support", "Updated description", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, updatedDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_NameExists_ThrowsException() {
        CategoryDto updatedDto = new CategoryDto(1L, "Existing Support", "Updated description", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Existing Support")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, updatedDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(1L);
    }
}
