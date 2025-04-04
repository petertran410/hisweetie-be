package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.service.base.BaseCommonRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends BaseCommonRepository<Category, Long> {

    @Query("SELECT COUNT(c) FROM Category c WHERE c.name = :name AND " +
            "(c.parentId = :parentId OR (c.parentId IS NULL AND :parentId IS NULL))")
    long countByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.priority = :priority AND " +
            "(c.parentId = :parentId OR (c.parentId IS NULL AND :parentId IS NULL))")
    long countByPriorityAndParentId(@Param("priority") Integer priority, @Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.priority = :priority AND " +
            "(c.parentId = :parentId OR (c.parentId IS NULL AND :parentId IS NULL))")
    Optional<Category> findByPriorityAndParentId(@Param("priority") Integer priority, @Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE (c.parentId = :parentId OR (c.parentId IS NULL AND :parentId IS NULL)) " +
            "ORDER BY c.priority ASC")
    List<Category> findByParentIdOrderByPriorityAsc(@Param("parentId") Long parentId);

    List<Category> findByName(String name);
}

