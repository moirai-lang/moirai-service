package moirai.service

import com.fasterxml.jackson.databind.JsonNode
import moirai.transport.*
import java.math.BigDecimal

fun jsonToMoirai(node: JsonNode, type: TransportType): TransportAst {
    when (type) {
        is TransportBasicType -> {
            if (type.name == "Boolean" && node.isValueNode) {
                return BooleanLiteralTransportAst(node.asBoolean())
            }

            if (type.name == "Int" && node.isValueNode) {
                return IntLiteralTransportAst(node.asInt())
            }

            if (type.name == "Char" && node.isTextual) {
                val t = node.toString().trimStart('"').trimEnd('"')
                if (t.length == 1) {
                    return CharLiteralTransportAst(t.toCharArray().first())
                }
            }

            jsonFail()
        }

        is TransportGroundRecordType -> {
            if (node.isObject || node.isPojo) {
                val fields = type.fields.map { jsonToMoirai(node[it.name], it.type) }
                return ApplyTransportAst(type.name, fields)
            }

            jsonFail()
        }

        is TransportObjectType -> {
            if (node.isTextual && node.asText() == type.name) {
                return RefTransportAst(type.name)
            }

            jsonFail()
        }

        is TransportParameterizedBasicType -> {
            if (type.name == "String" && node.isValueNode) {
                return StringLiteralTransportAst(node.toString())
            }

            if (type.name == "Decimal" && node.isValueNode) {
                return DecimalLiteralTransportAst(BigDecimal(node.toString()))
            }

            if (type.name == "List" && node.isArray) {
                val elements = node.elements().asSequence().toList().map { jsonToMoirai(it, type.typeArgs.first()) }
                return ApplyTransportAst(type.name, elements)
            }

            if (type.name == "Set" && node.isArray) {
                val elements = node.elements().asSequence().toList().map { jsonToMoirai(it, type.typeArgs.first()) }
                return ApplyTransportAst(type.name, elements)
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

                        ApplyTransportAst(
                            "Pair",
                            listOf(jsonToMoirai(k, type.typeArgs[0]), jsonToMoirai(v, type.typeArgs[1]))
                        )
                    }

                    jsonFail()
                }

                return ApplyTransportAst(type.name, elements)
            }

            jsonFail()
        }

        is TransportParameterizedRecordType -> {
            if (node.isObject || node.isPojo) {
                val fields = type.fields.map { jsonToMoirai(node[it.name], it.type) }
                return ApplyTransportAst(type.name, fields)
            }

            jsonFail()
        }

        is TransportPlatformObjectType -> {
            if (node.isTextual && node.asText() == type.name) {
                return RefTransportAst(type.name)
            }

            jsonFail()
        }

        is TransportPlatformSumType -> {
            if (type.name == "Option") {
                if (node.isNull) {
                    return RefTransportAst("None")
                } else {
                    return ApplyTransportAst("Some", listOf(jsonToMoirai(node, type.typeArgs.first())))
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
    throw MoiraiServiceException("Could not convert JSON to this type")
}