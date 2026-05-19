import java.util.ArrayList;
import java.util.List;

public class Lexer {

    String input;
    int position;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
    }

    public List<Token> tokenize() {

        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {

            char current = input.charAt(position);

            if (Character.isWhitespace(current)) {
                position++;
                continue;
            }

            if (Character.isLetter(current)) {

                String word = readWord();

                if (word.equals("rule")) {
                    tokens.add(new Token(TokenType.RULE, word));

                } else if (word.equals("if")) {
                    tokens.add(new Token(TokenType.IF, word));

                } else if (word.equals("then")) {
                    tokens.add(new Token(TokenType.THEN, word));

                } else if (word.equals("AND")) {
                    tokens.add(new Token(TokenType.AND, word));

                } else {
                    tokens.add(new Token(TokenType.ID, word));
                }

                continue;
            }

            if (Character.isDigit(current)) {

                String number = readNumber();

                tokens.add(new Token(TokenType.VALUE, number));

                continue;
            }
            if (current == ':') {
                tokens.add(new Token(TokenType.COLON, ":"));

            } else if (current == '>') {
                tokens.add(new Token(TokenType.GREATER, ">"));

            } else if (current == '<') {
                tokens.add(new Token(TokenType.LESS, "<"));

            } else if (current == '=') {
                tokens.add(new Token(TokenType.EQUAL, "="));

            } else {
                throw new RuntimeException(
                        "Carácter inválido: " + current
                );
            }

            position++;
        }

        tokens.add(new Token(TokenType.EOF, "EOF"));

        return tokens;
    }

    public String readWord() {

        String word = "";

        while (position < input.length() &&
                (Character.isLetterOrDigit(input.charAt(position))
                        || input.charAt(position) == '_')) {

            word += input.charAt(position);
            position++;
        }

        return word;
    }

    public String readNumber() {

        String number = "";

        while (position < input.length() &&
                Character.isDigit(input.charAt(position))) {

            number += input.charAt(position);
            position++;
        }

        return number;
    }
}