---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Customer API which is part of the Feature API project'
---

 Your goal is to implement a new API method ${input:methodName} in the Customer API of the Feature API project following the guidelines provided in the instructions files.
 
 When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the Customer API :

 - A `Customer` table exists in both the `Feature` database, so that will need to created or updated in both databases.
 - The `Feature` database is set as the primary database for the project, so when fetching data use this database unless otherwise specified.
 - A `Customer` is a `Context`, the `Customer` table is just an id reference to the `Context` table. All Customer data is stored in the `Context` table. This relationship is defined in the `Customer` entity.
 - When searching for a `Customer` by id, you will need to search in the `Context` table, not the `Customer` table, so use the `context id`.
 - A `Customer` belongs to a `Country`, but the API will receive the `Country Code` as input, so you will need to convert the `Country Code` to the `Country Id`. 
 - The above also applies when returning `Customer` data, you need to return the `Country Code` instead of the `Country Id`.
 - Unless otherwise specified, the `Country` data should be fetched from the `Feature` database through the existing `CountryGateway` interface.
 - Re-use exiting request and response DTOs for the Customer API, such as `CustomerRequest` and `CustomerResponse`, as much as possible.
 - Re-use `CustomerPresenter` for all methods that need to return a `Customer` object.
 - Re-use `InputData` and `OutputData` object for use cases as much as possible 
 - You are allowed to create new request and response DTOs if needed, but try to re-use existing ones as much as possible. The same goes for `InputData` and `OutputData` objects.