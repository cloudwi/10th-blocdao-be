package com.blocdao.project.service;

import com.blocdao.project.dto.member.request.MemberSingupRequestDto;
import com.blocdao.project.dto.member.response.MemberProfileResponseDto;
import com.blocdao.project.entity.Member;
import com.blocdao.project.entity.MemberStack;
import com.blocdao.project.entity.Stack;
import com.blocdao.project.exception.CustomException;
import com.blocdao.project.exception.ErrorCode;
import com.blocdao.project.repository.MemberRepository;
import com.blocdao.project.repository.MemberStackRepository;
import com.blocdao.project.repository.StackRepository;
import com.blocdao.project.util.RequestUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final FirebaseAuth firebaseAuth;
    private final MemberRepository memberRepository;
    private final StackRepository stackRepository;
    private final MemberStackRepository memberStackRepository;

    @Transactional
    public ResponseEntity<String> signup(MemberSingupRequestDto memberSingupRequestDto, String header) {

        String token = RequestUtil.getAuthorizationToken(header);
        Member member;
        if(token.startsWith("testToken")){
            log.info("memberservice : testToken");
            member = new Member(memberSingupRequestDto, token);
        }
        else {
            log.info("memberservice : Not testToken");
            FirebaseToken decodedToken = verifyToken(token);

            validateAlreadyRegistered(decodedToken.getUid());

            member = new Member(memberSingupRequestDto, decodedToken.getUid());
        }

        createMemberStack(memberSingupRequestDto.getStacks(), member);

        memberRepository.save(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(member.getNickName());
    }

    // ????????? ????????? ???????????? UserDetails??? ???????????????
    @Transactional
    public ResponseEntity<String> signupMock(MemberSingupRequestDto memberSingupRequestDto, String header) {

        String token = RequestUtil.getAuthorizationToken(header);
        Member member;
        if(token.startsWith("testToken")){
            log.info("memberservice : testToken");
            member = new Member(memberSingupRequestDto, token);
        }
        else {
            log.info("memberservice : Not testToken");
            FirebaseToken decodedToken = verifyToken(token);

            validateAlreadyRegistered(decodedToken.getUid());

            member = new Member(memberSingupRequestDto, decodedToken.getUid());
        }

        createMemberStack(memberSingupRequestDto.getStacks(), member);

        memberRepository.save(member);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(member.getNickName());
    }

    //????????? ??????
    private void validateAlreadyRegistered(String uid) {
        Optional<Member> optionalMember = memberRepository.findById(uid);
        if (optionalMember.isPresent()) {
            throw new CustomException(ErrorCode.EXIST_MEMBER);
        }
    }

    private FirebaseToken verifyToken(String token) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            return decodedToken;
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String uid) {
        return memberRepository.findById(uid)
                .orElseThrow(() -> {
                    // ????????? ?????? CustomException?????? ????????? 404??? ?????? 403??? ???????????? ???????????????
                    // ???????????? ????????? ?????????
                    throw new UsernameNotFoundException("?????? ????????? ???????????? ????????????.");
                });
    }

    public void createMemberStack(List<String> stacks, Member member) {
        stacks.forEach(
            stackName -> {
                Stack findStack = stackRepository.findByName(stackName)
                        .orElseThrow(() -> {
                            throw new CustomException(ErrorCode.NOT_FOUND_STACK);
                        });

            MemberStack memberStack = MemberStack.builder()
                    .member(member)
                    .stack(findStack).build();

            memberStackRepository.save(memberStack);
        });
    }

    public ResponseEntity<MemberProfileResponseDto> profile(Member principal) {
        MemberProfileResponseDto memberProfileResponseDto = new MemberProfileResponseDto(principal);

        return ResponseEntity
                .ok(memberProfileResponseDto);
    }

    public ResponseEntity<String> login(Member member) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(member.getNickName());
    }
}
