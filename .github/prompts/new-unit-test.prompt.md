---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate unit tests for a class in the Feature API project'
---

Your goal is to generate unit tests for the class ${input:className} in the Feature API project following the guidelines provided in the instructions files.

It is important that you read the unit-testing-guidelines.instructions.md file before generating the tests, and that you follow the instructions and patterns described in that file.

If the class that we need to test is a `use case` , identified by the presence of the `UseCase` suffix in the class name, you should follow the specific instructions for testing use cases detailed in the unit-testing-usecases-guidelines.instructions.md file.


