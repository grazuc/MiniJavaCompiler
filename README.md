# MiniJava Compiler - EN

This repository contains a complete implementation of a **MiniJava Compiler**, developed as part of an academic project. MiniJava is a simplified subset of Java, designed to exclude advanced features such as generics, lambdas, and multithreading, while maintaining core language constructs such as classes, methods, and primitive types.

## Structure

The compiler is structured into five progressive phases:
1. **Lexical Analysis**: Tokenizes the source code into meaningful units.
2. **Syntactic Analysis**: Validates the syntax by constructing a parse tree.
3. **Semantic Analysis - Symbol Tables and Declaration Checking**: Includes symbol table creation, type checking, and validation of declarations.
4. **Semantic Analysis - Abstract Syntax Trees (AST)**: Builds a high-level tree representation for semantic validation and further processing.
5. **Intermediate Code Generation**: Produces intermediate code for a valid MiniJava program.

## Features
- Modular design with well-documented phases.
- Comprehensive error detection for lexical, syntactic, and semantic errors.
- Intermediate code generation for MiniJava programs.

## Project Structure
- **Phases**: Each compiler phase is contained in its respective folder.
- **Documentation and Project Resources**: Contains reference material, syntax rules and implementation details for MiniJava.

## Language and Tools
The project is implemented in **Java 11**.

---

# Compilador de MiniJava - ES

Este repositorio contiene una implementación completa de un **Compilador de MiniJava**, desarrollado como parte de un proyecto académico. MiniJava es un subconjunto simplificado de Java, diseñado para excluir funciones avanzadas como genéricos, lambdas e hilos, mientras conserva las estructuras principales del lenguaje como clases, métodos y tipos primitivos.

## Estructura

El compilador está estructurado en cinco fases progresivas:
1. **Análisis Léxico**: Tokeniza el código fuente en unidades significativas.
2. **Análisis Sintáctico**: Valida la sintaxis construyendo un árbol de análisis.
3. **Análisis Semántico - Tablas de Símbolos y Chequeo de Declaraciones**: Incluye la creación de tablas de símbolos, verificación de tipos y validación de declaraciones.
4. **Análisis Semántico - Árbol Sintáctico Abstracto (AST)**: Construye una representación en forma de árbol de alto nivel para validación semántica y procesamiento posterior.
5. **Generación de Código Intermedio**: Produce código intermedio para un programa MiniJava válido.

## Características
- Diseño modular con fases bien documentadas.
- Detección integral de errores léxicos, sintácticos y semánticos.
- Generación de código intermedio para programas MiniJava.

## Estructura del Proyecto
- **Fases**: Cada fase del compilador se encuentra en su carpeta correspondiente.
- **Documentación y Recursos del Proyecto**: Contiene material de referencia, reglas de sintaxis y detalles de implementación para MiniJava.

## Lenguaje y Herramientas
El proyecto está implementado en **Java 11**.

