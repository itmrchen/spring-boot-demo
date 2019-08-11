package com.example.model.po;

import com.example.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResourceLink extends BaseModel {

    private static final long serialVersionUID = 1L;
    private Integer roleId;
    private Integer resourceId;

    @Override
    public Map<String, String> pkMap() {
        Map<String, String> map = new HashMap<>();
        map.put("roleId", "role_id");
        map.put("resourceId", "resource_id");
        return map;
    }

}
