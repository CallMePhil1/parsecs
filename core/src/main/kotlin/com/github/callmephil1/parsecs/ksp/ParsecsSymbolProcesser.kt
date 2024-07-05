package com.github.callmephil1.parsecs.ksp

import com.github.callmephil1.parsecs.ksp.entity.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.LocalDateTime

class ParsecsSymbolProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    private var invoked: Boolean = false
    private val logger = KotlinLogging.logger {}

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        val now = LocalDateTime.now()
        logger.info { "------ $now ------" }

        val files = resolver.getAllFiles()

        logger.info { "Start processing components" }

        val components = getComponents(resolver, files)

        components.forEach { logger.info { "Found component ${it.qualifiedName?.asString()}" } }

        val componentsHolder = createComponentsHolder(components)

        generateComponentsObject(codeGenerator, componentsHolder)

        val extensionProperties = components.map {
            getComponentExtensionProperties(it)
        }

        components.zip(extensionProperties).map {
            getComponentExtensionFile(it.first, it.second)
        }.forEach {
            writeComponentExtensionFile(codeGenerator, it)
        }

        logger.info { "Start processing systems" }

        val systems = getSystems(resolver, files)

        systems.forEach { system ->
            val systemName = system.qualifiedName?.asString()
            logger.info { "Found system '$systemName'" }

            val props = getQueryPropertiesFromClass(system)
            val propString = props.joinToString(prefix = "[", postfix = "]") { it.simpleName.asString() }

            logger.info { "Found ${props.size} component(s) $propString for system '$systemName'" }

            val entitiesClasses = props.map {
                val queryAnnotation = getQueryAnnotationFromProperty(it)
                val with = getWithFromQuery(queryAnnotation)

                val forEachFun = getQueryForEachFunSpec(with)

                return@map getEntityQueryClass(system.simpleName.asString(), it.simpleName.asString(), forEachFun)
            }

            writeEntitiesQueryFile(codeGenerator, getEntityQueryClassFile(system, entitiesClasses))
        }

//            val systemComponents = getSystemComponents(it)
//            logger.info { "Found ${systemComponents.size} component(s) ${systemComponents.joinToString(prefix = "[", postfix = "]")} for system '$systemName'" }
//
//            logger.info { "Creating scope for system '$systemName'" }
//
//            val systemScope = createSystemScope(it.toClassName(), systemComponents)
//
//            logger.info { "Created scope '${systemScope.name}' for system '$systemName'" }
//
//            logger.info { "Creating entity class for system '$systemName'" }
//
//            val systemEntityClass = createSystemEntityClass(it.containingFile!!, systemComponents, systemScope, it)
//
//            logger.info { "Created entity class '${systemEntityClass.name}' for system '$systemName'" }
//
//            generateSystemClasses(codeGenerator, it, systemScope, systemEntityClass)
//        }
//
        val entitiesObject = createEntitiesObject(components)

        generateEntitiesObject(codeGenerator, entitiesObject)

        invoked = true
        return emptyList()
    }
}
