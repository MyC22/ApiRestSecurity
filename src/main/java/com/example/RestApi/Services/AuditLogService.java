package com.example.RestApi.Services;

import com.example.RestApi.model.dto.AuditLogDto;
import com.example.RestApi.Mappers.AuditLogMapper;
import com.example.RestApi.Repository.AuditLogRepository;
import com.example.RestApi.model.entity.AuditLogEntity;
import com.example.RestApi.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;


    public List<AuditLogDto> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .map(auditLogMapper::toDto)
                .toList();
    }


}
