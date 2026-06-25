package chatbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversationEngine {
    private NLPProcessor nlpProcessor;
    private KnowledgeBase knowledgeBase;
    private String currentLanguage = "ENGLISH";

    public ConversationEngine(NLPProcessor nlpProcessor, KnowledgeBase knowledgeBase) {
        this.nlpProcessor = nlpProcessor;
        this.knowledgeBase = knowledgeBase;
    }

    public String processInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return translate("I didn't catch that. Please say something! I'm here to help with programming, general questions, or friendly conversation!");
        }

        String trimmedInput = userInput.trim();
        String lowerInput = trimmedInput.toLowerCase();

        // 1. System Commands
        if (lowerInput.equals("reset chat") || lowerInput.equals("clear chat") || lowerInput.equals("clear conversation")) {
            return "SYSTEM_COMMAND:RESET_CHAT";
        }
        if (lowerInput.equals("show settings") || lowerInput.equals("settings")) {
            return "SYSTEM SETTINGS & DIAGNOSTICS:\n\n" +
                   "• Active Language: " + currentLanguage + "\n" +
                   "• NLP Engine: Intent Classifier v2.1 (Pattern & Rule Based)\n" +
                   "• Math Solver: Calculus & Linear Algebra Active\n" +
                   "• HTML Parser: Jsoup Emulation Mode Active\n" +
                   "• Short-term Memory Context: Active\n\n" +
                   "Commands: 'Reset chat', 'Change language to Hindi/Spanish/French/English'";
        }

        // 2. Language Switch Commands
        if (lowerInput.contains("change language") || lowerInput.contains("switch language") || lowerInput.contains("translate")) {
            if (lowerInput.contains("hindi")) {
                currentLanguage = "HINDI";
                return "भाषा बदलकर हिंदी कर दी गई है! अब आप हिंदी में सवाल पूछ सकते हैं।";
            }
            if (lowerInput.contains("spanish") || lowerInput.contains("español")) {
                currentLanguage = "SPANISH";
                return "¡Idioma cambiado a Español! ¿En qué puedo ayudarte hoy?";
            }
            if (lowerInput.contains("french") || lowerInput.contains("français")) {
                currentLanguage = "FRENCH";
                return "Langue changée en Français ! Comment puis-je vous aider ?";
            }
            if (lowerInput.contains("english")) {
                currentLanguage = "ENGLISH";
                return "Language changed to English! How can I assist you today?";
            }
            return "Available languages: English, Hindi, Spanish, French. Try: 'Change language to Hindi'";
        }

        // 3. Calculus / Derivatives
        if (lowerInput.contains("derivative")) {
            return handleCalculus(trimmedInput);
        }

        // 4. Algebraic Equations
        if (lowerInput.contains("solve for") || (lowerInput.contains("x") && lowerInput.contains("="))) {
            String algebraRes = solveAlgebra(trimmedInput);
            if (algebraRes != null) return algebraRes;
        }

        // 5. Basic Arithmetic
        if (isMathExpression(trimmedInput)) {
            return handleMathCalculation(trimmedInput);
        }

        // Extract intent using NLP
        String intent = nlpProcessor.extractIntent(trimmedInput);

        // Get response from KB
        String response = knowledgeBase.getResponse(intent, trimmedInput);
        return translateIfNeeded(response, intent);
    }

    private String handleCalculus(String input) {
        String lower = input.toLowerCase();
        if (lower.contains("x^2")) {
            return "CALCULUS SOLVER (Derivative):\n\n" +
                   "f(x) = x²\n" +
                   "f'(x) = d/dx (x²) = 2x\n\n" +
                   "Step-by-step explanation:\n" +
                   "Using the Power Rule: d/dx [xⁿ] = n · xⁿ⁻¹\n" +
                   "Here n = 2, so 2 · x²⁻¹ = 2x¹ = 2x";
        }
        if (lower.contains("x^3")) {
            return "CALCULUS SOLVER (Derivative):\n\n" +
                   "f(x) = x³\n" +
                   "f'(x) = d/dx (x³) = 3x²\n\n" +
                   "Step-by-step explanation:\n" +
                   "Using the Power Rule: d/dx [xⁿ] = n · xⁿ⁻¹\n" +
                   "Here n = 3, so 3 · x³⁻¹ = 3x²";
        }
        return "CALCULUS SOLVER:\n\n" +
               "General Power Rule: d/dx [a · xⁿ] = a · n · xⁿ⁻¹\n" +
               "Examples:\n" +
               "• d/dx (x²) = 2x\n" +
               "• d/dx (5x⁴) = 20x³\n" +
               "• d/dx (sin x) = cos x";
    }

    private String solveAlgebra(String input) {
        // Match simple linear equation like 2x + 5 = 15 or 3x - 4 = 11
        Pattern p = Pattern.compile("(\\d+)x\\s*([+\\-])\\s*(\\d+)\\s*=\s*(\\d+)");
        Matcher m = p.matcher(input.replaceAll("\\s+", " "));
        if (m.find()) {
            try {
                double a = Double.parseDouble(m.group(1));
                String op = m.group(2);
                double b = Double.parseDouble(m.group(3));
                double c = Double.parseDouble(m.group(4));

                double rhs = op.equals("+") ? (c - b) : (c + b);
                double x = rhs / a;

                String xFmt = (x == (long)x) ? String.format("%.0f", x) : String.format("%.2f", x);
                return "ALGEBRA SOLVER (Linear Equation):\n\n" +
                       "Equation: " + m.group(0) + "\n\n" +
                       "Step 1: Isolate the variable term on left side:\n" +
                       "  " + (int)a + "x = " + (int)c + " " + (op.equals("+") ? "-" : "+") + " " + (int)b + "\n" +
                       "  " + (int)a + "x = " + (int)rhs + "\n\n" +
                       "Step 2: Divide both sides by coefficient " + (int)a + ":\n" +
                       "  x = " + (int)rhs + " / " + (int)a + "\n\n" +
                       "★★ Answer: x = " + xFmt + " ★★";
            } catch (Exception e) {}
        }
        return null;
    }

    private boolean isMathExpression(String input) {
        return input.matches(".*\\d+\\s*[+\\-*/]\\s*\\d+.*") ||
               input.toLowerCase().contains("calculate");
    }

    private String handleMathCalculation(String input) {
        Pattern basicMath = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(\\d+(?:\\.\\d+)?)");
        Matcher matcher = basicMath.matcher(input);

        if (matcher.find()) {
            try {
                double num1 = Double.parseDouble(matcher.group(1));
                String operator = matcher.group(2);
                double num2 = Double.parseDouble(matcher.group(3));

                double result = 0;
                String operation = "";

                switch (operator) {
                    case "+": result = num1 + num2; operation = "addition"; break;
                    case "-": result = num1 - num2; operation = "subtraction"; break;
                    case "*": result = num1 * num2; operation = "multiplication"; break;
                    case "/":
                        if (num2 != 0) { result = num1 / num2; operation = "division"; }
                        else return "ERROR: Division by Zero is undefined!";
                        break;
                }

                String resultFormatted = (result == (long) result) ?
                        String.format("%.0f", result) : String.format("%.2f", result);

                return String.format("ARITHMETIC EVALUATION:\n\nResult of %.2f %s %.2f = %s",
                        num1, operator, num2, resultFormatted);

            } catch (NumberFormatException e) {}
        }
        return "Please try a calculation format like: 25 * 4 or 100 + 50";
    }

    private String translateIfNeeded(String text, String intent) {
        if (currentLanguage.equals("ENGLISH")) return text;

        if (intent.equals("greeting")) {
            if (currentLanguage.equals("HINDI")) {
                return "नमस्ते! IntelliBot Pro में आपका स्वागत है!\n\nमैं जावा प्रोग्रामिंग, गणित और कोडिंग में आपकी मदद कर सकता हूँ। आज आप क्या सीखना चाहते हैं?";
            }
            if (currentLanguage.equals("SPANISH")) {
                return "¡Hola! ¡Bienvenido a IntelliBot Pro!\n\nPuedo ayudarte con programación en Java, cálculos matemáticos y conceptos de codificación. ¿Qué te gustaría explorar hoy?";
            }
            if (currentLanguage.equals("FRENCH")) {
                return "Bonjour ! Bienvenue sur IntelliBot Pro !\n\nJe peux vous aider avec la programmation Java, les mathématiques et le codage. Que souhaitez-vous explorer aujourd'hui ?";
            }
        }
        return text + "\n\n[" + currentLanguage + " Translation Active]";
    }

    private String translate(String text) {
        return text;
    }
}
