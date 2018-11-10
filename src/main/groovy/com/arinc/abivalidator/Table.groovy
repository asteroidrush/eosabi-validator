package com.arinc.abivalidator

/**
 * Created by Artem on 09.11.2018.
 */
class Table implements ValidateName {
    String name
    String type
    String index_type
    List<String> key_names
    List<String> key_types

    static List<String> allowedPrimaryKeyTypes = [
            "i64",
            "uint64",
            "name",
    ]

    static List<String> allowedKeyTypes = [
            "i64",
            "i128",
            "i256",
            "float64",
            "float128",
            "sha256",
            "ripemd160",
            "name",

            "uint64",
            "uint128",
            "uint256",
    ]

    List<String> invalidKeyNames() {
        return key_names.findAll {!Abi.base32Validate(it)}
    }

    List<String> invalidKeyTypes() {
        return key_types.findAll {!allowedKeyTypes.contains(it)}
    }

    List<String> validate() {
        def errors = []
        if(!isValidName()) {
            errors << "Invalid table name: $name"
        }

        def invalidKeyNames = invalidKeyNames()
        if(invalidKeyNames){
            errors << "Table '$name' contains invalid index names: $invalidKeyNames"
        }

        def invalidKeyTypes = invalidKeyTypes()
        if(invalidKeyTypes){
            errors << "Table '$name' contains invalid index types: $invalidKeyTypes"
        }

        if(!allowedPrimaryKeyTypes.contains(index_type)) {
            errors << "Table '$name' has invalid index type: $index_type"
        }

        if(key_types.size() > 0 && !allowedPrimaryKeyTypes.contains(key_types[0])) {
            errors << "Table '$name' has invalid first index type: ${key_types[0]}"
        }

        return errors
    }
}
