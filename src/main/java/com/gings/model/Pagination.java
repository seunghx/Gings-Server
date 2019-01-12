package com.gings.model;

import lombok.Data;

@Data
public class Pagination {
    private int offset = 0;
    private int limit = 50;
}
