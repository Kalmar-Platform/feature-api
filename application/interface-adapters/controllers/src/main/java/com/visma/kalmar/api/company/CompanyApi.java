package com.visma.kalmar.api.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/companies")
@Tag(name = "Companies", description = "Company management endpoints")
public interface CompanyApi {

    @PostMapping
    @Operation(summary = "Create a new company", description = "Creates a new company under a customer with associated context")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Parent customer or context type not found"),
            @ApiResponse(responseCode = "409", description = "Company with same name or organization number already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CompanyResponse> createCompany(@RequestBody CompanyRequest request);

    @GetMapping("/customer/{idCustomer}/company/{idCompany}")
    @Operation(summary = "Get company by ID", description = "Retrieves company details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CompanyResponse> getCompany(@PathVariable String idCustomer, @PathVariable String idCompany);

    @DeleteMapping("/customer/{idCustomer}/company/{idCompany}")
    @Operation(summary = "Delete a company", description = "Deletes a company by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<Void> deleteCompany(@PathVariable String idCustomer, @PathVariable String idCompany);

    @PutMapping("/customer/{idCustomer}/company/{idCompany}")
    @Operation(summary = "Update a company", description = "Updates an existing company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompanyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    ResponseEntity<CompanyResponse> updateCompany(@PathVariable String idCustomer, @PathVariable String idCompany, @RequestBody CompanyRequest request);
}
