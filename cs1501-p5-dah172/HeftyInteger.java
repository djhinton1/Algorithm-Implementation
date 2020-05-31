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

	public boolean isZeroOperand(HeftyInteger other){
		return this.isZero() || other.isZero();
	}

	public boolean isResultNegative(HeftyInteger other){
		return (this.isNegative() && !other.isNegative()) || (!this.isNegative() && other.isNegative());
	}

	public boolean isZero(){
		for(byte b : val){
			if(b != 0) return false;
		}
		return true;
	}

	//only need a 3 byte array for this implementation
	public byte[] intToByteArray(int value) {
		byte[] result = new byte[3];
		result[2] = (byte) (value & 0xFF);
		value = value >>> 8;
		result[1] = (byte) (value & 0xFF);
		value = value >>> 8;
		result[0] = (byte) (value & 0xFF);
		System.out.println(result[0] +" "+ result[1]+" "+result[2]);
		return result;
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

//MY CODE HERE
//##################################################################################
		/**
		 * Compute the product of this and other
		 * @param other HeftyInteger to multiply by this
		 * @return product of this and other
		 */
		 public HeftyInteger multiply(HeftyInteger other) {
		 	byte[] a, b;
			boolean res_neg = isResultNegative(other);
			HeftyInteger temp;

			//if either of the operands is zero then return zero
			if(isZeroOperand(other)) return new HeftyInteger(ZERO);

			if(this.isNegative()) temp = this.negate();
				else temp = this;
			if(other.isNegative()) other = other.negate();

		 	// If operands are of different sizes, put larger first ...
		 	if (temp.length() < other.length()) {
		 		a = other.getVal();
		 		b = temp.getVal();
		 	}
		 	else {
		 		a = temp.getVal();
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

		 	// Actually compute the multiply
		 	int carry = 0;
			HeftyInteger finalCarry = new HeftyInteger(ZERO);
		 	byte[] res = new byte[a.length + b.length];

		 	for (int i = b.length - 1; i >= 0; i--) {
				for(int j = a.length - 1; j >= 0; j--){
					// Be sure to bitmask so that cast of negative bytes does not
			 		//  introduce spurious 1 bits into result of cast
			 		carry = (((int) a[j] & 0xFF) * ((int) b[i] & 0xFF)) + carry + ((int) res[b.length + j - (b.length - 1 - i)] & 0xFF);

			 		// Assign to next byte
			 		res[b.length + j - (b.length - 1 - i)] = (byte) (carry & 0xFF);

			 		// Carry remainder over to next byte (always want to shift in 0s)
			 		carry = carry >>> 8;
				}

				//if there is carry over even after all of the multiplication
				if (carry != 0) {
					finalCarry.extend((byte) (carry & 0xFF));
					carry = 0;
				}
		 	}

		 	HeftyInteger res_li = new HeftyInteger(res);

			//align the final carry
			for(int i = 0; i < a.length; i++){
				finalCarry.extendRight((byte) 0);
			}

			//if final carry isnt zero then we will add the two
			if (!finalCarry.isZero()) { res_li.add(finalCarry);}

			//if it is negative (which it should not be because we are multiplying positive numbers)
			if (res_li.isNegative()) {
				res_li.extend((byte) 0);
			}

			//if the result should be negative, then make it so
			if(res_neg){
				res_li = res_li.negate();
			}
			return res_li;
		 }

		public HeftyInteger[] divide(HeftyInteger other){
			HeftyInteger temp;
			HeftyInteger a;
			HeftyInteger b;
			HeftyInteger c = new HeftyInteger(ZERO);
			HeftyInteger one = new HeftyInteger(ONE);
			HeftyInteger i = new HeftyInteger(ZERO);
			HeftyInteger[] result = new HeftyInteger[2];
			boolean res_neg = isResultNegative(other);

			if(other.isZero()) { System.out.println("Error");}

			if(this.isNegative()) temp = this.negate();
				else temp = this;
			if(other.isNegative()) other = other.negate();

			// If operands are of different sizes, put larger first ...
		 	if ((temp.subtract(other)).isNegative()) {
		 		a = other;
		 		b = temp;
		 	}
		 	else {
		 		a = temp;
		 		b = other;
		 	}

			while(!(a.subtract(c)).isNegative()){
				//System.out.println(c);
				i = i.add(one);
				c = b.multiply(i);
			}

			result[0] = i.subtract(one);
			if(res_neg){ result[0] = result[0].negate(); }
			result[1] = a.subtract(b.multiply(result[0]));

			return result;
		}



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
		 LinkedList theDivide = new LinkedList();
		 HeftyInteger a = this;
		 HeftyInteger b = other;
		 HeftyInteger d;
		 HeftyInteger temp;
		 HeftyInteger s = new HeftyInteger(ONE);
		 HeftyInteger t = new HeftyInteger(ZERO);
		 HeftyInteger[] divideResult;
		 HeftyInteger[] result = new HeftyInteger[3];

		 while(!b.isZero()){
			 divideResult = a.divide(b);
			 if (!divideResult[0].isZero()) { theDivide.add(divideResult[0]); System.out.println("added "+divideResult[0]); }
			 a = b;
			 b = divideResult[1];
		 }

		 result[0] = a;
		 d = a;


		 while(!theDivide.isEmpty()){
			 temp = s;
			 s = t;
			 t = temp.subtract((theDivide.remove()).multiply(s));
		 }

		 if(this.subtract(other).isNegative()){
			 result[1] = t;
			 result[2] = s;
		 } else{
			 result[1] = s;
			 result[2] = t;
		 }
		return result;
	 }

	 public String toString() {
		String a = "";
		for(byte b : val) {
			a = a +" "+String.format("%8s",Integer.toBinaryString(b & 0xFF)).replace(' ','0');
		}
		return a;
	}
}
