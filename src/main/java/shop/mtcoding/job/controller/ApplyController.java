package shop.mtcoding.job.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.job.config.aop.EntId;
import shop.mtcoding.job.config.aop.UserId;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.apply.ApplyReqDto.InsertApplyReqDto;
import shop.mtcoding.job.dto.apply.ApplyReqDto.UpdateApplicantResultReqDto;
import shop.mtcoding.job.dto.recruitmentPost.RecruitmentPostRespDto.RecruitmentPostDetailRespDto;
import shop.mtcoding.job.handler.exception.CustomApiException;
import shop.mtcoding.job.model.recruitmentPost.RecruitmentPostRepository;
import shop.mtcoding.job.service.ApplyService;
import shop.mtcoding.job.util.DateUtil;

@RequiredArgsConstructor
@RestController
@Tag(name = "5. 지원하기", description = "이력서 명세서")
public class ApplyController {
    private final ApplyService applyService;

    private final RecruitmentPostRepository recruitmentPostRepository;

    @PostMapping("/apply/{id}")
    @Operation(summary = "1. 이력서 제출", description = "유저의 이력서를 제출 합니다.")
    public @ResponseBody ResponseEntity<?> insertApply(@RequestBody InsertApplyReqDto insertApplyReqDto,
            @PathVariable int id, @UserId Integer principalId) {

        RecruitmentPostDetailRespDto recruitmentPostDto = recruitmentPostRepository.findByIdWithEnterpriseId(
                id);

        // d-day 계산
        long diffDays = DateUtil.deadline(recruitmentPostDto.getDeadline());
        System.out.println("테스트 : " + diffDays);

        if (diffDays < 0) {
            throw new CustomApiException("이력서 제출기간이 지났습니다", HttpStatus.BAD_REQUEST);
        }

        applyService.이력서제출(insertApplyReqDto, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "이력서 제출 성공", null), HttpStatus.CREATED);
    }

    @DeleteMapping("/apply/delete/{id}")
    @Operation(summary = "3. 지원한 이력서 삭제", description = "유저의 제출한 이력서를 삭제 합니다.")
    public @ResponseBody ResponseEntity<?> deleteApply(@PathVariable int id, @UserId Integer principalId) {
        applyService.이력서제출취소(id, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "지원서 삭제 성공", null), HttpStatus.OK);
    }

    @PutMapping("/apply/put/{id}")
    @Operation(summary = "2. 합/불처리", description = "유저의 제출한 이력서를 수정 합니다.(기업회원이 아니라서 실패함)")
    public @ResponseBody ResponseEntity<?> updateResult(
            @RequestBody UpdateApplicantResultReqDto updateApplicantResultReqDto, @PathVariable int id,
            @EntId Integer principalId) {
        applyService.합격불합격(id, updateApplicantResultReqDto, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "처리 성공", null), HttpStatus.OK);
    }

}
