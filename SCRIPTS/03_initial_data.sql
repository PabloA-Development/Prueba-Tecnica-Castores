USE inventario_castores;

INSERT INTO roles (nombre)
VALUES
    ('ADMINISTRADOR'),
    ('ALMACENISTA');

INSERT INTO productos (nombre, descripcion)
VALUES
    ('Laptop Dell',      'Laptop para uso administrativo'),
    ('Monitor Samsung',  'Monitor LED de 24 pulgadas'),
    ('Teclado Logitech', 'Teclado alámbrico USB');

INSERT INTO usuarios (nombre, correo, contrasena, idRol, estatus)
VALUES
    ('Administrador Demo', 'admin@castores.local',
     '$2a$10$tt08po8RIyd/ZNyYBTH3euQZyVRpWX97T55G2WFCTUbin3iJFkYwC',
     (SELECT idRol FROM roles WHERE nombre = 'ADMINISTRADOR'), TRUE),
    ('Almacenista Demo', 'almacen@castores.local',
     '$2a$10$/14bvUw00ddRO9AiyhyA3OZNNnehDnf73Ml4WKiPlumHiDaj5.iIy',
     (SELECT idRol FROM roles WHERE nombre = 'ALMACENISTA'), TRUE);
