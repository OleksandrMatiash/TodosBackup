package com.vortex.todobackup.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ToDoEntity {

    private int id;
    private String subject;
    private String dueDate;
    private boolean done;
}
