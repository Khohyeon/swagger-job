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
import shop.mtcoding.job.config.aop.UserId;
import shop.mtcoding.job.config.aop.UserInterface;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.userPage.UserPageApplyDto;
import shop.mtcoding.job.dto.userPage.UserPageBookmarkDto;
import shop.mtcoding.job.dto.userPage.UserPageMatchingDto;
import shop.mtcoding.job.handler.exception.CustomApiException;
import shop.mtcoding.job.handler.exception.CustomException;
import shop.mtcoding.job.model.apply.ApplyRepository;
import shop.mtcoding.job.model.bookmark.BookmarkRepository;
import shop.mtcoding.job.model.userSkill.UserSkillRepository;
import shop.mtcoding.job.util.Convert;

@RequiredArgsConstructor
@RestController
@Tag(name = "6. 개인회원 마이페이지", description = "개인회원 마이페이지")
public class UserPageController {
    private final ApplyRepository applyRepository;

    private final UserSkillRepository userSkillRepository;

    private final BookmarkRepository bookmarkRepository;

    @GetMapping("/myapply")
    @Operation(summary = "1. 개인회원의 지원 이력서", description = "기업에 지원한 개인의 이력서를 제공합니다.")
    public @ResponseBody ResponseEntity<?> mypage(@UserId Integer principalId) {
        try {
            if(principalId == null){
                throw new CustomApiException("기업 토큰을 가지고 있습니다");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<UserPageApplyDto> userPageDtos = applyRepository.findAllApply(principalId);
        for (UserPageApplyDto post : userPageDtos) {
            post.getRecruitmentList().calculateDiffDays(); // D-Day 계산
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "성공", userPageDtos), HttpStatus.OK);
    }

    @GetMapping("/mymatching")
    @Operation(summary = "2. 개인회원의 매칭서비스", description = "개인회원의 기술스택과 같은 기업의 매칭서비스를 제공합니다.")
    public @ResponseBody ResponseEntity<?> mymatching(@UserId Integer principalId) throws Exception {
        if(principalId == UserInterface.defaultValue){
            throw new CustomApiException("유저 토큰이 없어 인증이 안됩니다.");
        }
        List<UserPageMatchingDto> posts = userSkillRepository.userJoinRecruitmentWithMatching(principalId);

        for (UserPageMatchingDto post : posts) {
            List<String> skills = Convert.skillMapping(post.getUserMatching().getUserSkillDto().getSkill());
            post.getUserMatching().getUserSkillDto().setSkillString(skills);
            post.getRecruitment().calculateDiffDays();
        }

        return new ResponseEntity<>(new ResponseDto<>(1, "성공", posts), HttpStatus.OK);
    }

    @GetMapping("/mybookmark")
    @Operation(summary = "3. 개인회원의 북마크", description = "기업에 북마크한 정보를 제공합니다.")
    public @ResponseBody ResponseEntity<?> mybookmark(@UserId Integer principalId) {
        List<UserPageBookmarkDto> posts = bookmarkRepository.BookmarkJoinRecruitOfUserPage(principalId);
        // d-day 계산
        for (UserPageBookmarkDto post : posts) {
            post.getRecruitmentList().calculateDiffDays();
        }
        return new ResponseEntity<>(new ResponseDto<>(1, "성공", posts), HttpStatus.OK);
    }
}
