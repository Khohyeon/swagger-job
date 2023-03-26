package shop.mtcoding.job.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.job.config.aop.UserId;
import shop.mtcoding.job.dto.ResponseDto;
import shop.mtcoding.job.dto.apply.ApplyRespDto.NotifyListRespDto;
import shop.mtcoding.job.model.apply.ApplyRepository;

@RequiredArgsConstructor
@RestController
@Tag(name = "SSE 통신", description = "SSE 통신을 사용합니다.")
public class SseController {
    private final ApplyRepository applyRepository;

    @GetMapping(value = "/notify", produces = "text/event-stream")
    @Operation(summary = "SSE 통신", description = "SSE 통신으로 서비스를 제공합니다.")
    public @ResponseBody ResponseEntity<?> notify(HttpServletRequest request, HttpServletResponse response,
            @UserId Integer principalId)
            throws IOException {
        SseEmitter emitter = new SseEmitter();
        CompletableFuture.runAsync(() -> {
            try {
                List<NotifyListRespDto> notifies = applyRepository.findNotifyByUserId(principalId);
                for (NotifyListRespDto notify : notifies) {
                    if (notify.getNotify()) {
                        emitter.send("마이페이지-지원현황에서 결과를 확인해주세요");
                        applyRepository.updateNotifyById(principalId, false);
                    }
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
            }
        });
        return new ResponseEntity<>(new ResponseDto<>(1, "인증 성공", emitter), HttpStatus.OK);
    }
}
