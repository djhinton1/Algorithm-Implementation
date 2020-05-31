import java.util.Random;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};
	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

// Helper Methods
//##################################################################################
	public boolean isZero(){
		for(byte b : val){
			if(b != 0) return false;
		}
		return true;
	}

	//only need a 2 byte array for this implementation
	public byte[] intToByteArray(int value) {
    return new byte[] {
            (byte)(value >>> 8),
            (byte) value};
}

public void extendRight(byte extension) {
	int i;
	byte[] newv = new byte[val.length + 1];
	for(i = 0; i < val.length; i++){
		newv[i] = val[i];
	}
	newv[i] = extension;
	val = newv;
}

public boolean isZeroOperand(HeftyInteger other){
	return this.isZero() || other.isZero();
}

public boolean isResultNegative(HeftyInteger other){
	return (this.isNegative() && !other.isNegative()) || (!this.isNegative() && other.isNegative());
}

// Multiply
//##################################################################################
	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		int n;
		if (isZeroOperand(other)) return new HeftyInteger(ZERO); //base case -> one of the operands is zero.
		boolean neg_res = isResultNegative(other); //determine if the result will be negative or positive

		//base case -> the operands are smaller than the accepted size (in this case one byte) so we can use naiveMultiply
		if (this.length() == 1 && other.length() == 1){
			System.out.println("inside the base case");
			HeftyInteger result = this.naiveMultiply(other);
			return result;
		}

		//ensure both integers are positive
		if (this.isNegative()) val = this.negate().getVal();
		if (other.isNegative()) other = other.negate();

		//if they are unequal sizes, then we need to pad the smaller byte array with zeros using the extend method
		int size_this = this.length();
		int size_other = other.length();
		if ((size_this % 2) != 0){
			this.extend((byte) 0);
			size_this += 1;
		}
		if ((size_other % 2) != 0){
			other.extend((byte) 0);
			size_other += 1;
		}
		int difference;
		if(size_this > size_other){
			difference = size_this - size_other;
			for(int i = difference; i > 0; i--){
				other.extend((byte) 0);
			}
		}
		if(size_other > size_this){
			difference = size_other - size_this;
			for(int i = difference; i > 0; i--){
				this.extend((byte) 0);
			}
		}

		n = this.length(); // set n equal to the byte length of the operands

		//get x_H
		byte[] xH = new byte[size_this / 2];
		for(int i = 0; i < (size_this / 2); i++){
			xH[i] = this.getVal()[i];
			System.out.println("XH: "+xH[0]);
		} HeftyInteger x_H = new HeftyInteger(xH);

		//get x_L
		byte[] xL = new byte[size_this / 2];
		for(int i = size_this-1; i >= (size_this / 2); i--){
			xL[i-(size_this/2)] = this.getVal()[i];
			System.out.println("XL: "+xL[0]);
		} HeftyInteger x_L = new HeftyInteger(xL);

		//get y_H
		byte[] yH = new byte[size_other / 2];
		for(int i = 0; i < (size_other / 2); i++){
			yH[i] = other.getVal()[i];
			System.out.println("YH: "+yH[0]);
		}	HeftyInteger y_H = new HeftyInteger(yH);

		//get y_L
		byte[] yL = new byte[size_other / 2];
		for(int i = size_this-1; i >= (size_other / 2); i--){
			yL[i-(size_this/2)] = other.getVal()[i];
			System.out.println("YL: "+yL[0]);
		}	HeftyInteger y_L = new HeftyInteger(yL);


		HeftyInteger a = x_H.multiply(y_H);
		System.out.println("a value: "+a.getVal()[0]);
		HeftyInteger d = x_L.multiply(y_L);
		System.out.println("d value: "+d.getVal()[0]);
		//HeftyInteger e = (((x_H.add(x_L)).multiply((y_H.add(y_L)))).subtract(a)).subtract(d);

		for(int i = 0; i < n; i++) a.extendRight((byte) 0);
	//	for(int i = 0; i < (n/2); i++) e.extendRight((byte) 0);

		//if(neg_res) return (a.add(e).add(d)).negate();

		//return a.add(e).add(d);
		return null;
	}

	// naiveMultiply
	//##################################################################################
	public HeftyInteger naiveMultiply(HeftyInteger other){
		int res = 0;
		boolean res_neg = isResultNegative(other);

		//ensure both integers are positive
		if (this.isNegative()) val = this.negate().getVal();
		if (other.isNegative()) other = other.negate();

		//prepare for multiplication
		int xThis;
		int b_digit;


		//need to account for negate causing Val to be 2 bytes
		if (this.length() > 1) xThis = (int) this.getVal()[1];
			else xThis = (int) this.getVal()[0];

		if (other.length() > 1) b_digit = (int) other.getVal()[0];
			else b_digit = (int) other.getVal()[0];

System.out.println(xThis);
System.out.println(b_digit);

		/*for(int i = 0; i < 8; i++){
			int current_bit = (b_digit >> i) & 1;
			if(current_bit == 1) {
				res = res += (xThis << i);
			}
		}*/
		res = xThis * b_digit;
		System.out.println(res);
		byte[] result = new byte[2];
		result[1] = (byte) (res & 0xFF);
		res = res >>> 8;
		result[0] = (byte) (res & 0xFF);
		System.out.println(result[0]);
		return new HeftyInteger(result);
	}

	// XGCD
	//##################################################################################
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		return null;
	 }
}
