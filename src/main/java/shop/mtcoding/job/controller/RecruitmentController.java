package shop.mtcoding.job.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.job.config.aop.EntId;
import shop.mtcoding.job.config.aop.UserId;
import shop.mtcoding.job.config.auth.LoginEnt;
import shop.mtcoding.job.config.auth.LoginUser;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.bookmark.BookmartRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostAndSkillUpdateRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostDetailDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostReqDto.SaveRecruitmentPostReqDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostReqDto.UpdateRecruitmentPostReqDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostCategoryRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostDetailRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostListRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostSearchRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostSkillRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentUpdateRespDto.RecruitmentPostSkillUpdateRespDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentUpdateRespDto.RecruitmentPostUpdateRespDto;
import shop.mtcoding.job.handler.exception.CustomApiException;
import shop.mtcoding.job.handler.exception.CustomException;
import shop.mtcoding.job.model.bookmark.BookmarkRepository;
import shop.mtcoding.job.model.recruitmentPost.RecruitmentPost;
import shop.mtcoding.job.model.recruitmentPost.RecruitmentPostRepository;
import shop.mtcoding.job.model.recruitmentSkill.RecruitmentSkillRepository;
import shop.mtcoding.job.model.resume.Resume;
import shop.mtcoding.job.model.resume.ResumeRepository;
import shop.mtcoding.job.service.RecruitmentService;

@RequiredArgsConstructor
@RestController
@Tag(name = "8. 채용공고", description = "채용공고 명세서")
public class RecruitmentController {
    private final BookmarkRepository bookmarkRepository;

    private final RecruitmentPostRepository recruitmentPostRepository;

    private final RecruitmentSkillRepository recruitmentSkillRepository;

    private final RecruitmentService recruitmentService;

    private final HttpSession session;

    private final ResumeRepository resumeRepository;

    @DeleteMapping("/recruitment/delete/{id}")
    @Operation(summary = "5. 채용공고 삭제", description = "채용공고를 삭제합니다.")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable int id, @EntId Integer principalId) {
        recruitmentService.채용공고삭제(id, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "채용공고 삭제 성공", null), HttpStatus.OK);
    }

    @PutMapping("/recruitment/{id}")
    @Operation(summary = "3. 채용공고 수정", description = "채용공고를 수정합니다.")
    public @ResponseBody ResponseEntity<?> updateRecruitmentPost(@PathVariable int id,
            @RequestBody UpdateRecruitmentPostReqDto updateRecruitmentPostReqDto, @EntId Integer principalId) {
        LoginEnt loginEnt = (LoginEnt) session.getAttribute("loginEnt");
        if (loginEnt == null) {
            throw new CustomApiException("로그인을 먼저 해주세요", HttpStatus.UNAUTHORIZED);
        }
        if (updateRecruitmentPostReqDto.getTitle() == null || updateRecruitmentPostReqDto.getTitle().isEmpty()) {
            throw new CustomApiException("제목을 작성해주세요");
        }
        if (updateRecruitmentPostReqDto.getTitle().length() > 100) {
            throw new CustomApiException("제목의 길이가 100자 이하여야 합니다");
        }
        if (updateRecruitmentPostReqDto.getCareer() == null || updateRecruitmentPostReqDto.getCareer().isEmpty()
                || updateRecruitmentPostReqDto.getCareer().equals("Open this select menu")) {
            throw new CustomApiException("경력을 선택해주세요");
        }
        if (updateRecruitmentPostReqDto.getEducation() == null || updateRecruitmentPostReqDto.getEducation().isEmpty()
                || updateRecruitmentPostReqDto.getEducation().equals("Open this select menu")) {
            throw new CustomApiException("학력를 선택해주세요");
        }
        if (updateRecruitmentPostReqDto.getPay() == null || updateRecruitmentPostReqDto.getPay().isEmpty()) {
            throw new CustomApiException("급여란을 작성해주세요");
        }
        if (updateRecruitmentPostReqDto.getPay().length() > 100) {
            throw new CustomApiException("급여의 길이가 100자 이하여야 합니다");
        }
        if (updateRecruitmentPostReqDto.getSector() == null || updateRecruitmentPostReqDto.getSector().isEmpty()
                || updateRecruitmentPostReqDto.getSector().equals("Open this select menu")) {
            throw new CustomApiException("기업형태를 선택해주세요");
        }
        if (updateRecruitmentPostReqDto.getPosition() == null || updateRecruitmentPostReqDto.getPosition().isEmpty()
                || updateRecruitmentPostReqDto.getPosition().equals("Open this select menu")) {
            throw new CustomApiException("희망포지션을 선택해주세요");
        }
        if (updateRecruitmentPostReqDto.getAddress() == null || updateRecruitmentPostReqDto.getAddress().isEmpty()) {
            throw new CustomApiException("근무지역을 작성해주세요");
        }
        if (updateRecruitmentPostReqDto.getAddress().length() > 100) {
            throw new CustomApiException("근무지역의 길이가 100자 이하여야 합니다");
        }
        if (updateRecruitmentPostReqDto.getContent() == null || updateRecruitmentPostReqDto.getContent().isEmpty()) {
            throw new CustomApiException("채용공고 내용을 작성해주세요");
        }
        if (updateRecruitmentPostReqDto.getEnterpriseLogo() == null) {
            throw new CustomApiException("로고 사진을 선택해주세요");
        }
        if (updateRecruitmentPostReqDto.getDeadline() == null || updateRecruitmentPostReqDto.getDeadline().isEmpty()) {
            throw new CustomApiException("마감기한을 설정해주세요");
        }

        LocalDate deadline = LocalDate.parse(updateRecruitmentPostReqDto.getDeadline());
        LocalDate today = LocalDate.now();
        if (deadline.isBefore(today)) {
            throw new CustomApiException("과거는 선택 할 수 없습니다.");
        }

        recruitmentService.채용공고수정(id, updateRecruitmentPostReqDto, principalId);

        return new ResponseEntity<>(new ResponseDto<>(1, "채용공고 수정 성공", null), HttpStatus.CREATED);
    }

    @PostMapping("/recruitment")
    @Operation(summary = "1. 채용공고 작성", description = "채용공고를 작성합니다.")
    public @ResponseBody ResponseEntity<?> saveRecruitmentPost(
            @RequestBody SaveRecruitmentPostReqDto saveRecruitmentPostReqDto, @EntId Integer principalId) {
        LoginEnt loginEnt = (LoginEnt) session.getAttribute("loginEnt");
        if (loginEnt == null) {
            throw new CustomApiException("로그인을 먼저 해주세요", HttpStatus.UNAUTHORIZED);
        }
        if (saveRecruitmentPostReqDto.getTitle() == null || saveRecruitmentPostReqDto.getTitle().isEmpty()) {
            throw new CustomApiException("제목을 작성해주세요");
        }
        if (saveRecruitmentPostReqDto.getTitle().length() > 100) {
            throw new CustomApiException("제목의 길이가 100자 이하여야 합니다");
        }
        if (saveRecruitmentPostReqDto.getCareer() == null || saveRecruitmentPostReqDto.getCareer().isEmpty()
                || saveRecruitmentPostReqDto.getCareer().equals("Open this select menu")) {
            throw new CustomApiException("경력을 선택해주세요");
        }
        if (saveRecruitmentPostReqDto.getEducation() == null || saveRecruitmentPostReqDto.getEducation().isEmpty()
                || saveRecruitmentPostReqDto.getEducation().equals("Open this select menu")) {
            throw new CustomApiException("학력를 선택해주세요");
        }
        if (saveRecruitmentPostReqDto.getPay() == null || saveRecruitmentPostReqDto.getPay().isEmpty()) {
            throw new CustomApiException("급여란을 작성해주세요");
        }
        if (saveRecruitmentPostReqDto.getPay().length() > 100) {
            throw new CustomApiException("급여의 길이가 100자 이하여야 합니다");
        }
        if (saveRecruitmentPostReqDto.getSector() == null || saveRecruitmentPostReqDto.getSector().isEmpty()
                || saveRecruitmentPostReqDto.getSector().equals("Open this select menu")) {
            throw new CustomApiException("기업형태를 선택해주세요");
        }
        if (saveRecruitmentPostReqDto.getPosition() == null || saveRecruitmentPostReqDto.getPosition().isEmpty()
                || saveRecruitmentPostReqDto.getPosition().equals("Open this select menu")) {
            throw new CustomApiException("희망포지션을 선택해주세요");
        }
        if (saveRecruitmentPostReqDto.getAddress() == null || saveRecruitmentPostReqDto.getAddress().isEmpty()) {
            throw new CustomApiException("근무지역을 작성해주세요");
        }
        if (saveRecruitmentPostReqDto.getAddress().length() > 100) {
            throw new CustomApiException("근무지역의 길이가 100자 이하여야 합니다");
        }
        if (saveRecruitmentPostReqDto.getContent() == null || saveRecruitmentPostReqDto.getContent().isEmpty()) {
            throw new CustomApiException("채용공고 내용을 작성해주세요");
        }
        if (saveRecruitmentPostReqDto.getEnterpriseLogo() == null) {
            throw new CustomApiException("로고 사진을 선택해주세요");
        }
        if (saveRecruitmentPostReqDto.getDeadline() == null || saveRecruitmentPostReqDto.getDeadline().isEmpty()) {
            throw new CustomApiException("마감기한을 설정해주세요");
        }

        LocalDate deadline = LocalDate.parse(saveRecruitmentPostReqDto.getDeadline());
        LocalDate today = LocalDate.now();
        if (deadline.isBefore(today)) {
            throw new CustomApiException("과거는 선택 할 수 없습니다.");
        }

        recruitmentService.채용공고쓰기(saveRecruitmentPostReqDto, principalId);

        return new ResponseEntity<>(new ResponseDto<>(1, "채용공고 작성 성공", null), HttpStatus.CREATED);
    }

//    @GetMapping("/recruitment/saveForm")
//    @Operation(summary = "2. 채용공고 작성페이지", description = "채용공고의 작성페이지 입니다.")
//    public String recruitmentSaveForm() {
//        LoginEnt loginEnt = (LoginEnt) session.getAttribute("loginEnt");
//        if (loginEnt == null) {
//            throw new CustomException("기업회원으로 로그인을 해주세요", HttpStatus.UNAUTHORIZED);
//        }
//        return "recruitment/saveForm";
//    }

//    @GetMapping("/recruitment/{id}/updateForm")
//    @Operation(summary = "5. 채용공고 수정페이지", description = "채용공고를 수정하는 페이지입니다.")
//    public ResponseEntity<?> recruitmentUpdateForm(@PathVariable int id, @EntId Integer principalId) {
//        if (principalId == null) {
//            throw new CustomException("기업회원으로 로그인을 해주세요", HttpStatus.UNAUTHORIZED);
//        }
//        RecruitmentPost recruitmentPS = recruitmentPostRepository.findById(id);
//        if (recruitmentPS == null) {
//            throw new CustomException("없는 채용공고를 수정할 수 없습니다");
//        }
//        if (recruitmentPS.getEnterpriseId() != principalId) {
//            throw new CustomException("채용공고를 수정할 권한이 없습니다", HttpStatus.FORBIDDEN);
//        }
//
//        RecruitmentPostUpdateRespDto recruitmentPostRespDto = recruitmentPostRepository
//                .findPostById(id);
//        List<RecruitmentPostSkillUpdateRespDto> recruitmentPostSkillRespDto = recruitmentPostRepository
//                .findSkillById(id);
//
//        RecruitmentPostAndSkillUpdateRespDto recruitmentPostAndSkillUpdateRespDto = new RecruitmentPostAndSkillUpdateRespDto(
//                recruitmentPostRespDto, recruitmentPostSkillRespDto);
//        return new ResponseEntity<>(new ResponseDto<>(1, "채용공고 수정 화면", recruitmentPostAndSkillUpdateRespDto),
//                HttpStatus.OK);
//    }
//
    @GetMapping("/ns/recruitment/detail/{id}")
    @Operation(summary = "4. 채용공고 상세보기 페이지", description = "채용공고의 상세보기 페이지입니다.")
    public ResponseEntity<?> recruitmentPostDetail(@PathVariable int id, @UserId Integer principalId) {
        LoginUser principal = (LoginUser) session.getAttribute("loginUser");

        BookmartRespDto bookmartRespDto = new BookmartRespDto();
        if (principal != null) {
            bookmartRespDto = bookmarkRepository.findByRecruitmentIdAndUserId(id, principal.getId());
        } else {
            bookmartRespDto = null;
        }

        RecruitmentPostDetailRespDto recruitmentPostDto = recruitmentPostRepository.findByIdWithEnterpriseId(id);

        // d-day 계산
        recruitmentPostDto.calculateDiffDays();

        List<RecruitmentPostSkillRespDto> recruitmentPostSkillRespDtos = recruitmentSkillRepository
                .findByRecruitmentId(id);

        List<Resume> resumeDtos = new ArrayList<>();
        if (principal != null) {
            resumeDtos = resumeRepository.findByUserId(principalId);
        } else {
            resumeDtos = null;
        }

        RecruitmentPostDetailDto recruitmentDetailPageDto = new RecruitmentPostDetailDto(bookmartRespDto,
                recruitmentPostDto, recruitmentPostSkillRespDtos, resumeDtos);

        return new ResponseEntity<>(new ResponseDto<>(1, "상세보기 페이지 성공", recruitmentDetailPageDto), HttpStatus.OK);
    }

    @GetMapping("/ns/recruitment/list")
    @Operation(summary = "2. 채용공고 목록보기", description = "채용공고의 목록보기 입니다.")
    public ResponseEntity<?> recruitmentPostList() {
        List<RecruitmentPostListRespDto> posts = recruitmentPostRepository.findByPost();
        // d-day 계산
        for (RecruitmentPostListRespDto post : posts) {
            post.calculateDiffDays(); // D-Day 계산
        }

        return new ResponseEntity<>(new ResponseDto<>(1, "게시글 목록", posts), HttpStatus.OK);
    }

    @PostMapping("/ns/recruitment/search")
    @Operation(summary = "6. 채용공고 검색하기", description = "채용공고의 정보를 검색합니다.")
    public ResponseEntity<?> searchList(@RequestBody RecruitmentPostRespDto.RecruitmentPostSearchReqDto recruitmentPostSearchReqDto) {
        List<RecruitmentPostSearchRespDto> postPSList = recruitmentService.채용정보검색(recruitmentPostSearchReqDto);

        // d-day 계산
        for (RecruitmentPostSearchRespDto post : postPSList) {
            post.calculateDiffDays(); // D-Day 계산
        }

        return new ResponseEntity<>(new ResponseDto<>(1, "검색 성공", postPSList), HttpStatus.OK);
    }

    @PostMapping("/ns/recruitment/category")
    @Operation(summary = "7. 채용공고 카테고리 별 검색하기", description = "채용공고의 정보를 카테고리 별로 검색합니다.")
    public ResponseEntity<?> category(@RequestBody RecruitmentPostCategoryRespDto recruitmentPostCategoryRespDto) {
        List<RecruitmentPostCategoryRespDto> postPSList = recruitmentService.카테고리검색(recruitmentPostCategoryRespDto);

        // d-day 계산
//        for (RecruitmentPostCategoryRespDto post : postPSList) {
//            post.calculateDiffDays(); // D-Day 계산
//        }

        return new ResponseEntity<>(new ResponseDto<>(1, "검색 성공", postPSList), HttpStatus.OK);
    }

}