package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.CategoriesRequest;
import com.enterprise.backend.model.request.CategoryRequest;
import com.enterprise.backend.model.response.CategoryResponse;
import com.enterprise.backend.service.base.BaseService;
import com.enterprise.backend.service.repository.CategoryRepository;
import com.enterprise.backend.service.transfomer.CategoryTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService extends BaseService<Category, Long, CategoryRepository, CategoryTransformer, CategoryRequest, CategoryResponse> {
    private final CategoryTransformer categoryTransformer;

    protected CategoryService(CategoryRepository repo,
                              CategoryTransformer transformer,
                              EntityManager em,
                              CategoryTransformer categoryTransformer) {
        super(repo, transformer, em);
        this.categoryTransformer = categoryTransformer;
    }

    public List<Category> findByName(String name) {
        return Optional.ofNullable(repo.findByName(name))
                .filter(list -> !CollectionUtils.isEmpty(list))
                .orElse(new ArrayList<>());
    }

    private void validateName(CategoryRequest categoryRequest) {
        if (repo.countByNameAndParentId(categoryRequest.getName(), categoryRequest.getParentId()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.CATEGORY_CONFLICT);
        }
    }

    private void validatePriority(CategoryRequest categoryRequest) {
        if (repo.countByPriorityAndParentId(categoryRequest.getPriority(), categoryRequest.getParentId()) > 0) {
            throw new EnterpriseBackendException(ErrorCode.CONFLICT_PRIORITY);
        }
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        validateName(categoryRequest);
        validatePriority(categoryRequest);

        Category category = transformer.toEntity(categoryRequest);
        if (categoryRequest.getParentId() != null) {
            getOrElseThrow(categoryRequest.getParentId(), "Danh mục cha không tồn tại!");
        }
        return transformer.toResponse(repo.save(category));
    }

    @Transactional
    public void updateCategories(List<CategoriesRequest> requests) {
        requests.forEach(request -> {
            Category category = getOrElseThrow(request.getId());
            if (!Objects.equals(category.getPriority(), request.getPriority())
                    && ObjectUtils.isNotEmpty(request.getPriority())) {
                repo.findByPriorityAndParentId(request.getPriority(), category.getParentId())
                        .ifPresent(categoryReplace -> {
                            categoryReplace.setPriority(category.getPriority());
                            repo.save(categoryReplace);
                        });
            }
            BeanUtils.copyProperties(request, category);
            repo.save(category);
        });
    }

    @Transactional
    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long id) {
        Category category = getOrElseThrow(id);

        if (categoryRequest.getParentId() != null) {
            throw new EnterpriseBackendException(ErrorCode.BAD_REQUEST, "Danh mục cha không được phép thay đổi!");
        }

        categoryRequest.setParentId(category.getParentId());

        if (!(category.getName().equals(categoryRequest.getName()))) {
            validateName(categoryRequest);
        }

        if (!Objects.equals(category.getPriority(), categoryRequest.getPriority())
                && ObjectUtils.isNotEmpty(categoryRequest.getPriority())) {
            repo.findByPriorityAndParentId(categoryRequest.getPriority(), categoryRequest.getParentId())
                    .ifPresent(categoryReplace -> {
                        categoryReplace.setPriority(category.getPriority());
                        repo.save(categoryReplace);
                    });
        }

        BeanUtils.copyProperties(categoryRequest, category);
        return transformer.toResponse(repo.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getOrElseThrow(id);
        List<Category> children = repo.findByParentIdOrderByPriorityAsc(category.getId());
        children.forEach(child -> deleteCategory(child.getId()));
        repo.delete(category);
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = getOrElseThrow(categoryId);
        var response = categoryTransformer.toResponse(category);

        if (category.getParentId() != null) {
            var categoryParent = getOrElseThrow(category.getParentId());
            response.setParentName(categoryParent.getName());
        }

        return response;
    }

    public List<CategoryResponse> getAllSortByPriority(Long parentId) {
        return repo.findByParentIdOrderByPriorityAsc(parentId)
                .stream()
                .map(category -> {
                    var categoryResponse = categoryTransformer.toResponse(category);
                    var children = getAllSortByPriority(categoryResponse.getId());
                    children.forEach(child -> child.setParentName(category.getName()));
                    categoryResponse.setChildren(children);
                    return categoryResponse;
                })
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getAllSortByPriorityV2(Long parentId) {
        var category = getAllSortByPriority(parentId);
        var result = new ArrayList<>(category);
        buildResult(category, result);
        return result;
    }

    private void buildResult(List<CategoryResponse> category, List<CategoryResponse> result) {
        category.forEach(c -> {
            result.addAll(c.getChildren());
            buildResult(c.getChildren(), result);
        });
    }

    @Override
    protected String notFoundMessage() {
        return "Not found category";
    }
}
