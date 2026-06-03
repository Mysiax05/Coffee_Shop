package com.dbproject.backend.web;

import com.dbproject.backend.dto.BestSellerDto;
import com.dbproject.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<BestSellerDto>> getTopBestSellers() {
        return ResponseEntity.ok(reportService.getTopBestSellers());
    }

    @GetMapping("/best-sellers/by-category")
    public ResponseEntity<Map<String, BestSellerDto>> getBestSellerPerCategory() {
        return ResponseEntity.ok(reportService.getBestSellerPerCategory());
    }
}
