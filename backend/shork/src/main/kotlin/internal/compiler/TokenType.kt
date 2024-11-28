package software.shonk.interpreter.internal.compiler

internal enum class TokenType {
    // Operators
    PLUS,
    MINUS,
    STAR, // Star is also Address mode
    SLASH,
    MODULO,

    // Modifiers
    A,
    B,
    AB,
    BA,
    F,
    X,
    I,

    // Address modes
    DOLLAR,
    HASHTAG,
    AT,
    LEFT_BRACE,
    RIGHT_BRACE,
    LOWER_THAN,
    GREATER_THAN,

    // Opcodes
    DAT,
    NOP,
    MOV,
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    JMP,
    JMZ,
    JMN,
    DJN,
    CMP,
    SLT,
    SPL,
    SEQ,
    SNE,
    ORG,
    EQU,
    END,
    // Those are for private space which will not be implemented until everything else is done
    LDP,
    STP,

    // Literals
    // STRING, // Doesn't exist here
    NUMBER,
    IDENTIFIER,

    // Others
    SEMICOLON,
    COMMA,
    DOT,
    EOF;

    override fun toString(): String {
        return when (this) {
            PLUS -> "PLUS"
            MINUS -> "MINUS"
            STAR -> "STAR"
            SLASH -> "SLASH"
            MODULO -> "MODULO"
            A -> "A"
            B -> "B"
            AB -> "AB"
            BA -> "BA"
            F -> "F"
            X -> "X"
            I -> "I"
            HASHTAG -> "HASHTAG"
            DOLLAR -> "DOLLAR"
            AT -> "AT"
            LEFT_BRACE -> "LEFT_BRACE"
            RIGHT_BRACE -> "RIGHT_BRACE"
            LOWER_THAN -> "LOWER_THAN"
            GREATER_THAN -> "GREATER_THAN"
            DAT -> "DAT"
            NOP -> "NOP"
            MOV -> "MOV"
            ADD -> "ADD"
            SUB -> "SUB"
            MUL -> "MUL"
            DIV -> "DIV"
            MOD -> "MOD"
            JMP -> "JMP"
            JMZ -> "JMZ"
            JMN -> "JMN"
            DJN -> "DJN"
            CMP -> "CMP"
            SLT -> "SLT"
            SPL -> "SPL"
            ORG -> "ORG"
            EQU -> "EQU"
            END -> "END"
            SEQ -> "SEQ"
            SNE -> "SNE"
            LDP -> "LDP"
            STP -> "STP"
            // STRING -> "STRING"
            NUMBER -> "NUMBER"
            IDENTIFIER -> "IDENTIFIER"
            SEMICOLON -> "SEMICOLON"
            COMMA -> "COMMA"
            DOT -> "DOT"
            EOF -> "EOF"
        }
    }

    companion object {
        fun instructions(): List<TokenType> {
            return listOf(
                DAT,
                NOP,
                MOV,
                ADD,
                SUB,
                MUL,
                DIV,
                MOD,
                JMP,
                JMZ,
                JMN,
                DJN,
                CMP,
                SEQ,
                SNE,
                SLT,
                SPL,
                ORG,
                EQU,
                END,
                LDP,
                STP,
            )
        }

        fun modifiers(): List<TokenType> {
            return listOf(A, B, AB, BA, F, X, I)
        }

        fun addressModes(): List<TokenType> {
            return listOf(
                DOLLAR,
                HASHTAG,
                AT,
                LEFT_BRACE,
                RIGHT_BRACE,
                LOWER_THAN,
                GREATER_THAN,
                STAR,
            )
        }
    }
}
