package com.github.inikolaev.minispring;

import javax.inject.Inject;
import javax.inject.Named;

@Named("truckDriver")
public class Person {
    @Inject
    @Named("firstName")
    private String firstName;

    @Inject
    @Named("lastName")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Person { firstName = " + firstName + ", lastName = " + lastName + " }";
    }
}
