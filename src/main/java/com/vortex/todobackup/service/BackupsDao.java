package com.vortex.todobackup.service;

import com.vortex.todobackup.domain.BackupEntity;
import com.vortex.todobackup.domain.UserEntity;

import java.util.List;

public interface BackupsDao {

    BackupEntity saveBackupEntity(BackupEntity backupEntity);

    void saveUserEntitiesAndMarkWithOk(int backupId, List<UserEntity> userEntities);

    void markBackupEntityWithError(int backupId);

    BackupEntity getBackupEntity(int backupId);

    List<BackupEntity> getBackups();
}
