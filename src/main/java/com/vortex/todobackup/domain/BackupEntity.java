package com.vortex.todobackup.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BackupEntity {

    private int backupId;
    private String date;
    private BackupStatus status;
    private List<UserEntity> userEntities;
}
