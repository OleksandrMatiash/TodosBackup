package com.vortex.todobackup.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserEntity {

    private int id;
    private String username;
    private String email;
    private List<ToDoEntity> todos;
}
