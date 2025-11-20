package com.digi01.CMonroyProgramacionNCapasSpring.RestController;

import com.digi01.CMonroyProgramacionNCapasSpring.JPA.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/demo")
public class DemoRestController {

    @GetMapping("saludo")
    public String Saludo() {
        return "Hola Mundo";
    }

    @GetMapping("saludo2")
    public String SaludoNombre(@RequestParam("Nombre") String nombre) {

        Result result = new Result();

        try {
            if (nombre.isEmpty()) {
                result.correct = false;
                result.errorMessage = "Nombre vacio";
                result.status = 400;
            }
            result.correct = true;
            result.status = 200;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = "Nombre no procesado";
            result.ex = ex;
            result.status = 500;
        }

        return "Hola " + nombre;
    }

    @GetMapping("division")
    public ResponseEntity Division(@RequestParam("NumeroUno") int numeroUno,
            @RequestParam("NumeroDos") int numeroDos) {
        Result result = new Result();

        try {

            if (numeroDos == 0) {
                result.correct = false;
                result.errorMessage = "Syntax ERROR";
                result.status = 400;
            } else {
                int division = numeroUno / numeroDos;
                result.correct = true;
                result.status = 200;

            }

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("multiplicacion")
    public ResponseEntity multiplicacion(@RequestParam("NumeroUno") int numeroUno,
            @RequestParam("NumeroDos") int numeroDos) {
        Result result = new Result();

        try {

            if (numeroUno < 0 || numeroDos < 0) {
                result.correct = false;
                result.errorMessage = "No pueden ser negativos";
                result.status = 400;
            } else {
                int multiplicacion = numeroUno * numeroDos;
                result.correct = true;
                result.status = 200;
            }

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return ResponseEntity.status(result.status).body(result);
    }

    @GetMapping("sumaArreglo")
    public ResponseEntity sumaArreglo(@RequestParam("numeros") int[] numeros) {
        Result result = new Result();

        try {

            if (numeros == null || numeros.length == 0) {
                result.correct = false;
                result.errorMessage = "El arreglo no puede estar vacío";
                result.status = 400;
            } else {
                int suma = 0;
                for (int n : numeros) {
                    suma += n;
                }

                result.correct = true;
                result.status = 200;
                result.object = suma;
            }

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return ResponseEntity.status(result.status).body(result);
    }

}
