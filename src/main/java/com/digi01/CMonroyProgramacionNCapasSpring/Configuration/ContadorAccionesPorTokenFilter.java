package com.digi01.CMonroyProgramacionNCapasSpring.Configuration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContadorAccionesPorTokenFilter implements Filter {

    // token -> acciones usadas
    private final Map<String, Integer> usosPorToken = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String method = req.getMethod();

        System.out.println("➡ Revisando ruta: " + method + " " + path);

        // 1) Rutas que nunca cuentan
        if (esRutaExenta(path, method)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Solo contamos acciones para métodos que modifican datos
        if (!esMetodoQueCuenta(method) || !path.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        // 3) Leemos el token del header Authorization
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Sin token: dejamos que seguridad maneje
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // quitar "Bearer "

        // 4) Revisar cuántas acciones lleva este token
        int usadas = usosPorToken.getOrDefault(token, 0);

        if (usadas >= 5) {
            System.out.println("❌ Token ha superado el límite de 5 acciones");
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write("Límite de acciones alcanzado para este token. Inicia sesión de nuevo.");
            return;
        }

        // 5) Consumir una acción
        usadas++;
        usosPorToken.put(token, usadas);

        System.out.println("🔥 Token " + token.substring(0, 15) + "... -> acciones usadas: "
                + usadas + "/5");

        chain.doFilter(request, response);
    }

    private boolean esRutaExenta(String path, String method) {
        // GET nunca cuenta
        if (method.equalsIgnoreCase("GET")) return true;

        // rutas de auth / estáticos etc
        return path.startsWith("/login")
                || path.startsWith("/auth")
                || path.startsWith("/guardarToken")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/img")
                || path.startsWith("/images")
                || path.startsWith("/webjars")
                || path.startsWith("/error")
                || path.equals("/");
    }

    private boolean esMetodoQueCuenta(String method) {
        return switch (method) {
            case "POST", "PUT", "PATCH", "DELETE" -> true;
            default -> false;
        };
    }
}
