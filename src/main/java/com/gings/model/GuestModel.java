package com.gings.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GuestModel {
        @Getter
        @Setter
        public static class GuestModelReq{
            private int id;
            private String content;
        }

        @Getter
        @Setter
        public static class GuestModelRes{
            private GuestModelUser guestModelUser;
            private String content;
            private LocalDateTime time;
        }

        @Getter
        @Setter
        public static class GuestModelUser{
            private int id;
            private String name;
            private String image;
            private String company;  //소속
            private String job;     //역할
        }



}
