package com.vortex.todobackup.mvc.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BackupDto {
    private int backupId;
    private String date;
    private String status;
}
