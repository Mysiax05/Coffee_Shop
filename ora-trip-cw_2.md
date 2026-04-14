# Oracle PL/Sql

widoki, funkcje, procedury, triggery

ćwiczenie 2

(kontynuacja ćwiczenia 1)

---

Imiona i nazwiska autorów : Hubert Myszka, Michał Nowak

---

<style>
  {
    font-size: 16pt;
  }
</style>

<style scoped>
 li, p {
    font-size: 14pt;
  }
</style>

<style scoped>
 pre {
    font-size: 10pt;
  }
</style>

# Zadanie 6

Zmiana struktury bazy danych. W tabeli `trip` należy dodać redundantne pole `no_available_places`. Dodanie redundantnego pola uprości kontrolę dostępnych miejsc (sprawdzenie liczby dostępnych miejsc), ale nieco skomplikuje procedury dodawania rezerwacji, zmiany statusu czy też zmiany maksymalnej liczby miejsc na wycieczki (potrzebna będzie dodatkowa aktualizacja w tabeli `trip`).

Należy przygotować polecenie/procedurę przeliczającą wartość pola `no_available_places` dla wszystkich wycieczek (do jednorazowego wykonania)

Obsługę pola `no_available_places` można zrealizować przy pomocy procedur lub triggerów

Należy zwrócić uwagę na spójność rozwiązania.

> UWAGA
> Należy stworzyć nowe wersje tych widoków/procedur/triggerów (np. dodając do nazwy dopisek 6 - od numeru zadania). Poprzednie wersje procedur należy pozostawić w celu umożliwienia weryfikacji ich poprawności.

- zmiana struktury tabeli

```sql
alter table trip add
    no_available_places int null
```

- polecenie przeliczające wartość `no_available_places`
  - należy wykonać operację "przeliczenia" liczby wolnych miejsc i aktualizacji pola `no_available_places`

# Zadanie 6 - rozwiązanie

```sql

CREATE OR REPLACE PROCEDURE p_update_no_available_places
AS
BEGIN
    UPDATE trip t
    SET no_available_places =
        t.max_no_places - (
            SELECT COUNT(*)
            FROM reservation r
            WHERE r.trip_id = t.trip_id
              AND r.status IN ('N', 'P')
        );
END;


BEGIN
    p_update_no_available_places;
END;


-- TEST

SELECT trip_id, max_no_places, no_available_places FROM trip;

-- RESULT

1,100,95
2,60,58
3,10,7
4,1,1



```

---

# Zadanie 6a - procedury

Obsługę pola `no_available_places` należy zrealizować przy pomocy procedur

- procedura dodająca rezerwację powinna aktualizować pole `no_available_places` w tabeli trip
- podobnie procedury odpowiedzialne za zmianę statusu oraz zmianę maksymalnej liczby miejsc na wycieczkę
- należy przygotować procedury oraz jeśli jest to potrzebne, zaktualizować triggery oraz widoki

> UWAGA
> Należy stworzyć nowe wersje tych widoków/procedur/triggerów (np. dodając do nazwy dopisek 6a - od numeru zadania). Poprzednie wersje procedur należy pozostawić w celu umożliwienia weryfikacji ich poprawności.

- może być potrzebne wyłączenie 'poprzednich wersji' triggerów

# Zadanie 6a - rozwiązanie

```sql

-- p_add_reservation_6a

CREATE OR REPLACE PROCEDURE p_add_reservation_6a(
    vtrip_id INT,
    vperson_id INT
) AS
    vlog_date              DATE;
    existing_reservation   INT;
    vreservation_id        INT;
    vavailable_places      INT;
BEGIN
    p_person_exist(vperson_id);
    p_trip_exist(vtrip_id);

    SELECT no_available_places
    INTO vavailable_places
    FROM trip
    WHERE trip_id = vtrip_id;

    IF vavailable_places <= 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'No free places available for this trip!');
    END IF;

    SELECT COUNT(*)
    INTO existing_reservation
    FROM reservation r
    WHERE r.trip_id = vtrip_id
      AND r.person_id = vperson_id
      AND r.status IN ('N', 'P');

    IF existing_reservation > 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Reservation already exists!');
    END IF;

    INSERT INTO reservation(trip_id, person_id, status)
    VALUES (vtrip_id, vperson_id, 'N')
    RETURNING reservation_id INTO vreservation_id;

    UPDATE trip t
    SET no_available_places = no_available_places - 1
    WHERE t.trip_id = vtrip_id;

    vlog_date := TRUNC(SYSDATE);

    INSERT INTO log(reservation_id, log_date, status)
    VALUES (vreservation_id, vlog_date, 'N');
END;


-- p_modify_reservation_status_6a

CREATE OR REPLACE PROCEDURE p_modify_reservation_status_6a(
    vreservation_id INT,
    vnew_status CHAR
) AS
    vold_status CHAR(1);
    vtrip_id INT;
    vavailable_places INT;
BEGIN
    SELECT status, trip_id INTO vold_status, vtrip_id
    FROM reservation
    WHERE reservation_id = vreservation_id;

    IF vold_status = vnew_status THEN
        RETURN;
    END IF;

    IF vold_status = 'C' AND vnew_status IN ('N', 'P') THEN
        SELECT no_available_places INTO vavailable_places FROM trip WHERE trip_id = vtrip_id;
        IF vavailable_places <= 0 THEN
            RAISE_APPLICATION_ERROR(-20006, 'No free places available for this trip!');
        END IF;
        UPDATE trip SET no_available_places = no_available_places - 1 WHERE trip_id = vtrip_id;
    END IF;

    IF vold_status IN ('N', 'P') AND vnew_status = 'C' THEN
        UPDATE trip SET no_available_places = no_available_places + 1 WHERE trip_id = vtrip_id;
    END IF;

    UPDATE reservation SET status = vnew_status WHERE reservation_id = vreservation_id;

    INSERT INTO log(reservation_id, log_date, status) VALUES (vreservation_id, TRUNC(SYSDATE), vnew_status);
END;


-- p_modify_max_places_6a

CREATE OR REPLACE PROCEDURE p_modify_max_places_6a(
    vtrip_id INT,
    vnew_max_places INT
) AS
    vold_max_places INT;
    vplaces_difference INT;
BEGIN
    SELECT max_no_places INTO vold_max_places FROM trip WHERE trip_id = vtrip_id;
    vplaces_difference := vnew_max_places - vold_max_places;
    UPDATE trip
    SET max_no_places = vnew_max_places,
        no_available_places = no_available_places + vplaces_difference
    WHERE trip_id = vtrip_id;
END;


-- TEST

CALL p_add_reservation_6a(3, 5);
COMMIT;
SELECT trip_id, max_no_places, no_available_places FROM trip WHERE trip_id = 3;

-- RESULT

3,15,8

-- TEST

SELECT reservation_id FROM reservation WHERE trip_id = 3 AND person_id = 5;

-- RESULT

38


-- TEST

CALL p_modify_reservation_status_6a(38, 'C');
COMMIT;
SELECT trip_id, max_no_places, no_available_places FROM trip WHERE trip_id = 3;

-- RESULT

3,15,10



-- TEST

CALL p_modify_max_places_6a(3, 20);
COMMIT;
SELECT trip_id, max_no_places, no_available_places FROM trip WHERE trip_id = 3;

-- RESULT

3,20,15


```

---

# Zadanie 6b - triggery

Obsługę pola `no_available_places` należy zrealizować przy pomocy triggerów

- podczas dodawania rezerwacji trigger powinien aktualizować pole `no_available_places` w tabeli trip
- podobnie, podczas zmiany statusu rezerwacji
- należy przygotować trigger/triggery oraz jeśli jest to potrzebne, zaktualizować procedury modyfikujące dane oraz widoki

> UWAGA
> Należy stworzyć nowe wersje tych widoków/procedur/triggerów (np. dodając do nazwy dopisek 6b - od numeru zadania). Poprzednie wersje procedur należy pozostawić w celu umożliwienia weryfikacji ich poprawności.

- może być potrzebne wyłączenie 'poprzednich wersji' triggerów

# Zadanie 6b - rozwiązanie

```sql

-- t_changed_reservation_status_6b

CREATE OR REPLACE TRIGGER t_changed_reservation_status_6b
AFTER UPDATE OF status
ON reservation
FOR EACH ROW
BEGIN
    IF :OLD.status <> :NEW.status THEN
        INSERT INTO log(reservation_id, log_date, status)
        VALUES (:NEW.reservation_id, TRUNC(SYSDATE), :NEW.status);

        IF :OLD.status IN ('N', 'P') AND :NEW.status = 'C' THEN
            UPDATE trip
            SET no_available_places = no_available_places + 1
            WHERE trip_id = :NEW.trip_id;

        ELSIF :OLD.status = 'C' AND :NEW.status IN ('N', 'P') THEN
            UPDATE trip
            SET no_available_places = no_available_places - 1
            WHERE trip_id = :NEW.trip_id;
        END IF;
    END IF;
END;


-- t_add_reservation_6b

CREATE OR REPLACE TRIGGER t_add_reservation_6b
AFTER INSERT ON reservation
FOR EACH ROW
BEGIN
    INSERT INTO log(reservation_id, log_date, status)
    VALUES (:NEW.reservation_id, TRUNC(SYSDATE), :NEW.status);

    IF :NEW.status IN ('N', 'P') THEN
        UPDATE trip
        SET no_available_places = no_available_places - 1
        WHERE trip_id = :NEW.trip_id;
    END IF;
END;


-- t_update_trip_max_places_6b

CREATE OR REPLACE TRIGGER t_update_trip_max_places_6b
BEFORE UPDATE OF max_no_places ON trip
FOR EACH ROW
BEGIN
    IF :OLD.max_no_places <> :NEW.max_no_places THEN
        :NEW.no_available_places := :OLD.no_available_places + (:NEW.max_no_places - :OLD.max_no_places);
    END IF;
END;


-- TEST

INSERT INTO reservation(trip_id, person_id, status) VALUES (4, 2, 'N');
COMMIT;
SELECT trip_id, no_available_places FROM trip WHERE trip_id = 4;

-- RESULT

4,0


-- TEST

UPDATE reservation SET status = 'C' WHERE trip_id = 4 AND person_id = 2;
COMMIT;
SELECT trip_id, no_available_places FROM trip WHERE trip_id = 4;

-- RESULT

4,1

```
