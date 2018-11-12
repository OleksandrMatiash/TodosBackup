package com.vortex.todobackup.service;

import com.vortex.todobackup.domain.BackupEntity;
import com.vortex.todobackup.domain.BackupStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Service
public class BackupsServiceImpl implements BackupsService {

    private static final ScheduledExecutorService SCHEDULER_EXECUTOR = Executors.newScheduledThreadPool(4);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
    private static final int TIMEOUT_MILLIS = 2000;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private BackupsDao backupsDao;
    @Autowired
    private TodoItemServerService todoItemServerService;

    @Override
    public BackupEntity scheduleBackupAsync() {
        BackupEntity backupEntity = new BackupEntity()
                .setStatus(BackupStatus.IN_PROGRESS)
                .setDate(DATE_FORMAT.format(new Date()));
        backupEntity = backupsDao.saveBackupEntity(backupEntity);
        int backupId = backupEntity.getBackupId();

        supplyAsync(() -> todoItemServerService.getAllUserEntities())
                .thenAccept(userEntities -> {
                    backupsDao.saveUserEntitiesAndMarkWithOk(backupId, userEntities);
                    System.out.println("---- Asynchronous backup finished");
                })
                .exceptionally(throwable -> {
                    System.out.println("---- Asynchronous backup failed: " + throwable.getMessage());
                    backupsDao.markBackupEntityWithError(backupId);
                    return null;
                });

        System.out.println("---- Scheduled asynchronous backup");
        return backupEntity;
    }

    private <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        final CompletableFuture<T> cf = new CompletableFuture<T>();
        Future<?> future = EXECUTOR_SERVICE.submit(() -> {
            try {
                cf.complete(supplier.get());
            } catch (Throwable ex) {
                cf.completeExceptionally(ex);
            }
        });
        SCHEDULER_EXECUTOR.schedule(() -> {
            if (!cf.isDone()) {
                cf.completeExceptionally(new TimeoutException());
                future.cancel(true);
            }

        }, TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        return cf;
    }

    @Override
    public List<BackupEntity> getBackups() {
        return backupsDao.getBackups();
    }

    @Override
    public BackupEntity getBackup(int backupId) {
        return backupsDao.getBackupEntity(backupId);
    }
}
