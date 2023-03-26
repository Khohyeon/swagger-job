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
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostListRespDto;
import shop.mtcoding.job.service.MainService;

@RequiredArgsConstructor
@RestController
@Tag(name = "0. 메인", description = "메인사이트")
public class MainController {

    private final MainService mainService;

    @GetMapping("/ns/main")
    @Operation(summary = "메인페이지 목록", description = "메인페이지에서 게시글 목록을 제공합니다.")
    public @ResponseBody ResponseEntity<?> main() {
        List<RecruitmentPostListRespDto> posts = mainService.게시글목록보기();
        return new ResponseEntity<>(new ResponseDto<>(1, "게시글 목록", posts), HttpStatus.OK);
    }
}