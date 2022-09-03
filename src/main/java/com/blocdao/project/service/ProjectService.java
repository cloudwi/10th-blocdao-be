package com.blocdao.project.service;

import com.blocdao.project.dto.project.request.ProjectRequestDto;
import com.blocdao.project.dto.project.response.ProjectResponseDto;
import com.blocdao.project.dto.projectStacks.request.ProjectStackRequestDto;
import com.blocdao.project.entity.Member;
import com.blocdao.project.entity.Project;
import com.blocdao.project.entity.ProjectStack;
import com.blocdao.project.repository.ProjectRepository;
import com.blocdao.project.repository.ProjectStackRepository;
import com.blocdao.project.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final StackRepository stackRepository;

    private final ProjectStackRepository projectStackRepository;

    public Page<Project> findAllProjectsPage(Pageable pageable) {
        return projectRepository.findAllProjects(pageable);
    }

/*    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto projectRequestDto, Member member) {
        Project project = projectRequestDto.toEntity(projectRequestDto, member);

        // todo: projectStack save 점검
        Project savedProject = projectRepository.save(project);
        return new ProjectResponseDto(savedProject);
    }*/

    /*

     현재 프로젝트 데이터 생성 및 project_stack 테이블에 데이터 입력까지 확인했음.
     테스트 코드를 사용하는 것도 좋지만 postman으로 데이터 전송 후 h2에서 데이터 들어갔는지 까지 확인하는게 좋음.
     postman없이 사용하려면 Controller단으로 Mocking없이 테스트 들어가야함.

    포스트맨 링크에서 회원가입 api 전송 후 h2접속 후 Stack테이블에 stack id : 1, 2로 데이터 생성
    -> 이후 포스트맨에서 게시글 생성 api 전송하면 h2 테이블에 제대로 데이터들이 들어가는 것을 확인할 수 있고

    회원가입의 경우 uid값이 requestBody가 아닌 Authorization: Bearer uid를 통해 들어감
    Swagger로도 테스트 해보고 싶다면 Authorize 정보에 Bearer 123을 넣고 실행하면 된다.
    또한 게시글 생성 시에 회원가입 api 전송시 넣었던 Bearer uid값이 없으면 시큐리티 필터쪽에서
    userDetailsService.loadUserByUsername(header)가 없기 때문에 오류가 남.
    현재 validation 추가를 하진않았지만 게시글 수정 및 삭제시엔 헤더의 토큰값과 Project 테이블의 생성자 id를 확인해서
    validation하는 것이 필요함.

    작업을 하면서 어려웠던 부분이 일단 8기 팀에서 매개변수로 Authentication authentication을 받았는데
    Api 요청시마다 필터쪽에서 확인을 하므로 굳이 필요가 없는 것 같고, post 전송 시 어떤 형식으로 데이터를 땡겨오는지 알 수 없어서
    postman으로 테스트가 안됨. 화요일날 질문 후에 필요하다면 작업 후에 사용하고 일단 8기팀의 필터구조도 커스텀이 들어갔기때문에
    목적과 사용법을 알아야 쓸듯.

    두번째로 현재 작성한 Entity와 Dto로 Swagger에서 기본으로 뜨는 json형식이 api명세서와 너무 달랐음.
    이유는 dto에서 맞춰진 형식대로 나타나는데 아래 코드에서 ProjectStack Entity 클래스가 project, stack 엔티티와
    연관관계가 맺어있어 불필요한 데이터까지 나오게됨 따라서 stacksRequestDto라는 stack추가에 필요한 dto를 생성하고
    이후 아래 참고 1에서 따로 재처리하였음.

    private List<ProjectStack> projectStacks;

    public Project toEntity(ProjectRequestDto projectRequestDto, Member member) {
        return new Project(projectRequestDto, member);
    }

     */
    @Transactional
    public Project createProject(ProjectRequestDto projectRequestDto, UserDetails member) {
        Project project = Project.builder()
                .address("서울시 신림동")
                .createUid("1234")
                .contact(projectRequestDto.getContact())
                .content(projectRequestDto.getContent())
                //.expectedStartDate()
                .isOnline(true)
                .isRecruitment(true)
                .period(projectRequestDto.getPeriod())
                .recruitmentNumber(projectRequestDto.getRecruitmentNumber())
                .recruitmentType(projectRequestDto.getRecruitmentType())
                .title(projectRequestDto.getTitle())
                .view(10)
                .member((Member) member)
                //.projectStacks(new ArrayList<>())
                .build();

        Project savedProject = projectRepository.saveAndFlush(project);

        for(ProjectStackRequestDto projectStackRequestDto : projectRequestDto.getStacksRequestDto()){

            /*
            projectStackRequestDto
              "stacksRequestDto": [
                {
                  "classification": "string",
                  "image": "string",
                  "name": "string",
                  "stackId": 0
                } ]
             */
            // 참고 1
            ProjectStack projectStack = ProjectStack.builder()
                    .stack(stackRepository.findById(projectStackRequestDto.getStackId())
                                    .orElseThrow())
                    .project(savedProject)
                            .build();

            projectStackRepository.save(projectStack);
        }
        // todo: projectStack save 점검
        return savedProject;
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow();
    }
}
