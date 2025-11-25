---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for the Feature API which is part of the Feature API project'
---
 Your goal is to implement a new API method ${input:methodName} in the Feature API of the Feature API project following the guidelines provided in the instructions files.
    
 When implementing the method, besides the guidelines in the #copilot-instructions.md and all the other instruction files you also need to consider the particularities of the Feature API:
 - The `Feature` API is part of the `Feature` database, so you will need to create or update the `Feature` table in the `Feature` database.
 - The request and response DTOs for the Feature API should be defined as generic as possible, so they can be re-used in other methods.
 - The `InputData` and `OutputData` records should be defined as generic as possible, so they can be re-used in other methods.
 - The `Presenter` for the Feature API should be defined as generic as possible, so it can be re-used in other methods. It should support responding with `HTTP 201 Created` when a new `Feature` is created, and `HTTP 200 OK` when a `Feature` is returned.

 A `Feature` requires the following : 
 - `IdCustomer` - the id of the customer that owns the feature . We are working with the `Feature` database so make sure you look up the `Customer` in this database
 - `IdPackage` - the id of the package that the feature is for. 
 - `IdSuite` - the id of the suite that the feature is for. Must match the suite of the package.
 - `IdPartner` - the id of the partner that the feature is for.

 When implementing a method that returns a list of feature you should follow the guidelines for returning lists in the #api-guidelines.instructions.md file.
 A new presenter should be created for this purpose, so it can be re-used in other methods that return lists of packages.
  
 When returning a list of feature it would be nice to group them by the `Suite`, and also return the name of the `Suite`, and the name of the `Package` in the response. 
 This will make it easier for the client to understand the feature.