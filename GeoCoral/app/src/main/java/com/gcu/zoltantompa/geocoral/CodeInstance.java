package com.gcu.zoltantompa.geocoral;

import java.io.Serializable;

/**
 * code taken and modified from Lab5
 * This is a storage class for instances from the database
 */

public class CodeInstance implements Serializable {

// *********************************************
// Declare variables etc.
// *********************************************

    private String code;
    private String typicalValues;
    private String description;

    private static final long serialVersionUID = 0L;

// *********************************************
// Declare getters and setters etc.
// *********************************************

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getTypicalValues() {
        return typicalValues;
    }
    public void setTypicalValues(String typicalValues) {
        this.typicalValues = typicalValues;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        String codeIndexData;
        codeIndexData = "CodeInstance [code=" + code;
        codeIndexData += ", typicalValues=" + typicalValues;
        codeIndexData += ", description=" + description + "]";
        return codeIndexData;
    }
}
