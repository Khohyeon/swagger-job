package shop.mtcoding.job.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.service.UserService;

@RequiredArgsConstructor
@RestController
@Tag(name = "2. 유저의 기술스택", description = "유저의 기술 스택을 제공합니다.")
public class UserSkillController {

    private final UserService userService;

    @PostMapping("/ns/user/skill")
    @Operation(summary = "유저의 스택", description = "개인회원의 기술스택을 제공합니다.")
    public ResponseEntity<?> skill(Integer userId, @RequestParam("skill") List<Integer> skill) {
        userService.유저스킬추가(userId, skill);
        return new ResponseEntity<>(new ResponseDto<>(1, "유저스킬 추가", null), HttpStatus.OK);
    }
}
