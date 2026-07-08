USE inventario_castores;

CREATE TABLE roles (
    idRol   INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    idUsuario   INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    correo      VARCHAR(100) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    idRol       INT NOT NULL,
    estatus     BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (idRol) REFERENCES roles (idRol)
);

CREATE TABLE productos (
    idProducto     INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    descripcion    VARCHAR(255) NULL,
    cantidad       INT NOT NULL DEFAULT 0,
    estatus        BOOLEAN NOT NULL DEFAULT TRUE,
    fechaCreacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_producto_cantidad CHECK (cantidad >= 0)
);

CREATE TABLE movimientos (
    idMovimiento     INT AUTO_INCREMENT PRIMARY KEY,
    idProducto       INT NOT NULL,
    idUsuario        INT NOT NULL,
    tipoMovimiento   ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad         INT NOT NULL,
    fechaMovimiento  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_movimiento_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (idProducto) REFERENCES productos (idProducto),
    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (idUsuario) REFERENCES usuarios (idUsuario)
);
