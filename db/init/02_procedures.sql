
create or replace procedure p_check_customer_exists(f_customer_id int) as $$
begin
    if not exists (select 1 from customers where customerid = f_customer_id) then
        raise exception 'Customer with ID % does not exist', f_customer_id;
    end if;
end;
$$ language plpgsql;

create or replace procedure p_check_order_exists(f_order_id int) as $$
begin
    if not exists (select 1 from orders where orderid = f_order_id) then
        raise exception 'Order with ID % does not exist', f_order_id;
    end if;
end;
$$ language plpgsql;