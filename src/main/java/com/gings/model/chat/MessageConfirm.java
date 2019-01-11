package com.gings.model.chat;

import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MessageConfirm {
    
    @Getter
    @Setter
    @ToString
    public static class LastReadConfirm {
        @Positive
        private int messageId;
    }
    
    @Getter
    @Setter
    @ToString
    public static class LatestReceiveConfirm {
        @Positive
        private int messageId;
    }
    
}
