public class Token {

    // Guarda el tipo del token
    // Por ejemplo: RULE, IF, ID, VALUE, etc.
    TokenType type;

    // Guarda el valor original del token
    // Ejemplo: "rule", "temp", "30"
    String value; 

    // Constructor de la clase
    // Se utiliza para crear un token con su tipo y valor
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    // Método que retorna el valor del token
    public String getValue() {
        return this.value;
    }

    // Método toString para imprimir el token de forma más clara
    // Ejemplo: RULE: rule
    @Override
    public String toString() {
        return type + ": " + value;
    }
}