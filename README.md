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

    cd classes && jar --verbose --create --main-class=main.Main --file ../StackInterpreter.jar *


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
**RET**  | -                           | End execution of the program and print remaining stack elements to the console.
|        |                             |
**ADD**  | -                           | Add the top two elements on the stack and push the result back.
|        |                             | *Subtraction is realized by pushing the negative equivalent on the stack or multiplying with -1.*
**SUM**  | -                           | Sum up all elements on the stack. After this operation there is exactly one element left holding the result.
**MUL**  | -                           | Multiply the top two elements on the stack and push the result back.
**PROD** | -                           | Multiply all elements on the stack with each other. After this operation there is exactly one element left holding the result.
**DIV**  | -                           | Divide the second element by the top one and push the result back.
**MUL**  | -                           | Same as **DIV**, but the rest is pushed back on the stack. *This operation will only work with Integers!*
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
**#**    | *[comment]*                 | Mark this line as a comment line.


Important
---------

**Any branch operation** removes two elements from the stack. Use **DUP** beforehand if you want to keep the result of the previous calculation.

Branch to labels using ">label_name" and to absolute line numbers using "=number".

Execution starts at the first line, the ":main" label is not necessary but is considered good practice.


Example program
---------------

test.si

```
:main
push  21
jmp   >Lcond0


# End program if (top % 3 == 0)
:Lcond1
dup
push  3
mod
peek  "Condition 1"
bez   >end


# Subtract one
:Lsub
push  -0.5
dup
sum
peek  Subtraction
jmp   >Lcond1


# End program if (top < 5)
:Lcond0
peek  "Condition 0"
dup
push  5
# Jump to line 15 aka "Lsub" (not recommended)
bge   =15

:end
ret
```


Example output
--------------

```
$ java -jar StackInterpreter.jar  test.si
PEEK [Condition 0]: 21
[WARNING] (conditional) jump based on line number! (Line: 30)
PEEK [Subtraction]: 20.0
PEEK [Condition 1]: 2
PEEK [Subtraction]: 19.0
PEEK [Condition 1]: 1
PEEK [Subtraction]: 18.0
PEEK [Condition 1]: 0
Remaining stack elements on finish: TOP -> [18.0]
```
