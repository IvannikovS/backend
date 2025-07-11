package ru.inline.ivannikov.backend.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter {
    private String name;
    private String inn;
    private Boolean isOrganization;
}
