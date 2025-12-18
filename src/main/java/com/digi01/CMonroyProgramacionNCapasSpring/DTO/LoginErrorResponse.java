
package com.digi01.CMonroyProgramacionNCapasSpring.DTO;


public class LoginErrorResponse {

    private String message;
    private String code;

    public LoginErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}

