package com.gings.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MessageConfirm {
    
    @Getter
    @Setter
    @ToString
    public static class LastReadConfirm {
        private int messageId;
    }
    
    @Getter
    @Setter
    @ToString
    public static class LatestReceivedConfirm {
        private int messageId;
    }
    
}
