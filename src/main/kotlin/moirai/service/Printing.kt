package moirai.service

import moirai.eval.*

fun printConstruct(value: Value): String =
    when(value) {
        // Literals
        is StringValue -> value.canonicalForm
        is BooleanValue -> value.canonicalForm.toString()
        is CharValue -> value.canonicalForm.toString()
        is DecimalValue -> value.canonicalForm.toString()
        is IntValue -> value.canonicalForm.toString()

        // Collections
        is ListValue -> "List(${value.elements.map { printConstruct(it) }.joinToString { ", " }})"
        is DictionaryValue -> "Dictionary(${
            value.dictionary.entries.map {
                "${printConstruct(it.key)} to ${
                    printConstruct(
                        it.value
                    )
                }"
            }.joinToString { ", " }
        })"

        is SetValue -> "Set(${value.elements.map { printConstruct(it) }.joinToString { ", " }})"

        // Objects
        is ObjectValue -> value.id
        is SumObjectValue -> value.id
        UnitValue -> "Unit"

        // Records
        is RecordValue -> "${value.id}(${value.inOrderValues.map { printConstruct(it) }.joinToString { ", " }})"
        is SumRecordValue -> "${value.id}(${value.inOrderValues.map { printConstruct(it) }.joinToString { ", " }})"

        // Errors
        else -> throw Exception("Unexpected Value")
    }