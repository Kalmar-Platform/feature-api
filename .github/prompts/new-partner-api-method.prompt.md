---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Partner API which is part of the Feature API project'
---

 Your goal is to implement a new API method ${input:methodName} in the Suite API of the Feature API project following the guidelines provided in the instructions files.
 If the API doesn't exist yet, you need to create it following the guidelines in the #api-guidelines.instructions.md file.

 When implementing the method, besides the guidelines in all the instruction files you also need to consider the particularities of the Suite API:
 - The `Partner` API is part of the `Feature` database, so you will need to create or update the `Partner` table in the `Feature` database.
 - A `Partner` is a `Context`, the `Partner` table is just an id reference to the `Context` table. All partner data is stored in the `Context` table. This relationship is defined in the `Partner` entity.
 - When saving or updating a `Partner`, use `JPA inheritance` to save only once in both the `Context` and `Partner` tables. 
 - When searching for a `Partner` by id, you will need to search in the `Context` table, not the `Partner` table
 - A `Partner` only exists in the `Feature` database, so any search for a `Partner` should be done in the `Feature` database.
 - A `Partner` belongs to a `Country`, but the API will receive the `Country Code` as input, so you will need to convert the `Country Code` to the `Country Id`.
 - The above also applies when returning `Partner` data, you need to return the `Country` instead of the `Country Id`.
 - In this case you need to use the `CountryGateway` interface to fetch the `Country` data from the `Feature` database.
 - Re-use existing request and response DTOs for the Partner API, such as `PartnerRequest` and `PartnerResponse`, as much as possible.
 - Re-use `PartnerPresenter` for all methods that need to return a `Partner` object.
 - Re-use `InputData` and `OutputData` object for use cases as much as possible.
 
 
