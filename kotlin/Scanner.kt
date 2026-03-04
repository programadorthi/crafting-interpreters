internal class Scanner(
  private val source: String,
) {

  private val keywords = mapOf(
    "and"    to TokenType.AND,
    "class"  to TokenType.CLASS,
    "else"   to TokenType.ELSE,
    "false"  to TokenType.FALSE,
    "for"    to TokenType.FOR,
    "fun"    to TokenType.FUN,
    "if"     to TokenType.IF,
    "nil"    to TokenType.NIL,
    "or"     to TokenType.OR,
    "print"  to TokenType.PRINT,
    "return" to TokenType.RETURN,
    "super"  to TokenType.SUPER,
    "this"   to TokenType.THIS,
    "true"   to TokenType.TRUE,
    "var"    to TokenType.VAR,
    "while"  to TokenType.WHILE,
  )
  private val tokens = mutableListOf<Token>()

  private var current = 0
  private var start = 0
  private var line = 1

  fun scanTokens(): List<Token> {
    while (!isAtEnd()) {
      start = current
      scanToken()
    }

    tokens += Token(
      type = TokenType.EOF,
      lexeme = "",
      literal = null,
      line = line,
    )
    return tokens
  }

  private fun isAtEnd(): Boolean {
    return current >= source.length
  }

  private fun advance(): Char {
    return source[current++]
  }

  private fun addToken(type: TokenType) {
    addToken(type = type, literal = null)
  }

  private fun addToken(type: TokenType, literal: Any?) {
    val text = source.substring(startIndex = start, endIndex = current)
    tokens += Token(
      type = type,
      lexeme = text,
      literal = literal,
      line = line,
    )
  }

  private fun match(expected: Char): Boolean {
    if (isAtEnd()) return false
    if (source[current] != expected) return false

    current++
    return true
  }

  private fun peek(): Char {
    if (isAtEnd()) return Char.MIN_VALUE
    return source[current]
  }

  private fun peekNext(): Char {
    if (current + 1 >= source.length) {
      return Char.MIN_VALUE
    }
    return source[current + 1]
  }

  private fun string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++
      }
      advance()
    }

    if (isAtEnd()) {
      Lox.error(line = line, message = "Unterminated string.")
      return
    }

    advance()

    val value = source.substring(startIndex = start + 1, endIndex = current - 1)
    addToken(type = TokenType.STRING, literal = value)
  }

  private fun isDigit(c: Char): Boolean {
    return c >= '0' && c <= '9'
  }

  private fun number() {
    while (isDigit(peek())) {
      advance()
    }

    if (peek() == '.' && isDigit(peekNext())) {
      advance()

      while (isDigit(peek())) {
        advance()
      }
    }

    val value = source.substring(startIndex = start, endIndex = current)
    addToken(type = TokenType.NUMBER, literal = value.toDouble())
  }

  private fun isAlpha(c: Char): Boolean {
    return (c >= 'A' && c <= 'Z') ||
           (c >= 'a' && c <= 'z') ||
           c == '_'
  }

  private fun isAlphaNumeric(c: Char): Boolean {
    return isAlpha(c) || isDigit(c)
  }

  private fun identifier() {
    while (isAlphaNumeric(peek())) {
      advance()
    }

    val text = source.substring(startIndex = start, endIndex = current)
    val type = keywords[text] ?: TokenType.IDENTIFIER
    addToken(type = type)
  }

  private fun scanToken() {
    when (val c = advance()) {
      ' ', '\r', '\t' -> {}
      '\n' -> line++
      '"' -> string()
      '(' -> addToken(TokenType.LEFT_PAREN)
      ')' -> addToken(TokenType.RIGHT_PAREN)
      '{' -> addToken(TokenType.LEFT_BRACE)
      '}' -> addToken(TokenType.RIGHT_BRACE)
      ',' -> addToken(TokenType.COMMA)
      '.' -> addToken(TokenType.DOT)
      '-' -> addToken(TokenType.MINUS)
      '+' -> addToken(TokenType.PLUS)
      ';' -> addToken(TokenType.SEMICOLON)
      '*' -> addToken(TokenType.STAR)
      '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
      '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
      '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
      '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
      '/' -> {
        if (match('/')) {
          while (peek() != '\n' && !isAtEnd()) {
            advance()
          }
        } else {
          addToken(TokenType.SLASH)
        }
      }
      else -> {
        if (isDigit(c)) {
          number()
        } else if (isAlpha(c)) {
          identifier()
        } else {
          Lox.error(line = line, message = "Unexpected character.")
        }
      }
    }
  }
}
