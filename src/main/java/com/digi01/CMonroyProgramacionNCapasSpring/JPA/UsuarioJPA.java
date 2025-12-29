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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre")
    private String Nombre;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Column(name = "apellidopaterno")
    private String ApellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Column(name = "apellidomaterno")
    private String ApellidoMaterno;

    @NotBlank(message = "El usuario es obligatorio")
    @Column(name = "username")
    private String userName;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Column(name = "email")
    private String Email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(name = "password")
    private String Password;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Temporal(TemporalType.DATE)
    @Column(name = "fechanacimiento")
    private Date FechaNacimiento;

    @NotBlank(message = "El sexo es obligatorio")
    @Column(name = "sexo")
    private String Sexo;

    @NotBlank(message = "El celular es obligatorio")
    @Column(name = "celular")
    private String Celular;

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(name = "telefono")
    private String Telefono;

    @NotBlank(message = "La CURP es obligatoria")
    @Pattern(
            regexp = "^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[0-9]{2}$",
            message = "La CURP no tiene un formato válido"
    )

    @Column(name = "curp")
    private String Curp;

    @Lob
    @Column(name = "imagen")
    private String Imagen;

    @Column(name = "status")
    private Integer Status;

    @Column(name = "isverified")
    private Integer IsVerified;

    @Column(name = "verificationtoken")
    private String VerificationToken;

    @NotNull(message = "El rol es obligatorio")
    @ManyToOne
    @JoinColumn(name = "idrol")
    public RolJPA Rol;

    @OneToMany(mappedBy = "UsuarioJPA", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<DireccionJPA> DireccionesJPA = new ArrayList<>();

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer Status) {
        this.Status = Status;
    }

    public Integer getIsVerified() {
        return IsVerified;
    }

    public void setIsVerified(Integer IsVerified) {
        this.IsVerified = IsVerified;
    }

    public String getVerificationToken() {
        return VerificationToken;
    }

    public void setVerificationToken(String VerificationToken) {
        this.VerificationToken = VerificationToken;
    }

    public List<DireccionJPA> getDireccionesJPA() {
        return DireccionesJPA;
    }

    public void setDireccionesJPA(List<DireccionJPA> DireccionesJPA) {
        this.DireccionesJPA = DireccionesJPA;
    }

    public RolJPA getRol() {
        return Rol;
    }

    public void setRol(RolJPA Rol) {
        this.Rol = Rol;
    }

    public UsuarioJPA(int IdUsuario, String Nombre, String ApellidoPaterno, String ApellidoMaterno, String Email, String Password, Date FechaNacimiento, String Sexo, String Telefono, String Celular, String userName, String Curp, String Imagen, Integer Status, Integer IsVerified, String VerificationToken) {
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
        this.userName = userName;
        this.Curp = Curp;
        this.Imagen = Imagen;
        this.Status = Status;
        this.IsVerified = IsVerified;
        this.VerificationToken = VerificationToken;
    }

    public UsuarioJPA() {
    }

}
