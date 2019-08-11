create or replace view t_role_resources_info as
select t_role.id, group_concat(t_resource.resources_info order by t_resource.resource_seq) resources_info
from t_role t_role
left join t_role_resource_link t_role_resource_link on t_role.id = t_role_resource_link.role_id
left join (
    select res.id, res.resource_seq,
    case
    when res.resource_method is null then concat(res.resource_desc, ":", res.resource_type, ".", res.resource_pattern, ".", res.resource_method)
    else concat(res.resource_desc, ": ", res.resource_type, ".", res.resource_pattern)
    end resources_info
    from t_resource res
) t_resource on t_role_resource_link.resource_id = t_resource.id
group by t_role.id;
