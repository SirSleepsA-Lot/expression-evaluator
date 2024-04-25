# Logical evaluator

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This project serves as a solution to the exercise for leapwise technical assigment.

### My approach
There are two main problems in this task:
1. #### Parsing the expression in a way that can preserve order of operations:  
   My first idea was to modify the input string to place it in a postfix format on a stack.

    1. Postfix stack is easily solvable:

       1. If the top of the stack is an operator, push it to the helper stack.
       2. If the top of the stack is an operand, check if the top element of the helper stack or the next element is also an operand.
       3. If yes, get the top of the helper stack (that element HAS to be an operator) and apply that operation. Otherwise, push it to the helper stack and continue.
       4. This approach is easily solvable if you have the postfix stack. The problem was that it wasn't optimized. If you check the example expression `(customer.firstName == "JOHN" && customer.salary < 100) OR (customer.address != null && customer.address.city == "Washington")` and the provided JSON object:

          ```json
          {
            "customer": {
              "firstName": "JOHN",
              "lastName": "DOE",
              "address": {
                "city": "Chicago",
                "zipCode": 1234,
                "street": "56th",
                "houseNumber": 2345
              },
              "salary": 99,
              "type": "BUSINESS"
            }
          }
          ```

          You can see that the compiler will just evaluate the first parenthesis as `true`, and the 2nd group can be ignored.
          The postfix stack calculates elements from right to left, so it will start from `customer.address.city == "Washington"`. Furthermore, the next element is `customer.address != null` which has to be calculated before the previous expression since it can stop a NullPointerException if `customer.address` is null when trying to access `customer.address.city`.
          One solution could be to change it to a prefix stack which will then calculate from right to left, but the issue of calculating the entire stack still persists.

    2. Another approach:
        1. A different way to parse the expression is in the expression tree. An element of the tree is its value (an operator, or operand). 
            Tree can easily be calculated from a postfix stack so the `infixToPostfix` method was used to parse the string to postfix stack and that was used to generate an expression tree.
           If the value is an operator its left and right children are the elements that can be used to resolve that specific operation otherwise they are null.
           In the postman collection I provided a /visualize function that will print a tree for a specific expression. Here's the tree for the provided example
           ```
               └── ||
                   ├── &&
                   │   ├── ==
                   │   │   ├── customer.firstName
                   │   │   └── JHON
                   │   └── <
                   │       ├── customer.salary
                   │       └── 100.0
                   └── &&
                   ├── !=
                   │   ├── customer.address
                   │   └── null
                   └── ==
                       ├── customer.address.city
                       └── Washington
           ```
           For a specific tree node the algorithm is as follows:
            1. If the value is a variable resolve and return the value of the variable, if it's a constant return the constant
           2. If the value is an operator, evaluate the left subtree
           3. Check if the operator is && || or ! and depending on the left subtree value stop the algorithm.
           4. Evaluate the right subtree
           5. Evaluate the node and return its value
        2. This way the program will stop the calculation if it's already possible to evaluate an entire expression based only on the part of it.
        3. In the example provided, the program will return true after evaluating the first parenthesis (left subtree of the root node)
       
2. #### Solving the typing issue
   1. Since the expression is a String, there's a need to cast the specific substring to a desirable type (constant, operator, operand)
   2. My approach was to create two helper classes called `Variable` and `Operator`. `Operator` holds all the found operators in the string while `Variable` holds all the variables that have to be calculated from JSON file.
   3. Constants are found based on specific characters such as digits, \", \' and parenthesis.
   4. Using `Variable` and `Operator` helps us to see if a specific value in the tree is an operator or a variable
3. #### Custom JSON
   1. To accept any type of JSON I mapped it to `Map<String, Object>` type. That type was used to create a `JsonObject` variable that can be used to extract the value of a `Variable` in it. 
      That was the only 3rd party library used and as to my knowledge, I was allowed to use any 3rd party library as long as I didn't use it to evaluate the expressions.
      If I misinterpreted the restriction and I wasn't allowed to use it I apologize but the `Object getVariableValue(Variable variable, JsonObject variables)` function that was used to find the value of the variable
      can be easily changed to `Object getVariableValue(Variable variable, Map<String, Object> variables)` and just change the way the Object value in the map is cast. 
## Features

Two main functionalities are:
1. Creating and saving an expression in the database
2. Using a custom JSON to evaluate an expression from the database

## Installation

All you need to do is pull the project and run `mvn spring-boot:run` command

## Usage

The project provides a postman solution that can be used to test the functionalities.
There are also test cases that can be run to test various expressions.

## Examples

By calling /expression function an expression is saved and the function returns the expression id.
Id can be used with a custom JSON using /evaluate/{expressionId} function to evaluate said expression.

