public class Token {
    TokenType type;
    String value; 

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return type + ": " + value;
    }
}