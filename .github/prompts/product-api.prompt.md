---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for Product API'
---

Your task is to implement the API for managing Products follwoing the guidelines provided in the instruction files.

 Following the instructions presented in #copilot-instructions.md file, plus all the other instruction files you task is to implement a new api method ${input:methodName}
 
 If the API doesn't exist yet, you need to create it following the guidelines in the #api-guidelines.instructions.md file.

 Besides the guidelines in all the instruction files you also need to consider the particularities of the Product API:
  - The `Product` API is part of the `Feature` database, so you will need to create or update the `Product` table in the `Feature` database.
  - A `Product` has a name is linked to a `Business Unit`, the `IdBusinessUnit` is mandatory in the `Product` entity and must be validated
  - A `Product` also contains a list of features that are associated with it. The input contains a liist of `IdFeature` that must be validated and stored in the `ProductFeature` junction table.
  - The `InputData` and `OutputData` objects should be defined as generic as possible, so they can be re-used in other methods.
  - The `Presenter` for the Package API should be defined as generic as possible, so it can be re-used in other methods. It should support responding with `HTTP 201 Created` when a new `Package` is created, and `HTTP 200 OK` when a `Package` is returned.

  
 The project uses two MySQL relational databases to store the data, make sure you follow the instructions in the #repositories-guidelines.instructions.md and #gataway-guidelines files to understand how to access the databases and where the data is stored.

  
