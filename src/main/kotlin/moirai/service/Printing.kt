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

fun localize(error: ErrorKind): String =
    when(error) {
        CalculateCostFailed -> TODO()
        is CannotExplicitlyInstantiate -> TODO()
        is CannotFindBestType -> TODO()
        CannotInstantiate -> TODO()
        is CannotRefFunctionParam -> TODO()
        is CannotUsePlatformSumTypeMember -> TODO()
        is CannotUseRawType -> TODO()
        CostOverLimit -> TODO()
        DecimalInfiniteDivide -> TODO()
        is DictionaryArgsMustBePairs -> TODO()
        is DuplicateCaseDetected -> TODO()
        is DuplicateImport -> TODO()
        is DuplicateTypeParameter -> TODO()
        ExpectOtherError -> TODO()
        ExpectedNamedScript -> TODO()
        is ExpectedTransientScript -> TODO()
        is FinMismatch -> TODO()
        is ForEachFeatureBan -> TODO()
        is FormalParamFeatureBan -> TODO()
        is FunctionAssign -> TODO()
        is FunctionReturnType -> TODO()
        is IdentifierAlreadyExists -> TODO()
        is IdentifierCouldNotBeDefined -> TODO()
        is IdentifierNotFound -> TODO()
        is ImmutableAssign -> TODO()
        is ImpossibleState -> TODO()
        is IncompatibleString -> TODO()
        is IncorrectNumberOfArgs -> TODO()
        is IncorrectNumberOfTypeArgs -> TODO()
        is IndexOutOfBounds -> TODO()
        InvalidAssign -> TODO()
        is InvalidCostExpressionFunctionName -> TODO()
        InvalidCostUpperLimit -> TODO()
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