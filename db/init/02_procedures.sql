
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
$$ language plpgsql;CREATE OR REPLACE PROCEDURE p_check_product_exists(f_product_id int)
AS $$
BEGIN
    if not exists(select 1 from products where productid = f_product_id) then
        raise exception 'Product with ID % does not exist', f_product_id;
    end if;
END;
$$ LANGUAGE plpgsql;

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

CREATE OR REPLACE PROCEDURE p_update_stock(f_product_id int, quantity int)
AS $$
BEGIN
    CALL p_check_product_exists(f_product_id);
    IF (SELECT stock + quantity FROM products WHERE productid = f_product_id) < 0 THEN
        RAISE EXCEPTION 'Stock cannot be negative';
    END IF;
    UPDATE products
    SET stock = stock + quantity
    WHERE productid = f_product_id;
END;
$$ LANGUAGE plpgsql;

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
