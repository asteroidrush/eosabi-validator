package com.arinc.abivalidator

/**
 * Created by Artem on 09.11.2018.
 */
class Abi {

    static List<String> defaultTypes = [
            "bool",
            "uint8",
            "uint16",
            "uint32",
            "uint64",
            "uint128",
            "int8",
            "int16",
            "int32",
            "int64",
            "int128",
            "varint32",
            "varuint32",

            "float32",
            "float64",
            "float128",

            "time_point",
            "time_point_sec",
            "block_timestamp_type",

            "name",

            "bytes",
            "string",

            "checksum160",
            "checksum256",
            "checksum512",

            "public_key",
            "signature",

            "symbol",
            "symbol_code",
            "asset",
            "extended_asset",


    ]

    static boolean base32Validate(String str) {
        return str.matches(/^[a-z1-5]+$/) && str.length() < 13
    }

    String ____comment
    String version
    List<Struct> structs
    List<Type> types
    List<Action> actions
    List<Table> tables
    List<Map> ricardian_clauses
    List<Map> abi_extensions

    List<String> getStructTypes() {
        List<List<String>> listOfLists = structs.collect { struct ->
            struct.fields.collect { field ->
                field.type.replace("[]", "")
            }
        }
        return listOfLists.flatten().unique() as List<String>
    }

    List<String> getDeclaredTypes() {
        return types*.new_type_name
    }

    List<String> getUnknownTypes() {
        return structTypes - declaredTypes - structNames - defaultTypes
    }

    List<String> getStructNames() {
        return structs*.name
    }

    List<String> getActionNames() {
        return actions*.name
    }

    List<String> getActionsTypes() {
        return actions*.type
    }

    List<String> getTableNames() {
        return tables*.name
    }

    List<String> getTableTypes() {
        return tables*.type
    }

    List<String> validate() {
        def errors = []
        def nonUniqueTypes = declaredTypes.groupBy { it }.findAll { it.value.size() > 1 }.keySet()
        if (nonUniqueTypes) {
            errors << "Multiple types with same name: $nonUniqueTypes"
        }

        def unknownTypes = getUnknownTypes()
        if (unknownTypes) {
            errors << "Unknown types: ${unknownTypes}"
        }

        def nonDefaultTypes = types*.type - defaultTypes
        if (nonDefaultTypes) {
            errors << "Declared types based on non default types: $nonDefaultTypes"
        }

        def undefinedActionTypes = actionsTypes - structNames
        if (undefinedActionTypes) {
            errors << "Undefined action types: $undefinedActionTypes"
        }

        def invalidActionNames = actions.find { !it.isValidName() }*.name
        if (invalidActionNames) {
            errors << "Invalid action name: $invalidActionNames"
        }

        def actionWithoutTypes = actionsTypes - structNames
        if (actionWithoutTypes) {
            errors << "Actions without corresponding types: $actionWithoutTypes"
        }

        def tablesWithoutTypes = tableTypes - structNames
        if (tablesWithoutTypes) {
            errors << "Tables without corresponding types: $tablesWithoutTypes"
        }

        tables.each { errors += it.validate() }

        return errors
    }

}
