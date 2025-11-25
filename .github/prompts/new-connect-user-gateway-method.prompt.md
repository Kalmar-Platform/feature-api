---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Implement a call to a new user method in the Visma Connect Public API'
---

 Your goal is to implement a new call to the Visma Connect Public API to manage users in the Identity Provider.
 The method should be implemented in the `VismaConnectUserGatewayAdapater` class, which is part of the `connect-adapter` package in the `interface-adapters` layer of the Feature API project.

 The HTTP method is ${input:httpMethod} and the URL is ${input:url}.

 An example of a JSON request body is provided below ${input:jsonRequestBody}. If no DTO for the request body exists, you should create a new one in the `connect-adapter` module.

 An example of a JSON response body is provided below ${input:jsonResponseBody}. If no DTO for the response body exists, you should create a new one in the `connect-adapter` module.

 The call is authenticated using an `OAuth2` . `Spring Security` is already configured to handle the `OAuth2` authentication, please use the provided client. 
  
 The method should be implemented by following the same patterns as the `createUser` method in the `VismaConnectUserGatewayAdapater` class
