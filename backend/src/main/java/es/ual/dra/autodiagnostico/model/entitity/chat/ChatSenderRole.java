package es.ual.dra.autodiagnostico.model.entitity.chat;

public enum ChatSenderRole {
    MECANICO,
    USUARIO;

    public static ChatSenderRole from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El rol del remitente es obligatorio");
        }

        String normalized = value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return ChatSenderRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Rol invalido. Valores permitidos: MECANICO, USUARIO");
        }
    }
}
