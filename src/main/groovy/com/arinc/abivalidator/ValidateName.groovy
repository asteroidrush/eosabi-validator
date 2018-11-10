package com.arinc.abivalidator

/**
 * Created by Artem on 09.11.2018.
 */
trait ValidateName {
    abstract String getName()

    boolean isValidName() {
        return Abi.base32Validate(getName())
    }
}