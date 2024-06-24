package parsecs.ksp

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
import parsecs.ecs.entity.Entity
import parsecs.ecs.system.System
import parsecs.ext.camelcase

private val logger = KotlinLogging.logger {}

/**
 * @param fileSpec The file that contains the system
 * @param scopeTypeSpec Spec for the scope object that was created
 * @param systemTypeSpec Spec for the system this entity class is used for
 * @return [TypeSpec] Entity class for the system that contains foreach for entities the system needs
 *
 * Creates the entity class that contains only forEach function that uses the system
 * scope to add receiver functions for [Entity]
 */
fun createSystemEntityClass(
    fileSpec: KSFile,
    scopeTypeSpec: TypeSpec,
    systemTypeSpec: KSClassDeclaration
): TypeSpec {
    val scopeTypeClassName = ClassName(fileSpec.packageName.asString(), scopeTypeSpec.name!!)

    val forEachLambda = LambdaTypeName.get(scopeTypeClassName, emptyList(), Unit::class.asTypeName())

    val forEachFunSpec = FunSpec
        .builder("forEach")
        .addParameter(ParameterSpec.builder("block", forEachLambda).build())
        .build()

    return TypeSpec
        .classBuilder("${systemTypeSpec.simpleName.asString()}Entities")
        .addFunction(forEachFunSpec)
        .build()
}

/**
 * @param className Name of system for this scope to create for
 * @param components List of components that the system requires
 *
 * Returns scope object that contains receiver functions
 */
fun createSystemScope(
    className: ClassName,
    components: List<KSType>
): TypeSpec {
    val systemSimpleName = className.simpleName
    val scopeType = TypeSpec
        .classBuilder("${systemSimpleName}Scope")
        .addModifiers(KModifier.PUBLIC)

    val componentProperties = components.map {
        val componentName = it.toClassName()
        logger.info { "Generating property for '${componentName.canonicalName}'" }

        val componentTypeName = it.toTypeName()

        PropertySpec
            .builder(componentName.simpleName.camelcase(), componentTypeName)
            .receiver(Entity::class)
            .getter(
                FunSpec
                    .getterBuilder()
                    .addCode("return %N.%N[this]", Constants.COMPONENT_HOLDER_NAME, formatProperty(componentName.simpleName))
                    .build()
            ).build()
    }

    componentProperties.forEach {
        scopeType.addProperty(it)
    }

    return scopeType.build()
}

fun getSystems(resolver: Resolver, files: Sequence<KSFile>): List<KSClassDeclaration> {
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

fun getSystemComponents(system: KSClassDeclaration): List<KSType> {
    val annotation = system
        .annotations
        .firstOrNull {
            it.annotationType.resolve()
                .toClassName() == Entities::class.asClassName()
        }

    if (annotation == null) {
        logger.info { "Could not find 'Entities' annotation for '${system.qualifiedName?.asString()}'" }
        return emptyList()
    }

    val with = annotation.arguments.firstOrNull {
        it.name?.asString() == "with"
    }

    return with!!.value as List<KSType>
}

fun generateSystemClasses(
    codeGenerator: CodeGenerator,
    componentsHolder: ClassName,
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
        .addImport("parsecs", Constants.COMPONENT_HOLDER_NAME)
        .addType(systemScope)
        .addType(systemEntityClass)
        .build()
        .writeTo(fileWriter)

    fileWriter.close()
}


