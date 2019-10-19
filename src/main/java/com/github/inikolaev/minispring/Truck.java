package com.github.inikolaev.minispring;

import javax.inject.Inject;
import javax.inject.Named;

@Named("truck")
public class Truck {
    @Inject
    @Named("truckDriver")
    private Person driver;

    public Person getDriver() {
        return driver;
    }

    public void setDriver(Person driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return "Truck { driver = " + driver + " }";
    }
}
