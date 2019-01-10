package com.gings.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class GuestModel {
        @Getter
        @Setter
        public static class GuestModelReq{
            private int guestBoardId;
            private String content;
        }

        @Getter
        @Setter
        public static class GuestModelRes{
            private String content;
            private LocalDateTime time;
            private GuestModelUser guestModelUser;
        }

        @Getter
        @Setter
        public static class GuestModelUser{
            private int id;
            private String name;
            private String job;     //역할
            private String company;  //소속
            private String image;

        }



}
