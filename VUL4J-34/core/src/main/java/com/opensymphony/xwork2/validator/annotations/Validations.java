

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target( { ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Validations {

    
    CustomValidator[] customValidators() default {};

    ConversionErrorFieldValidator[] conversionErrorFields() default {};

    DateRangeFieldValidator[] dateRangeFields() default {};

    EmailValidator[] emails() default {};

    FieldExpressionValidator[] fieldExpressions() default {};

    IntRangeFieldValidator[] intRangeFields() default {};

    RequiredFieldValidator[] requiredFields() default {};

    RequiredStringValidator[] requiredStrings() default {};

    StringLengthFieldValidator[] stringLengthFields() default {};

    UrlValidator[] urls() default {};

    ConditionalVisitorFieldValidator[] conditionalVisitorFields() default {};

    VisitorFieldValidator[] visitorFields() default {};

    RegexFieldValidator[] regexFields() default {};

    ExpressionValidator[] expressions() default {};
}
