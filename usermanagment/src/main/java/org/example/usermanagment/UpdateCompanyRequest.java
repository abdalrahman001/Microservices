package org.example.usermanagment;

public class UpdateCompanyRequest {
    private String id;
    private String newName;

    public UpdateCompanyRequest() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
