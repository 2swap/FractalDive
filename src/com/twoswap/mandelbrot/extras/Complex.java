package com.twoswap.mandelbrot.extras;

// Complex.java

/*
 * Copyright (c) 2003 Jon S. Squire.  All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the author or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. THE AUTHOR AND CONTRIBUTORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE
 * AS A RESULT OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL THE AUTHOR OR CONTRIBUTORS
 * OR SUCCEEDING LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA,
 * OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF THE AUTHOR
 * OR CONTRIBUTORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any human use medical device.
 */

/**
 * Immutable, complex numbers. A Complex consists of a real and imaginary part, called Cartesian coordinates.
 *
 * The Complex class provides methods for arithmetic such as: add, subtract, multiply, divide, negate and invert. Also provided are complex functions sin, cos, tan, asin, acos, atan, sqrt, log, exp, pow, sinh, cosh, tanh, atanh.
 *
 * Source code <a href="Complex.java">Complex.java</a>
 */
import net.jafama.*;

public strictfp class Complex extends Object {
	public double x, y; // Cartesian representation of complex

	/** cartesian coordinates real and imaginary are NaN */
	public Complex() {
		x = Double.NaN;
		y = Double.NaN;
	}

	/** construct a copy of a Complex object */
	public Complex(Complex z) {
		x = z.real();
		y = z.imaginary();
	}

	/** real value, imaginary=0.0 */
	public Complex(double x) {
		this.x = x;
		y = 0.0;
	}

	/** cartesian coordinates real and imaginary */
	public Complex(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/** convert cartesian to polar */
	public Complex polar() {
		double r = FastMath.sqrt(this.x * this.x + this.y * this.y);
		double a = FastMath.atan2(this.y, this.x);
		return new Complex(r, a);
	}

	/** convert polar to cartesian */
	public Complex cartesian() {
		return new Complex(this.x * FastMath.cos(this.y), this.x * FastMath.sin(this.y));
	}

	/** extract the real part of the complex number */
	public double real() {
		return this.x;
	}

	/** extract the imaginary part of the complex number */
	public double imaginary() {
		return this.y;
	}

	/** extract the magnitude of the complex number */
	public double magnitude() {
		return FastMath.sqrt(this.x * this.x + this.y * this.y);
	}

	/** extract the argument of the complex number */
	public double argument() {
		return FastMath.atan2(this.y, this.x);
	}

	/** add complex numbers */
	public Complex add(Complex z) {
		return new Complex(this.x + z.x, this.y + z.y);
	}

	/** add a double to a complex number */
	public Complex add(double d) {
		return new Complex(this.x + d, this.y);
	}

	/** subtract z from the complex number */
	public Complex subtract(Complex z) {
		return new Complex(this.x - z.x, this.y - z.y);
	}

	/** subtract the double d from the complex number */
	public Complex subtract(double d) {
		return new Complex(this.x - d, this.y);
	}

	/** negate the complex number */
	public Complex negate() {
		return new Complex(-this.x, -this.y);
	}

	/** multiply complex numbers */
	public Complex multiply(Complex z) {
		return new Complex(this.x * z.x - this.y * z.y, this.x * z.y + this.y * z.x);
	}

	/** multiply a complex number by a double */
	public Complex multiply(double d) {
		return new Complex(this.x * d, this.y * d);
	}

	/** divide the complex number by z */
	public Complex divide(Complex z) {
		double r = z.x * z.x + z.y * z.y;
		return new Complex((this.x * z.x + this.y * z.y) / r, (this.y * z.x - this.x * z.y) / r);
	}

	/** divide the complex number by the double d */
	public Complex divide(double d) {
		return new Complex(this.x / d, this.y / d);
	}

	/** invert the complex number */
	public Complex invert() {
		double r = this.x * this.x + this.y * this.y;
		return new Complex(this.x / r, -this.y / r);
	}

	/** conjugate the complex number */
	public Complex conjugate() {
		return new Complex(this.x, -this.y);
	}

	/** compute the absolute value of a complex number */
	public double abs() {
		return FastMath.sqrt(this.x * this.x + this.y * this.y);
	}

	/** compare complex numbers for equality */
	public boolean equals(Complex z) {
		return (z.x == this.x) && (z.y == this.y);
	}

	/**
	 * convert a complex number to a String. Complex z = new Complex(1.0,2.0); System.out.println("z="+z);
	 */
	public String toString() {
		return new String("(" + this.x + "," + this.y + ")");
	}

	/**
	 * convert text representation to a Complex. input format (real_double,imaginary_double)
	 */
	public static Complex parseComplex(String s) {
		int from = s.indexOf('(');
		if (from == -1)
			return null;
		int to = s.indexOf(',', from);
		double x = Double.parseDouble(s.substring(from + 1, to));
		from = to;
		to = s.indexOf(')', from);
		double y = Double.parseDouble(s.substring(from + 1, to));
		return new Complex(x, y);
	}

	/** compute e to the power of the complex number */
	public Complex exp() {
		double exp_x = FastMath.exp(this.x);
		return new Complex(exp_x * FastMath.cos(this.y), exp_x * FastMath.sin(this.y));
	}

	/** compute the natural logarithm of the complex number */
	public Complex log() {
		double rpart = FastMath.sqrt(this.x * this.x + this.y * this.y);
		double ipart = FastMath.atan2(this.y, this.x);
		if (ipart > FastMath.PI)
			ipart = ipart - 2.0 * FastMath.PI;
		return new Complex(FastMath.log(rpart), ipart);
	}

	/** compute the square root of the complex number */
	public Complex sqrt() {
		double r = FastMath.sqrt(this.x * this.x + this.y * this.y);
		double rpart = FastMath.sqrt(0.5 * (r + this.x));
		double ipart = FastMath.sqrt(0.5 * (r - this.x));
		if (this.y < 0.0)
			ipart = -ipart;
		return new Complex(rpart, ipart);
	}

	/** compute the complex number raised to the power z */
	public Complex pow(Complex z) {
		Complex a = z.multiply(this.log());
		return a.exp();
	}

	/** compute the complex number raised to the power double d */
	public Complex pow(double d) {
		Complex a = (this.log()).multiply(d);
		return a.exp();
	}

	/** compute the sin of the complex number */
	public Complex sin() {
		return new Complex(FastMath.sin(this.x) * cosh(this.y), FastMath.cos(this.x) * sinh(this.y));
	}

	/** compute the cosine of the complex number */
	public Complex cos() {
		return new Complex(FastMath.cos(this.x) * cosh(this.y), -FastMath.sin(this.x) * sinh(this.y));
	}

	/** compute the tangent of the complex number */
	public Complex tan() {
		return (this.sin()).divide(this.cos());
	}

	/** compute the arcsine of a complex number */
	public Complex asin() {
		Complex IM = new Complex(0.0, -1.0);
		Complex ZP = this.multiply(IM);
		Complex ZM = (new Complex(1.0, 0.0)).subtract(this.multiply(this)).sqrt().add(ZP);
		return ZM.log().multiply(new Complex(0.0, 1.0));
	}

	/** compute the arccosine of a complex number */
	public Complex acos() {
		Complex IM = new Complex(0.0, -1.0);
		Complex ZM = (new Complex(1.0, 0.0)).subtract(this.multiply(this)).sqrt().multiply(IM).add(this);
		return ZM.log().multiply(new Complex(0.0, 1.0));
	}

	/** compute the arctangent of a complex number */
	public Complex atan() {
		Complex IM = new Complex(0.0, -1.0);
		Complex ZP = new Complex(this.x, this.y - 1.0);
		Complex ZM = new Complex(-this.x, -this.y - 1.0);
		return IM.multiply(ZP.divide(ZM).log()).divide(2.0);
	}

	/** compute the hyperbolic sin of the complex number */
	public Complex sinh() {
		return new Complex(sinh(this.x) * FastMath.cos(this.y), cosh(this.x) * FastMath.sin(this.y));
	}

	/** compute the hyperbolic cosine of the complex number */
	public Complex cosh() {
		return new Complex(cosh(this.x) * FastMath.cos(this.y), sinh(this.x) * FastMath.sin(this.y));
	}

	/** compute the hyperbolic tangent of the complex number */
	public Complex tanh() {
		return (this.sinh()).divide(this.cosh());
	}

	/** compute the inverse hyperbolic tangent of a complex number */
	public Complex atanh() {
		return (((this.add(1.0)).log()).subtract(((this.subtract(1.0)).negate()).log()).divide(2.0));
	}

	// local - should be a good implementation in FastMath
	private double sinh(double x) {
		return (FastMath.exp(x) - FastMath.exp(-x)) / 2.0;
	}

	private double cosh(double x) {
		return (FastMath.exp(x) + FastMath.exp(-x)) / 2.0;
	}
}