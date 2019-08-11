create or replace view t_mail_to_users as
select t_mail_to_user_link.mail_id mail_id, group_concat(t_user.nickname separator ',') info
from t_mail_to_user_link t_mail_to_user_link
left join t_user t_user on t_user.id = t_mail_to_user_link.to_user_id
group by t_mail_to_user_link.mail_id;
