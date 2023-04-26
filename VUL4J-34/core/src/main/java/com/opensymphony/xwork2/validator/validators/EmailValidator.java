
package com.opensymphony.xwork2.validator.validators;



public class EmailValidator extends RegexFieldValidator {

	
    public static final String EMAIL_ADDRESS_PATTERN =
    	"\\b^['_a-z0-9-\\+]+(\\.['_a-z0-9-\\+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2}|aero|arpa|asia|biz|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|nato|net|org|pro|tel|travel|xxx|tech)$\\b";

    public EmailValidator() {
        setRegex(EMAIL_ADDRESS_PATTERN);
        setCaseSensitive(false);
    }

}
