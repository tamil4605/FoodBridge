package com.foodbridge.service;

import com.foodbridge.entity.AuditLog;
import com.foodbridge.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String actor, String action, String entityName, Long entityId, String details) {
        AuditLog log = new AuditLog(actor, action, entityName, entityId, details, "127.0.0.1");
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop20ByOrderByTimestampDesc();
    }
}
