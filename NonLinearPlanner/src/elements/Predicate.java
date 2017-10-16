package elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will create generic predicates objects. These can be used together with the GenericOperators
 * class in order to define our system.
 * @author paubr
 *
 */
public class Predicate {
	public static enum PredicateType{
		ON_TABLE, ON, CLEAR, EMPTY_ARM, HOLDING, HEAVIER, LIGHT_BLOCK, USED_COLS_NUM_OK,  USED_COLS_NUM_INC,  USED_COLS_NUM_DEC
	}
	
	public static PredicateType findType(String predicate) {
		Matcher matcher = Pattern.compile("(.*)\\(.*").matcher(predicate);
		if(matcher.matches()) {
			String temp = matcher.group(1);
			switch(temp) {
			case "ON-TABLE":
				return PredicateType.ON_TABLE;
			case "ON":
				return PredicateType.ON;
			case "CLEAR":
				return PredicateType.CLEAR;
			case "EMPTY-ARM":
				return PredicateType.EMPTY_ARM;
			case "HOLDING":
				return PredicateType.HOLDING;
			case "HEAVIER":
				return PredicateType.HEAVIER;
			case "LIGHT-BLOCK":
				return PredicateType.LIGHT_BLOCK;
			case "USED-COLS-NUM":
				if(predicate.equals("USED-COLS-NUM(n) n>0")) {
					return PredicateType.USED_COLS_NUM_OK;
				}else if(predicate.equals("USED-COLS-NUM(n+1)")) {
					return PredicateType.USED_COLS_NUM_INC;
				}else if(predicate.equals("USED-COLS-NUM(n-1)")) {
					return PredicateType.USED_COLS_NUM_DEC;
				}
			}
		}
		return null;
	}	
}
