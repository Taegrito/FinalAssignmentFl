
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
    
        System.out.println("==========================================================");
        System.out.println("       COMPILADOR E INTÉRPRETE DE REGLAS DE NEGOCIO       ");
        System.out.println("==========================================================");
        System.out.println("Cuando termines, escribe 'EJECUTAR' en una línea sola y presiona Enter:\n");

        StringBuilder inputCompleto = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("EJECUTAR")) {
                break; 
            }
            inputCompleto.append(line).append("\n");
        }

        String todoElTexto = inputCompleto.toString();

        String[] partes = todoElTexto.split("(?i)State:");
        String textoReglas = partes[0].trim();
        String textoEstado = partes.length > 1 ? partes[1].trim() : "";

        System.out.println("\n==========================================================");
        System.out.println("                       PROCESANDO                         ");
        System.out.println("==========================================================");

        try {
            Lexer lexer = new Lexer(textoReglas);
            List<Token> tokens = lexer.tokenize();

            System.out.println("\n[LEXER] Componentes léxicos encontrados (Tokens):");
            for (Token token : tokens) {
                System.out.println("  " + token);
            }

            Parser parser = new Parser(tokens);
            AST root = parser.parseProgram(); 
            
            System.out.println("\n--- ÁRBOL SINTÁCTICO ---");
            if (root != null) {
                root.print("");
            }
            System.out.println("------------------------\n");

            AnalisadorEstatico analyzer = new AnalisadorEstatico(); 
            analyzer.analyze(root);

            Map<String, Integer> variablesAmbiente = new HashMap<>();
            Set<String> hechosAmbiente = new HashSet<>();

            String[] lineasEstado = textoEstado.split("\n");
            for (String linea : lineasEstado) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                if (linea.contains("=")) {

                    String[] partesVariable = linea.split("=");
                    String nombreVar = partesVariable[0].trim();
                    int valorVar = Integer.parseInt(partesVariable[1].trim());
                    variablesAmbiente.put(nombreVar, valorVar);
                } else {
                    hechosAmbiente.add(linea);
                }
            }

            Interpreter interpreter = new Interpreter();
            Set<String> hechosResultantes = interpreter.execute(root, variablesAmbiente, hechosAmbiente);

            System.out.println("\n--- SALIDA DEL INTÉRPRETE ---");
            interpreter.printOutput(hechosResultantes);

        } catch (RuntimeException e) {
            System.err.println("\n ERROR DETECTADO: " + e.getMessage());
        }

        System.out.println("\n==========================================================");
        System.out.println("               FIN DEL PROCESO DE ANÁLISIS                ");
        System.out.println("==========================================================");
        
        scanner.close(); 
    }
}