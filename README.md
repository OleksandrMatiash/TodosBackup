**Build** (jdk 1.8, maven should be installed) from root directory:
```
mvn clean install
```

**Run** single jar (after build) from target directory:
```
java -jar todo-backup-1.0-SNAPSHOT-war-exec.jar -httpPort 8000
```

### List backups
Request method: GET

Request URL: ```http://localhost:8000/todo-backup/backups```

### Backup accounts
Request method: POST

Request URL: ```http://localhost:8000/todo-backup/backups```
Response:
```
{
    "backupId": 10,
    "date": "2018-11-11 21:01:51",
    "status": "In progress"
}
```

### Export backup
Request method: GET

Request URL: ```http://localhost:8000/todo-backup/exports/<backupId>```

Response: .csv file with data
