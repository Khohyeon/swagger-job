package shop.mtcoding.job.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shop.mtcoding.job.config.aop.UserId;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.resume.SaveResumeDto;
import shop.mtcoding.job.dto.resume.UpdateResumeDto;
import shop.mtcoding.job.model.resume.Resume;
import shop.mtcoding.job.model.resume.ResumeRepository;
import shop.mtcoding.job.service.ResumeService;

@RequiredArgsConstructor
@RestController
@Tag(name = "3. 이력서", description = "이력서 명세서")
public class ResumeController {
    private final ResumeService resumeService;

    private final ResumeRepository resumeRepository;

    @PostMapping("/resume")
    @Operation(summary = "1. 이력서 쓰기", description = "이력서를 작성 합니다.")
    public @ResponseBody ResponseEntity<?> save(@RequestBody SaveResumeDto saveResumeDto, @UserId Integer principalId) {
        resumeService.이력서쓰기(saveResumeDto, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "이력서 쓰기 성공", null), HttpStatus.CREATED);
    }


    @GetMapping("/resumes")
    @Operation(summary = "2. 이력서 목록", description = "이력서 목록을 제공합니다.")
    public @ResponseBody ResponseEntity<?> resumeList(@UserId Integer principalId) {
        List<Resume> resumeList = resumeRepository.findByUserId(principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "이력서 목록 보기 완료", resumeList), HttpStatus.OK);
    }

    @PutMapping("/resume/{id}")
    @Operation(summary = "3. 이력서 수정", description = "이력서 수정을 제공합니다.")
    public @ResponseBody ResponseEntity<?> update(@PathVariable int id,
                                                  @RequestBody UpdateResumeDto updateResumeDto, @UserId Integer principalId) throws Exception {
        resumeService.이력서수정(id, updateResumeDto, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "이력서 수정 성공", null), HttpStatus.OK);
    }

    @DeleteMapping("/resume/{id}")
    @Operation(summary = "4. 이력서 삭제", description = "이력서 삭제를 제공합니다.")
    public @ResponseBody ResponseEntity<?> delete(@PathVariable int id, @UserId Integer principalId) {
        resumeService.이력서삭제(id, principalId);
        return new ResponseEntity<>(new ResponseDto<>(1, "이력서 삭제 성공", null), HttpStatus.OK);
    }


}
