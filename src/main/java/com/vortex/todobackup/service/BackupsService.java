package com.vortex.todobackup.service;

import com.vortex.todobackup.domain.BackupEntity;

import java.util.List;

public interface BackupsService {

    /**
     * Asynchronously schedule backup and return scheduled backup without user data
     *
     * @return scheduled backup without user data
     */
    BackupEntity scheduleBackupAsync();

    /**
     * Return all backups
     *
     * @return all backups
     */
    List<BackupEntity> getBackups();

    /**
     * Returns BackupEntity by given id or <code>null</code> if backup with such id does not exist
     *
     * @param backupId id of backup
     * @return BackupEntity or <code>null</code>
     */
    BackupEntity getBackup(int backupId);
}
