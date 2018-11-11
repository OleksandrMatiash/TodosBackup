package com.vortex.todobackup.service;

import com.vortex.todobackup.domain.BackupEntity;
import com.vortex.todobackup.domain.BackupStatus;
import com.vortex.todobackup.domain.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryBackupsDao implements BackupsDao {

    private AtomicInteger id = new AtomicInteger(0);
    private List<BackupEntity> backupEntities = new ArrayList<>();

    @Override
    public BackupEntity saveBackupEntity(BackupEntity backupEntity) {
        backupEntity.setBackupId(id.getAndIncrement());
        backupEntities.add(backupEntity);
        return backupEntity;
    }

    @Override
    public void saveUserEntitiesAndMarkWithOk(int backupId, List<UserEntity> userEntities) {
        findBackupEntity(backupId)
                .ifPresent(backupEntity -> {
                    backupEntity.setStatus(BackupStatus.OK);
                    backupEntity.setUserEntities(userEntities);
                });
    }

    private Optional<BackupEntity> findBackupEntity(int backupId) {
        return backupEntities.stream()
                .filter(backupEntity -> backupEntity.getBackupId() == backupId)
                .findAny();
    }

    @Override
    public void markBackupEntityWithError(int backupId) {
        findBackupEntity(backupId)
                .ifPresent(backupEntity -> backupEntity.setStatus(BackupStatus.FAILED));
    }

    @Override
    public BackupEntity getBackupEntity(int backupId) {
        return findBackupEntity(backupId)
                .orElse(null);
    }

    @Override
    public List<BackupEntity> getBackups() {
        return backupEntities;
    }
}
