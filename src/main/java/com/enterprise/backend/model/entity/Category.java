package com.enterprise.backend.model.entity;

import com.enterprise.backend.service.converter.ObjectConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Category extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;
    private String name;
    private Integer priority;

    @Lob
    @Convert(converter = ObjectConverter.class)
    private List<String> imagesUrl;

    @Lob
    private String description;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
    private Set<Product> products;

}
