package com.example.recommendershop.dto;

import com.example.recommendershop.enums.TypeSort;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiListBaseRequest{
    protected String orderBy = "date";
    protected TypeSort orderDirection = TypeSort.DESC;
    protected Integer size = 10;
    protected Integer page = 0;
}
