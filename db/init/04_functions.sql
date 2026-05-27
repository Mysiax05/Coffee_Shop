CREATE OR REPLACE FUNCTION f_get_category_subtree(f_category_id int)
RETURNS TABLE (categoryid int)
AS $$
BEGIN
    RETURN QUERY
    WITH RECURSIVE category_tree AS (
        SELECT c.categoryid
        FROM categories c
        WHERE c.categoryid = f_category_id

        UNION ALL

        SELECT c.categoryid
        FROM categories c
        INNER JOIN category_tree ct ON c.parentcategoryid = ct.categoryid
    )
    SELECT ct.categoryid
    FROM category_tree ct;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_filter_products(
    min_price decimal default null,
    max_price decimal default null,
    f_category_id int default null,
    f_attributes_filter jsonb default null
)
RETURNS TABLE(
    productid int,
    name varchar,
    price decimal,
    stock int,
    attributes jsonb
 ) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.productid,
        p.name,
        p.price,
        p.stock,
        p.attributes
    FROM products p
    WHERE p.isactive = true
        and (min_price is null or p.price >= min_price)
        and (max_price is null or p.price <= max_price)
        and (f_category_id is null or p.categoryid in (
            SELECT sub.categoryid FROM f_get_category_subtree(f_category_id) as sub))
        and (f_attributes_filter is null or p.attributes @> f_attributes_filter);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_report_best_sellers(
    number_of_best_sellers int default null,
    f_category_id int default null)
RETURNS TABLE (
    product_id int,
    name varchar,
    total_sold int,
    revenue numeric
    ) AS $$
BEGIN
    RETURN QUERY
    SELECT p.productid,
           p.name,
           sum(od.quantity)::int AS total_sold,
           sum(od.unitprice * od.quantity) AS revenue
        FROM products p
        INNER JOIN orderdetails od ON od.productid = p.productid
        INNER JOIN orders o ON o.orderid = od.orderid
        WHERE o.status IN ('pending', 'delivered', 'canceled', 'packed')
        AND (f_category_id is null or p.categoryid in (
            SELECT sub.categoryid FROM f_get_category_subtree(f_category_id) as sub))
        GROUP BY p.productid, p.name
        ORDER BY total_sold DESC
        LIMIT number_of_best_sellers;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_report_revenue_by_category()
RETURNS TABLE (
    category_id int,
    category_name varchar,
    revenue decimal
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        c.categoryid,
        c.categoryname,
        coalesce(sum(od.unitprice * od.quantity), 0) AS revenue
    FROM categories c
    LEFT JOIN products p ON p.categoryid IN (
        SELECT categoryid FROM f_get_category_subtree(c.categoryid)
    )
    LEFT JOIN orderdetails od ON od.productid = p.productid
    LEFT JOIN orders o ON o.orderid = od.orderid
    WHERE (o.status IN ('pending', 'delivered', 'canceled', 'packed')
    OR o.status IS NULL)
    GROUP BY c.categoryid, c.categoryname
    ORDER BY c.categoryid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION t_f_validate_leaf_category()
RETURNS TRIGGER AS $$
BEGIN
    if exists(select 1 from categories where parentcategoryid = NEW.categoryid) then
        raise exception 'Category with ID % is not a leaf category', NEW.categoryid;
    end if;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION t_f_validate_product_active()
RETURNS TRIGGER AS $$
BEGIN
    if exists(select 1 from products where productid = NEW.productid and isactive = false) then
        raise exception 'Product with ID % is not active', NEW.productid;
    end if;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION t_f_validate_payment_method_active()
RETURNS TRIGGER AS $$
BEGIN
    if exists(select 1 from paymentmethods where paymentmethodid = NEW.paymentmethodid and isactive = false) then
        raise exception 'Payment method with ID % is not active', NEW.paymentmethodid;
    end if;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



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
	
	
create or replace function t_f_restore_stock_on_cancel()
returns trigger as $$
    declare
        v_item record;
    begin
        if old.status='packed' and new.status='cancelled' then
            for v_item in (
                select od.productid, od.quantity
                from orderdetails od
                where od.orderid = old.orderid
            ) loop
                call p_update_stock(v_item.productid, v_item.quantity);
                end loop;
        end if;
        return new;
    end;
    $$ language plpgsql;
	
	
create or replace function t_f_validate_order_status_transition()
returns trigger as $$
    begin
        if old.status = new.status then
            return new;
        end if;
        if (old.status='cancelled') or
           (old.status='delivered') or
           (old.status='pending' and new.status='delivered') or
           (old.status='packed' and new.status='pending')
           then
            raise exception 'Invalid order status transition from % to %', old.status, new.status;
        end if;
        return new;
    end;
    $$ language plpgsql;
	
create or replace function t_f_validate_address_active()
returns trigger as $$
    begin
        if not exists(select 1 from vw_active_addresses where new.addressid=addressid) then
            raise exception 'Address with ID % is not active', new.addressid;
        end if;
        return new;
    end;
    $$ language plpgsql;
