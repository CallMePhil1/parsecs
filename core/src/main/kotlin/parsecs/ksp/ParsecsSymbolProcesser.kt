package parsecs.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.ksp.toClassName
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

        logger.info { "Start processing systems" }

        val systems = getSystems(resolver, files)

        systems.forEach {
            val systemName = it.qualifiedName?.asString()
            logger.info { "Found system '$systemName'" }

            val systemComponents = getSystemComponents(it)
            logger.info { "Found ${systemComponents.size} component(s) ${systemComponents.joinToString(prefix = "[", postfix = "]")} for system '$systemName'" }

            logger.info { "Creating scope for system '$systemName'" }

            val systemScope = createSystemScope(it.toClassName(), systemComponents)

            logger.info { "Created scope '${systemScope.name}' for system '$systemName'" }

            logger.info { "Creating entity class for system '$systemName'" }

            val systemEntityClass = createSystemEntityClass(it.containingFile!!, systemComponents, systemScope, it)

            logger.info { "Created entity class '${systemEntityClass.name}' for system '$systemName'" }

            generateSystemClasses(codeGenerator, it, systemScope, systemEntityClass)
        }

        invoked = true
        return emptyList()
    }
}
