package com.gings.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Detail> details = new ArrayList<>();

    public void addDetail(String target, String message) {

        details.add(new Detail(target, message));
    }

    /**
     * 
     * 바인딩 과정에 예외가 발생할 경우 예외가 발생한 위치와 해당 프로퍼티에 지정된 검증 메세지 정보를 담는다.
     * 
     * @author leeseunghyun
     *
     */
    private static class Detail {

        private final String target;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final String message;

        Detail(String target, String message) {
            this.target = target;
            this.message = message;
        }

        @SuppressWarnings("unused")
        public String getTarget() {
            return target;
        }

        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "[targetProperty=" + target + ", message=" + message + "]";
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[status=" + status + ", message=" + message + ", details=" + details + "]";
    }
}
