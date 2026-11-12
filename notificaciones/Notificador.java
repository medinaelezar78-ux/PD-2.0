package notificaciones;

/** Contrato para enviar avisos; permite cambiar el envío real de correo sin tocar la lógica. */
public interface Notificador {
    void enviar(String destinatario, String asunto, String mensaje);
}
