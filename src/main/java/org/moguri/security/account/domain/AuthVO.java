package org.moguri.security.account.domain;

import lombok.Data;
//권한 정보 관리
@Data
public class AuthVO {
    private String username; // admin
    private String auth; // ROLE_MEMBER
    private String MemberId;

}