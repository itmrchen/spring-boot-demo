create or replace view t_attachment_view as
select t_attachment.*,
       case
       when t_user.head_img_id is not null then 't_user.head_img_id'
       when tmal.attachment_id is not null then 't_mail_attachment_link.attachment_id'
       else null
       end link_info
from t_attachment t_attachment
left join t_user t_user on t_attachment.id = t_user.head_img_id
left join t_mail_attachment_link tmal on t_attachment.id = tmal.attachment_id;
