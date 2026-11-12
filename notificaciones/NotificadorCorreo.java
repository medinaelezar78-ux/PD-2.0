package notificaciones;

import datos.Configuracion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Notificador por correo. Aquí el envío es simulado (imprime y guarda un historial);
 * para correo real basta reemplazar enviar() por JavaMail/Jakarta Mail.
 */
public class NotificadorCorreo implements Notificador {
    private final Configuracion config;
    private final List<String> historial = new ArrayList<>();

    public NotificadorCorreo(Configuracion config) {
        this.config = config;
    }

    @Override
    public void enviar(String destinatario, String asunto, String mensaje) {
        String registro = String.format("De: %s | Para: %s | Asunto: %s%n    %s",
                config.getCorreoRemitente(), destinatario, asunto, mensaje);
        historial.add(registro);
        System.out.println("[CORREO] " + registro);
    }

    public List<String> getHistorial() { return Collections.unmodifiableList(historial); }
}
