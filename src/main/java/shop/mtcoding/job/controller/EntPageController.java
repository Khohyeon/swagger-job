package shop.mtcoding.job.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.job.config.aop.EntId;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.entPage.EntPageMyApplicantRespDto;
import shop.mtcoding.job.dto.entPage.EntPageMyBookmarkRespDto;
import shop.mtcoding.job.dto.entPage.EntPageMyRecommendRespDto;
import shop.mtcoding.job.model.apply.ApplyRepository;
import shop.mtcoding.job.model.bookmark.BookmarkRepository;
import shop.mtcoding.job.model.recruitmentSkill.RecruitmentSkillRepository;

@RequiredArgsConstructor
@RestController
@Tag(name = "9. 기업회원 마이페이지", description = "기업회원 정보 제공")
public class EntPageController {

    private final ApplyRepository applyRepository;

    private final RecruitmentSkillRepository recruitmentSkillRepository;

    private final BookmarkRepository bookmarkRepository;

    @GetMapping("/myapplicant")
    @Operation(summary = "1. 지원한 유저", description = "기업에 지원한 유저 정보를 제공합니다.")
    public @ResponseBody ResponseEntity<?> myapplicant(@EntId Integer principalId) {
        List<EntPageMyApplicantRespDto> myApplicantRespDtos = applyRepository
                .findByEnterpriseIdJoinApplyResume(principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "인증 성공", myApplicantRespDtos), HttpStatus.OK);
    }

    @GetMapping("/myrecommend")
    @Operation(summary = "2. 매칭된 유저", description = "기업의 스킬과 같은 유저 정보를 제공합니다.")
    public @ResponseBody ResponseEntity<?> myrecommend(@EntId Integer principalId) {
        List<EntPageMyRecommendRespDto> myrecommendRespDto = recruitmentSkillRepository
                .enterpriseMatching(principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "인증 성공", myrecommendRespDto), HttpStatus.OK);
    }

    @GetMapping("/mybookmarkEnt")
    @Operation(summary = "3. 북마크한 유저", description = "기업에 북마크한 유저 정보를 보여줍니다.")
    public @ResponseBody ResponseEntity<?> mybookmark(@EntId Integer principalId) {
        List<EntPageMyBookmarkRespDto> mybookmarkEntRespDto = bookmarkRepository
                .findByEnterpriseId(principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "인증 성공", mybookmarkEntRespDto), HttpStatus.OK);
    }
}
