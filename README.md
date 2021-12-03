# Stack Interpreter
A simple runtime stack-assembly interpreter and calculator


Build
-----

Check the version of the installed java (compiler):

    java --version
    javac --version

Sample output:

    $ java --version
    openjdk 17.0.1 2021-10-19
    $ javac --version
    javac 17.0.1
    ...

***To compile the code successfully, you need at least version 16!***

Install the Java 17 Development Kit (optional):

    sudo apt install openjdk-17-jdk
    
Run the Java compiler:

    javac -verbose -d ./classes $(find ./src/**/*.java)
    
Run the JAR archiver:

    (cd classes && jar --verbose --create --main-class=main.Main --file ../StackInterpreter.jar *)


Usage
-----

    java -jar StackInterpreter.jar <file name>
    
Mnemonics
---------

Lower case is possible.

Mnemonic | Arguments | Description
:------: | :-------: | :----------
**PUSH** | *\<decimal number\>*        | Push a decimal number on the stack.
**PEEK** | *[String]*                  | Print the value of the most recently added element to the console **without removing it**.
**POP**  | *[String]*                  | **Remove** the most recently added element and print its value to the console.
|        |                             |
**DUP**  | -                           | Duplicate the most recently added value.
**SWAP** | -                           | Change the order of the top two elements on the stack.
**DROP** | -                           | Same as **POP**, but silent.
**CALL** | *\<label \| line number\>*  | Call a subroutine.
**RET**  | -                           | End execution of the current subroutine and return to the previous one.
|        |                             | If the program is returning from *:main* the remaining stack elements will be printed to the console.
|        |                             |
**ADD**  | -                           | Add the top two elements on the stack and push the result back.
**SUB**  | -                           | Subtract the top element from the second one and push the result back.
**SUM**  | -                           | Sum up all elements on the stack. After this operation there is exactly one element left holding the result.
**MUL**  | -                           | Multiply the top two elements on the stack and push the result back.
**PROD** | -                           | Multiply all elements on the stack with each other. After this operation there is exactly one element left holding the result.
**DIV**  | -                           | Divide the second element by the top one and push the result back.
**MOD**  | -                           | Same as **DIV**, but the rest is pushed back on the stack. *This operation will only work with Integers!*
**SQRT** | -                           | Calculate the square root of the topmost element and push it back on the stack.
|        |                             |
**BEQ**  | *\<label \| line number\>*  | Branch if the second element is equal to the top one.
**BNEQ** | *\<label \| line number\>*  | Branch if the second element is not equal to the top one.
**BGT**  | *\<label \| line number\>*  | Branch if the second element is greater than the top one.
**BGE**  | *\<label \| line number\>*  | Branch if the second element is greater than or equal to the top one.
**BLT**  | *\<label \| line number\>*  | Branch if the second element is less than the top one.
**BLE**  | *\<label \| line number\>*  | Branch if the second element is less than or equal to the top one.
**BEZ**  | *\<label \| line number\>*  | Branch if the second element is equal to zero.
**BNEZ** | *\<label \| line number\>*  | Branch if the second element is not equal to zero.
**JMP**  | *\<label \| line number\>*  | Branch unconditionally.
|        |                             |
**:**    | *\<name\>*                  | Create a new Label (Do **not** use whitespace or any other character between ':' and the label name!).
**#**    | *[comment]*                 | Everything in this line is ignored after this character. Use at least one whitespace after a valid mnemonic (or its argument) to comment a code line.


Important
---------

**Any branch operation** (B...) removes **two** elements from the stack. Use **DUP** beforehand if you want to keep the result of the previous calculation.

Branch to a label using ">label_name" and to an absolute line number using "=number".

Execution starts at the "*:main*" label.


Example program
---------------

Subtract 1 from the starting value 24 as long as the remainder of the division by 3 is not equal to 1.

Pseudo code:

```
void main() {
    int top = 24;
    while (top % 3 != 1) {
        top -= 1;
    }
    print(top);
    return;
}
```

Equivalent (demo) code for the stack interpreter:

```
:Lcond0               # if (top % 3 == 1) return;
dup
peek   Value
push   3 
mod
peek   "Rest"
push   1
beq    >end
call   >Rsub
jmp    >Lcond0


:Rsub                 # top -= 1;
push   0.5
dup
add
push   -1
mul
add
ret


:main
push   24
jmp    >Lcond0


:end
ret
```


Example output
--------------

```
$ java -jar StackInterpreter.jar example.si
PEEK [Value]: 24
PEEK [Rest]: 0
PEEK [Value]: 23.0
PEEK [Rest]: 2
PEEK [Value]: 22.0
PEEK [Rest]: 1
Remaining stack elements on finish: TOP -> [22.0]
```
