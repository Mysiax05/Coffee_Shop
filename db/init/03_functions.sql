create or replace function
f_get_customer_expenses(f_customer_id int) returns numeric as $$
    begin
    call p_check_customer_exists(f_customer_id);

    return (
        select coalesce(sum(p.amount), 0.00)
        from payments p
        inner join orders o on o.orderid = p.orderid
        where p.status = 'completed'
        and o.customerid = f_customer_id
    );
    end;
$$ language plpgsql;



create or replace function
f_get_customer_orders(f_customer_id int) returns setof orders as $$
    begin
        call p_check_customer_exists(f_customer_id);

        return query (select *
        from orders
        where customerid=f_customer_id);
    end;
    $$ language plpgsql;



create or replace function
f_get_customer_addresses(f_customer_id int) returns setof addresses as $$
    begin
        call p_check_customer_exists(f_customer_id);
        return query (select *
        from addresses
        where customerid=f_customer_id);
    end;
    $$ language plpgsql;



create or replace function
    f_get_order_total(f_order_id int) returns numeric as $$
    begin
    call p_check_order_exists(f_order_id);
    return (
        select sum(unitprice*quantity)
        from orderdetails
        where orderid=f_order_id
        );
    end;
    $$ language plpgsql;



create or replace function
    f_get_order_products(f_order_id int) returns table(
    productid   int,
    name        varchar,
    quantity    int,
    unitprice   numeric(10,2),
    total       numeric(10,2)
) as $$
    begin
    call p_check_order_exists(f_order_id);
    return query (
        select
        od.productid,
        p.name,
        od.quantity,
        od.unitprice,
        od.quantity * od.unitprice as total
        from orderdetails od
        inner join products p on p.productid = od.productid
        where od.orderid = f_order_id
        );
    end;
    $$ language plpgsql;