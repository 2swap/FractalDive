package com.twoswap.mandelbrot.extras;

import java.util.ArrayList;

public class Calculator {

	public static String calc(String input) {// TODO Fix order of operations and sin(pi)
		try {
			input = input.replaceAll("squareroot", ">").replaceAll("sqrt", ">").replaceAll("root", ">");
			input = input.replaceAll("cuberoot", "<").replaceAll("cbrt", "<");
			input = input.replaceAll("sin", "~").replaceAll("cos", ";").replaceAll("tan", ":");
			input = input.replaceAll("ln", "`").replaceAll("log", "`");
			input = input.replaceAll("pi", "" + Math.PI).replaceAll("phi", "((1 + (5 ^ .5)) / 2)").replaceAll("e", "" + Math.E);
			input = input.replaceAll("\\{", "(").replaceAll("\\[", "(").replaceAll("\\}", ")").replaceAll("\\]", ")");

			int counter = 0;
			for (int i = 0; i < input.length(); i++)
				if (input.charAt(i) == '(')
					counter++;
				else if (input.charAt(i) == ')')
					counter--;
			if (counter != 0)
				return "Parentheses Error"; // TEST FOR UNEVEN PARENTHESES COUNT

			while (input.contains("(")) {
				int firstPar = input.indexOf('('), lastPar = input.lastIndexOf(')');
				if (firstPar != -1 && lastPar != -1) {
					String calculated = calc(input.substring(firstPar + 1, lastPar));
					
					input = input.substring(0, firstPar) + ((Character.isDigit(calculated.charAt(0)) && Character.isDigit(input.charAt(firstPar - 1))) ? "*" : "") + calculated + ((Character.isDigit(calculated.charAt(calculated.length() - 1)) && (lastPar+1<input.length() && Character.isDigit(input.charAt(lastPar + 1)))) ? "*" : "") + input.substring(lastPar + 1, input.length());
				}
			} // SIMPLIFY PARENTHESES

			byte parsingNumber = 0;
			ArrayList<NumOp> numOps = new ArrayList<>();
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				if (Character.isDigit(c) || c == '.')
					parsingNumber = 1;
				else {
					if (parsingNumber == 1) {
						numOps.add(new NumOp(Double.parseDouble(input.substring(0, i))));
						input = input.substring(i);
						i = 0;
					}
					parsingNumber = 2;
					numOps.add(new NumOp(input.charAt(0)));
					input = input.substring(1);
					i = -1;
				}
			}
			if (parsingNumber == 1)
				numOps.add(new NumOp(Double.parseDouble(input)));

			for (int j = 1; j < numOps.size() - 1; j++) { // Fixing x*-ys
				if (numOps.get(j - 1).isOp && numOps.get(j).op == '-' && !numOps.get(j + 1).isOp) {
					numOps.remove(j);
					numOps.get(j).val *= -1;
				}
			}
			if (numOps.get(0).op == '-' && !numOps.get(1).isOp) {
				numOps.remove(0);
				numOps.get(0).val *= -1;
			}

			for (int j = 0; j < numOps.size(); j++) {
				if (numOps.get(j).op == '`') {
					numOps.remove(j);
					numOps.set(j, new NumOp(Math.log(numOps.get(j).val)));
				} else if (numOps.get(j).op == '>') {
					numOps.remove(j);
					numOps.set(j, new NumOp(Math.sqrt(numOps.get(j).val)));
				} else if (numOps.get(j).op == '<') {
					numOps.remove(j);
					numOps.set(j, new NumOp(Math.cbrt(numOps.get(j).val)));
				} else if (numOps.get(j).op == '~') {
					numOps.remove(j);
					numOps.set(j, new NumOp(Math.sin(numOps.get(j).val)));
				} else if (numOps.get(j).op == ';') {
					numOps.remove(j);
					numOps.set(j, new NumOp(Math.cos(numOps.get(j).val)));
				} else if (numOps.get(j).op == ':') {
					numOps.remove(j);
					numOps.set(j, new NumOp(Math.tan(numOps.get(j).val)));
				} // SIMPLIFY TRIGS AND LNS
			}

			for (int j = 1; j < numOps.size(); j++) // Normalize implicit multiplication
				if (!numOps.get(j).isOp && !numOps.get(j - 1).isOp)
					numOps.add(j, new NumOp('*'));

			for (int j = numOps.size() - 1; j > -1; j--) { // Evaluate Exponents
				if (numOps.get(j).op == '^') {
					double num1 = numOps.remove(j + 1).val;
					numOps.remove(j);
					double num2 = numOps.remove(j - 1).val;
					numOps.add(j - 1, new NumOp(Math.pow(num2, num1)));
				}
			}

			for (int j = 0; j < numOps.size(); j++) {
				if (numOps.get(j).op == '*') {
					double num1 = numOps.remove(j + 1).val;
					numOps.remove(j);
					double num2 = numOps.remove(j - 1).val;
					numOps.add(j - 1, new NumOp(num2 * num1));
					j--;
				}
				if (numOps.get(j).op == '/') {
					double num1 = numOps.remove(j + 1).val;
					numOps.remove(j);
					double num2 = numOps.remove(j - 1).val;
					numOps.add(j - 1, new NumOp(num2 / num1));
					j--;
				}
				if (numOps.get(j).op == '%') {
					double num1 = numOps.remove(j + 1).val;
					numOps.remove(j);
					double num2 = numOps.remove(j - 1).val;
					numOps.add(j - 1, new NumOp(num2 % num1));
				}
			}

			for (int j = 0; j < numOps.size(); j++) {
				if (numOps.get(j).op == '+') {
					double num1 = numOps.remove(j + 1).val;
					numOps.remove(j);
					double num2 = numOps.remove(j - 1).val;
					numOps.add(j - 1, new NumOp(num2 + num1));
					j--;
				}
				if (numOps.get(j).op == '-') {
					double num1 = numOps.remove(j + 1).val;
					numOps.remove(j);
					double num2 = numOps.remove(j - 1).val;
					numOps.add(j - 1, new NumOp(num2 - num1));
				}
			}
			return "" + numOps.get(0).val;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

}
