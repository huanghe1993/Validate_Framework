package com.huanghe;

import com.huanghe.model.Car;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class CarTest {

    private static Validator validator;

    @BeforeClass
    public static void setUpValidator(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void manufactureIsNull(){
        Car car = new Car(null,"aba",4);
        Set<ConstraintViolation<Car>> constraintViolations = validator.validate(car);
        System.out.println(1);
//        assertEuqals(1,constraintViolations.size());
//        assert("must not be null",constraintViolations.iterator().next().getMessage());

    }
}
