package com.gings.utils.code;

public interface Code {
        
    /**
     * for JSON serialization
     */
    public String getName();

    /**
     * for JSON serialization
     */
    public String getCode();

    public boolean equalsByCode(String code);

}
