
package com.digi01.CMonroyProgramacionNCapasSpring.JPA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = "USUARIO")
@DynamicUpdate
public class UsuarioJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private int IdUsuario;

    @Column(name = "nombre")
    private String Nombre;

    @Column(name = "apellidopaterno")
    private String ApellidoPaterno;

    @Column(name = "apellidomaterno")
    private String ApellidoMaterno;

    @Column(name = "email")
    private String Email;

    @Column(name = "password")
    private String Password;

    @Temporal(TemporalType.DATE)
    @Column(name = "fechanacimiento")
    private Date FechaNacimiento;

    @Column(name = "sexo")
    private String Sexo;

    @Column(name = "telefono")
    private String Telefono;

    @Column(name = "celular")
    private String Celular;

    @Column(name = "username")
    private String Username;

    @Column(name = "curp")
    private String Curp;

    @Lob
    @Column(name = "imagen")
    private String Imagen;

    @ManyToOne
    @JoinColumn(name = "idrol")
    public RolJPA Rol;

    @OneToMany(mappedBy = "UsuarioJPA", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<DireccionJPA> DireccionesJPA = new ArrayList<>() ;

    public int getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(int IdUsuario) {
        this.IdUsuario = IdUsuario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getApellidoPaterno() {
        return ApellidoPaterno;
    }

    public void setApellidoPaterno(String ApellidoPaterno) {
        this.ApellidoPaterno = ApellidoPaterno;
    }

    public String getApellidoMaterno() {
        return ApellidoMaterno;
    }

    public void setApellidoMaterno(String ApellidoMaterno) {
        this.ApellidoMaterno = ApellidoMaterno;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public Date getFechaNacimiento() {
        return FechaNacimiento;
    }

    public void setFechaNacimiento(Date FechaNacimiento) {
        this.FechaNacimiento = FechaNacimiento;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String Sexo) {
        this.Sexo = Sexo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String Celular) {
        this.Celular = Celular;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getCurp() {
        return Curp;
    }

    public void setCurp(String Curp) {
        this.Curp = Curp;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String Imagen) {
        this.Imagen = Imagen;
    }
    
    

    public UsuarioJPA(int IdUsuario, String Nombre, String ApellidoPaterno, String ApellidoMaterno, String Email, String Password, Date FechaNacimiento, String Sexo, String Telefono, String Celular, String Username, String Curp, String Imagen) {
        this.IdUsuario = IdUsuario;
        this.Nombre = Nombre;
        this.ApellidoPaterno = ApellidoPaterno;
        this.ApellidoMaterno = ApellidoMaterno;
        this.Email = Email;
        this.Password = Password;
        this.FechaNacimiento = FechaNacimiento;
        this.Sexo = Sexo;
        this.Telefono = Telefono;
        this.Celular = Celular;
        this.Username = Username;
        this.Curp = Curp;
        this.Imagen = Imagen;
    }
    
    public UsuarioJPA(){
    }

   
}
