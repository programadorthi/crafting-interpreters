internal class AstPrinter : Expr.Visitor<String> {

    override fun visitBinaryExpr(expr: Expr.Binary): String {
	return parenthesize(
	    name = expr.operator.lexeme,
	    expr.left, expr.right,
	)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
	return parenthesize(
	    name = "group", expr.expression,
	)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
	if (expr.value == null) {
	    return "nil"
	}
	return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
	return parenthesize(
	    name = expr.operator.lexeme, expr.right,
	)
    }

    fun print(expr: Expr): String {
	return expr.accept(this)
    }

    private fun parenthesize(name: String?, vararg exprs: Expr): String {
	return buildString {
	    append("(")
	    append(name)

	    for (expr in exprs) {
		append(" ")
		append(print(expr))
	    }

	    append(")")
	}
    }

}
