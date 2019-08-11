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
public class BroadcastToUserLink extends BaseModel {

    private static final long serialVersionUID = 1L;
    private Integer broadcastId;
    private Integer toUserId;
    private Integer readStatus;

    @Override
    public Map<String, String> pkMap() {
        Map<String, String> map = new HashMap<>();
        map.put("broadcastId", "broadcast_id");
        map.put("toUserId", "to_user_id");
        return map;
    }

}
