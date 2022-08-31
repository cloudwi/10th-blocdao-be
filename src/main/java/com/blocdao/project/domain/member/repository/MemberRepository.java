package com.blocdao.project.domain.member.repository;

import com.blocdao.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}