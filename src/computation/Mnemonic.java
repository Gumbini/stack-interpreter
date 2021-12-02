package computation;

import java.util.Locale;

enum Mnemonic {
    PEEK, PUSH, POP,
    DUP,
    SWAP,
    DROP,
    RET,

    ADD, SUM,
    MUL, PROD,
    DIV, MOD,
    SQRT,

    BEQ, BNEQ,
    BGT, BGE,
    BLT, BLE,

    BEZ, BNEZ,

    JMP;

    /**
     * Case-insensitive variant of {@link #valueOf(String)}
     *
     * @param s the {@link String } which should be parsed
     * @return the enum constant with the specified name
     * @throws IllegalArgumentException if this enum type has no constant with the specified name
     */
    public static Mnemonic parseMnemonic(String s) throws IllegalArgumentException {
        return valueOf(s.toUpperCase(Locale.ROOT));
    }
}
