package moirai.service

import moirai.eval.*
import moirai.semantics.core.*

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

fun localize(errors: List<LanguageError>): String {
    val sb = StringBuilder()

    errors.forEach {
        val msg = localize(it.error)
        when (val ctx = it.ctx) {
            is InNamedSource -> sb.appendLine("${msg}; File: ${ctx.fileName}; Line: ${ctx.line}; Char: ${ctx.char}")
            else -> sb.appendLine(msg)
        }
    }

    return sb.toString()
}

// TODO: Actually localize these error messages, possibly
fun localize(error: ErrorKind): String =
    when(error) {
        CalculateCostFailed -> "Cost calculation phase failed"
        is CannotExplicitlyInstantiate -> "Explicit type arguments are not supported for ${error.symbol.value}"
        is CannotFindBestType -> "Cannot find best type: ${error.types.map { it.value }.joinToString { ", " }}"
        CannotInstantiate -> "Cannot instantiate"
        is CannotRefFunctionParam -> "Formal parameter ${error.identifier.value} has a function type and cannot be referenced"
        is CannotUsePlatformSumTypeMember -> "Usage of sum type member ${error.type.value} is not allowed, use the sum type instead"
        is CannotUseRawType -> "Cannot use raw type without type ${error.type.value} parameters"
        CostOverLimit -> "The cost of executing the script exceeds the upper bound allowed by this architecture"
        DecimalInfiniteDivide -> "Divide by zero"
        is DictionaryArgsMustBePairs -> "The arguments to the Dictionary constructor must have type Pair, actual: ${error.actual.value}"
        is DuplicateCaseDetected -> "Duplicate case ${error.name}"
        is DuplicateImport -> "Duplicate import ${error.import.joinToString { "." }}"
        is DuplicateTypeParameter -> "Duplicate type parameter ${error.identifier.value}"
        ExpectOtherError -> "Invalid state, expect other error"
        ExpectedNamedScript -> "A named script was expected, but a transient script was observed"
        is ExpectedTransientScript -> "A transient script was expected, but the named script ${error.name} was observed"
        is FinMismatch -> "Pessimistic upper bound mismatch, expected: ${error.expected}, actual: ${error.actual}"
        is ForEachFeatureBan -> "Source type ${error.type.value} is not supported when using the for loop"
        is FormalParamFeatureBan -> "This type ${error.type.value} cannot appear as a formal parameter to a function"
        is FunctionAssign -> "Functions cannot appear in the assign statement, local ${error.identifier.value}"
        is FunctionReturnType -> "Function types cannot be used as the return type of a function"
        is IdentifierAlreadyExists -> "Identifier ${error.identifier.value} already exists"
        is IdentifierCouldNotBeDefined -> "Identifier ${error.identifier.value} could not be defined"
        is IdentifierNotFound -> "Identifier ${error.signifier.value} not be found"
        is ImmutableAssign -> "Attempt to assign to immutable symbol ${error.symbol.value}"
        is ImpossibleState -> "Impossible state: ${error.msg}"
        is IncompatibleString -> "Type ${error.type.value} is not compatible with String"
        is IncorrectNumberOfArgs -> "Incorrect number of arguments, expected: ${error.expected}, actual: ${error.actual}"
        is IncorrectNumberOfTypeArgs -> "Incorrect number of type arguments, expected: ${error.expected}, actual: ${error.actual}"
        is IndexOutOfBounds -> "Index out of bounds, index: ${error.index}, size: ${error.size}"
        InvalidAssign -> "Invalid assign"
        is InvalidCostExpressionFunctionName -> "Only Sum, Min, and Max functions are supported in cost expressions, actual: ${error.name}"
        InvalidCostUpperLimit -> "Invalid cost upper limit"
        is InvalidDefinitionLocation -> TODO()
        is InvalidFinLiteral -> TODO()
        is InvalidFinTypeSub -> TODO()
        is InvalidIntegerLiteral -> TODO()
        is InvalidPluginLocation -> TODO()
        InvalidRangeArg -> TODO()
        is InvalidRef -> TODO()
        is InvalidSource -> TODO()
        is InvalidStandardTypeSub -> TODO()
        is MaskingTypeParameter -> TODO()
        is MissingMatchCase -> TODO()
        NegativeFin -> TODO()
        is NoSuchFile -> TODO()
        is ParameterizedGroundMismatch -> TODO()
        is PluginAlreadyExists -> TODO()
        RandomRequiresIntLong -> TODO()
        is RecordFieldFeatureBan -> TODO()
        is RecordFieldFunctionType -> TODO()
        is RecursiveFunctionDetected -> TODO()
        RecursiveNamespaceDetected -> TODO()
        is RecursiveRecordDetected -> TODO()
        is ReturnTypeFeatureBan -> TODO()
        RuntimeCostExpressionEvalFailed -> TODO()
        is RuntimeFinViolation -> TODO()
        RuntimeImmutableViolation -> TODO()
        is SecondDegreeHigherOrderFunction -> TODO()
        SelfImport -> TODO()
        is SumTypeRequired -> TODO()
        is SymbolCouldNotBeApplied -> TODO()
        is SymbolHasNoFields -> TODO()
        is SymbolHasNoMembers -> TODO()
        is SymbolHasNoParameters -> TODO()
        is SymbolIsNotAField -> TODO()
        is SyntaxError -> TODO()
        is TooManyElements -> TODO()
        is TypeArgFeatureBan -> TODO()
        is TypeInferenceFailed -> TODO()
        is TypeMismatch -> TODO()
        is TypeMustBeCostExpression -> TODO()
        is TypeRequiresExplicit -> TODO()
        is TypeRequiresExplicitFin -> TODO()
        TypeSystemBug -> TODO()
        is UnknownCaseDetected -> TODO()
    }