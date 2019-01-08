package com.gings.utils.code;

import java.util.function.Predicate;


public enum ChatRoomType {
    
    OneToOne(num -> num == 2), 
    // 후에  단체 채팅방 정원이 생기면 바꿀 예정
    Group(num -> num <= Integer.MAX_VALUE);
    
    private final Predicate<Integer> capacityValidator;
    
    ChatRoomType(Predicate<Integer> capacityValidator){
        this.capacityValidator = capacityValidator;
    }
    
    public boolean isCapable(int capacity) {
        return capacityValidator.test(capacity);
    }
}
