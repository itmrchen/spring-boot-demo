package com.example.params;

import com.example.model.BaseModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Params<T extends BaseModel> implements Serializable {

    private static final long serialVersionUID = 1;
    private T entity;
    private Map<String, Object> map = new HashMap<>();

    public Params(T entity) {
        this.entity = entity;
    }
    public Params<T> put(String key, Object value) {
        map.put(key, value);
        return this;
    }

}
