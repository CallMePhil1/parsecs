package parsecs.ksp.entity

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import io.github.oshai.kotlinlogging.KotlinLogging
import parsecs.ecs.entity.EntityID
import parsecs.ksp.Constants
import parsecs.ksp.EntityQuery
import parsecs.ksp.formatArrayProperty

private val logger = KotlinLogging.logger {}

fun getEntityQueryClass(systemName: String, propertyName: String, forEachFunSpec: FunSpec): TypeSpec {
    return TypeSpec.classBuilder("${systemName}${propertyName.capitalize()}")
        .addSuperinterface(Entities::class)
        .addFunction(forEachFunSpec)
        .build()
}

fun getEntityQueryClassFile(system: KSClassDeclaration, queryClasses: List<TypeSpec>): FileSpec {
    return FileSpec
        .builder(system.toClassName())
        .addImport(Constants.COMPONENTS_PACKAGE_NAME, Constants.DATA_HOLDER_NAME)
        .addTypes(queryClasses.asIterable())
        .build()
}

fun getQueryAnnotationFromProperty(prop: KSPropertyDeclaration) =
    prop.annotations.first { it.annotationType.resolve().toClassName() == EntityQuery::class.asClassName() }

fun getQueryForEachFunSpec(with: List<KSType>): FunSpec {
    val entityIDParameter = ParameterSpec.builder("", EntityID::class).build()
    val forEachLambda = LambdaTypeName.get(null, listOf(entityIDParameter), Unit::class.asTypeName())

    val withStatement = with
        .map { formatArrayProperty(it.toClassName().simpleName) }
        .map { "${Constants.DATA_HOLDER_NAME}.$it[i].inUse" }
        .joinToString(prefix = "if (", postfix = ")", separator = " && ")

    return FunSpec
        .builder("forEach")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("block", forEachLambda)
        .beginControlFlow("for (i in 0 until %L.%L.size)", Constants.DATA_HOLDER_NAME, Constants.ENTITY_IN_USE_ARRAY_NAME)
        .beginControlFlow(withStatement)
        .addCode("block(i)")
        .endControlFlow()
        .endControlFlow()
        .build()
}

fun getQueryPropertiesFromClass(cls: KSClassDeclaration): List<KSPropertyDeclaration> =
    cls.getAllProperties().filter { prop ->
        val propertyName = prop.simpleName.asString()
        logger.debug { "Property: $propertyName annotations: ${prop.annotations.toList().size}" }
        val entityQuery = prop.annotations.firstOrNull { it.annotationType.resolve().toClassName() == EntityQuery::class.asClassName() }

        if (entityQuery == null) {
            logger.debug { "Property '$propertyName' does not have 'EntityQuery' annotation" }
            return@filter false
        }
        return@filter true
    }.toList()

fun getWithFromQuery(query: KSAnnotation) = query.arguments.firstOrNull {
    it.name?.asString() == "with"
}!!.value as List<KSType>

fun writeEntitiesQueryFile(codeGenerator: CodeGenerator, file: FileSpec) {
    val newFile = codeGenerator.createNewFile(Dependencies(false), file.packageName, file.name)
    val writer = newFile.writer()

    file.writeTo(writer)
    writer.close()
}