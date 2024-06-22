package parsecs

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.oshai.kotlinlogging.KotlinLogging
import parsecs.ext.camelcase
import parsecs.interfaces.Component

const val initializerTemplate = """Array(4096) { %T() }"""
const val inUseInitializerTemplate = """Array(4096) { %L }"""
const val growArrayTemplate =
"""%N = Array(size) {
  if (it < %N.size) %N[it]
  else %T()
}
"""
const val growInUseArrayTemplate =
"""%N = Array(size) {
  if (it < %N.size) %N[it]
  else false
}
"""

val inUseType = Array::class.asTypeName().parameterizedBy(Boolean::class.asClassName())
val inUsePropSpec = PropertySpec.builder("entityInUseArray", inUseType, KModifier.INTERNAL)
    .mutable()
    .initializer(inUseInitializerTemplate, false)
    .build()

fun getComponents(files: Sequence<KSFile>): Sequence<KSClassDeclaration> {
    val logger = KotlinLogging.logger {}

    return files.map { file ->
        file.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .filter { cls ->
                cls.superTypes.any { types -> types.resolve().declaration.qualifiedName?.asString() == Component::class.qualifiedName }
            }
    }.flatten().also {
        it.forEach { logger.info { "Found component ${it.simpleName.asString()}" } }
    }
}

fun growFunctionSpec(propsAndComponentNames: Sequence<Pair<PropertySpec, TypeName>>): FunSpec {
    val funSpec = FunSpec.builder("grow")
        .addModifiers(KModifier.INTERNAL)
        .addParameter("size", Int::class)

    propsAndComponentNames.forEach {
        val propSpec = it.first
        val componentTypeName = it.second

        funSpec.addCode(growArrayTemplate, propSpec.name, propSpec.name, propSpec.name, componentTypeName)
    }

    funSpec.addCode(growInUseArrayTemplate, inUsePropSpec.name, inUsePropSpec.name, inUsePropSpec.name)

    return funSpec.build()
}


fun generateComponentsObject(codeGenerator: CodeGenerator, components: Sequence<KSClassDeclaration>) {
    val logger = KotlinLogging.logger {}

    val className = ClassName("parsecs", "ParsecsComponentHolder")
    val fileWriter = codeGenerator.createNewFile(Dependencies(false), className.packageName, className.simpleName).writer()

    val fileSpec = FileSpec.builder(className)

    val holderType = TypeSpec
        .objectBuilder(className.simpleName)
        .addModifiers(KModifier.INTERNAL)

    val propAndComponents = components.map { component ->
        val componentName = component.simpleName.asString()
        val componentTypeName = component.asType(emptyList()).toTypeName()

        logger.info { "Processing $componentName" }

        val arrayType = Array::class.asTypeName().parameterizedBy(componentTypeName)

        return@map PropertySpec
            .builder("${componentName}Array".camelcase(), arrayType, KModifier.INTERNAL)
            .mutable()
            .initializer(initializerTemplate, componentTypeName)
            .build() to componentTypeName
    }

    propAndComponents.map { it.first }.toMutableList().also { it.add(inUsePropSpec) }.forEach { holderType.addProperty(it) }
    holderType.addFunction(growFunctionSpec(propAndComponents))

    fileSpec.addType(holderType.build()).build().writeTo(fileWriter)
    fileWriter.close()
}
