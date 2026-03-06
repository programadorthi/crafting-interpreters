import java.io.IOException
import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.system.exitProcess

internal object Lox {

  private var hadError = false

  @Throws(IOException::class)
  fun runFile(path: String) {
    val text = Paths.get(path).readText()
    runScript(text)
    if (hadError) {
      exitProcess(65)
    }
  }

  @Throws(IOException::class)
  fun runPrompt() {
    while (true) {
      print("> ")
      val line = readlnOrNull()
      if (line.isNullOrBlank()) break
      runScript(line)
      hadError = false
    }
  }

  fun error(line: Int, message: String) {
    report(line = line, where = "", message = message)
  }

  fun error(token: Token, message: String) {
    val location = when (token.type) {
      TokenType.EOF -> " at end"
      else -> " at '${token.lexeme}'"
    }
    report(line = token.line, where = location, message = message)
  }

  private fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] Error $where: $message")
    hadError = true
  }

  private fun runScript(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()
    val parser = Parser(tokens)
    val expression = parser.parse()

    if (hadError || expression == null) return

    println(AstPrinter().print(expression))
  }
}
