-- ============================================================
-- Script de creación de la base de datos
-- Base de datos: gestion_universitaria
-- ============================================================

-- Eliminar tablas en orden inverso a dependencias
DROP TABLE IF EXISTS matriculas;
DROP TABLE IF EXISTS asignaturas;
DROP TABLE IF EXISTS profesores;
DROP TABLE IF EXISTS titulaciones;
DROP TABLE IF EXISTS alumnos;
DROP TABLE IF EXISTS usuarios;

-- Usuarios del sistema (login + autoregistro)
-- rol: 'admin' tiene acceso a la gestión de usuarios; 'usuario' accede al resto
CREATE TABLE usuarios (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol      VARCHAR(20)  NOT NULL DEFAULT 'usuario'
);

-- Titulaciones universitarias
CREATE TABLE titulaciones (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(200) NOT NULL,
    descripcion TEXT
);

-- Profesores
CREATE TABLE profesores (
    id      INT          AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(200) NOT NULL,
    email   VARCHAR(200)
);

-- Asignaturas: cada una pertenece a una titulación y puede tener un profesor asignado
CREATE TABLE asignaturas (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(200) NOT NULL,
    capacidad_maxima INT          NOT NULL DEFAULT 30,
    id_titulacion    INT          NOT NULL,
    id_profesor      INT,
    FOREIGN KEY (id_titulacion) REFERENCES titulaciones(id),
    FOREIGN KEY (id_profesor)   REFERENCES profesores(id)
);

-- Alumnos
CREATE TABLE alumnos (
    id      INT          AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(200) NOT NULL,
    email   VARCHAR(200),
    dni     VARCHAR(20)  UNIQUE
);

-- Matrículas: relación N:M entre alumnos y asignaturas
-- Un alumno puede estar en varias asignaturas; una asignatura tiene varios alumnos
CREATE TABLE matriculas (
    id_alumno     INT NOT NULL,
    id_asignatura INT NOT NULL,
    PRIMARY KEY (id_alumno, id_asignatura),
    FOREIGN KEY (id_alumno)     REFERENCES alumnos(id),
    FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id)
);

-- ============================================================
-- Datos iniciales
-- ============================================================

-- Usuario administrador por defecto (usuario: admin / contraseña: admin).
-- La contraseña se guarda como hash PBKDF2 (formato de gestion.util.Passwords).
-- Cámbiala tras el primer acceso desde la gestión de usuarios.
INSERT INTO usuarios (nombre, password, rol) VALUES
    ('admin', 'pbkdf2$210000$xkio3R/CS7gTfPXtUV/8CQ==$026SjJvt6YoqVOCkgitP40xfe/BrGFA5wfo1NslXMaQ=', 'admin');

-- Datos de ejemplo para pruebas
INSERT INTO titulaciones (nombre, descripcion) VALUES
    ('Grado en Informática',   'Estudios de informática y desarrollo de software'),
    ('Grado en Matemáticas',   'Estudios de matemáticas puras y aplicadas');

INSERT INTO profesores (nombre, email) VALUES
    ('Juan Pérez',  'juan.perez@uni.es'),
    ('María López', 'maria.lopez@uni.es');

INSERT INTO asignaturas (nombre, capacidad_maxima, id_titulacion, id_profesor) VALUES
    ('Programación I',  3, 1, 1),
    ('Bases de Datos',  5, 1, 2),
    ('Cálculo I',       4, 2, NULL);

INSERT INTO alumnos (nombre, email, dni) VALUES
    ('Ana García',    'ana@correo.es',   '11111111A'),
    ('Luis Martínez', 'luis@correo.es',  '22222222B'),
    ('Sara Ruiz',     'sara@correo.es',  '33333333C');

-- Matrículas de ejemplo
INSERT INTO matriculas (id_alumno, id_asignatura) VALUES
    (1, 1),
    (2, 1),
    (1, 2);
