import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val exitCode = CommandLine(BaseCommand())
        .setUsageHelpAutoWidth(true)
        .setAbbreviatedOptionsAllowed(true)
        .setAbbreviatedSubcommandsAllowed(true)
        .execute(*args)
    exitProcess(exitCode)
}
