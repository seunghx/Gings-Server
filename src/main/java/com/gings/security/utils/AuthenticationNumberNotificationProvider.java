package com.gings.security.utils;

/**
 * 
 * 사용자의 핸드폰 또는 이메일 등으로의 인증 번호 전달을 담당하는 메서드를 정의한 인터페이스.
 * 
 * 
 * @author leeseunghyun
 *
 */
public interface AuthenticationNumberNotificationProvider {

    // Methods
    // ==================================================================================================

    /**
     * 
     * @param destination - 핸드폰 번호 또는 이메일 주소 등 인증 번호가 전달 될 도착지 정보.
     * @param authenticationNumber - 전달할 인증 번호.
     *
     */
    public void sendAuthenticationNumber(String destination, String authenticationNumber);

}
