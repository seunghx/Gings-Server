package com.gings.model.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
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

        private List<String> prevImagesUrl;
        private List<MultipartFile> postImages;

        private List<String> prevKeywords;
        private List<String> postKeywords;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UpBoardRes {
    }
}
