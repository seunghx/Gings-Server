package com.gings.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

public class ModifyBoard {
    @Getter
    @Setter
    @NotBlank
    public static class ModifyBoardReq {
        private int boardId;
        private int writerId;

        private String title;
        private String content;
        private String category;

        private List<Integer> indexOfPrevImages;
        private List<MultipartFile> images;

        private List<Integer> indexOfPrevKeywords;
        private List<String> keywords;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpBoardRes {
    }
}
