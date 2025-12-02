
package com.digi01.CMonroyProgramacionNCapasSpring.DTO;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.ErrorCarga;
import java.util.List;

public class ResultLog {
    private boolean correct;
    private int idLog;
    private String mensaje;
     private String token;
    private List<ErrorCarga> errores;


    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ErrorCarga> getErrores() {
        return errores;
    }

    public void setErrores(List<ErrorCarga> errores) {
        this.errores = errores;
    }
    
    
}

