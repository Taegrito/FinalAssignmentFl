public enum TokenType {

    // Palabras reservadas del lenguaje
    // Son instrucciones que tienen un significado especial
    RULE,
    IF,
    THEN,
    AND,

    // Símbolos utilizados en las condiciones
    COLON,
    GREATER,
    LESS,
    EQUAL,

    // Tipos de datos básicos del lenguaje
    // ID representa identificadores
    ID,

    // VALUE representa números enteros
    VALUE,

    // EOF significa End Of File indica el final de la entrada
    EOF
}