---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Role API which is part of the Feature API project'
---

Your goal is to implement a new API method ${input:methodName} in the Role API of the Feature API project following the guidelines provided in the instructions files.

When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the Role API :

- A `Role` table exists in both the `Feature` database, so that will need to created or updated in both databases.
- The `Feature` database is set as the primary database for the project, so when fetching data use this database unless otherwise specified.
- The `Role` is a simple entity, it only has an id and a name.
- Re-use exiting request and response DTOs for the Role API, such as `RoleRequest` and `RoleResponse`, as much as possible.
- Re-use `RolePresenter` for all methods that need to return a `Role` object.
- Re-use `InputData` and `OutputData` object for use cases as much as possible
- You are allowed to create new request and response DTOs if needed, but try to re-use existing ones as much as possible. The same goes for `InputData` and `OutputData` objects.