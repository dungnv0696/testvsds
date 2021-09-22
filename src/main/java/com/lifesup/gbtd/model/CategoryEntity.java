package com.lifesup.gbtd.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Integer categoryId;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;
    @Column(name = "CATEGORY_CODE")
    private String categoryCode;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "EDITABLE")
    private Integer editable;
}
