import java.util.ArrayList;
import java.util.List;

public class Lexer {

    // Guarda todo el texto que ingresa el usuario
    String input;

    // Indica la posición actual dentro del texto
    int position;

    // Constructor del lexer
    // Recibe el texto que será analizado
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
    }

    // Método principal del lexer
    // Función que realiza el análisis léxico
    public List<Token> tokenize() {

        // Lista donde se almacenarán los tokens encontrados
        List<Token> tokens = new ArrayList<>();

        // Recorre el texto mientras no llegue al final
        while (position < input.length()) {

            // Obtiene el carácter actual
            char current = input.charAt(position);

            // Ignora espacios en blanco y saltos de línea
            if (Character.isWhitespace(current)) {
                position++;
                continue;
            }

            // Verificar si el carácter es una letra
            if (Character.isLetter(current)) {

                String word = readWord();

                // Verificar si la palabra es una keyword del lenguaje
                if (word.equals("rule")) {
                    tokens.add(new Token(TokenType.RULE, word));

                } else if (word.equals("if")) {
                    tokens.add(new Token(TokenType.IF, word));

                } else if (word.equals("then")) {
                    tokens.add(new Token(TokenType.THEN, word));

                } else if (word.equals("AND")) {
                    tokens.add(new Token(TokenType.AND, word));

                } else {

                    // Si no es keyword, se considera un identificador
                    tokens.add(new Token(TokenType.ID, word));
                }

                continue;
            }

            // Verificar si el carácter es un número
            if (Character.isDigit(current)) {

                String number = readNumber();

                // Agrega el número como token VALUE
                tokens.add(new Token(TokenType.VALUE, number));

                continue;
            }

            // Verifica símbolos especiales del lenguaje
            if (current == ':') {
                tokens.add(new Token(TokenType.COLON, ":"));

            } else if (current == '>') {
                tokens.add(new Token(TokenType.GREATER, ">"));

            } else if (current == '<') {
                tokens.add(new Token(TokenType.LESS, "<"));

            } else if (current == '=') {
                tokens.add(new Token(TokenType.EQUAL, "="));

            } else {

                // Si encuentra un símbolo inválido lanza un error
                throw new RuntimeException("Carácter inválido: " + current);
            }

            // Avanza a la siguiente posición
            position++;
        }

        // Agrega el token EOF para indicar final del archivo
        tokens.add(new Token(TokenType.EOF, "EOF"));

        return tokens;
    }

    // Método para leer palabras completas
    public String readWord() {

        String word = "";
        while (position < input.length() && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {

            word += input.charAt(position);
            position++;
        }

        return word;
    }

    // Método para leer números completos
    public String readNumber() {

        String number = "";
        while (position < input.length() && Character.isDigit(input.charAt(position))) {

            number += input.charAt(position);
            position++;
        }

        return number;
    }
}