package com.blocdao.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectsResponseDto {

    private List<ProjectResponseDto> projectResponseDtos;
}