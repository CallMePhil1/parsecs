package parsecs.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.oshai.kotlinlogging.KotlinLogging
import parsecs.ecs.Component
import parsecs.ext.camelcase

private val logger = KotlinLogging.logger {}

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

val componentsHolderClassName = ClassName("parsecs", Constants.COMPONENT_HOLDER_NAME)

fun createComponentsHolder(components: Sequence<KSClassDeclaration>): TypeSpec {
    val holderType = TypeSpec
        .objectBuilder(componentsHolderClassName.simpleName)
        .addModifiers(KModifier.INTERNAL)

    val componentList = components.toList()

    logger.info { "Generating '${componentsHolderClassName.simpleName}' with ${componentList.size} component(s)" }

    val propAndComponents = componentList.map { component ->
        val componentName = component.simpleName.asString()
        val componentTypeName = component.asType(emptyList()).toTypeName()

        logger.info { "Generating property for $componentName" }

        val arrayType = Array::class.asTypeName().parameterizedBy(componentTypeName)

        return@map PropertySpec
            .builder(formatProperty(componentName), arrayType, KModifier.INTERNAL)
            .mutable()
            .initializer(initializerTemplate, componentTypeName)
            .build() to componentTypeName

    }

    propAndComponents.map { it.first }.toMutableList().also { it.add(inUsePropSpec) }.forEach { holderType.addProperty(it) }
    holderType.addFunction(growFunctionSpec(propAndComponents))

    return holderType.build()
}

fun formatProperty(simpleName: String) = "${simpleName.camelcase()}Array"

fun generateComponentsObject(codeGenerator: CodeGenerator, componentsHolderSpec: TypeSpec) {
    val fileWriter = codeGenerator.createNewFile(Dependencies(false), componentsHolderClassName.packageName, componentsHolderClassName.simpleName).writer()
    val fileSpec = FileSpec.builder(componentsHolderClassName)

    fileSpec.addType(componentsHolderSpec).build().writeTo(fileWriter)
    fileWriter.close()
}

fun getComponents(resolver: Resolver, files: Sequence<KSFile>): Sequence<KSClassDeclaration> {
    val componentType = resolver.getClassDeclarationByName<Component>()!!.asType(emptyList())

    return files.map { file ->
        file.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .filter { cls -> componentType.isAssignableFrom(cls.asType(emptyList())) }
    }.flatten()
}

private fun growFunctionSpec(propsAndComponentNames: List<Pair<PropertySpec, TypeName>>): FunSpec {
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
