package com.blocdao.project.entity;

import com.blocdao.project.entity.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Enumerated(STRING)
    private RecruitmentType recruitmentType;

    private Integer recruitmentNumber;

    private Boolean isOnline;

    private Integer period;

    private LocalDate expectedStartDate;

    private String contact;

    private Boolean isRecruitment;

    private Integer view;

    private String address;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
