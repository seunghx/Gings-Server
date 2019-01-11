package com.gings.utils;

import javax.xml.soap.SAAJResult;

public class ResponseMessage {
    public static final String OKAY = "오케이";
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGIN_FAIL = "로그인 실패";

    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String NOT_FOUND_USER = "회원을 찾을 수 없습니다.";
    public static final String ALREADY_USER = "이미 존재하는 Email입니다.";
    public static final String CREATED_USER = "회원 가입 성공";
    public static final String FAIL_CREATE_USER = "회원 가입 실패";
    public static final String UPDATE_USER = "회원 정보 수정 성공";
    public static final String FAIL_UPDATE_USER = "회원 정보 수정 실패";
    public static final String DELETE_USER = "회원 탈퇴 성공";
    public static final String CHANGED_PWD = "비밀번호 변경 성공";
    //public static final String FAILED_TO_CHANGE_PWD = "비밀번호 변경 실패";
    public static final String OLD_PWD_IS_WRONG = "현재 비밀번호가 잘못 되었습니다.";
    public static final String PWD_CORRECT = "비밀번호가 일치합니다.";
    public static final String NOT_SAME_PWD = "두 비밀번호가 일치하지 않습니다.";


    public static final String UNQUALIFIED = "자격 없음";

    public static final String CREATE_GUESTBOARD = "게스트 보드 작성 성공";
    public static final String READ_GUESTBOARD = "게스트 보드 조회 성공";
    public static final String FAILED_TO_GET_GUESTBOARD = "게스트 보드 조회 실패";
    public static final String FAILED_TO_CREATE_GUESTBOARD = "게스트 보드 저장 실패";

    public static final String NO_INTRODUCE = "자기소개가 없습니다.";
    public static final String YES_INTRODUCE = "자기소개 조회 성공";
    public static final String CREATE_INTRODUCE = "자기소개 저장 성공";
    public static final String FAILED_TO_CREATE_INTRODUCE = "자기소개 저장 실패";
    public static final String NO_KEYWORD = "키워드가 없습니다.";
    public static final String NO_INTRODUCE_IMG = "자기소개 이미지가 없습니다.";
    public static final String UPDATED_INTRODUCE = "자기소개 수정 성공";
    public static final String FAILED_UPDATING_INTRODUCE = "자기소개 수정 실패";

    public static final String CREATED_PROFILE_IMG = "프로필 이미지 저장 성공";
    public static final String FAILED_TO_CREATE_PROFILE_IMG ="프로필 이미지 저장 실패";
    public static final String CANT_FIND_PROFILEIMG = "프로필 이미지를 찾을 수 없음";
    public static final String YES_PROFILEIMG = "프로필 조회 성공";

    public static final String CREATED_PROFILE_INFO = "프로필 정보 입력 성공";
    public static final String FAILED_TO_CREATE_PROFILE_INFO = "프로필 정보 입력 실패";
    public static final String CANT_FIND_PROFILE_INFO = "프로필 정보 조회 실패";
    public static final String YES_PROFILE_INFO = "프로필 정보 조회 성공";
    public static final String CREATED_PROFILE_KEYWORD = "프로필 정보 키워드 입력 성공";
    public static final String FAILED_TO_CREATE_PROFILE_KEYWORD = "프로필 정보 키워드 입력 실패";

    public static final String READ_ALL_BOARDS = "모든 보드 조회 성공";
    public static final String READ_BOARD = "보드 조회 성공";
    public static final String NOT_FOUND_BOARD = "보드가 존재하지 않습니다.";
    public static final String READ_BOARD_INFO = "보드 정보 조회 성공";
    public static final String NOT_FOUND_BOARD_INFO = "보드의 정보가 존재하지 않습니다.";
    public static final String CREATE_BOARD = "보드 작성 성공";
    public static final String FAIL_CREATE_BOARD = "보드 작성 실패";
    public static final String UPDATE_BOARD = "보드 수정 성공";
    public static final String FAIL_UPDATE_BOARD = "보드 수정 실패";
    public static final String DELETE_BOARD = "보드 삭제 성공";
    public static final String LIKE_BOARD = "보드 추천 성공";
    public static final String CANCEL_LIKE_BOARD = "보드 추천 해제 성공";
    public static final String SHARE_BOARD = "보드 공유 성공";
    public static final String BLOCK_BOARD = "보드 가리기 성공";
    public static final String CANCEL_BLOCK_BOARD = "보드 가리기 해제 성공";



    public static final String JOIN_CLUB = "클럽 가입 승인 신청 성공";
    public static final String JOIN_EVENT = "이벤트 참여 승인 신청 성공";
    public static final String READ_CLUB = "클럽 조회 성공";
    public static final String READ_EVENT = "이벤트 조회 성공";
    public static final String NOT_FOUND_EVENT = "이벤트가 존재하지 않습니다.";
    public static final String NOT_FOUND_CLUB = "클럽가 존재하지 않습니다.";
    public static final String READ_CLUB_INFO = "클럽 정보 조회 성공";
    public static final String NOT_FOUND_CLUB_INFO = "클럽의 정보가 존재하지 않습니다.";
    public static final String CREATE_CLUB = "클럽 작성 성공";
    public static final String FAIL_CREATE_CLUB = "클럽 작성 실패";
    public static final String UPDATE_CLUB = "클럽 수정 성공";
    public static final String FAIL_UPDATE_CLUB = "클럽 수정 실패";
    public static final String DELETE_CLUB = "클럽 삭제 성공";
    public static final String LIKE_CLUB = "클럽 추천 성공";
    public static final String CANCEL_LIKE_CLUB = "클럽 추천 해제 성공";
    public static final String FAIL_LIKE_CLUB = "클럽 추천/해제 실패";

    public static final String READ_ALL_REBOARD = "모든 리보드 조회 성공";
    public static final String READ_REBOARD = "리보드 조회 성공";
    public static final String NOT_FOUND_REBOARD = "리보드가 존재하지 않습니다.";
    public static final String CREATE_REBOARD = "리보드 작성 성공";
    public static final String FAIL_CREATE_REBOARD = "리보드 작성 실패";
    public static final String UPDATE_REBOARD = "리보드 수정 성공";
    public static final String FAIL_UPDATE_REBOARD = "리보드 수정 실패";
    public static final String DELETE_REBOARD = "리보드 삭제 성공";
    public static final String FAIL_DELETE_REBOARD = "리보드 삭제 실패";
    public static final String LIKE_REBOARD = "리보드 추천 성공";
    public static final String CANCEL_LIKE_REBOARD = "리보드 추천 해제 성공";

    public static final String SEARCH_DIRECTORY = "디렉토리 검색 성공";
    public static final String NO_SEARCH_RESULT = "검색 결과가 없습니다.";


    public static final String SEARCH_BOARD = "보드 검색 성공";

    public static final String AUTHORIZED = "인증 성공";
    public static final String UNAUTHORIZED = "인증 실패";
    public static final String FORBIDDEN = "인가 실패";

    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String SERVICE_UNAVAILABLE = "현재 서비스를 사용하실 수 없습니다. 잠시후 다시 시도해 주세요.";

    public static final String DB_ERROR = "데이터베이스 에러";
}
