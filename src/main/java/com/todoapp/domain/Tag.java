package com.todoapp.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tag {

    private Long id;

    @EqualsAndHashCode.Include
    private String name;
}
