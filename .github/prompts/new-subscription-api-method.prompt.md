---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Subscription API which is part of the Subscription API project'
---
 Your goal is to implement a new API method ${input:methodName} in the Subscription API of the Subscription API project following the guidelines provided in the instructions files.
    
 When implementing the method, besides the guidelines in the #copilot-instructions.md and all the other instruction files you also need to consider the particularities of the Subscription API:
 - The `Subscription` API is part of the `Subscription` database, so you will need to create or update the `Subscription` table in the `Subscription` database.
 - The request and response DTOs for the Subscription API should be defined as generic as possible, so they can be re-used in other methods.
 - The `InputData` and `OutputData` records should be defined as generic as possible, so they can be re-used in other methods.
 - The `Presenter` for the Subscription API should be defined as generic as possible, so it can be re-used in other methods. It should support responding with `HTTP 201 Created` when a new `Subscription` is created, and `HTTP 200 OK` when a `Subscription` is returned.

 A `Subscription` requires the following : 
 - `IdCustomer` - the id of the customer that owns the subscription . We are working with the `Subscription` database so make sure you look up the `Customer` in this database
 - `IdPackage` - the id of the package that the subscription is for. 
 - `IdSuite` - the id of the suite that the subscription is for. Must match the suite of the package.
 - `IdPartner` - the id of the partner that the subscription is for.

 When implementing a method that returns a list of subscriptions you should follow the guidelines for returning lists in the #api-guidelines.instructions.md file.
 A new presenter should be created for this purpose, so it can be re-used in other methods that return lists of packages.
  
 When returning a list of subscriptions it would be nice to group them by the `Suite`, and also return the name of the `Suite`, and the name of the `Package` in the response. 
 This will make it easier for the client to understand the subscriptions.