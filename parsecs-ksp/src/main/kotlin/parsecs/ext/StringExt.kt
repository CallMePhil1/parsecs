package parsecs.ext

fun String.camelcase(): String = this.replaceFirst(this[0], this[0].lowercaseChar())