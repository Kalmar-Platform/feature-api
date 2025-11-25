---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the User API which is part of the Feature API project'
---

Your goal is to implement a new API method ${input:methodName} in the User API of the Feature API project following the guidelines provided in the instructions files.
If the API doesn't exist yet, you need to create it following the guidelines in the #api-guidelines.instructions.md file.

 When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the User API:
 - The `User` table is part of the `Feature` database, so you need to update only the `Feature` database.
 - When creating or updating a user the `languageCode` , not the `languageId` is part of the input. The language code will need to be converted to a `languageId` when saving the user.
 - The same logic applies when returning a user, the `languageCode` should be returned instead of the `languageId`.
 - The request and response DTOs for the User API should be defined as genric as possible, so they can be re-used in other methods.
 - The `InputData` and `OutputData` objects should be defined as generic as possible, so they can be re-used in other methods.
 - The `Presenter` for the User API should be defined as generic as possible, so it can be re-used in other methods. It should support responding with `HTTP 201 Created` when a new `Suite` is created, and `HTTP 200 OK` when a `Suite` is returned.
 - Users must be synchronized with `VismaConnect` identity provider, so each time a user is created or updated, the user must be synchronized by calling the appropriate method in the `VismaConnect Pulbic API`. 
When implementing the synchronization, you should follow the guidelines in the [Visma Connect Integration](visma-connect-integration.instructions.md) file.