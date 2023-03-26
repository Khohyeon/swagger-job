package shop.mtcoding.job.controller;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.job.config.aop.EntId;
import shop.mtcoding.job.config.auth.JwtProvider;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.enterprise.EnterpriseReqDto.JoinEnterpriseReqDto;
import shop.mtcoding.job.dto.enterprise.EnterpriseReqDto.LoginEnterpriseReqDto;
import shop.mtcoding.job.dto.enterprise.EnterpriseReqDto.UpdateEnterpriseReqDto;
import shop.mtcoding.job.handler.exception.CustomApiException;
import shop.mtcoding.job.handler.exception.CustomException;
import shop.mtcoding.job.model.enterprise.Enterprise;
import shop.mtcoding.job.model.enterprise.EnterpriseRepository;
import shop.mtcoding.job.service.EnterpriseService;

@RequiredArgsConstructor
@RestController
@Tag(name = "7. 기업회원", description = "기업회원 컨트롤러")
public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    private final EnterpriseRepository enterpriseRepository;

    @PostMapping("/ns/enterprise/login")
    @Operation(summary = "2. 기업회원 로그인", description = "기업회원으로 로그인 합니다.")
    public @ResponseBody ResponseEntity<?> enterpriseLogin(@RequestBody LoginEnterpriseReqDto loginEnterpriseReqDto,
            HttpServletResponse response) {
        if (loginEnterpriseReqDto.getEnterpriseName() == null || loginEnterpriseReqDto.getEnterpriseName().isEmpty()) {
            throw new CustomApiException("아이디를 작성해주세요");
        }

        if (loginEnterpriseReqDto.getPassword() == null || loginEnterpriseReqDto.getPassword().isEmpty()) {
            throw new CustomApiException("비밀번호를 작성해주세요");
        }
        // 1. 로그인하기 service
        Optional<Enterprise> principalEnt = enterpriseService.기업로그인하기(loginEnterpriseReqDto);

        // 2. 아이디 기억
        if (loginEnterpriseReqDto.getRememberEnt().equals("true")) {
            Cookie cookie = new Cookie("rememberEnt", loginEnterpriseReqDto.getEnterpriseName());
            cookie.setPath("/");
            cookie.setMaxAge(600);
            response.addCookie(cookie);
        }

        if (loginEnterpriseReqDto.getRememberEnt().equals("false")) {
            Cookie cookie = new Cookie("rememberEnt", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        // 3. 토큰 헤더에 저장
        if (principalEnt.isPresent()) { // 값이 있다면
            String jwt = JwtProvider.createEnt(principalEnt.get());
            return ResponseEntity.ok().header(JwtProvider.HEADER, jwt).body("로그인 성공");
        } else {
            return ResponseEntity.badRequest().body("로그인 실패");
        }
    }

    @PostMapping("/ns/enterprise/join")
    @Operation(summary = "1. 기업회원 회원가입", description = "기업회원으로 회원가입 합니다.")
    public ResponseEntity<ResponseDto<Object>> enterpriseJoin(@RequestBody JoinEnterpriseReqDto joinEnterpriseReqDto) {
        if (joinEnterpriseReqDto.getEnterpriseName() == null || joinEnterpriseReqDto.getEnterpriseName().isEmpty()) {
            throw new CustomException("아이디를 작성해주세요");
        }
        if (joinEnterpriseReqDto.getPassword() == null || joinEnterpriseReqDto.getPassword().isEmpty()) {
            throw new CustomException("비밀번호를 작성해주세요");
        }
        if (joinEnterpriseReqDto.getAddress() == null || joinEnterpriseReqDto.getAddress().isEmpty()) {
            throw new CustomException("주소를 작성해주세요");
        }
        if (joinEnterpriseReqDto.getEmail() == null || joinEnterpriseReqDto.getEmail().isEmpty()) {
            throw new CustomException("email을 작성해주세요");
        }
        if (joinEnterpriseReqDto.getContact() == null || joinEnterpriseReqDto.getContact().isEmpty()) {
            throw new CustomException("전화번호를 입력해주세요");
        }
        if (joinEnterpriseReqDto.getSector() == null || joinEnterpriseReqDto.getSector().isEmpty()) {
            throw new CustomException("분야를 작성해주세요");
        }
        if (joinEnterpriseReqDto.getSize() == null || joinEnterpriseReqDto.getSize().isEmpty()) {
            throw new CustomException("기업규모를 작성해주세요");
        }
        if (joinEnterpriseReqDto.getRole() == null || joinEnterpriseReqDto.getRole().isEmpty()) {
            throw new CustomException("role을 작성해주세요");
        }

        enterpriseService.기업가입하기(joinEnterpriseReqDto);
        return ResponseEntity.ok(new ResponseDto<>(1, "회원가입 성공", null));
    }

    @GetMapping("/ns/enterprise/enterpriseNameSameCheckEnt")
    @Operation(summary = "0. 기업회원 중복체크", description = "기업회원으로 회원가입 시 중복체크 합니다.")
    public @ResponseBody ResponseDto<?> check(@RequestParam String enterpriseName) {
        if (enterpriseName == null || enterpriseName.isEmpty()) {
            return new ResponseDto<>(-1, "아이디가 입력되지 않았습니다.", null);
        }
        Enterprise sameeEnterprise = enterpriseRepository.findByName(enterpriseName);
        if (sameeEnterprise != null) {
            return new ResponseDto<>(1, "동일한 아이디가 존재합니다.", false);
        } else {
            return new ResponseDto<>(1, "해당 아이디로 회원가입이 가능합니다.", true);
        }
    }

    @PostMapping("/enterprise/update")
    @Operation(summary = "3. 기업회원 수정", description = "기업회원의 정보를 수정합니다.")
    public ResponseEntity<?> enterpriseUpdate(@RequestBody UpdateEnterpriseReqDto updateEnterpriseReqDto,
            @EntId Integer principalId) {
        if (updateEnterpriseReqDto.getPassword() == null || updateEnterpriseReqDto.getPassword().isEmpty()) {
            throw new CustomException("비밀번호를 작성해주세요");
        }
        if (updateEnterpriseReqDto.getAddress() == null || updateEnterpriseReqDto.getAddress().isEmpty()) {
            throw new CustomException("주소를 작성해주세요");
        }
        if (updateEnterpriseReqDto.getEmail() == null || updateEnterpriseReqDto.getEmail().isEmpty()) {
            throw new CustomException("email을 작성해주세요");
        }
        if (updateEnterpriseReqDto.getContact() == null || updateEnterpriseReqDto.getContact().isEmpty()) {
            throw new CustomException("전화번호를 입력해주세요");
        }
        if (updateEnterpriseReqDto.getSector() == null || updateEnterpriseReqDto.getSector().isEmpty()) {
            throw new CustomException("분야를 작성해주세요");
        }
        if (updateEnterpriseReqDto.getSize() == null || updateEnterpriseReqDto.getSize().isEmpty()) {
            throw new CustomException("기업규모를 작성해주세요");
        }

        enterpriseService.기업정보수정하기(updateEnterpriseReqDto, principalId);
        return ResponseEntity.ok(new ResponseDto<>(1, "회원수정 성공", null));
    }
}
