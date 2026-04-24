package es.ual.dra.autodiagnostico.model.entitity.chat;

public enum ChatRoomType {
    SEGUIMIENTO;

    public static ChatRoomType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La sala de chat es obligatoria");
        }

        String normalized = value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return ChatRoomType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Sala invalida. Valor permitido: SEGUIMIENTO");
        }
    }
}
