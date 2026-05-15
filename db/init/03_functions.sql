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
        WHERE (f_category_id is null or p.categoryid in (
            SELECT sub.categoryid FROM f_get_category_subtree(f_category_id) as sub))
        GROUP BY p.productid, p.name
        ORDER BY total_sold DESC
        LIMIT number_of_best_sellers;
END;
$$ LANGUAGE plpgsql;
