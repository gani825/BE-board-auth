package com.green.boardauth.application.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserSignInReq {
    private String uid;
    private String upw;
}
