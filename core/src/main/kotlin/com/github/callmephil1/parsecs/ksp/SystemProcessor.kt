package com.github.callmephil1.parsecs.ksp

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import io.github.oshai.kotlinlogging.KotlinLogging
import com.github.callmephil1.parsecs.ecs.entity.EntityID
import com.github.callmephil1.parsecs.ecs.system.System
import com.github.callmephil1.parsecs.ext.camelcase

private val logger = KotlinLogging.logger {}

private fun forEachFunctionBody(components: List<KSType>, scopeObjectName: String): CodeBlock {
    val componentCheck = components
        .map { "${Constants.DATA_HOLDER_NAME}.${formatArrayProperty(it.toClassName().simpleName)}[index].inUse" }
        .joinToString(" && ")

    return CodeBlock.builder()
        .addStatement("var index = 0")
        .beginControlFlow("while (index < %N.%N.size)", Constants.DATA_HOLDER_NAME, Constants.ENTITY_IN_USE_ARRAY_NAME)
        .beginControlFlow("if (%N.%N[index])", Constants.DATA_HOLDER_NAME, Constants.ENTITY_IN_USE_ARRAY_NAME)
        .beginControlFlow("if (%L)", componentCheck)
        .addStatement("block(%N, index)", scopeObjectName)
        .endControlFlow()
        .endControlFlow()
        .addStatement("index += 1")
        .endControlFlow()
        .build()
}

/**
 * @param fileSpec The file that contains the system
 * @param scopeTypeSpec Spec for the scope object that was created
 * @param systemTypeSpec Spec for the system this entity class is used for
 * @return [TypeSpec] Entity class for the system that contains foreach for entities the system needs
 *
 * Creates the entity class that contains only forEach function that uses the system
 * scope to add receiver functions for [EntityID]
 */
internal fun createSystemEntityClass(
    fileSpec: KSFile,
    components: List<KSType>,
    scopeTypeSpec: TypeSpec,
    systemTypeSpec: KSClassDeclaration
): TypeSpec {
    val scopeTypeClassName = ClassName(fileSpec.packageName.asString(), scopeTypeSpec.name!!)

    val entityIDParameter = ParameterSpec.builder("", EntityID::class).build()
    val forEachLambda = LambdaTypeName.get(scopeTypeClassName, listOf(entityIDParameter), Unit::class.asTypeName())

    val forEachFunSpec = FunSpec
        .builder("forEach")
        .addParameter(ParameterSpec.builder("block", forEachLambda).build())
        .addCode(forEachFunctionBody(components, scopeTypeSpec.name!!))
        .build()

    return TypeSpec
        .objectBuilder("${systemTypeSpec.simpleName.asString()}Entities")
        .addFunction(forEachFunSpec)
        .build()
}

/**
 * @param className Name of system for this scope to create for
 * @param components List of components that the system requires
 *
 * Returns scope object that contains receiver functions
 */
internal fun createSystemScope(
    className: ClassName,
    components: List<KSType>
): TypeSpec {
    val systemSimpleName = className.simpleName
    val scopeType = TypeSpec
        .objectBuilder("${systemSimpleName}Scope")

    val componentProperties = components.map {
        val componentName = it.toClassName()
        logger.info { "Generating property for '${componentName.canonicalName}'" }

        val componentTypeName = it.toTypeName()

        PropertySpec
            .builder(componentName.simpleName.camelcase(), componentTypeName)
            .receiver(EntityID::class)
            .getter(
                FunSpec
                    .getterBuilder()
                    .addCode("return %N.%N[this]", Constants.DATA_HOLDER_NAME, formatArrayProperty(componentName.simpleName))
                    .build()
            ).build()
    }

    componentProperties.forEach {
        scopeType.addProperty(it)
    }

    return scopeType.build()
}

internal fun getSystems(resolver: Resolver, files: Sequence<KSFile>): List<KSClassDeclaration> {
    val systemInterfaceType = resolver.getClassDeclarationByName<System>()!!.asType(emptyList())

    return files
        .map {
            it.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { systemInterfaceType.isAssignableFrom(it.asType(emptyList())) }

        }
        .flatten()
        .toList()
}

//internal fun getSystemComponents(system: KSClassDeclaration): List<KSType> {
//    val annotation = system
//        .annotations
//        .firstOrNull {
//            it.annotationType.resolve()
//                .toClassName() == SystemEntities::class.asClassName()
//        }
//
//    if (annotation == null) {
//        logger.info { "Could not find 'Entities' annotation for '${system.qualifiedName?.asString()}'" }
//        return emptyList()
//    }
//
//    val with = annotation.arguments.firstOrNull {
//        it.name?.asString() == "with"
//    }
//
//    return with!!.value as List<KSType>
//}

internal fun generateSystemClasses(
    codeGenerator: CodeGenerator,
    system: KSClassDeclaration,
    systemScope: TypeSpec,
    systemEntityClass: TypeSpec
) {
    val packageName = system.packageName.asString()
    val systemFileName = "${system.simpleName.asString()}Objects"

    val newFile = codeGenerator.createNewFile(Dependencies(false), packageName, systemFileName)
    val fileWriter = newFile.writer()

    FileSpec
        .builder(packageName, systemFileName)
        .addImport(componentsHolderClassName.packageName, componentsHolderClassName.simpleName)
        .addType(systemScope)
        .addType(systemEntityClass)
        .build()
        .writeTo(fileWriter)

    fileWriter.close()
}
