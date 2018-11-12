package com.vortex.todobackup.mvc.controller;

import com.vortex.todobackup.domain.BackupEntity;
import com.vortex.todobackup.domain.BackupStatus;
import com.vortex.todobackup.domain.ToDoEntity;
import com.vortex.todobackup.domain.UserEntity;
import com.vortex.todobackup.mvc.model.BackupDto;
import com.vortex.todobackup.service.BackupsService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class BackupController {
    @Autowired
    private BackupsService backupsService;

    @RequestMapping(value = "/backups", method = RequestMethod.POST)
    public ResponseEntity<BackupDto> scheduleBackup() {
        BackupEntity backupEntity = backupsService.scheduleBackupAsync();
        return new ResponseEntity<>(convertToDto(backupEntity), HttpStatus.OK);
    }

    @RequestMapping(value = "/backups", method = RequestMethod.GET)
    public ResponseEntity<List<BackupDto>> getBackups() {
        List<BackupDto> result = convertToDtos(backupsService.getBackups());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private List<BackupDto> convertToDtos(List<BackupEntity> backups) {
        return backups.stream()
                .map(this::convertToDto)
                .collect(toList());
    }


    private BackupDto convertToDto(BackupEntity backupEntity) {
        return new BackupDto()
                .setBackupId(backupEntity.getBackupId())
                .setDate(backupEntity.getDate())
                .setStatus(backupEntity.getStatus().getName());
    }

    @RequestMapping(value = "/exports/{backup_id}", method = RequestMethod.GET)
    public void exportBackups(@PathVariable("backup_id") int backupId, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=backup-" + backupId + ".csv");
        ServletOutputStream outputStream = response.getOutputStream();

        BackupEntity backup = backupsService.getBackup(backupId);
        if (backup == null || backup.getStatus() != BackupStatus.OK) {
            throw new IllegalArgumentException(String.format("backup with id:%s not found", backupId));
        }
        writeCsvDataToOutputStream(outputStream, backup);
    }

    private void writeCsvDataToOutputStream(ServletOutputStream outputStream, BackupEntity backup) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withDelimiter(';')
                .withHeader("Username", "TodoItemId", "Subject", "DueDate", "Done"));
        List<UserEntity> userEntities = backup.getUserEntities();
        for (UserEntity userEntity : userEntities) {
            for (ToDoEntity todoEntity : userEntity.getTodos()) {
                csvPrinter.printRecord(userEntity.getUsername(), todoEntity.getId(), todoEntity.getSubject(), todoEntity.getDueDate(), todoEntity.isDone());
            }
        }
        writer.flush();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleIOException(Throwable ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
