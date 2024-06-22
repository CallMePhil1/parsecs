package parsecs

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.recovery.ResilientFileOutputStream
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.LoggerFactory
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

        val components = getComponents(resolver.getAllFiles())
        generateComponentsObject(codeGenerator, components)

        components.forEach {
            logger.info { it.qualifiedName?.asString() }
        }

        invoked = true
        return emptyList()
    }
}
