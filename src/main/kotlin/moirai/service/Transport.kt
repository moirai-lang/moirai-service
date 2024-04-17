package moirai.service

import com.fasterxml.jackson.databind.JsonNode
import moirai.transport.*

fun jsonToMoirai(node: JsonNode, type: TransportType): String {
    when (type) {
        is TransportBasicType -> {
            if (type.name == "Boolean" && node.isValueNode) {
                return node.asBoolean().toString()
            }

            if (type.name == "Int" && node.isValueNode) {
                return node.asInt().toString()
            }

            if (type.name == "Char" && node.isTextual) {
                val t = node.toString().trimStart('"').trimEnd('"')
                if (t.length == 1) {
                    return "\'${t}\'"
                }
            }

            jsonFail()
        }

        is TransportGroundRecordType -> {
            if (node.isObject || node.isPojo) {
                val fields = type.fields.map { jsonToMoirai(node[it.name], it.type) }.joinToString { ", " }
                return "${type.name}($fields)"
            }

            jsonFail()
        }

        is TransportObjectType -> {
            if (node.isTextual && node.asText() == type.name) {
                return type.name
            }

            jsonFail()
        }

        is TransportParameterizedBasicType -> {
            if (type.name == "String" && node.isValueNode) {
                val t = node.toString().trimStart('"').trimEnd('"')
                return "\"${t}\""
            }

            if (type.name == "Decimal" && node.isValueNode) {
                return node.asDouble().toString()
            }

            if (type.name == "List" && node.isArray) {
                val elements = node.elements().asSequence().toList().map { jsonToMoirai(it, type.typeArgs.first()) }
                return "List(${elements.joinToString { ", " }})"
            }

            if (type.name == "Set" && node.isArray) {
                val elements = node.elements().asSequence().toList().map { jsonToMoirai(it, type.typeArgs.first()) }
                return "Set(${elements.joinToString { ", " }})"
            }

            if (type.name == "Dictionary" && node.isArray) {
                val elements = node.elements().asSequence().toList().map {
                    if (it.isPojo || it.isObject) {
                        val fn = it.fieldNames().asSequence().toList()
                        if (fn.size != 2) {
                            jsonFail()
                        }

                        // TODO: Define your semantics for dictionaries
                        // Sorted field names alphabetically
                        val s = fn.sorted()
                        // First is key
                        val k = it[s[0]]
                        // Second is value
                        val v = it[s[1]]

                        "${jsonToMoirai(k, type.typeArgs[0])} to ${jsonToMoirai(v, type.typeArgs[1])}"
                    }

                    jsonFail()
                }

                return "Dictionary(${elements.joinToString { ", " }})"
            }

            jsonFail()
        }

        is TransportParameterizedRecordType -> {
            if (node.isObject || node.isPojo) {
                val fields = type.fields.map { jsonToMoirai(node[it.name], it.type) }.joinToString { ", " }
                return "${type.name}($fields)"
            }

            jsonFail()
        }

        is TransportPlatformObjectType -> {
            if (node.isTextual && node.asText() == type.name) {
                return type.name
            }

            jsonFail()
        }

        is TransportPlatformSumType -> {
            if (type.name == "Option") {
                if (node.isNull) {
                    return "None"
                } else {
                    return "Some(${jsonToMoirai(node, type.typeArgs.first())})"
                }
            }

            jsonFail()
        }

        NonPublicTransportType,
        TransportConstantFin,
        is TransportFin,
        is TransportFinTypeParameter,
        is TransportMaxCostExpression,
        is TransportProductCostExpression,
        is TransportSumCostExpression,
        is TransportFunctionType,
        is TransportPlatformSumObjectType,
        is TransportPlatformSumRecordType,
        is TransportStandardTypeParameter -> jsonFail()
    }
}

fun jsonFail(): Nothing {
    throw Exception("Could not convert JSON to this type")
}