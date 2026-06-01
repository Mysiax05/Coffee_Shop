CREATE OR REPLACE PROCEDURE p_check_product_exists(f_product_id int)
AS $$
BEGIN
    if not exists(select 1 from products where productid = f_product_id) then
        raise exception 'Product with ID % does not exist', f_product_id;
    end if;
END;
$$ LANGUAGE plpgsql;

create or replace procedure p_check_customer_exists(f_customer_id int) as $$
begin
    if not exists (select 1 from customers where customerid = f_customer_id) then
        raise exception 'Customer with ID % does not exist', f_customer_id;
    end if;
end;
$$ language plpgsql;

create or replace procedure p_check_address_exists(p_address_id int) as $$
begin
    if not exists (select 1 from addresses where addressid = p_address_id) then
        raise exception 'Address with ID % does not exist', p_address_id;
    end if;
end;
$$ language plpgsql;

create or replace procedure p_check_address_belongs_to_customer(p_addressid int, p_customerid int) as $$
begin
    if not exists (select 1 from addresses where addressid = p_addressid and customerid = p_customerid) then
        raise exception 'Address with ID % does not belong to Customer with ID %', p_addressid,p_customerid;
    end if;
end;
$$ language plpgsql;

CREATE OR REPLACE PROCEDURE p_check_category_exists(f_category_id int)
AS $$
BEGIN
    if not exists(select 1 from categories where categoryid = f_category_id) then
        raise exception 'Category with ID % does not exist', f_category_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_check_payment_method_exists(f_payment_method_id int)
AS $$
BEGIN
    if not exists(select 1 from paymentmethods where paymentmethodid = f_payment_method_id) then
        raise exception 'Payment method with ID % does not exist', f_payment_method_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_check_order_exists(f_order_id int)
AS $$
BEGIN
    if not exists(select 1 from orders where orderid = f_order_id) then
        raise exception 'Order with ID % does not exist', f_order_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_check_customers_order_exists(f_customer_id int, f_order_id int)
AS $$
BEGIN
    if not exists(select 1
                  from orders
                  where orderid = f_order_id
                  and customerid = f_customer_id) then
        raise exception 'Customer % has no order with ID %', f_customer_id, f_order_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_check_orders_status_pending(f_order_id int)
AS $$
BEGIN
    if not exists(select 1
                  from orders
                  where orderid = f_order_id
                  and status = 'pending') then
        raise exception 'Order with ID % has different status than pending', f_order_id;
    end if;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_add_category(
    f_category_name varchar,
    f_parent_category_id int default null)
AS $$
BEGIN
    IF f_parent_category_id IS NOT NULL THEN
        CALL p_check_category_exists(f_parent_category_id);
    END IF;
    INSERT INTO categories(categoryname, parentcategoryid)
    VALUES (f_category_name,
            f_parent_category_id);
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_add_payment_method(
    f_provider varchar,
    f_type varchar,
    f_is_active boolean
)
AS $$
BEGIN
    INSERT INTO paymentmethods(provider, type, isactive)
    VALUES (f_provider,
            f_type,
            f_is_active);
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_add_product(
    f_category_id int,
    f_name varchar,
    f_price decimal,
    f_stock int,
    f_is_active boolean,
    f_attributes jsonb
)
AS $$
BEGIN
    CALL p_check_category_exists(f_category_id);
    INSERT INTO products(categoryid, name, price, stock, isactive, attributes)
    VALUES (
            f_category_id,
            f_name,
            f_price,
            f_stock,
            f_is_active,
            f_attributes
           );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_deactivate_product(f_product_id int)
AS $$
BEGIN
    CALL p_check_product_exists(f_product_id);
    UPDATE products
    SET isactive = false
    WHERE productid = f_product_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_deactivate_payment_method(f_payment_method_id int)
AS $$
BEGIN
    CALL p_check_payment_method_exists(f_payment_method_id);
    UPDATE paymentmethods
    SET isactive = false
    WHERE paymentmethodid = f_payment_method_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_set_price(f_product_id int, new_price decimal)
AS $$
BEGIN
    CALL p_check_product_exists(f_product_id);
    UPDATE products
    SET price = new_price
    WHERE productid = f_product_id;
END;
$$ LANGUAGE plpgsql;

create procedure p_update_stock(
    in f_product_id integer,
    in quantity integer
)
language plpgsql
as $$
declare
    v_stock integer;
begin
    call p_check_product_exists(f_product_id);

    select stock
    into v_stock
    from products
    where productid = f_product_id
    for update;

    if v_stock + quantity < 0 then
        raise exception 'Stock cannot be negative';
    end if;

    update products
    set stock = stock + quantity
    where productid = f_product_id;
end;
$$;

CREATE OR REPLACE PROCEDURE p_set_category(f_product_id int, f_category_id int)
AS $$
BEGIN
    CALL p_check_product_exists(f_product_id);
    CALL p_check_category_exists(f_category_id);
    UPDATE products
    SET categoryid = f_category_id
    WHERE productid = f_product_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE p_pay_order(
    f_customer_id int,
    f_order_id int,
    f_payment_method_id int)
AS $$
DECLARE
    v_item record;
    v_amount decimal;
BEGIN
    CALL p_check_customers_order_exists(f_customer_id, f_order_id);
    CALL p_check_orders_status_pending(f_order_id);
    CALL p_check_payment_method_exists(f_payment_method_id);

    FOR v_item IN
        SELECT productid, quantity
        FROM orderdetails
        WHERE orderid = f_order_id
        FOR UPDATE
    LOOP
        CALL p_update_stock(v_item.productid, -v_item.quantity);
    END LOOP;

    UPDATE orders
    SET status = 'packed'
    WHERE orderid = f_order_id;

    SELECT SUM(quantity * unitprice) INTO v_amount
    FROM orderdetails
    WHERE orderid = f_order_id;

    INSERT INTO payments(orderid,
                         paymentmethodid,
                         amount,
                         status,
                         paidat)
    VALUES (f_order_id,
            f_payment_method_id,
            v_amount,
            'completed',
            now());

END;
$$ LANGUAGE plpgsql;

create or replace procedure p_register_customer(
    p_firstname varchar,
    p_lastname varchar,
    p_email varchar,
    p_phone varchar,
    p_passwordhash varchar
) as $$
begin
    insert into customers(firstname, lastname, email, phone, passwordhash)
    values (p_firstname, p_lastname, p_email, p_phone, p_passwordhash)
    on conflict (email) do update
        set firstname = excluded.firstname,
            lastname = excluded.lastname,
            phone = excluded.phone,
            passwordhash = excluded.passwordhash
        where customers.passwordhash is null;

    if not found then
        raise exception 'Customer with email % already exists', p_email;
    end if;
end;
$$ language plpgsql;

create or replace procedure p_add_address(
    p_customerid integer,
    p_label varchar,
    p_street varchar,
    p_city varchar,
    p_postalcode varchar,
    p_country varchar) as $$
declare
    v_is_default boolean;
    v_existing_id integer;
begin
    call p_check_customer_exists(p_customerid);

    select addressid into v_existing_id
    from addresses
    where customerid = p_customerid
    and street = p_street
    and city = p_city
    and postalcode = p_postalcode
    and country = p_country
    limit 1;

    if v_existing_id is not null then
        update addresses
        set isactive = true, label = p_label
        where addressid = v_existing_id;
        return;
    end if;

    select not exists (
        select 1 from vw_active_addresses
        where customerid = p_customerid
    ) into v_is_default;

    insert into addresses(customerid, label, street, city, postalcode, country, isdefault)
    values (p_customerid, p_label, p_street, p_city, p_postalcode, p_country, v_is_default);
end;
$$ language plpgsql;

create or replace procedure p_set_default_address(
    p_customerid integer,
    p_addressid integer
) as $$
    begin

        call p_check_address_exists(p_addressid);
        call p_check_customer_exists(p_customerid);
        call p_check_address_belongs_to_customer(p_addressid, p_customerid);

        if exists (
        select 1 from addresses
        where addressid = p_addressid
        and customerid = p_customerid
        and isactive = false
    ) then
        raise exception 'Inactive address % cannot be default', p_addressid;
    end if;

        update addresses
        set isdefault = false
        where customerid = p_customerid;

        update addresses
        set isdefault=true
        where addressid = p_addressid;
    end;
    $$ language plpgsql;

create or replace procedure p_deactivate_address(
    p_customerid integer,
    p_addressid integer)
as $$
declare
    v_was_default boolean;
begin
    call p_check_address_exists(p_addressid);
    call p_check_customer_exists(p_customerid);
    call p_check_address_belongs_to_customer(p_addressid, p_customerid);

    select isdefault into v_was_default
    from addresses where addressid = p_addressid;

    update addresses
    set isactive = false, isdefault = false
    where addressid = p_addressid;

    if v_was_default then
        update addresses
        set isdefault = true
        where addressid = (
            select addressid from vw_active_addresses
            where customerid = p_customerid
            limit 1
        );
    end if;
end;
$$ language plpgsql;

create or replace procedure p_change_password(
    p_customerid integer,
    p_newpasswordhash varchar
) as $$
begin
    call p_check_customer_exists(p_customerid);
    update customers
    set passwordhash = p_newpasswordhash
    where customerid = p_customerid;
end;
$$ language plpgsql;

create or replace procedure p_change_email(
    p_customerid integer,
    p_newemail varchar
) as $$
begin
    call p_check_customer_exists(p_customerid);
    if exists (select 1 from customers where email = p_newemail) then
        raise exception 'Email % is already taken', p_newemail;
    end if;
    update customers
    set email = p_newemail
    where customerid = p_customerid;
end;
$$ language plpgsql;

create or replace procedure p_change_phone(
    p_customerid integer,
    p_newphone varchar
) as $$
begin
    call p_check_customer_exists(p_customerid);
    update customers
    set phone = p_newphone
    where customerid = p_customerid;
end;
$$ language plpgsql;

create or replace procedure p_check_address_belongs_to_customer(p_addressid int, p_customerid int) as $$
begin
    if not exists (select 1 from addresses where customerid = p_customerid) then
        raise exception 'Address with ID % does not belong to Customer with ID %', p_addressid,p_customerid;
    end if;
end;
$$ language plpgsql;

create or replace procedure p_create_order(
    p_customerid integer,
    p_addressid integer,
    p_items jsonb
) as $$
declare
    v_orderid int;
    v_item jsonb;
    v_productid int;
begin
    call p_check_customer_exists(p_customerid);
    call p_check_address_exists(p_addressid);
    call p_check_address_belongs_to_customer(p_addressid, p_customerid);

    insert into orders(customerid, addressid)
    values(p_customerid, p_addressid)
    returning orderid into v_orderid;

    for v_item in select * from jsonb_array_elements(p_items)
    loop
        v_productid := (v_item->>'productId')::int;
        call p_check_product_exists(v_productid);

        if exists(select 1 from products where productid = v_productid and isactive = false) then
            raise exception 'Product with ID % is not active', v_productid;
        end if;

        insert into orderdetails(orderid, productid, unitprice, quantity)
        values (v_orderid,
                v_productid,
                (select price from products where productid = v_productid),
                (v_item->>'quantity')::int);
    end loop;
end;
$$ language plpgsql;

create or replace procedure p_cancel_order(
    p_customerid int,
    p_orderid int
) as $$
begin
    call p_check_customers_order_exists(p_customerid, p_orderid);

    update orders
    set status = 'cancelled'
    where orderid = p_orderid;
end;
$$ language plpgsql;

create or replace procedure p_change_order_status(
    p_orderid int,
    p_newstatus varchar
) as $$
begin

    call p_check_order_exists(p_orderid);
	if p_newstatus not in ('pending', 'packed', 'cancelled', 'delivered') then
		raise exception 'Invalid status: %', p_newstatus;
	end if;
    update orders
    set status = p_newstatus
    where orderid = p_orderid;
end;
$$ language plpgsql;
