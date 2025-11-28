package com.mju.db_project.controller;

import com.mju.db_project.dto.request.EvaluationRequestDto;
import com.mju.db_project.dto.response.EvaluationResponseDto;
import com.mju.db_project.dto.response.PeerEvaluationListDto;
import com.mju.db_project.service.PeerEvaluationService;
import io.swagger.v3.oas.annotations.Operation; 
import io.swagger.v3.oas.annotations.tags.Tag; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/peer-evaluations")
@RequiredArgsConstructor
@Tag(name = "Peer Evaluation", description = "동료 평가(Peer Review) 관리 API")
public class PeerEvaluationController {

    private final PeerEvaluationService evaluationService;

    /**
     * 1. 동료평가 목록 조회
     * GET /api/peer-evaluations/list?evaluatorId={로그인ID}
     */
    @GetMapping("/list")
    @Operation(summary = "작성한 동료 평가 목록 조회",
               description = "로그인한 직원(evaluatorId)이 작성한 모든 동료 평가의 요약 정보를 조회합니다.")
    public ResponseEntity<List<PeerEvaluationListDto>> getList(@RequestParam Integer evaluatorId) {
        List<PeerEvaluationListDto> list = evaluationService.getEvaluationList(evaluatorId);
        return ResponseEntity.ok(list);
    }

    /**
     * 2. 동료평가 작성
     * POST /api/peer-evaluations?evaluatorId={로그인ID}
     */
    @PostMapping
    @Operation(summary = "동료 평가 신규 작성 및 저장",
               description = "평가자 ID를 기반으로 특정 대상자(targetEmpId)에 대한 평가를 작성하고 DB에 저장합니다.")
    public ResponseEntity<Integer> createEvaluation(@RequestParam Integer evaluatorId,
                                                 @RequestBody EvaluationRequestDto dto) {
        
        Integer newEvaluationId = evaluationService.createEvaluation(evaluatorId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvaluationId);
    }

    /**
     * 3. 동료평가 상세 조회
     * GET /api/peer-evaluations/{evaluationId}
     */
    @GetMapping("/{evaluationId}")
    @Operation(summary = "동료 평가 상세 내용 조회",
               description = "평가 ID(evaluationId)를 이용하여 해당 평가의 모든 점수 및 서술 내용을 조회합니다.")
    public ResponseEntity<EvaluationResponseDto> getDetail(@PathVariable Integer evaluationId) {
        EvaluationResponseDto detail = evaluationService.getEvaluationDetail(evaluationId);
        return ResponseEntity.ok(detail);
    }
}