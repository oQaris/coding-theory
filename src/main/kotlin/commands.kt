import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "coder",
    version = ["CD 0.0.1"],
    subcommands = [Hamming::class, Noise::class],
    description = ["A lightweight CLI-utility that can encode messages to protect data from interference."],
    mixinStandardHelpOptions = true
)
class BaseCommand


@Command(
    name = "noise",
    description = ["Adds errors to the encoded message."]
)
class Noise : Runnable {

    @Option(
        required = true,
        names = ["-m", "--message"],
        description = ["Binary message."]
    )
    lateinit var message: String

    @Option(
        names = ["-s", "--block-size"],
        description = ["Block size for injecting the error."]
    )
    var size: Int = 7

    @Option(
        names = ["-e", "--errors"],
        description = ["Errors in each block. Range format: (min,max)"],
        converter = [IntRangeConverter::class]
    )
    var errors: IntRange = (0 until 1)

    class IntRangeConverter : CommandLine.ITypeConverter<IntRange> {
        override fun convert(value: String): IntRange {
            val arr = value.split("[\\p{Punct}\\s]+".toRegex()).filter { it.isNotEmpty() }
            if (arr.size == 1)
                return arr[0].toInt().let { it..it }
            if (arr.size == 2)
                return arr[0].toInt()..arr[1].toInt()
            else throw CommandLine.TypeConversionException(
                "Invalid format: must be '(min,max)' or 'value' but was '$value'"
            )
        }
    }

    override fun run() {
        message.chunked(size).forEach {
            val out = StringBuilder(it)
            val availableIndex = it.indices.toMutableList()
            for (i in (0 until errors.random())) {
                val idx = availableIndex.random()
                out[idx] = ((out[idx].toString().toInt() + 1) % 2).toString()[0]
                availableIndex.remove(idx)
                if (availableIndex.isEmpty())
                    break
            }
            print(out.toString())
        }
    }
}

@Command(
    name = "hamming",
    subcommands = [EncodeHamming::class, DecodeHamming::class],
    description = ["Allows you to encode and decode messages with a binary hamming algorithm."]
)
class Hamming

open class HammingBase {
    // В разработке!
    /*@Option(
        names = ["--no-convert"],
        negatable = true,
        description = ["Convert message to/from binary."]
    )
    var isConvert: Boolean = true*/

    // В разработке!
    /*@Option(
        names = ["-d", "--dimension"],
        description = ["Code length parameter. Should be > 1, default = 3 -> (7, 4)-code"]
    )
    var d: Int = 3*/

    @Option(
        required = true,
        names = ["-m", "--message"],
        description = ["Text message to encode/decode."]
    )
    lateinit var message: String
}

@Command(
    name = "encode",
    description = ["Encodes the message with a binary Hamming code."]
)
class EncodeHamming : HammingBase(), Runnable {

    override fun run() {
        println("Binary message:")
        val inputBin = buildString {
            message.forEach {
                append(
                    Integer.toBinaryString(it.code)
                        .padStart(8, '0')
                )
            }
        }
        println(inputBin)
        println("Encoded message:")
        println(encode(inputBin))
    }
}

@Command(
    name = "decode",
    description = ["Decodes binary code using Hamming algorithm and corrects errors."]
)
class DecodeHamming : HammingBase(), Runnable {

    override fun run() {
        println("Decoded binary message:")
        val decodeMessage = decode(message)
        println(decodeMessage)
        println("Decoded message:")
        val sb = StringBuilder()
        decodeMessage.chunked(8).forEach { s ->
            sb.append(s.toInt(2).toChar())
        }
        print(sb.toString())
    }
}
