# Oracle PL/Sql

widoki, funkcje, procedury, triggery

ćwiczenie 2

(kontynuacja ćwiczenia 1)

---

Imiona i nazwiska autorów :

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
              AND r.status = 'P'
        );
END;
/

BEGIN
    p_update_no_available_places;
END;
/

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
        RAISE_APPLICATION_ERROR(-20006, 'No free places available for this trip');
    END IF;

    SELECT COUNT(*)
    INTO existing_reservation
    FROM reservation r
    WHERE r.trip_id = vtrip_id
      AND r.person_id = vperson_id
      AND r.status IN ('N', 'P');

    IF existing_reservation > 0 THEN
        RAISE_APPLICATION_ERROR(-20004, 'Reservation already exists');
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
/

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


CREATE OR REPLACE TRIGGER T_CHANGED_RESERVATION_STATUS_6b
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
/

```
