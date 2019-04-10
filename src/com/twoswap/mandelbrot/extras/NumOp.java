package com.twoswap.mandelbrot.extras;

public class NumOp {

	public double val = 0;
	public char op = ' ';
	public boolean isOp = false;

	public NumOp(char op) {
		this.op = op;
		this.isOp = true;
	}

	public NumOp(double val) {
		this.val = val;
		this.isOp = false;
	}

	public String toString() {
		return "" + (this.isOp ? Character.toString(op) : val);
	}

}
