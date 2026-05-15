CREATE OR REPLACE FUNCTION f_report_best_sellers(number_of_best_sellers int)
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
        GROUP BY p.productid, p.name
        ORDER BY total_sold DESC
        LIMIT number_of_best_sellers;
END;
$$ LANGUAGE plpgsql;
