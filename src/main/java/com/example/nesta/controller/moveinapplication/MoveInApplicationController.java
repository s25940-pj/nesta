package com.example.nesta.controller.moveinapplication;


import com.example.nesta.dto.moveinapplication.DecisionRequest;
import com.example.nesta.dto.moveinapplication.RescheduleRequest;
import com.example.nesta.model.MoveInApplication;
import com.example.nesta.service.moveinapplication.MoveInApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/move-in-applications")
public class MoveInApplicationController {
    private final MoveInApplicationService moveInApplicationService;

    public MoveInApplicationController(MoveInApplicationService moveInApplicationService) {
        this.moveInApplicationService = moveInApplicationService;
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).RENTIER)")
    @PostMapping
    public ResponseEntity<MoveInApplication> createMoveInApplication(@RequestBody @Valid MoveInApplication moveInApplication, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(moveInApplicationService.create(moveInApplication, jwt));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).RENTIER) or hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @GetMapping("/{id}")
    public ResponseEntity<MoveInApplication> getMoveInApplicationById(@PathVariable Long id) {
        return moveInApplicationService.getMoveInApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).RENTIER)")
    @GetMapping("/by-rentier/{rentierId}")
    public ResponseEntity<List<MoveInApplication>> getMoveInApplicationsByRentierId(@PathVariable String rentierId) {
        return ResponseEntity.ok(moveInApplicationService.getMoveInApplicationsByRentierId(rentierId));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @GetMapping("/by-landlord/{landlordId}")
    public List<MoveInApplication> getMoveInApplicationsByLandlordId(@PathVariable String landlordId) {
        return moveInApplicationService.getMoveInApplicationsByLandlordId(landlordId);
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).RENTIER)")
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Void> rescheduleViewing(
            @PathVariable Long id,
            @RequestBody @Valid RescheduleRequest rescheduleRequest) {
        moveInApplicationService.rescheduleViewing(id, rescheduleRequest);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).RENTIER) or hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PutMapping("/{id}/decision")
    public ResponseEntity<Void> setDecision(@PathVariable Long id, @RequestBody @Valid DecisionRequest request, @AuthenticationPrincipal Jwt jwt) {
        moveInApplicationService.setDecision(id, request, jwt);
        return ResponseEntity.noContent().build();
    }
}
