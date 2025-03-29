package com.mainapp.dto;
/**
 * Data Transfer Object (DTO) for entity requests.
 * This class is used to transfer entity-related data between layers of the application.
 * It includes attributes such as name, description, type, and policy.
 *
 * @author Pankaj Kataria
 */
public class EntityRequestDTO {
    private String name;
    private String description;
    private String type;
    private String policy;


    public EntityRequestDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
}
