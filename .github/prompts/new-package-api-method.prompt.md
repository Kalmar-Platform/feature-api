---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Package API which is part of the Feature API project'
---
 Your goal is to implement a new API method ${input:methodName} in the Package API of the Feature API project following the guidelines provided in the instructions files.
 If the API doesn't exist yet, you need to create it following the guidelines in the #api-guidelines.instructions.md file.

 When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the Package API:
  - The `Package` API is part of the `Feature` database, so you will need to create or update the `Package` table in the `Feature` database.
  - A `Package` is part of a suite, so an `IdSuite` is mandatory in the `Package` entity and must be validated.
  - Besides the `IdSuite`, a `Package` contains an `Id` and a `Name`, so you can use the `Package` entity as is.
  - The request and response DTOs for the Package API should be defined as generic as possible, so they can be re-used in other methods.
  - The `InputData` and `OutputData` objects should be defined as generic as possible, so they can be re-used in other methods.
  - The `Presenter` for the Package API should be defined as generic as possible, so it can be re-used in other methods. It should support responding with `HTTP 201 Created` when a new `Package` is created, and `HTTP 200 OK` when a `Package` is returned.

 When implementing a method that returns a list of packages you should follow the guidelines for returning lists in the #api-guidelines.instructions.md file.
 A new presenter should be created for this purpose, so it can be re-used in other methods that return lists of packages.
 
  
  
  