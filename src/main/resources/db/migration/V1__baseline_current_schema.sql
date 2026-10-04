CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    username VARCHAR(120) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE app_roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE staff (
    id UUID PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    fname VARCHAR(120) NOT NULL,
    mname VARCHAR(120) NOT NULL,
    lname VARCHAR(120) NOT NULL,
    gender VARCHAR(120) NOT NULL,
    age INTEGER NOT NULL,
    address VARCHAR(120) NOT NULL,
    status VARCHAR(50) NOT NULL,
    date_of_join DATE NOT NULL,
    position VARCHAR(120) NOT NULL,
    user_id UUID UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES app_users (id)
);

CREATE TABLE app_user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_app_user_roles_user FOREIGN KEY (user_id) REFERENCES app_users (id),
    CONSTRAINT fk_app_user_roles_role FOREIGN KEY (role_id) REFERENCES app_roles (id)
);

CREATE TABLE next_of_kin (
    id UUID PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    phone_number VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255) NOT NULL,
    staff_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_next_of_kin_staff FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE
);

CREATE TABLE training (
    id UUID PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    provider VARCHAR(150) NOT NULL,
    attachment VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE,
    staff_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_training_staff FOREIGN KEY (staff_id) REFERENCES staff (id)
);

CREATE INDEX idx_next_of_kin_staff_id ON next_of_kin (staff_id);
CREATE INDEX idx_training_staff_id ON training (staff_id);

INSERT INTO app_roles (id, name)
VALUES ('00000000-0000-0000-0000-000000000001', 'ROLE_USER');