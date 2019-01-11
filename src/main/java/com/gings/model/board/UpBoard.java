package com.gings.model.board;

import com.gings.domain.board.BoardReply;
import com.gings.utils.ImageExtension;
import com.gings.utils.code.BoardCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class UpBoard {
    @Getter
    @Setter
    public static class UpBoardReq {
        private int boardId;
        private int writerId;

        @NotBlank
        private String title;
        @NotBlank
        private String content;

        @NotNull
        private BoardCategory category;

        private List<MultipartFile> images;
        private List<String> keywords;

        private int recommender;
        private int share;

        private boolean likeChk;

    }
}
