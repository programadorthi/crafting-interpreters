import kotlin.system.exitProcess

/*fun main(args: Array<String>) {
  if (args.size > 1) {
    println("Usage: klox [script]")
    exitProcess(64)
  } else if (args.size == 1) {
    Lox.runFile(args[0])
  } else {
    Lox.runPrompt()
  }
}*/

fun main() {
    val expression = Expr.Binary(
	left = Expr.Unary(
	    operator = Token(
		type = TokenType.MINUS,
		lexeme = "-",
		literal = null,
		line = 1,
	    ),
	    right = Expr.Literal(value = 123),
	),
	operator = Token(
	    type = TokenType.STAR,
	    lexeme = "*",
	    literal = null,
	    line = 1,
	),
	right = Expr.Grouping(
	    expression = Expr.Literal(45.67),
	),
    )

    println(AstPrinter().print(expression))
}
