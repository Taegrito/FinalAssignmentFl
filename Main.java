import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        // Scanner para leer datos desde consola
        Scanner scanner = new Scanner(System.in);

        // Mensaje inicial del programa
        System.out.println("==========================================================");
        System.out.println("       COMPILADOR E INTÉRPRETE DE REGLAS DE NEGOCIO       ");
        System.out.println("==========================================================");
        System.out.println("Cuando termines, escribe 'EJECUTAR' en una línea sola y presiona Enter:\n");

        // StringBuilder para almacenar todo el texto ingresado
        StringBuilder inputCompleto = new StringBuilder();

        // Lee línea por línea hasta encontrar la palabra EJECUTAR
        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();

            // Si el usuario escribe EJECUTAR termina la lectura
            if (line.trim().equalsIgnoreCase("EJECUTAR")) {
                break;
            }

            // Guarda la línea dentro del StringBuilder
            inputCompleto.append(line).append("\n");
        }

        // Convierte todo el contenido a String
        String todoElTexto = inputCompleto.toString();

        // Divide el texto en dos partes:
        // reglas y estado del ambiente
        String[] partes = todoElTexto.split("(?i)State:");

        // Primera parte: reglas
        String textoReglas = partes[0].trim();

        // Segunda parte: variables y hechos del ambiente
        String textoEstado = partes.length > 1 ? partes[1].trim() : "";

        System.out.println("\n==========================================================");
        System.out.println("                       PROCESANDO                         ");
        System.out.println("==========================================================");

        try {

            // =========================
            // FASE 1: LEXER
            // =========================

            // Se crea el lexer con las reglas ingresadas
            Lexer lexer = new Lexer(textoReglas);

            // Convierte el texto en tokens
            List<Token> tokens = lexer.tokenize();

            // Imprime todos los tokens encontrados
            System.out.println("\n[LEXER] Componentes léxicos encontrados (Tokens):");

            for (Token token : tokens) {
                System.out.println("  " + token);
            }

            // =========================
            // FASE 2: PARSER
            // =========================

            // Se crea el parser usando la lista de tokens
            Parser parser = new Parser(tokens);

            // Construye el árbol sintáctico AST
            AST root = parser.parseProgram();

            // Muestra el árbol sintáctico en consola
            System.out.println("\n--- ÁRBOL SINTÁCTICO ---");

            if (root != null) {
                root.print("");
            }

            System.out.println("------------------------\n");

            // =========================
            // FASE 3: ANÁLISIS ESTÁTICO
            // =========================

            // Se crea el analizador estático
            AnalisadorEstatico analyzer = new AnalisadorEstatico();

            // Analiza posibles errores lógicos
            analyzer.analyze(root);

            // ===================================================
            // FASE 4: PREPARAR AMBIENTE (CON NOMBRE CORREGIDO)
            // ===================================================
            Map<String, Integer> variablesAmbiente = new HashMap<>();
            Set<String> hechosAmbiente = new HashSet<>();

            // Control de seguridad: Si el estado está vacío, evitamos procesar tokens
            if (textoEstado != null && !textoEstado.isEmpty()) {

                // 1. Estandarizamos absolutamente todos los saltos de línea y tabulaciones
                textoEstado = textoEstado.replace("\r", " ").replace("\n", " ").replace("\t", " ");

                // 2. Cambiamos el nombre a 'tokensEstado' para evitar conflictos de duplicado
                String[] tokensEstado = textoEstado.trim().split("\\s+");

                int i = 0;
                while (i < tokensEstado.length) {
                    String tokenActual = tokensEstado[i].trim();
                    if (tokenActual.isEmpty()) {
                        i++;
                        continue;
                    }

                    // CASO 1: Estructura explícita con igual -> variable = valor (Ej: temp = 35)
                    if (i + 2 < tokensEstado.length && tokensEstado[i + 1].equals("=")) {
                        String nombreVar = tokenActual;
                        String valorStr = tokensEstado[i + 2];
                        try {
                            int valorVar = Integer.parseInt(valorStr);
                            variablesAmbiente.put(nombreVar, valorVar);
                        } catch (NumberFormatException e) {
                            hechosAmbiente.add(nombreVar);
                        }
                        i += 3; // Consumimos la terna: variable, =, valor
                    } 
                    
                    // CASO 2: Estructura directa separada solo por espacio -> variable valor (Ej: humidity 40)
                    else if (i + 1 < tokensEstado.length && tokensEstado[i + 1].matches("\\d+")) {
                        String nombreVar = tokenActual;
                        try {
                            int valorVar = Integer.parseInt(tokensEstado[i + 1]);
                            variablesAmbiente.put(nombreVar, valorVar);
                        } catch (NumberFormatException e) {
                            hechosAmbiente.add(nombreVar);
                        }
                        i += 2; // Consumimos el par: variable, valor
                    } 
                    
                    // CASO 3: Hecho simple aislado (Ej: alert, fan_on, o "a")
                    else {
                        if (!tokenActual.equals("=")) {
                            hechosAmbiente.add(tokenActual);
                        }
                        i++; // Avanzamos al siguiente token individual
                    }
                }
            }

            // =========================
            // FASE 5: INTERPRETER
            // =========================

            // Se crea el intérprete
            Interpreter interpreter = new Interpreter();

            // Ejecuta las reglas usando el AST y el ambiente
            Set<String> hechosResultantes = interpreter.execute(root, variablesAmbiente, hechosAmbiente);

            // 1. IMPRIMIR OUTPUT (Ej: fan_on o alert)
            // Quitamos el banner decorativo "--- SALIDA DEL INTÉRPRETE ---" para cumplir con el formato estricto
            // El enunciado pide únicamente los identificadores de hechos o (no output)
            interpreter.printOutput(hechosResultantes);

            // 2. IMPRIMIR ANALYSIS (Ej: Action fan_on generated by r1, r2)
            // Evaluamos e imprimimos las métricas al final de todo el proceso de salida
            // Pasamos hechosResultantes en lugar de hechosAmbiente iniciales para conocer el estado final
            interpreter.printAnalysis(root, hechosResultantes, variablesAmbiente, hechosAmbiente);

        } catch (RuntimeException e) {

            // Captura errores léxicos, sintácticos o semánticos
            System.err.println("\n ERROR DETECTADO: " + e.getMessage());
        }

        // Mensaje final
        System.out.println("\n==========================================================");
        System.out.println("               FIN DEL PROCESO DE ANÁLISIS                ");
        System.out.println("==========================================================");
        scanner.close();
    }
}