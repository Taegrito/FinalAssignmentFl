# Final project: Rule-based Language with Execution and Analysis
An end-to-end implementation of a custom rule-based language system, built from scratch.

## Authors
* Ana Isabella Gómez García
* Andrés Rengifo Zapata

## Environment and tools
* _Operating system:_ Windows 11
* _Programming language:_ Java
* _Compiler:_ JDK
* _Workbench:_ BlueJ 5.5.0

## Project architecture
1. **_Lexer:_** Tokenizies the input stream and recognize keywords, rules, simbols and integer
2. **_Parser:_** A hand-written parser that validates the program structure according to th formal grammar specifications.
3. **_AST builder:_** Constructs a structurally equivalent representation of the rules, conditional conjunctions, comparisons, and actions
4. **_interpreter:_** Iteratively evaluates rules using an environment mapping variables to integers and managing active facts. It runs until a fixed point is achieved
5. **_Static Analysis:_** Detects rule conflicts, redundancies, and potentially inactive execution paths

## Execution

1. Clone this repository:

```bash 
git clone https://github.com/Taegrito/FinalAssignmentFl.git
```
2. Open the project in a Java compiler such as BlueJ or visual studio

3. Run the code
## Video for more information
```bash
https://youtu.be/gYIzL1GAWdM
```
