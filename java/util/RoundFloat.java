package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This method rounds the input to 4 decimal places
 */

public class RoundFloat {

	public static double roundFloat(double input, int dp)
	{
		BigDecimal inputBigDecimal = new BigDecimal(input).setScale(dp, RoundingMode.HALF_UP);
		double output = inputBigDecimal.floatValue();
		return output;
	}
}
