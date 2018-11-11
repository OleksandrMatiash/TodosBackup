package com.vortex.todobackup.service;

import com.vortex.todobackup.domain.BackupEntity;
import com.vortex.todobackup.domain.BackupStatus;
import com.vortex.todobackup.domain.UserEntity;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class InMemoryBackupsDaoTest {
    @Test
    public void getBackupEntity_entityDoesNotExist() {
        BackupsDao dao = new InMemoryBackupsDao();

        BackupEntity actual = dao.getBackupEntity(100500);
        assertNull(actual);
    }

    @Test
    public void getBackupEntity_entityDoesExist() {
        BackupsDao dao = new InMemoryBackupsDao();
        dao.saveBackupEntity(getBackupEntity());

        BackupEntity actual = dao.getBackupEntity(0);

        assertNotNull(actual);
        assertEquals(BackupStatus.IN_PROGRESS, actual.getStatus());
        assertEquals("some date", actual.getDate());
    }

    private BackupEntity getBackupEntity() {
        return new BackupEntity()
                .setStatus(BackupStatus.IN_PROGRESS)
                .setDate("some date");
    }

    @Test
    public void markBackupEntityWithError_entityDoesNotExist() {
        BackupsDao dao = new InMemoryBackupsDao();
        dao.saveBackupEntity(getBackupEntity());

        dao.markBackupEntityWithError(100500);

        BackupEntity actual = dao.getBackupEntity(100500);
        assertNull(actual);
    }

    @Test
    public void markBackupEntityWithError_entityDoesExist() {
        BackupsDao dao = new InMemoryBackupsDao();
        dao.saveBackupEntity(getBackupEntity());

        dao.markBackupEntityWithError(0);

        BackupEntity actual = dao.getBackupEntity(0);
        assertNotNull(actual);
        assertEquals(BackupStatus.FAILED, actual.getStatus());
    }

    @Test
    public void saveUserEntitiesAndMarkWithOk_entityDoesNotExist() {
        BackupsDao dao = new InMemoryBackupsDao();
        dao.saveBackupEntity(getBackupEntity());

        dao.saveUserEntitiesAndMarkWithOk(100500, Collections.emptyList());

        BackupEntity actual = dao.getBackupEntity(100500);
        assertNull(actual);
    }

    @Test
    public void saveUserEntitiesAndMarkWithOk_entityDoesExist() {
        BackupsDao dao = new InMemoryBackupsDao();
        dao.saveBackupEntity(getBackupEntity());

        dao.saveUserEntitiesAndMarkWithOk(0, singletonList(new UserEntity().setId(5)));

        BackupEntity actual = dao.getBackupEntity(0);
        assertNotNull(actual);
        assertEquals(BackupStatus.OK, actual.getStatus());
        assertEquals(singletonList(new UserEntity().setId(5)), actual.getUserEntities());
    }

    @Test
    public void getBackups_noBackups() {
        BackupsDao dao = new InMemoryBackupsDao();

        List<BackupEntity> actual = dao.getBackups();

        assertNotNull(actual);
        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    public void getBackups_twoBackupsExist() {
        BackupsDao dao = new InMemoryBackupsDao();
        dao.saveBackupEntity(getBackupEntity());
        dao.saveBackupEntity(getBackupEntity());

        List<BackupEntity> actual = dao.getBackups();

        assertNotNull(actual);
        assertEquals(2, actual.size());
    }
}