---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Suite API which is part of the Feature API project'
---

 Your goal is to implement a new API method ${input:methodName} in the Suite API of the Feature API project following the guidelines provided in the instructions files.
 If the API doesn't exist yet, you need to create it following the guidelines in the #api-guidelines.instructions.md file.

 When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the Suite API: 
- The `Suite` API is part of the `Feature` database, so you will need to create or update the `Suite` table in the `Feature` database.
- The `Suite` is a simple object, just contains an id and a name, so you can use the `Suite` entity as is.
- The request and response DTOs for the Suite API should be defined as genric as possible, so they can be re-used in other methods.
- The `InputData` and `OutputData` objects should be defined as generic as possible, so they can be re-used in other methods.
- The `Presenter` for the Suite API should be defined as generic as possible, so it can be re-used in other methods. It should support responding with `HTTP 201 Created` when a new `Suite` is created, and `HTTP 200 OK` when a `Suite` is returned.