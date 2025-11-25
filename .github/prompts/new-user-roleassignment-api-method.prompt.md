---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Role Assignment API which is part of the Feature API project'
---

Your goal is to implement a new API method ${input:methodName} in the Role Assignment API of the Feature API project following the guidelines provided in the instructions files.

When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the Role API :

- The `UserRoleAssignment` table exists only in the `Feature` database, so you will only need to update this database.
- Re-use exiting request and response DTOs for the Role API, such as `RoleAssignmentRequest` and `RoleAssignmentResponse`, as much as possible.
- Re-use `RoleAssignmentPresenter` for all methods that need to return a `RoleAssignment` object.
- Re-use `InputData` and `OutputData` object for use cases as much as possible
- You are allowed to create new request and response DTOs if needed, but try to re-use existing ones as much as possible. The same goes for `InputData` and `OutputData` objects.

A `RoleAssignment` requires the following :
- `IdUser` - the id of the user that the role is assigned to.
- `IdRole` - the id of the role that is assigned to the user.
- `IdCustomer` - the id of the customer that the role assignment is for. 

When implementing a method that returns a list of roles  assigned to a user you should follow the guidelines for returning lists in the #api-guidelines.instructions.md file.
A new presenter should be created for this purpose, so it can be re-used in other methods that return lists of user roles. 
It is also mandatory that the roles are group by `Customer`, so the list of roles for a user should be returned as a list of customers, each customer containing a list of roles assigned to the user for that customer. Customer name and id should be returned, role name and id should be returned.

