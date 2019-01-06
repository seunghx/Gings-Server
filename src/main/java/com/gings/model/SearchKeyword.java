package com.gings.model;

import lombok.Getter;
import lombok.Setter;

public class SearchKeyword {
    @Getter
    @Setter
    public static class SearchKeywordReq {
        private String keyword;
    }
}
