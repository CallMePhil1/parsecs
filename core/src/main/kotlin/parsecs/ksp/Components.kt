package parsecs.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import parsecs.ecs.entity.EntityID
import parsecs.ext.camelcase

fun getComponentExtensionFile(componentClass: KSClassDeclaration, extensionProp: PropertySpec): FileSpec {
    val className = componentClass.toClassName()

    return FileSpec
        .builder("${className.packageName}.ext", className.simpleName)
        .addImport(Constants.COMPONENTS_PACKAGE_NAME, Constants.DATA_HOLDER_NAME)
        .addProperty(extensionProp)
        .build()
}

fun getComponentExtensionProperties(component: KSClassDeclaration): PropertySpec {
    val className = component.toClassName()
    val componentArrayName = formatArrayProperty(component.simpleName.asString())

    val getterFun = FunSpec
        .getterBuilder()
        .addModifiers(KModifier.INTERNAL)
        .addStatement("return %N.%N[this]", Constants.DATA_HOLDER_NAME, componentArrayName)
        .build()

    return PropertySpec.builder(className.simpleName.camelcase(), className)
        .addModifiers(KModifier.INTERNAL)
        .receiver(EntityID::class)
        .getter(getterFun)
        .build()
}

fun writeComponentExtensionFile(codeGenerator: CodeGenerator, file: FileSpec) {
    val newFile = codeGenerator.createNewFile(Dependencies(false), file.packageName, file.name)
    val writer = newFile.writer()

    file.writeTo(writer)
    writer.close()
}