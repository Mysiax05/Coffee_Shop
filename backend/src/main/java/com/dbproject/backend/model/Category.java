package com.dbproject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryid")
    private Integer categoryId;

    @NotBlank
    @Size(max = 200)
    @Column(name = "categoryname", nullable = false, length = 200)
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "parentcategoryid")
    private Category parentCategory;
}
