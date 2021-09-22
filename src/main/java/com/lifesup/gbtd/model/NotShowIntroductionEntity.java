package com.lifesup.gbtd.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Entity
@Table(name = "NOT_SHOW_INTRODUCTION")
@Getter
@Setter
public class NotShowIntroductionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOT_SHOW_INTRODUCTION_SEQ")
    @SequenceGenerator(name = "NOT_SHOW_INTRODUCTION_SEQ", sequenceName = "NOT_SHOW_INTRODUCTION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "USER_ID")
    private Long userId;
}
