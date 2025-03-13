package com.example.RestApi.controller;

import com.example.RestApi.model.dto.AuditLogRecord;
import com.example.RestApi.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/log/audit")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLogRecord>> getAllAuditLogs(){
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

}
