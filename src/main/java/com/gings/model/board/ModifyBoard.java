package com.gings.model.board;

import com.gings.utils.ImageExtension;
import com.gings.utils.code.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ModifyBoard {
    @Getter
    @Setter
    @NotBlank
    public static class ModifyBoardReq {
        private int boardId;

        private int writerId;
        private String writer;
        private String writerImage;

        @NotBlank
        private String title;
        @NotBlank
        private String content;
        @NotNull
        private BoardCategory category;

        private List<String> prevImagesUrl;
        private List<@ImageExtension MultipartFile> postImages;

        private List<String> prevKeywords;
        private List<String> postKeywords;

    }
}
