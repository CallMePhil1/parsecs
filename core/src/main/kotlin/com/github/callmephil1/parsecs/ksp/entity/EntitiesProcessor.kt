package com.github.callmephil1.parsecs.ksp.entity

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.github.callmephil1.parsecs.ecs.entity.EntityID
import com.github.callmephil1.parsecs.ksp.Constants
import com.github.callmephil1.parsecs.ksp.componentsHolderClassName
import com.github.callmephil1.parsecs.ksp.formatArrayProperty

val getUnusedFunTemplate = """
    if (availableEntityCount <= 0) {
        entityCursor = %1N.%2N.size - 1
        %1N.resize()
    }

    while (entityCursor < %1N.%2N.size) {
        entityCursor += 1

        if (%1N.%2N[entityCursor])
            continue

        %1N.%2N[entityCursor] = true
        availableEntityCount -= 1
        return entityCursor
    }
    
    entityCursor = -1
    return getUnusedEntityID()
""".trimIndent()

fun createAddComponentFunction(component: ClassName): FunSpec {
    return FunSpec
        .builder("add${component.simpleName}")
        .addModifiers(KModifier.INTERNAL)
        .addParameter("entity", EntityID::class)
        .addCode(CodeBlock
            .builder()
            .add(createInUseFunSpec(component, true))
            .addStatement("%N.%N[entity].reset()",
                Constants.DATA_HOLDER_NAME, formatArrayProperty(component.simpleName)
            )
            .build()
        )
        .build()
}

fun createEntitiesObject(components: Sequence<KSClassDeclaration>): TypeSpec {
    val adders = components.map { createAddComponentFunction(it.toClassName()) }.asIterable()
    val componentClassNames = components.map { it.toClassName() }.asIterable()
    val availableEntityCountProperty = PropertySpec.builder("availableEntityCount", Int::class).mutable().initializer("0").build()
    val entityCursorProperty = PropertySpec.builder("entityCursor", Int::class).mutable().initializer("-1").build()

    return TypeSpec
        .objectBuilder(Constants.ENTITY_OBJECT_NAME)
        .addModifiers(KModifier.INTERNAL)
        .addProperty(availableEntityCountProperty)
        .addProperty(entityCursorProperty)
        .addFunctions(adders)
        .addFunction(createReleaseFunction(componentClassNames))
        .addFunction(createGetUnusedEntityIDFun())
        .build()
}

fun createGetUnusedEntityIDFun() =
    FunSpec
        .builder("getUnusedEntityID")
        .addModifiers(KModifier.INTERNAL)
        .returns(EntityID::class)
        .addCode(getUnusedFunTemplate, Constants.DATA_HOLDER_NAME, Constants.ENTITY_IN_USE_ARRAY_NAME)
        .build()

private fun createInUseFunSpec(component: ClassName, value: Boolean) =
    CodeBlock
        .builder()
        .addStatement("%N.%N[entity].inUse = %L",
            Constants.DATA_HOLDER_NAME, formatArrayProperty(component.simpleName), value)
        .build()

fun createReleaseFunction(components: Iterable<ClassName>): FunSpec {
    val entityReleaseCodeBlockBuilder = CodeBlock
        .builder()
        .addStatement("%N.%N[entity] = false", Constants.DATA_HOLDER_NAME, Constants.ENTITY_IN_USE_ARRAY_NAME)

    components.forEach {
        entityReleaseCodeBlockBuilder
            .add(createInUseFunSpec(it, false))
    }

    return FunSpec
        .builder("release")
        .addModifiers(KModifier.INTERNAL)
        .addParameter("entity", EntityID::class)
        .addCode(entityReleaseCodeBlockBuilder.build())
        .build()
}

fun generateEntitiesObject(codeGenerator: CodeGenerator, entitiesObject: TypeSpec) {
    val file = codeGenerator.createNewFile(Dependencies(false), "parsecs.entity", Constants.ENTITY_OBJECT_NAME)
    val writer = file.writer()

    FileSpec
        .builder("parsecs.entity", Constants.ENTITY_OBJECT_NAME)
        .addImport(componentsHolderClassName.packageName, componentsHolderClassName.simpleName)
        .addType(entitiesObject)
        .build()
        .writeTo(writer)

    writer.flush()
}