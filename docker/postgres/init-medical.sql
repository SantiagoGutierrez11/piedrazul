-- ============================================================
-- Medical Staff Service — Script de inicialización
-- Base de datos: medical_db
-- ============================================================

CREATE TABLE IF NOT EXISTS doct_type (
    doct_type_id   SERIAL,
    doct_type_name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_doc_type  PRIMARY KEY (doct_type_id),
    CONSTRAINT uk_type_name UNIQUE (doct_type_name)
);

CREATE TABLE IF NOT EXISTS doctors (
    doct_user_id         INTEGER      NOT NULL,
    doct_professional_id VARCHAR(50)  NOT NULL,
    doct_first_name      VARCHAR(100) NOT NULL,
    doct_first_surname   VARCHAR(100) NOT NULL,
    doct_type_id         INTEGER      NOT NULL,
    CONSTRAINT pk_doctors            PRIMARY KEY (doct_user_id),
    CONSTRAINT uk_doct_professional_id UNIQUE (doct_professional_id),
    CONSTRAINT fk_doct_type          FOREIGN KEY (doct_type_id) REFERENCES doct_type (doct_type_id)
);

CREATE TABLE IF NOT EXISTS specialties (
    spec_id SERIAL,
    spec_name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_specialties PRIMARY KEY (spec_id),
    CONSTRAINT uk_spec_name UNIQUE (spec_name)
);

CREATE TABLE IF NOT EXISTS doctor_specialties (
    ds_doct_id INTEGER NOT NULL,
    ds_spec_id INTEGER NOT NULL,
    CONSTRAINT pk_doctor_specialties PRIMARY KEY (ds_doct_id, ds_spec_id),
    CONSTRAINT fk_ds_doct FOREIGN KEY (ds_doct_id) REFERENCES doctors (doct_user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ds_spec FOREIGN KEY (ds_spec_id) REFERENCES specialties (spec_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS doctor_schedules (
    sched_id SERIAL,
    sched_doctor_id INTEGER NOT NULL,
    sched_day_of_week INTEGER NOT NULL,
    sched_start_time TIME NOT NULL,
    sched_end_time TIME NOT NULL,
    sched_interval_minutes INTEGER NOT NULL,
    CONSTRAINT pk_doctor_schedules PRIMARY KEY (sched_id),
    CONSTRAINT fk_sched_doctor FOREIGN KEY (sched_doctor_id) REFERENCES doctors (doct_user_id) ON DELETE CASCADE,
    CONSTRAINT ck_sched_day_valid CHECK (sched_day_of_week BETWEEN 1 AND 7),
    CONSTRAINT ck_sched_interval_positive CHECK (sched_interval_minutes > 0)
);

CREATE TABLE IF NOT EXISTS system_parameters (
    parameter_key VARCHAR(100),
    parameter_value TEXT NOT NULL,
    CONSTRAINT pk_system_parameters PRIMARY KEY (parameter_key)
);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Tipos de profesional
INSERT INTO doct_type (doct_type_name) VALUES ('Médico')    ON CONFLICT (doct_type_name) DO NOTHING;
INSERT INTO doct_type (doct_type_name) VALUES ('Terapeuta') ON CONFLICT (doct_type_name) DO NOTHING;

-- Especialidades adicionales de los profesionales (Consulta General y Fisioterapia son servicios base, no requieren especialidad)
INSERT INTO specialties (spec_name) VALUES ('Terapia Neural') ON CONFLICT (spec_name) DO NOTHING;
INSERT INTO specialties (spec_name) VALUES ('Quiropraxia')    ON CONFLICT (spec_name) DO NOTHING;
INSERT INTO specialties (spec_name) VALUES ('Fisioterapia')   ON CONFLICT (spec_name) DO NOTHING;

-- ------------------------------------------------------------
-- Médicos (deben coincidir con los usuarios DOCTOR de identity)
-- ------------------------------------------------------------
INSERT INTO doctors (doct_user_id, doct_professional_id, doct_first_name, doct_first_surname, doct_type_id)
VALUES (1000000002, 'MED-001', 'Juan', 'Pérez', (SELECT doct_type_id FROM doct_type WHERE doct_type_name = 'Médico'))
ON CONFLICT (doct_user_id) DO NOTHING;

INSERT INTO doctors (doct_user_id, doct_professional_id, doct_first_name, doct_first_surname, doct_type_id)
VALUES
    (1000000005, 'MED-002', 'Ana',    'Martínez', (SELECT doct_type_id FROM doct_type WHERE doct_type_name = 'Médico')),
    (1000000006, 'MED-003', 'Pedro',  'Gómez',    (SELECT doct_type_id FROM doct_type WHERE doct_type_name = 'Médico')),
    (1000000007, 'MED-004', 'Laura',  'Torres',   (SELECT doct_type_id FROM doct_type WHERE doct_type_name = 'Médico')),
    (1000000008, 'MED-005', 'Miguel', 'Castro',   (SELECT doct_type_id FROM doct_type WHERE doct_type_name = 'Médico'))
ON CONFLICT (doct_user_id) DO NOTHING;

-- ------------------------------------------------------------
-- Asignación de servicios
--   Regla: TODOS los médicos atienden Consulta General y Terapia Neural.
--   La Quiropraxia solo la ofrecen los especialistas asignados
--   (que además atienden los otros dos servicios).
-- ------------------------------------------------------------

-- Especialistas en Quiropraxia: Pedro Gómez (1000000006) y Miguel Castro (1000000008)
INSERT INTO doctor_specialties (ds_doct_id, ds_spec_id)
SELECT d.doct_user_id, s.spec_id
FROM doctors d
CROSS JOIN specialties s
WHERE s.spec_name = 'Quiropraxia'
  AND d.doct_user_id IN (1000000006, 1000000008)
ON CONFLICT DO NOTHING;

-- Nota: Consulta General y Fisioterapia son servicios base.
-- Todo profesional puede atenderlos sin especialidad registrada.

-- ============================================================
-- HORARIOS DE MÉDICOS (Lunes=1 ... Domingo=7, ISO 8601)
-- Todos atienden Lun–Vie, 08:00–12:00, intervalos de 30 min
-- ============================================================

-- Juan Pérez (1000000002) — Medicina General
INSERT INTO doctor_schedules (sched_doctor_id, sched_day_of_week, sched_start_time, sched_end_time, sched_interval_minutes)
SELECT 1000000002, d, '08:00', '12:00', 30
FROM generate_series(1, 5) AS d
WHERE NOT EXISTS (
    SELECT 1 FROM doctor_schedules WHERE sched_doctor_id = 1000000002 AND sched_day_of_week = d
);

-- Ana Martínez (1000000005) — Pediatría
INSERT INTO doctor_schedules (sched_doctor_id, sched_day_of_week, sched_start_time, sched_end_time, sched_interval_minutes)
SELECT 1000000005, d, '08:00', '12:00', 30
FROM generate_series(1, 5) AS d
WHERE NOT EXISTS (
    SELECT 1 FROM doctor_schedules WHERE sched_doctor_id = 1000000005 AND sched_day_of_week = d
);

-- Pedro Gómez (1000000006) — Cardiología
INSERT INTO doctor_schedules (sched_doctor_id, sched_day_of_week, sched_start_time, sched_end_time, sched_interval_minutes)
SELECT 1000000006, d, '08:00', '12:00', 30
FROM generate_series(1, 5) AS d
WHERE NOT EXISTS (
    SELECT 1 FROM doctor_schedules WHERE sched_doctor_id = 1000000006 AND sched_day_of_week = d
);

-- Laura Torres (1000000007) — Dermatología
INSERT INTO doctor_schedules (sched_doctor_id, sched_day_of_week, sched_start_time, sched_end_time, sched_interval_minutes)
SELECT 1000000007, d, '08:00', '12:00', 30
FROM generate_series(1, 5) AS d
WHERE NOT EXISTS (
    SELECT 1 FROM doctor_schedules WHERE sched_doctor_id = 1000000007 AND sched_day_of_week = d
);

-- Miguel Castro (1000000008) — Medicina General
INSERT INTO doctor_schedules (sched_doctor_id, sched_day_of_week, sched_start_time, sched_end_time, sched_interval_minutes)
SELECT 1000000008, d, '08:00', '12:00', 30
FROM generate_series(1, 5) AS d
WHERE NOT EXISTS (
    SELECT 1 FROM doctor_schedules WHERE sched_doctor_id = 1000000008 AND sched_day_of_week = d
);
