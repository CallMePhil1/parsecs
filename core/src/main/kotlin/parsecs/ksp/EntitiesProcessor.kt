package parsecs.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import parsecs.ecs.Component
import parsecs.ecs.entity.Entity
import kotlin.reflect.KClass

private fun createInUseFunSpec(component: ClassName, value: Boolean) =
    CodeBlock
        .builder()
        .addStatement("%N.%N[entity].inUse = %L", Constants.COMPONENT_HOLDER_NAME, formatArrayProperty(component.simpleName), value)
        .build()

fun createAddComponentFunction(component: ClassName): FunSpec {
    val parameterClass = KClass::class.asClassName()

    return FunSpec
        .builder("add${component.simpleName}")
        .addModifiers(KModifier.INTERNAL)
        .addParameter("entity", Entity::class)
        .addCode(CodeBlock
            .builder()
            .add(createInUseFunSpec(component, true))
            .addStatement("%N.%N[entity].reset()", Constants.COMPONENT_HOLDER_NAME, formatArrayProperty(component.simpleName))
            .build()
        )
        .build()
}

fun createReleaseFunction(components: Iterable<ClassName>): FunSpec {
    val entityReleaseCodeBlockBuilder = CodeBlock.builder()
    components.forEach {
        entityReleaseCodeBlockBuilder
            .addStatement("%N.%N[entity] = false", Constants.COMPONENT_HOLDER_NAME, Constants.ENTITY_IN_USE_ARRAY_NAME)
            .add(createInUseFunSpec(it, false))
    }
    return FunSpec
        .builder("release")
        .addModifiers(KModifier.INTERNAL)
        .addParameter("entity", Entity::class)
        .addCode(entityReleaseCodeBlockBuilder.build())
        .build()
}

fun createEntitiesObject(components: Sequence<KSClassDeclaration>): TypeSpec {
    val adders = components.map { createAddComponentFunction(it.toClassName()) }.asIterable()
    val componentClassNames = components.map { it.toClassName() }.asIterable()

    return TypeSpec
        .objectBuilder(Constants.ENTITY_OBJECT_NAME)
        .addModifiers(KModifier.INTERNAL)
        .addFunctions(adders)
        .addFunction(createReleaseFunction(componentClassNames))
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