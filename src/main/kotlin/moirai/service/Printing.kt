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
        is InvalidDefinitionLocation -> "Symbol ${error.identifier.value} cannot be defined here"
        is InvalidFinLiteral -> "Invalid Fin literal"
        is InvalidFinTypeSub -> "Only cost expressions and Fin type parameters can be used here, actual: ${error.type.value}"
        is InvalidIntegerLiteral -> "Invalid Int literal"
        is InvalidPluginLocation -> "Plugins cannot be defined in named or transient scripts"
        InvalidRangeArg -> "Arguments to the range plugin must be Int literals"
        is InvalidRef -> "Symbol ${error.symbol.value} cannot be referenced"
        is InvalidSource -> "Invalid for loop source type ${error.type.value}"
        is InvalidStandardTypeSub -> "Cost expressions and Fin type parameters cannot be used here, actual: ${error.type.value}"
        is MaskingTypeParameter -> "Symbol ${error.identifier.value} masks a type parameter"
        is MissingMatchCase -> "Missing match case ${error.name}"
        NegativeFin -> "Cost expressions and Fin type parameters must never evaluate to negative values"
        is NoSuchFile -> "No such file ${error.import.joinToString { "." }}"
        is ParameterizedGroundMismatch -> "Type parameters cannot be defined here"
        is PluginAlreadyExists -> "Plugin ${error.name} already exists"
        RandomRequiresIntLong -> "The random plugin requires Int arguments"
        is RecordFieldFeatureBan -> "Record fields cannot contain type ${error.type.value}"
        is RecordFieldFunctionType -> "Record fields cannot contain function types, record: ${error.record.value}, field: ${error.field.value}"
        is RecursiveFunctionDetected -> "Recursive functions are never allowed, function: ${error.symbol.value}"
        RecursiveNamespaceDetected -> "Recursive namespace detected"
        is RecursiveRecordDetected -> "Recursive records are never allowed, record: ${error.type.value}"
        is ReturnTypeFeatureBan -> "The return type ${error.type.value} is not allowed here"
        RuntimeCostExpressionEvalFailed -> "Failed to evaluate a cost expression at runtime"
        is RuntimeFinViolation -> "A Fin type parameter promise has been violated at runtime"
        RuntimeImmutableViolation -> "An immutable value was modified at runtime"
        is SecondDegreeHigherOrderFunction -> "Second-degree higher-order functions are never allowed"
        SelfImport -> "Scripts cannot import themselves"
        is SumTypeRequired -> "A sum type is required in this context, actual: ${error.type.value}"
        is SymbolCouldNotBeApplied -> "Symbol ${error.signifier.value} is not a function or type constructor"
        is SymbolHasNoFields -> "Symbol ${error.type.value} has no fields"
        is SymbolHasNoMembers -> "Symbol ${error.type.value} has no members"
        is SymbolHasNoParameters -> "Symbol ${error.identifier.value} has no type parameters"
        is SymbolIsNotAField -> "Symbol ${error.signifier.value} is not a field"
        is SyntaxError -> "Syntax error"
        is TooManyElements -> "Too many elements, Fin: ${error.fin}, elements: ${error.elements}"
        is TypeArgFeatureBan -> "Symbol ${error.type.value} cannot be used as a type argument"
        is TypeInferenceFailed -> "Type inference failed for type parameter ${error.typeParam.value}"
        is TypeMismatch -> "Type mismatch, expected: ${error.expected.value}, actual: ${error.actual.value}"
        is TypeMustBeCostExpression -> "Type must be a cost expression, actual: ${error.type.value}"
        is TypeRequiresExplicit -> "Symbol ${error.identifier.value} requires explicit type arguments"
        is TypeRequiresExplicitFin -> "Symbol ${error.identifier.value} requires explicit Fin"
        TypeSystemBug -> "Type system or interpreter bug"
        is UnknownCaseDetected -> "Unknown case ${error.name}"
    }