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

        is TransportObjectType -> TODO()
        is TransportParameterizedBasicType -> TODO()
        is TransportParameterizedRecordType -> TODO()
        is TransportPlatformObjectType -> TODO()
        is TransportPlatformSumObjectType -> TODO()
        is TransportPlatformSumRecordType -> TODO()
        is TransportPlatformSumType -> TODO()

        NonPublicTransportType,
        TransportConstantFin,
        is TransportFin,
        is TransportFinTypeParameter,
        is TransportMaxCostExpression,
        is TransportProductCostExpression,
        is TransportSumCostExpression,
        is TransportFunctionType,
        is TransportStandardTypeParameter -> jsonFail()
    }
}

fun jsonFail(): Nothing {
    throw Exception("Could not convert JSON to this type")
}