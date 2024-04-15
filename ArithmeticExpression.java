package com.javarush.task.task34.task3403;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticExpression {
    public static void main(String[] args) {
        String expressionTest = "sin(90) * cos(260) / tan(32) + cot(20)";

        List<Lexeme> lexemes = lexAnalyze(expressionTest);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        System.out.printf("%s = %f\n", expressionTest, expr(lexemeBuffer));
    }

    public static double expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static double plusminus(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static double multdiv(LexemeBuffer lexemes) {
        double value = pow(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    value *= pow(lexemes);
                    break;
                case OP_DIV:
                    value /= pow(lexemes);
                    break;
                default:
                    lexemes.back();
                    return value;
            }
        }
    }

    public static double pow(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            if (lexeme.type == LexemeType.OP_POW) {
                value = Math.pow(value, factor(lexemes));
            } else {
                lexemes.back();
                return value;
            }
        }
    }

        public static double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case OP_MINUS:
                return -factor(lexemes);
            case OP_SIN:
                return Math.sin(Math.toRadians(factor(lexemes)));
            case OP_COS:
                return Math.cos(Math.toRadians(factor(lexemes)));
            case OP_TAN:
                return Math.tan(Math.toRadians(factor(lexemes)));
            case OP_COT:
                return 1 / Math.tan(Math.toRadians(factor(lexemes)));
            case NUMBER:
                return Double.parseDouble(lexeme.value);
            case LEFT_BRACKET:
                double value = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value +
                            " at position " + lexemes.getPos());

                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value +
                        " at position " + lexemes.getPos());
        }
    }


    public static List<Lexeme> lexAnalyze(String expTest) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expTest.length()) {
            char c = expTest.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                case '^':
                    lexemes.add(new Lexeme(LexemeType.OP_POW, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expTest.length()) {
                                break;
                            }
                            c = expTest.charAt(pos);
                        } while (c <= '9' && c >= '0' || c == '.');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else if (Character.isAlphabetic(c)) {
                        switch (expTest.substring(pos, pos + 3).toLowerCase()) {
                            case "sin":
                                lexemes.add(new Lexeme(LexemeType.OP_SIN, "sin"));
                                pos += 3;
                                continue;
                            case "cos":
                                lexemes.add(new Lexeme(LexemeType.OP_COS, "cos"));
                                pos += 3;
                                continue;
                            case "tan":
                                lexemes.add(new Lexeme(LexemeType.OP_TAN, "tan"));
                                pos += 3;
                                continue;
                            case "cot":
                                lexemes.add(new Lexeme(LexemeType.OP_COT, "cot"));
                                pos += 3;
                        }
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }
}
